package edu.ncku.wmmks.java_ghost_crawler;

import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

public class App {
    public static void main( String[] args ) {
    	// prepare capabilities
        Capabilities caps = new DesiredCapabilities();
        ((DesiredCapabilities) caps).setJavascriptEnabled(true);                //< not really needed: JS enabled by default
        ((DesiredCapabilities) caps).setCapability("takesScreenshot", true);    //< yeah, GhostDriver haz screenshotz!
        ((DesiredCapabilities) caps).setCapability(
            PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
            "C:/Users/HenryJhou/java_Workspace/java_ghost_crawler/phantomjs-2.1.1/bin/phantomjs.exe"
        );

        // Launch driver (will take care and ownership of the phantomjs process)
        WebDriver driver = new PhantomJSDriver(caps);
        
        ruten_action(driver);
        //example_action(driver);      

        // done
        driver.quit();
    }

	private static void ruten_action(WebDriver driver) {
		RutenHandler ruten_handler = new RutenHandler(driver);
		ruten_handler.analyzeClickOrder("click_order.xml");
		ruten_handler.crawl_iterator(ruten_handler.click_head, driver, "./");
	}

	private static void example_action(WebDriver driver) {
        // Load Google.com
        driver.get("http://www.google.com");
        // Locate the Search field on the Google page
        WebElement element = driver.findElement(By.name("q"));
        // Type Cheese
        String strToSearchFor = "Cheese!";
        element.sendKeys(strToSearchFor);
        // Submit form
        element.submit();
        
        // Check results contains the term we searched for
        assertTrue(driver.getTitle().toLowerCase().contains(strToSearchFor.toLowerCase()));
	}

	private static void assertTrue(boolean contains) {
		
	}
	
    
}
