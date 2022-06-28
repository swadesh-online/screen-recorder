package com.mindfire.poc.screenrecorder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SoxUtility extends Thread{
	
	ProcessBuilder processBuilder;
	ProcessBuilder processBuilder2;
	Process process;
	String recordingPath;
	String working_dir = System.getProperty("user.dir");
	
	SoxUtility(String recording_path){
		
		processBuilder = new ProcessBuilder();
		processBuilder2 = new ProcessBuilder();
		recordingPath = recording_path;
	}
	
	public void run(){
		
		String soxRecordingCommand = "sox -t waveaudio \"Stereo Mix\" ${path}output.wav".replace("${path}", recordingPath);
		
		try {
			FileWriter fw = new FileWriter(working_dir + File.separator + "runSoxRecording.bat");
			fw.write(soxRecordingCommand);
			fw.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		processBuilder.command(working_dir + File.separator + "runSoxRecording.bat");
		
		try {
			process = processBuilder.inheritIO().start();
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 
	}
	
	public void destroy() throws IOException {
		
		processBuilder2.command(working_dir + File.separator + "stopSoxRecording.bat");
		
		process = processBuilder2.start();
	}

}
