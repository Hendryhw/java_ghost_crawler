package edu.ncku.wmmks.java_ghost_crawler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

public class CrawlProduct extends BasicCrawler{
	private static final String RUTEN_PRODUCT_TITLE_XPATH = "//h3[@class='item-name']/a";
	private static final String SPLIT_SIGN = " : ";
	
	// final variable for storing files' name
	private static final String BASIC_FILE = "basic.txt";
	private static final String DESCRIPTION_FILE = "description.txt";
	private static final String RECORDS_FILE = "records.txt";
	private static final String QA_FILE = "qa.txt";
	private static final String PAY_FILE = "pay.txt";
	private static final String DELIVERY_FILE = "delivery.txt";
	
	private WebDriver driver;
	private String start_url;
	private String store_path;
	private String main_html;
	private String phantomjs_path;
	
	private int crawl_pages;
	
	public CrawlProduct(String start_url, String store_path, String phantomjs_path){
		this.start_url = start_url;
		this.store_path = store_path;
		this.crawl_pages = 0;
		this.phantomjs_path = phantomjs_path;
		
		// prepare capabilities
        Capabilities caps = new DesiredCapabilities();
        ((DesiredCapabilities) caps).setJavascriptEnabled(true);                //< not really needed: JS enabled by default
        ((DesiredCapabilities) caps).setCapability("takesScreenshot", false);    //< yeah, GhostDriver haz screenshotz!
        ((DesiredCapabilities) caps).setCapability(
            PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
            this.phantomjs_path
        );
        
        
        
        this.driver = new PhantomJSDriver(caps);
	}
	
	public void run() {
		System.out.println(Thread.currentThread().getName()+" Start.");
		crawlCommand();
        System.out.println(Thread.currentThread().getName()+" End.");
        this.driver.quit();
	}

