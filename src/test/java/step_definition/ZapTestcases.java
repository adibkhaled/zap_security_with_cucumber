package step_definition;

import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.zaproxy.clientapi.core.ClientApiException;

import helpers.ZapDriverSet;
import helpers.ZapHelper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ZapTestcases {

	private WebDriver zapProxyDriver;
	private static final String TARGET_URL = System.getenv("URL");
	ZapDriverSet zapDriverSet = new ZapDriverSet();

	public ZapTestcases() {
		zapProxyDriver = zapDriverSet.zapProxyDriver();
	}

	@Given("I open URL with ZAP browser")
	public void openURL() throws ClientApiException, InterruptedException {
		zapProxyDriver.get(TARGET_URL);
	}

	@SuppressWarnings("deprecation")
	@When("I check the URL")
	public void currentURL() {
		String currentURL = zapProxyDriver.getCurrentUrl();
		Assert.assertEquals(TARGET_URL, currentURL);
	}

	@Then("I run spider scann in Homepage")
	public void spiderHomePage() throws ClientApiException, InterruptedException {
		ZapHelper.spiderTarget(TARGET_URL);
	}

	@Then("I run passive scann in Homepage")
	public void passiveScanHomePage() throws ClientApiException {
		zapProxyDriver.get(TARGET_URL);
		// some more logic using page object to move to different pages goes here
		ZapHelper.waitForPassiveScanToComplete();
		ZapHelper.checkRiskCount(TARGET_URL);
	}

	@Then("I run active scann in Homepage")
	public void activeScanHomePage() throws ClientApiException, InterruptedException {
		zapProxyDriver.get(TARGET_URL);
		ZapHelper.activeScan(TARGET_URL);
		ZapHelper.checkRiskCount(TARGET_URL);
	}

}
