package edu.ncku.wmmks.java_ghost_crawler;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.w3c.dom.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.ncku.wmmks.java_ghost_crawler.data_struct.click_unit;

public class RutenHandler extends BasicHandler{
	final static public String RUTEN_URL = "http://www.ruten.com.tw/";
	
	
	
	public click_unit click_head;
	
	private int computer_core_amount;
	private String driver_path;
	
	// executor to control thread amount
	private volatile ExecutorService executor;
	
	public RutenHandler(String driver_path) {
		this.driver_path = driver_path;
		this.computer_core_amount = Runtime.getRuntime().availableProcessors();
		System.out.println("CPU thread number : " + this.computer_core_amount);
		this.executor = Executors.newFixedThreadPool(this.computer_core_amount);
	}
	
	public RutenHandler(WebDriver driver, String driver_path) {
		this.main_driver = driver;
		this.driver_path = driver_path;
		this.computer_core_amount = Runtime.getRuntime().availableProcessors();
		System.out.println("CPU thread number : " + this.computer_core_amount);
		this.executor = Executors.newFixedThreadPool(this.computer_core_amount);
		driver.get(RutenHandler.RUTEN_URL);
	}
	
	public void driver_get_ruten(){
		this.main_driver.get(RutenHandler.RUTEN_URL);
	}
	
	
	/**
	 * 
	 * @param head
	 * @param driver
	 * @param parents_store
	 */
	public void crawl_iterator(click_unit head, String parents_store){
//		System.out.println(head.getTitle() + " : " + driver.getCurrentUrl());
		head.setUrl(this.main_driver.getCurrentUrl());
		head.setStore(parents_store + head.getTitle() + "/");
		if(BasicHandler.create_directory(head.getStore())){
			System.out.println(head.getStore() + " be created.");
		}
		
		
		if(head.getSubSize() == 0){
			if(this.view_browser){
				// crawl product with chrome driver single thread here
				// crawl product here , multi-thread here, call CrawlProduct.java thread here
				CrawlProduct leaf_category = new CrawlProduct(head.getUrl(), head.getStore(), this.driver_path, this.main_driver);
				leaf_category.crawlCommand();
				System.out.println("Start crawl with single thread.");
			}
			else{
				// crawl product here , multi-thread here, call CrawlProduct.java thread here
				CrawlProduct leaf_category = new CrawlProduct(head.getUrl(), head.getStore(), this.driver_path);
				//leaf_category.start();
				executor.execute(leaf_category);
				System.out.println("Start crawl with multi thread.");
			}
			System.out.println(head.getStore() + " - over");
		} else {
			for(int i = 0; i < head.getSubSize(); i++){
				try{
					this.main_driver.findElement(By.partialLinkText(head.getNextClick(i).getTitle())).click();
					this.sleep(3000);
				}catch(Exception e){
					System.out.println(head.getNextClick(i).getTitle() + " - Click Failed");
				}
				crawl_iterator(head.getNextClick(i), head.getStore());
				this.main_driver.navigate().to(head.getUrl());
			}		
		}// else
	}// end method crawl_iterator
	
	public void analyzeClickOrder(String crawl_src){
		File ipu_xml = new File(crawl_src);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		org.w3c.dom.Document doc = null;
		click_unit click_list = new click_unit();
		click_list.setUrl(RutenHandler.RUTEN_URL);
		click_list.setTitle("露天拍賣");
		
		try {
			builder = factory.newDocumentBuilder();
			doc = (org.w3c.dom.Document) builder.parse(ipu_xml);
			this.click_head = 
					storeClickOrder(doc.getElementsByTagName("product").item(0).getChildNodes()
							, click_list, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private click_unit storeClickOrder(NodeList xml, click_unit head, int stair){
		
		for ( int i = 0; i < xml.getLength(); i++) {
			if(!xml.item(i).getNodeName().toString().contains("#")){
				NodeList childes = xml.item(i).getChildNodes();
				click_unit child = new click_unit();
				child.setTitle(xml.item(i).getAttributes().getNamedItem("text").getTextContent());
				/*
				for(int s = 1; s <= stair; s++){
					System.out.print("   ");
				}
				System.out.println(child.getTitle());
				*/
				child = storeClickOrder(childes, child, stair+1);
				head.addSub(child);
			}
		}
		return head;
		
	}
	
	
}
