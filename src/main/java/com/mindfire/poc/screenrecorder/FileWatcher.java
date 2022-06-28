package com.mindfire.poc.screenrecorder;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileWatcher extends Thread {

	boolean exit = false;
	
	String recordingPath;
	
	public FileWatcher(String folderName) throws IOException{
		
		ConfigurationReader reader = new ConfigurationReader();
		this.recordingPath = reader.prop.getProperty("PrimaryRecordingPath") + folderName;
	}
	
	@Override
	public void run() {

		while (!exit) {
			try {
				for (Path path : listFiles(Paths.get(this.recordingPath))) {
					
					//System.out.println(path.getFileName());
					if(path.getFileName().toString().contains("stop")) {
						
						exit = true;
					}
				}
			} catch (IOException e) {
				System.out.println("Exception: " + e);
			}

			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				System.out.println("Exception: " + e);
			}
		}
	}
	
	List<Path> listFiles(Path path) throws IOException {
        final List<Path> files = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path entry : stream) {
                if (Files.isDirectory(entry)) {
                    files.addAll(listFiles(entry));
                }
                files.add(entry);
            }
        }
        return files;
    }

}
