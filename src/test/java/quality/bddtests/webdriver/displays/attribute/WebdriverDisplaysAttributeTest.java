package quality.bddtests.webdriver.displays.attribute;

import io.cucumber.junit.CucumberOptions;
import io.cucumber.junit.Cucumber;
import org.junit.runner.RunWith;


/**
 * Hook for JUnit.
 */
@RunWith(Cucumber.class)
@CucumberOptions(glue = {"quality", "classpath:io.dtective"},
        plugin = { "json:target/cucumber-report/report.json"})

public class WebdriverDisplaysAttributeTest {
}
