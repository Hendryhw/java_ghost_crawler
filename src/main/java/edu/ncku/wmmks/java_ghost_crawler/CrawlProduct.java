package edu.ncku.wmmks.java_ghost_crawler;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

public class CrawlProduct extends Thread{
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
            "C:/Users/HenryJhou/java_Workspace/java_ghost_crawler/phantomjs-2.1.1/bin/phantomjs.exe"
        );
        this.driver = new PhantomJSDriver(caps);
	}
	
	public void run() {
		
	}

}
