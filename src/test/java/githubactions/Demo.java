package githubactions;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
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
    }
}
