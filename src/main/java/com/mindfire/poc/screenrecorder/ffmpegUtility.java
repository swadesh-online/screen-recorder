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

	public void mergeAudioAndVideo() throws IOException {

		processBuilder.command("ffmpeg", "-i", this.recordingPath + File.separator + "monteOutput.mov", "-i",
				this.recordingPath + "soxOutput.wav", "-filter_complex", "\"[0:a][1:a]amix=2:shortest[aout]\"", "-map",
				"0:v", "-map", "\"[aout]\"", this.recordingPath + "recording.mp4");
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

		File videoFile = new File(recordingPath + "monteOutput.mov");
		File audioFile = new File(recordingPath + "soxOutput.wav");
		File stopFile = new File(recordingPath + "stop.scr");
		if (videoFile.delete() && audioFile.delete() && stopFile.delete()) {

			return true;
		}
		return false;
	}
}
