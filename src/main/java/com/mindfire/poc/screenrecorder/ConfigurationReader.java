package com.mindfire.poc.screenrecorder;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigurationReader {
	
	public Properties prop = new Properties();
	
	public ConfigurationReader() throws IOException{
		
		try (FileInputStream in = new FileInputStream("config.ini")) {
		    prop.load(in);
		}
		
	}
	
	public static void main(String[] args) throws IOException {
		
		ConfigurationReader configReader = new ConfigurationReader();
		
		System.out.println(configReader.prop.get("RecordingPath"));		
	}

}
