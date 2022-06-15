package modules;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(
		features = "src/test/java/feature/"
		,glue = {"step_definition"}
		,plugin= {"pretty",
				"json:target/cucumberTest.json",
				"me.jvt.cucumber.report.PrettyReports:target/cucumber"
		}
		)
public class RunZapTest {

}
