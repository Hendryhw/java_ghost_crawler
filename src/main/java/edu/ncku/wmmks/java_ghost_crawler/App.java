package edu.ncku.wmmks.java_ghost_crawler;

import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

public class App {
	private static String OS = System.getProperty("os.name").toLowerCase();
	private static String phantomjs_path;
	static String crawl_file;
	
    public static void main( String[] args ) {
    	
    	if (isWindows()) {
    		System.out.println("Found Operating System : Windows");
    		phantomjs_path = "phantomjs/bin/phantomjs.exe";
		} else if (isMac()) {
			System.out.println("Found Operating System : Mac");
		} else if (isUnix()) {
			System.out.println("Found Operating System : Unix Base");
			phantomjs_path = "phantomjs/bin/phantomjs";
		} else if (isSolaris()) {
			System.out.println("Found Operating System : Solaris");
			System.out.println("This is Solaris");
		} else {
			System.out.println("Not support this Operating System, Crawler Stop !");
			System.exit(0);
		}
    	
    	if(args.length != 1){
			System.out.println("usage: java -jar ghost_crawler.jar <crawl_input_file.xml>");
			crawl_file = "click_order.xml";
		}else{
			crawl_file = args[0];
		}
    	
    	
    	// prepare capabilities
        Capabilities caps = new DesiredCapabilities();
        ((DesiredCapabilities) caps).setJavascriptEnabled(true);                //< not really needed: JS enabled by default
        ((DesiredCapabilities) caps).setCapability("takesScreenshot", false);    //< yeah, GhostDriver haz screenshotz!
        ((DesiredCapabilities) caps).setCapability(
            PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
            phantomjs_path
        );

        // Launch driver (will take care and ownership of the phantomjs process)
        WebDriver driver = new PhantomJSDriver(caps);
        
        ruten_action(driver);
        //example_action(driver);      

        // done
        driver.quit();
    }

	private static void ruten_action(WebDriver driver) {
		RutenHandler ruten_handler = new RutenHandler(driver, phantomjs_path);
		ruten_handler.analyzeClickOrder(crawl_file);
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
	
	public static boolean isWindows() {

		return (OS.indexOf("win") >= 0);

	}

	public static boolean isMac() {

		return (OS.indexOf("mac") >= 0);

	}

	public static boolean isUnix() {

		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
		
	}

	public static boolean isSolaris() {

		return (OS.indexOf("sunos") >= 0);

	}
    
}
