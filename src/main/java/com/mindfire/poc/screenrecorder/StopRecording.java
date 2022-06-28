package com.mindfire.poc.screenrecorder;

import java.io.File;
import java.io.IOException;

public class StopRecording {

	private String recordingPath;

	public StopRecording(String folderName) throws IOException {

		ConfigurationReader reader = new ConfigurationReader();
		this.recordingPath = reader.prop.getProperty("PrimaryRecordingPath") + folderName;
	}

	public static void main(String[] args) throws IOException {

		
		StopRecording stopRecord = new StopRecording(args[0]);
		
		File file = new File(stopRecord.recordingPath + File.separator + "stop.scr");
		
		if(file.createNewFile()) {
			
			System.exit(0);
		}
		
		
		
		
	}
}