	private void crawlCommand() {
		System.out.println("Store directory : " + this.store_path);
		System.out.println("Start url       : " + this.start_url);
		this.driver.navigate().to(this.start_url);
		
		// Crawl the product title 
		List<WebElement> p_href = this.driver.findElements(By.xpath(RUTEN_PRODUCT_TITLE_XPATH));
		
		while(p_href.size() >= 10 ){
			ArrayList<String> p_title = new ArrayList<String>();
						
			for(int i = 0 ; i < p_href.size() ; i++){
				String tmp_title = p_href.get(i).getText();
				// Filter appropriate product name check redundant or not
				if(checkProduct(tmp_title)){
					p_title.add(tmp_title);
				}//end-if
	        }// end-for
			
			// Click and Crawl details
			crawlProductDetails(p_title);
			
			// next page
			this.driver.findElement(By.partialLinkText("下頁>")).click();
			this.crawl_pages++;
			
			// Sleep 1 mins if crawl 100 pages
			if(this.crawl_pages % 100 == 0){
				try {
					Thread.sleep(60000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			p_href = this.driver.findElements(By.xpath(RUTEN_PRODUCT_TITLE_XPATH));
		}// end product size check while loop
	}
	
	private void crawlProductDetails(ArrayList<String> p_title) {
		String list_url = this.driver.getCurrentUrl();
		for(String product_name : p_title){
			String item_storage = this.store_path + getMD5(product_name);
			if(BasicHandler.create_directory(item_storage)){
				System.out.println("Find new product : " + product_name);
				
				// handle error if any step having errors
				try{
					//Crawl Basic Information
					crawlProductBasic(item_storage, product_name);
					
					//Crawl Product Pay Method
					crawlProductPay(item_storage);
					
					//Crawl Product Delivery Method
					crawlProductDelivery(item_storage);
					
					//Crawl Product Description
					crawlProductDescription(item_storage);
					
					//Crawl Product Question and Answers
					crawlProductQA(item_storage);
					
					//Crawl Product Trade Records
					crawlProductTradeRecords(item_storage);
					
				}catch(Exception e){
					// Before continuing, delete the created directory
					BasicHandler.delete_directory(item_storage);
					System.out.println("Error occur : " + product_name);
					// navigate back product list page
					this.driver.navigate().to(list_url);
					continue;
				}
				
			}
			// navigate back product list page
			this.driver.navigate().to(list_url);
			
		}
		
	}
	
	private void crawlProductBasic(String item_storage, String product_name) throws Exception{
		String basic_content = "";
		// Store product title
		basic_content += "<product>" + CrawlProduct.SPLIT_SIGN + product_name + "\n";
		// navigate to product page
		this.driver.findElement(By.partialLinkText(product_name)).click();
		main_html = this.driver.getPageSource();
		writeString(item_storage + "/" + "src.txt", main_html);
		// Store product URL
		basic_content += "<url>" + CrawlProduct.SPLIT_SIGN + this.driver.getCurrentUrl() + "\n";
		// Get the whole seller
		WebElement seller = this.driver.findElement(By.cssSelector("a[title='查看賣家資料']"));
		// Store product seller
		basic_content += "<seller>" + CrawlProduct.SPLIT_SIGN + seller.getText() + "\n";
		// Store product seller
		basic_content += "<seller_store_url>" + CrawlProduct.SPLIT_SIGN + seller.getAttribute("href") + "\n";
		// Store price
		basic_content += "<price>" + CrawlProduct.SPLIT_SIGN + this.driver.findElement(By.className("price")).getText();
		
		writeString(item_storage + "/" + CrawlProduct.BASIC_FILE, basic_content);
	}
	
	private void crawlProductPay(String item_storage) throws Exception{
		
		String pay_xpath = "div.item-info-detail table:eq(1) tbody tr > td:eq(1)";
		String basic_content = "";
		//System.out.println(main_html);
		//basic_content = this.driver.findElement(By.xpath(pay_xpath)).getText();
		
		Document main_doc = Jsoup.parse(main_html);
		
		Elements pay = main_doc.select(pay_xpath);
		basic_content = pay.text();
		/*for(Element single_record:records){
			basic_content += (single_record.text() + "\n");
		}*/
		
		writeString(item_storage + "/" + CrawlProduct.PAY_FILE, basic_content);
	}

	private void crawlProductDelivery(String item_storage) throws Exception{
		String delivery_xpath = "div.item-info-detail table:eq(2) tbody tr > td:eq(1)";
		String basic_content = "";
		//basic_content = this.driver.findElement(By.xpath(delivery_xpath)).getText();
		
		Document main_doc = Jsoup.parse(main_html);
		Elements delivery = main_doc.select(delivery_xpath);
		basic_content = delivery.text();
		
		writeString(item_storage + "/" + CrawlProduct.DELIVERY_FILE, basic_content);
	}
	
	private void crawlProductDescription(String item_storage) throws Exception {
		// If I have technique here, it will be better
		String basic_content = "";
		// Switch to product iframe
		this.driver.switchTo().frame(driver.findElement(By.name("embedded_goods_comments")));
		String p_des = this.driver.getPageSource();
		this.driver.switchTo().defaultContent();// switch back to original page
		
		Elements des_doc = Jsoup.parse(p_des).body().select("*");
		for (Element element : des_doc) {
			// Appropriate features selection condition
			if(element.ownText().length() > 1 && element.ownText().length() < 20)
				basic_content += (element.ownText() + "\n");
		}
		
		// Check this file crawl or not, and 
		// Skip it if this product have no description
		/*if(basic_content.length() == 0){
			System.out.println("Crawl Description Fail");
			throw new Exception();// I call it empty description exception
		}*/
		
		writeString(item_storage + "/" + CrawlProduct.DESCRIPTION_FILE, basic_content);
	}
	
	

	private void crawlProductQA(String item_storage) throws Exception{
		String basic_content = "";
		int qa_size = 1;
		// Crawl Question & Answer
		this.driver.findElement(By.partialLinkText("問與答")).click();
		
		if(this.driver.findElements(By.partialLinkText("詳細問與答紀錄")).size() > 0)
			this.driver.findElement(By.partialLinkText("詳細問與答紀錄")).click();
		
		while(this.driver.findElements(By.partialLinkText("下一頁")).size() > 0){
			System.out.println("QA p" + qa_size);
			this.driver.findElement(By.partialLinkText("下一頁")).click();
			qa_size++;
			// Crawl data
			String p_qa = this.driver.getPageSource();
			Document qa_doc = Jsoup.parse(p_qa);
			Elements qnas = qa_doc.select("div.product-qna > form > ul > li");
			String page_qa = "";
			for(Element qa : qnas){
				Elements question = qa.select("div.q > div.content");
				Elements answer = qa.select("div.a > div.content");
				page_qa += ("<user>" + CrawlProduct.SPLIT_SIGN + question.get(0).select("span.user-id").text() + "\n");
				page_qa += ("<question>" + CrawlProduct.SPLIT_SIGN + question.get(0).select("div.msg").text() + "\n");
				page_qa += ("<answer>" + CrawlProduct.SPLIT_SIGN + answer.get(0).text() + "\n");
			}
			basic_content += page_qa;
		}
		writeString(item_storage + "/" + CrawlProduct.QA_FILE, basic_content);
	}

	private void crawlProductTradeRecords(String item_storage) throws Exception{
		String basic_content = "";
		this.driver.findElement(By.linkText("出價紀錄")).click();
		if(this.driver.findElements(By.partialLinkText("詳細出價紀錄")).size() > 0)
			this.driver.findElement(By.partialLinkText("詳細出價紀錄")).click();
		
		// Crawl data
		String p_sellrecords = this.driver.getPageSource();
		Document sellrecord_doc = Jsoup.parse(p_sellrecords);
		Elements records = sellrecord_doc.select("form>table>tbody>tr td:eq(1) table:eq(2)>tbody"
				+ ">tr:eq(1)>td>table>tbody>tr td:eq(1)>table");
		for(Element single_record:records){
			basic_content += (single_record.text() + "\n");
		}
		
		writeString(item_storage + "/" + CrawlProduct.RECORDS_FILE, basic_content);
	}

	

	

	private static boolean checkProduct(String title) {
		// Check product rules
		return true;
	}

}
