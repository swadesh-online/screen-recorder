package com.mindfire.poc.screenrecorder;

import java.io.File;
import java.io.IOException;

public class ffmpegUtility {

	ProcessBuilder processBuilder;
	Process process;
	String recordingPath;

	public ffmpegUtility(String recordingPath) {
		// TODO Auto-generated constructor stub
		processBuilder = new ProcessBuilder();
		this.recordingPath = recordingPath;
	}

	public void mergeAudioAndVideo(String fileName) throws IOException {

		// ffmpeg -i demo.mp4 -i output.wav -filter_complex
		// "[0:a][1:a]amix=2:shortest[aout]" -map 0:v -map "[aout]" final_output.mp4

		processBuilder.command("ffmpeg", "-i", this.recordingPath + fileName + ".mov", "-i",
				this.recordingPath + "output.wav", "-filter_complex", "\"[0:a][1:a]amix=2:shortest[aout]\"", "-map",
				"0:v", "-map", "\"[aout]\"", this.recordingPath + "final_output2.mp4");
		process = processBuilder.inheritIO().start();

		try {
			int exitCode = process.waitFor();
			if (exitCode == 0) {
				if (deleteResidualFiles()) {

					System.out.println("Residual Files Deleted!");
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	boolean deleteResidualFiles() {

		File videoFile = new File(recordingPath + "demo.mov");
		File audioFile = new File(recordingPath + "output.wav");
		if (videoFile.delete() && audioFile.delete()) {

			return true;
		}
		return false;
	}
}
