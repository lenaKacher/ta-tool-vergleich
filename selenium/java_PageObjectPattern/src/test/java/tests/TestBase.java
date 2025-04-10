package tests;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testcontainers.containers.BrowserWebDriverContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;

public class TestBase {
    public WebDriver driver;
    static long beforeBrowserStartTS =0;
    static long beforeTestStartTS = 0;
    

    //public static final String URL = "https://advantageonlineshopping.com/#/";
    public static final String URL = "http://172.16.15.213:8080/";
    static File file = new File("recordings");


    @ClassRule
    public static BrowserWebDriverContainer chrome = new BrowserWebDriverContainer<>(DockerImageName.parse("selenium/standalone-chrome:126.0"))
                    //.withRecordingMode(VncRecordingMode.RECORD_ALL, file, VncRecordingFormat.MP4)
                    //.withRecordingFileFactory(new DefaultRecordingFileFactory())
                    .withCapabilities(new ChromeOptions());

    @Before
    public  void setupTest() {
        init();
    }

     @BeforeClass
    public static void setupScreenshotsFolder() {
        try {
            Path screenshotsDir = Paths.get("screenshots");
            if (!Files.exists(screenshotsDir)) {
                Files.createDirectory(screenshotsDir);
                System.out.println("[init] Created screenshots/ directory");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void init() {        
        beforeBrowserStartTS = System.currentTimeMillis();
        driver = new RemoteWebDriver(chrome.getSeleniumAddress(), new ChromeOptions());
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
        driver.get(URL);
        beforeTestStartTS = System.currentTimeMillis();
        //driver.manage().window().maximize();
        
    }

    @After
    public void tearDown(){
        long testStoppedTS = System.currentTimeMillis();
        String testRunTimeWithBrowser = Long.toString(testStoppedTS - beforeBrowserStartTS);
        String testRunTime = Long.toString(testStoppedTS - beforeTestStartTS);

        
        try {
            Files.writeString(new File("timings.csv").toPath(), testRunTimeWithBrowser + "\t" + testRunTime + "\n", StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        driver.quit();
    }
}
