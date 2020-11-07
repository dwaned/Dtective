package quality.bddtests.integrations.cloudprovider;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;


/**
 * Hook for JUnit.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        glue = {"quality", "classpath:io.dtective"},
        plugin = {"json:target/cucumber-report/report.json"})

public class CloudProviderTest {
}
