package edu.ncku.wmmks.java_ghost_crawler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class BasicCrawler extends Thread{
	
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
