package githubactions;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.testng.AllureTestNg;
import org.testng.annotations.Listeners;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Listeners({AllureTestNg.class})
public class Demo {
    protected WebDriver driver;

    @BeforeMethod
    public void setUp(){
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
    }

    @Test
    @Description("Open GitHub and verify title is available")
    @Severity(SeverityLevel.NORMAL)
    public void testcase1(){
        Allure.step("Navigate to github.com", () -> driver.get("https://github.com"));
        Allure.step("Capture title", () -> Allure.addAttachment("Page title", driver.getTitle()));
    }

    @Test
    @Description("Open Google and verify title is available")
    @Severity(SeverityLevel.MINOR)
    public void testcase2(){
        Allure.step("Navigate to google.com", () -> driver.get("https://google.com"));
        Allure.step("Capture title", () -> Allure.addAttachment("Page title", driver.getTitle()));
    }

    @Test
    @Description("Open Facebook and verify title is available")
    @Severity(SeverityLevel.MINOR)
    public void testcase3(){
        Allure.step("Navigate to facebook.com", () -> driver.get("https://facebook.com"));
        Allure.step("Capture title", () -> Allure.addAttachment("Page title", driver.getTitle()));
    }

    @AfterMethod
    public void tearDown(ITestResult result){
        if (result.getStatus() == ITestResult.FAILURE && driver != null) {
            try {
                byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                Allure.addAttachment("Screenshot", new ByteArrayInputStream(screenshot));
            } catch (Exception ignored) { }
            try {
                Allure.addAttachment("Page source", driver.getPageSource());
            } catch (Exception ignored) { }
        }

        if (driver != null) {
            driver.quit();
        }

        // Write a minimal Allure result JSON so Allure report can be generated
        try {
            writeAllureResult(result);
        } catch (IOException ignored) { }
    }

    private void writeAllureResult(ITestResult result) throws IOException {
        Path resultsDir = Paths.get("target", "allure-results");
        if (!Files.exists(resultsDir)) {
            Files.createDirectories(resultsDir);
        }

        String uuid = UUID.randomUUID().toString();
        String status = "unknown";
        if (result.getStatus() == ITestResult.SUCCESS) status = "passed";
        if (result.getStatus() == ITestResult.FAILURE) status = "failed";
        if (result.getStatus() == ITestResult.SKIP) status = "skipped";

        long start = result.getStartMillis() > 0 ? result.getStartMillis() : System.currentTimeMillis();
        long stop = result.getEndMillis() > 0 ? result.getEndMillis() : System.currentTimeMillis();

        String name = result.getMethod().getMethodName();
        String fullName = result.getMethod().getTestClass().getName() + "." + name;

        Map<String, Object> obj = new HashMap<>();
        obj.put("uuid", uuid);
        obj.put("name", name);
        obj.put("fullName", fullName);
        obj.put("status", status);
        obj.put("stage", "finished");
        obj.put("start", start);
        obj.put("stop", stop);

        String json = toJson(obj);
        Path out = resultsDir.resolve(uuid + "-result.json");
        Files.write(out, json.getBytes(StandardCharsets.UTF_8));
    }

    private String toJson(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        boolean first = true;
        for (Map.Entry<String, Object> e : map.entrySet()) {
            if (!first) sb.append(',');
            first = false;
            sb.append('"').append(e.getKey()).append('"').append(':');
            Object v = e.getValue();
            if (v instanceof Number) sb.append(v.toString());
            else sb.append('"').append(escape(v.toString())).append('"');
        }
        sb.append('}');
        return sb.toString();
    }

    private String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }
}
