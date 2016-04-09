package edu.ncku.wmmks.java_ghost_crawler;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

public class CrawlProduct extends Thread{
	// final variable for storing files' name
	private static final String BASIC_FILE = "basic.txt";
	private static final String DESCRIPTION_FILE = "description.txt";
	private static final String RECORDS_FILE = "qa.txt";
	private static final String QA_FILE = "records.txt";
	private static final String PAY_FILE = "pay.txt";
	private static final String DELIVERY_FILE = "delivery.txt";
	
	private WebDriver driver;
	private String start_url;
	private String store_path;
	
	public CrawlProduct(String start_url, String store_path){
		this.start_url = start_url;
		this.store_path = store_path;
		
		// prepare capabilities
        Capabilities caps = new DesiredCapabilities();
        ((DesiredCapabilities) caps).setJavascriptEnabled(true);                //< not really needed: JS enabled by default
        ((DesiredCapabilities) caps).setCapability("takesScreenshot", true);    //< yeah, GhostDriver haz screenshotz!
        ((DesiredCapabilities) caps).setCapability(
            PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
            "./phantomjs/bin/phantomjs.exe"
        );
        this.driver = new PhantomJSDriver(caps);
	}
	
	public void run() {
		System.out.println(Thread.currentThread().getName()+" Start.");
        processCommand();
        System.out.println(Thread.currentThread().getName()+" End.");
	}

	private void processCommand() {
		try {
			System.out.println("Store directory : " + this.store_path);
			System.out.println("Start url       : " + this.start_url);
            Thread.sleep(50000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
	}

}
