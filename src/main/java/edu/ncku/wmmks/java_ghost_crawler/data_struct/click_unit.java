package edu.ncku.wmmks.java_ghost_crawler.data_struct;

import java.util.LinkedList;

public class click_unit {
	private LinkedList<click_unit> sub = null;
	private String click_title;
	private String store_directory;
	private String url;
	
	public click_unit() {
		this.sub = new LinkedList<click_unit>();
	}
	
	public String getStore(){ return this.store_directory; }
	
	public void setStore(String path){ this.store_directory = path; }
	
	public void setTitle(String title) { this.click_title = title; }
	
	public void addSub(click_unit entity) { this.sub.add(entity); }
	
	public void setUrl(String past) { this.url = past; }
	
	public int getSubSize() { return sub.size(); }
	
	public String getUrl() { return this.url; }
	
	public String getTitle() { return this.click_title; }
		
	public click_unit getNextClick(int index) { return sub.get(index); }

}
