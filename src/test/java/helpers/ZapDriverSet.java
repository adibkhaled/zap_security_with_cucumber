package helpers;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;

import io.github.bonigarcia.wdm.WebDriverManager;

public class ZapDriverSet {
	private WebDriver driver;

    @After
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
    
    @Before
    public WebDriver zapProxyDriver() {
        if (driver == null) {
            ChromeOptions chromeOptions = new ChromeOptions();
            WebDriverManager.chromedriver().setup();

            // ZAP proxy config
            String zapProxyHost = "127.0.0.1";
            String zapProxyPort = "8080";

            //set the proxy to use ZAP host and port
            String proxyAddress = zapProxyHost + ":" + zapProxyPort;
            Proxy zap_proxy = new Proxy();
            zap_proxy.setHttpProxy(proxyAddress).setSslProxy(proxyAddress);
           
            chromeOptions.addArguments("--ignore-certificate-errors");
            chromeOptions.setCapability(CapabilityType.PROXY, zap_proxy);
            driver = new ChromeDriver(chromeOptions);
        }
        return driver;
    }

}
