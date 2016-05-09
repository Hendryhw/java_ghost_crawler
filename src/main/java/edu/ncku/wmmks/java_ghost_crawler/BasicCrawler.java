package edu.ncku.wmmks.java_ghost_crawler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

public abstract class BasicCrawler extends Thread{
	private final static int WAIT_LOADING = 25;
	
	
	protected WebDriver main_driver;
	protected boolean view_browser;
	
	public void quit_dirver(){
		this.main_driver.quit();
	}
	
	public abstract void crawlCommand();
	
	public void prepare_driver(String chrome_path, boolean view_browser){
		System.setProperty("webdriver.chrome.driver", chrome_path);// Set driver path
		
		// Set chrome driver options
		ChromeOptions opt = new ChromeOptions();
			opt.addArguments("--disable-plugins");
			
		DesiredCapabilities caps = new DesiredCapabilities();
        ((DesiredCapabilities) caps).setJavascriptEnabled(true);                //< not really needed: JS enabled by default
        ((DesiredCapabilities) caps).setCapability("takesScreenshot", true);   
		this.view_browser = view_browser;
		this.main_driver = new ChromeDriver(caps);
		this.main_driver.manage().timeouts().pageLoadTimeout(WAIT_LOADING, TimeUnit.SECONDS);
	}
	
	/**
	 * Scroll to the bottom of page
	 */
	public void scroll_down(){
		((JavascriptExecutor)this.main_driver)
			.executeScript("window.scroll(0, document.body.scrollHeight);");
	}
	
	public void prepare_driver(String phantomjs_path){
		// prepare capabilities
		DesiredCapabilities caps = new DesiredCapabilities();
        ((DesiredCapabilities) caps).setJavascriptEnabled(true);                //< not really needed: JS enabled by default
        ((DesiredCapabilities) caps).setCapability("takesScreenshot", true);    //< yeah, GhostDriver haz screenshotz!
        ((DesiredCapabilities) caps).setCapability(
            PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
            phantomjs_path
        );

        // Launch driver (will take care and ownership of the phantomjs process)
        this.main_driver = new PhantomJSDriver(caps);
        //this.main_driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
        //this.main_driver.manage().window().setSize(new Dimension(1024, 768));
	}
	
	protected static long dirSize(File dir){
		long length = 0;
	    for (File file : dir.listFiles()) {
	        if (file.isFile())
	            length += file.length();
	        else
	            length += dirSize(file);
	    }
	    
	    return length;
		
	}
	
	protected static boolean actionClick(WebDriver driver, String click_element){
		try{
			driver.findElement(By.partialLinkText(click_element)).click();
			return true;
		}catch(NoSuchElementException e){
			System.out.println("Click " + click_element + ", but exception occur, skip this page !!");
			return false;
		}
	}
	
	protected static void writeString(String fileName, String content){
		try {
			PrintWriter out = new PrintWriter(fileName, "UTF-8");
			out.append(content);
			out.flush();
			out.close();
		} catch (FileNotFoundException e ) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	
	protected static String getMD5(String text) {
		try {
			MessageDigest m = MessageDigest.getInstance("MD5");
			m.reset();
			m.update(text.getBytes());
			byte[] digest = m.digest();
			BigInteger bigInt = new BigInteger(1, digest);
			String hashtext = bigInt.toString(16);
			// Now we need to zero pad it if you actually want the full 32 chars.
			while(hashtext.length() < 32 ){
			  hashtext = "0" + hashtext;
			}
			return hashtext;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return text;
		
	}
	
}
