package githubactions;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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
    public void testcase1(){
        driver.get("https://github.com");
        System.out.println("Title" + driver.getTitle());
    }

    @Test
    public void testcase2(){
        driver.get("https://google.com");
        System.out.println("Title" + driver.getTitle());
    }

    @Test
    public void testcase3(){
        driver.get("https://facebook.com");
        System.out.println("Title" + driver.getTitle());
    }

    @AfterMethod
    public void tearDown(){
        if (driver != null) {
            driver.quit();
        }
    }
}
