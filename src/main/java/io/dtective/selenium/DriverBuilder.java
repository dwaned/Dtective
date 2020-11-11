package io.dtective.selenium;

import com.savoirtech.logging.slf4j.json.LoggerFactory;
import io.dtective.configuration.ParameterMap;
import io.dtective.selenium.Extensions.QAAndroidDriver;
import io.dtective.selenium.Extensions.QAIOSDriver;
import io.dtective.selenium.Extensions.QAWebDriver;
import io.dtective.selenium.Extensions.SharedWebDriver;
import io.dtective.webdrivers.CloudWebDriverCapabilities;
import io.dtective.webdrivers.WebDriverCapabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariDriverService;
import org.openqa.selenium.safari.SafariOptions;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * Created by Matyas Banszki on 2/18/2017.
 */
public class DriverBuilder {

    /**
     * System property for setting the location of the local chrome driver
     */
    private static final String SYSTEM_PROPERTY_CHROME_DRIVER = "webdriver.chrome.driver";

    /**
     * System property for setting the location of the local firefox driver
     */
    private static final String SYSTEM_PROPERTY_FIREFOX_DRIVER = "webdriver.gecko.driver";

    /**
     * System property for obtaining the current operating system type
     */
    private static final String SYSTEM_PROPERTY_OS_NAME = "os.name";

    /**
     * System propery for identifying MS Windows operating system
     */
    private static final String SYSTEM_PROPERTY_OS_WINDOWS = "windows";

    /**
     * MS Windows based web driver executable extension
     */
    private static final String WINDOWS_EXTENSION = ".exe";

    /**
     * Name of the executable for local chrome webdriver
     */
    private static final String CHROME_DRIVER = "chromedriver";

    /**
     * Name of the executable for local firefox webdriver
     */
    private static final String FIREFOX_DRIVER_GECKO = "geckodriver";

    /**
     * Name of the executable for local safari webdriver
     */
    private static final String SAFARI_DRIVER = "safaridriver";

    /**
     * Class logger
     */
    private static final com.savoirtech.logging.slf4j.json.logger.Logger
            LOGGER = LoggerFactory.getLogger("JSONLogger");

