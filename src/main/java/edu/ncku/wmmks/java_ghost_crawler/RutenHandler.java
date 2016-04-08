package edu.ncku.wmmks.java_ghost_crawler;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.w3c.dom.*;

import edu.ncku.wmmks.java_ghost_crawler.data_struct.click_unit;

public class RutenHandler {
	final static public String RUTEN_URL = "http://www.ruten.com.tw/";
	
	private WebDriver main_driver;
	
	public click_unit click_head;
	
	public RutenHandler(WebDriver driver) {
		this.main_driver = driver;
		driver.get(RutenHandler.RUTEN_URL);
	}
	
	/**
	 * 
	 * @param head
	 * @param driver
	 * @param parents_store
	 */
	public void crawl_iterator(click_unit head, WebDriver driver, String parents_store){
//		System.out.println(head.getTitle() + " : " + driver.getCurrentUrl());
		head.setUrl(driver.getCurrentUrl());
		head.setStore(parents_store + head.getTitle() + "/");
//		System.out.println(head.getStore());
		
		if(head.getSubSize() == 0){
			// crawl product here , multi-thread here, call CrawlProduct.java thread here
			CrawlProduct leaf_category = new CrawlProduct(head.getUrl(), head.getStore());
		} else {
			for(int i = 0; i < head.getSubSize(); i++){
				try{
					driver.findElement(By.partialLinkText(head.getNextClick(i).getTitle())).click();
				}catch(Exception e){
					System.out.println(head.getNextClick(i).getTitle() + " - Click Failed");
				}
				crawl_iterator(head.getNextClick(i), driver, head.getStore());
				driver.navigate().to(head.getUrl());
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
							, click_list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private click_unit storeClickOrder(NodeList xml, click_unit head){
		
		for ( int i = 0; i < xml.getLength(); i++) {
			if(!xml.item(i).getNodeName().toString().contains("#")){
				NodeList childes = xml.item(i).getChildNodes();
				click_unit child = new click_unit();
				child.setTitle(xml.item(i).getAttributes().getNamedItem("text").getTextContent());
				//System.out.println(child.getTitle());
				child = storeClickOrder(childes, child);
				head.addSub(child);
			}
		}
		return head;
		
	}
	
	
}
