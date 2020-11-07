package quality.bdd;

import io.cucumber.junit.CucumberOptions;
import io.cucumber.junit.Cucumber;
import org.junit.runner.RunWith;


/**
 * Hook for JUnit.
 */
@RunWith(Cucumber.class)
@CucumberOptions(glue = {"io.dtective.webdriver", "io.dtective.data"}
)

public class DataSetupBDDTest {
}
