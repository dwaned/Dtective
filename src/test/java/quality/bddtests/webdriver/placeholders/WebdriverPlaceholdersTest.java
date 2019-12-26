package quality.bddtests.webdriver.placeholders;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;


/**
 * Hook for JUnit.
 */
@RunWith(Cucumber.class)
@CucumberOptions(glue = {"quality", "classpath:io.dtective"},
        plugin = {"io.qameta.allure.cucumber4jvm.AllureCucumber4Jvm"})

public class WebdriverPlaceholdersTest {
}
