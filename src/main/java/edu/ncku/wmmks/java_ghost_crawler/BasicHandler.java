package edu.ncku.wmmks.java_ghost_crawler;

import java.io.File;

public class BasicHandler extends BasicCrawler{
	
	
	public static boolean delete_directory(String dirName){
		//Construct directory structure
		File directory = new File(dirName);
		if(directory.exists()){
			System.out.println("Deleting directory : " + dirName);
			boolean result = false;
			try{
				directory.delete();
				result = true;
				return result;
			}catch(SecurityException se){}
		}
		return false;
	}
	
	public static boolean create_directory(String dirName) {
		//Construct directory structure
		File directory = new File(dirName);
		
		if(!directory.exists()){
			System.out.println("Creating directory : " + dirName);
			boolean result = false;
			
			try{
				directory.mkdir();
				result = true;
				return result;
			}catch(SecurityException se){}

		}
		return false;
	}// end of function
}