    /**
     * Evaluates parameters
     * -
     *
     * @return - WebDriver to be used for browsing
     */
    public WebDriver getWebDriver() {

        WebDriver driver = null;

        String browserType = ParameterMap.getParamBrowserType();

        int width, height;
        width = ParameterMap.getParamBrowserWidth();
        height = ParameterMap.getParamBrowserHeight();

        if (!ParameterMap.getParamIsSingleInstance()) {

            driver = new QAWebDriver(getDriver());

            if (!browserType.equals(BrowserType.ANDROID)) {
                driver.manage().window().setSize(new Dimension(width, height));
            }
        } else {

            LOGGER.debug().message("Adding new Remote Web-Driver")
                    .field("browser-type", browserType)
                    .field("remote-driver", true)
                    .field("single-instance", true)
                    .field("selenium-hub", ParameterMap.getParamSeleniumHubUrl()).log();

            if (SharedWebDriver.getInstance() == null) {
                SharedWebDriver.setInstance(new SharedWebDriver(getDriver()));
            }

            driver = SharedWebDriver.getInstance();

            if (!browserType.equals(BrowserType.ANDROID) && !browserType.equals(BrowserType.IPHONE)) {
                driver.manage().window().setSize(new Dimension(width, height));
            }
        }


        if (driver == null) {
            LOGGER.error().message("Failed to initialize the driver");
            throw new Error("Unable to initialize the driver \n " + ParameterMap.mapToString());
        }

        try {

            int timeout = ParameterMap.getParamWebDriverTimeout();
            driver.manage().timeouts().implicitlyWait(timeout, TimeUnit.SECONDS);
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        return driver;
    }

    public static WebDriver getDriver() {

        if (Boolean.parseBoolean(ParameterMap.getParamIsRemoteInstance()) || !ParameterMap.getParamCloudProvider().isEmpty()) {
            return getRemoteDriver(ParameterMap.getParamBrowserType());
        } else {
            return getLocalWebDriver(ParameterMap.getParamBrowserType());
        }

    }

    /**
     * Method to get the local webdriver session based on the selected browser type
     *
     * @param browserType Selenium browser type identifier e.g.:"chrome", "firefox"
     * @return Local browser session
     */
    private static WebDriver getLocalWebDriver(String browserType) {
        WebDriver driver;
        switch (browserType) {
            case BrowserType.CHROME:

                driver = getLocalChromeDriver();
                break;
            case BrowserType.FIREFOX:
                driver = getLocalFirefoxDriver();
                break;
            case BrowserType.SAFARI:
                driver = getLocalSafariDriver();
                break;
            default:
                throw new Error("Driver Not Implemented - " + browserType);
        }
        return driver;
    }

    /**
     * Requests a remote chrome driver
     * <p> REQUIREMENT : URL of the remote node</p>
     *
     * @param browserType Webdriver type - chrome, firefox, android
     * @return Encapsulated webdriver
     */
    public static WebDriver getRemoteDriver(String browserType) {

        WebDriver driver = null;

        if (!ParameterMap.getParamCloudProvider().isEmpty()) {
            LOGGER.debug().message("Creating new remote cloud web-driver - " + ParameterMap.getParamSeleniumHubUrl());
            try {
                driver = new RemoteWebDriver(new URL(CloudWebDriverCapabilities.getCloudURL()),
                        CloudWebDriverCapabilities.getCloudCapabilities());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else {

            if (ParameterMap.getParamSeleniumHubUrl() == null
                    || ParameterMap.getParamSeleniumHubUrl().isEmpty()
                    || ParameterMap.getParamSeleniumHubUrl().contains("N/A")) {
                LOGGER.error().message("Selenium Hub URL not set");
                System.exit(-1);
            }

            try {

                LOGGER.debug().message("Creating new remote web-driver - " + ParameterMap.getParamSeleniumHubUrl());

                if (browserType.equals(BrowserType.ANDROID)) {
                    driver = new QAAndroidDriver(new URL(ParameterMap.getParamSeleniumHubUrl()),
                            WebDriverCapabilities.getCapabilities(browserType));
                } else if (browserType.equals(BrowserType.IPHONE)) {
                    driver = new QAIOSDriver(new URL(ParameterMap.getParamSeleniumHubUrl()),
                            WebDriverCapabilities.getCapabilities(browserType));
                } else {
                    driver = new RemoteWebDriver(new URL(ParameterMap.getParamSeleniumHubUrl()),
                            WebDriverCapabilities.getCapabilities(browserType));
                }

            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error().message(e.getMessage()).log();

            }
        }
        return driver;
    }

    /**
     * Requests a local chrome driver
     * <p> REQUIREMENT : chromedriver.exe in the project root</p>
     *
     * @return - Local Chrome Driver instance
     */
    public static WebDriver getLocalChromeDriver() {

        WebDriver driver = null;

        LOGGER.debug().message("System.getProperty - OS.Name - "
                + System.getProperty(SYSTEM_PROPERTY_OS_NAME)).log();


        if (System.getProperty(SYSTEM_PROPERTY_OS_NAME).toLowerCase().contains(SYSTEM_PROPERTY_OS_WINDOWS)) {

            System.setProperty(SYSTEM_PROPERTY_CHROME_DRIVER, CHROME_DRIVER + WINDOWS_EXTENSION);

            ChromeDriverService service = new ChromeDriverService.Builder()
                    .usingDriverExecutable(new File(CHROME_DRIVER))
                    .usingAnyFreePort()
                    .build();

            ChromeOptions options = new ChromeOptions();
            options.merge(WebDriverCapabilities.getCapabilities(BrowserType.CHROME));
            driver = new ChromeDriver(service, options);

        } else {

            System.setProperty(SYSTEM_PROPERTY_CHROME_DRIVER, CHROME_DRIVER);

            try {
                ChromeDriverService service = new ChromeDriverService.Builder()
                        .usingDriverExecutable(new File(CHROME_DRIVER))
                        .usingAnyFreePort()
                        .build();

                ChromeOptions options = new ChromeOptions();

                if (ParameterMap.getParamChromeHeadlessMode())
                    options.addArguments("--headless");

                options.addArguments("--incognito");

                options.merge(WebDriverCapabilities.getCapabilities(BrowserType.CHROME));
                driver = new ChromeDriver(service, options);

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }

        if (driver == null)
            throw new AssertionError("Unable to create driver instance");


        return driver;

    }

    /**
     * Requests a local safari driver
     * <p> REQUIREMENT : Allow remote automation enabled in Safari</p>
     *
     * @return - Local Safari Driver instance
     */
    public static WebDriver getLocalSafariDriver() {

        SafariDriverService service = new SafariDriverService.Builder()
                .usingDriverExecutable(new File(SAFARI_DRIVER))
                .usingAnyFreePort()
                .build();

        SafariOptions options = new SafariOptions();
        options.merge(WebDriverCapabilities.getCapabilities(BrowserType.SAFARI));

        return new SafariDriver(service, options);

    }

    /**
     * Requests a local firefox driver
     * <p> REQUIREMENT : geckodriver.exe in the project root</p>
     *
     * @return - Local Firefox Driver instance
     */
    public static WebDriver getLocalFirefoxDriver() {

        if (System.getProperty(SYSTEM_PROPERTY_OS_NAME).toLowerCase().contains(SYSTEM_PROPERTY_OS_WINDOWS)) {

            System.setProperty("webdriver.firefox.bin", "C:\\Program Files\\Mozilla Firefox\\firefox.exe");
            System.setProperty(SYSTEM_PROPERTY_FIREFOX_DRIVER, FIREFOX_DRIVER_GECKO + WINDOWS_EXTENSION);
        } else {
            System.setProperty(SYSTEM_PROPERTY_FIREFOX_DRIVER, FIREFOX_DRIVER_GECKO);
        }

        GeckoDriverService service = new GeckoDriverService.Builder()
                .usingDriverExecutable(new File(FIREFOX_DRIVER_GECKO))
                .usingAnyFreePort()
                .build();

        FirefoxOptions options = new FirefoxOptions();
        options.merge(WebDriverCapabilities.getCapabilities(BrowserType.FIREFOX));

        return new FirefoxDriver(service, options);

    }


}
