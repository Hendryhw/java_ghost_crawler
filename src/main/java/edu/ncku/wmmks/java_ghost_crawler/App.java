package edu.ncku.wmmks.java_ghost_crawler;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class App {
	private static String OS = System.getProperty("os.name").toLowerCase();
	private static String driver_path;
	
	private static String crawl_file;
	
	
    public static void main( String[] args ) {
    	
    	check_input(args);
    	ruten_action(driver_path.contains("chrome"));

    }

	private static void check_input(String[] args) {
		
		if(args.length != 2){
			System.out.println("usage: java -jar ghost_crawler.jar <crawl_input_file.xml> <0:chorme dirver, 1:ghostdriver>");
			crawl_file = "click_order.xml";
			driver_path = "C:\\Program Files (x86)\\Google\\Chrome\\"
					+ "Application\\chromedriver.exe";
			return;
		}else{
			crawl_file = args[0];
		}
		
		if (isWindows()) {
    		System.out.println("Found Operating System : Windows");
    		if(Integer.parseInt(args[1]) == 0){
    			driver_path = "C:\\Program Files (x86)\\Google\\Chrome\\"
    					+ "Application\\chromedriver.exe";
    		}else{
    			driver_path = "phantomjs/bin/phantomjs.exe";
    		}
    		
		} else if (isMac()) {
			System.out.println("Found Operating System : Mac");
		} else if (isUnix()) {
			System.out.println("Found Operating System : Unix Base");
			if(Integer.parseInt(args[1]) == 0){
    			driver_path = "C:\\Program Files (x86)\\Google\\Chrome\\"
    					+ "Application\\chromedriver.exe";
    		}else{
    			driver_path = "phantomjs/bin/phantomjs";
    		}
		} else if (isSolaris()) {
			System.out.println("Found Operating System : Solaris");
			System.out.println("This is Solaris");
		} else {
			System.out.println("Not support this Operating System, Crawler Stop !");
			System.exit(0);
		}
    	
    	
	}

	private static void ruten_action(boolean view_browser) {
        //example_action(driver);      
		RutenHandler ruten_handler = new RutenHandler(driver_path);
		ruten_handler.analyzeClickOrder(crawl_file);
		ruten_handler.prepare_driver(driver_path, view_browser);
		ruten_handler.driver_get_ruten();
		ruten_handler.crawl_iterator(ruten_handler.click_head, "./");
		ruten_handler.quit_dirver();
		System.out.println("quit the main thread");
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
