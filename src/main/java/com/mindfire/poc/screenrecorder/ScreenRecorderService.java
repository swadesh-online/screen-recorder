package com.mindfire.poc.screenrecorder;

import java.io.File;
import java.io.IOException;

public class ScreenRecorderService implements Runnable {

	private static volatile Thread screenRecorderThread;

	private final File stopFile;

	public ScreenRecorderService(File file) {

		this.stopFile = file;
	}

	public static void main(String args[]) throws IOException {

		String mode = getArg(args, 0);
		if ("start".equals(mode)) {

			File f = tmpFile(getArg(args, 1));
			f.createNewFile();
			startThread(f);

		} else if ("stop".equals(mode)) {

			final File tmpFile = tmpFile(getArg(args, 1));

			tmpFile.delete();
		}

	}

	private static File tmpFile(String fileName) {

		return new File(System.getProperty("java.io.tmpdir"),
				fileName != null ? fileName : "ScreenRecorderService.tmp");
	}

	private static String getArg(String[] args, int argnum) {

		if (args.length > argnum) {

			return args[argnum];

		} else {

			return null;
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		try {
			ScreenRecorderUtil.startRecord("demo");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ScreenRecorderUtil.sox.start();
	}

	public static void start(String args[]) throws InterruptedException {

		startThread(null);

		while (screenRecorderThread.isAlive()) {

			screenRecorderThread.join();
		}
	}

	private static void startThread(File file) {
		System.out.println("Starting the thread");
		screenRecorderThread = new Thread(new ScreenRecorderService(file));
		screenRecorderThread.start();

	}

	public static void stop(String args[]) throws Exception {
		if (screenRecorderThread != null) {
			System.out.println("Interrupting the thread");
			ScreenRecorderUtil.sox.destroy();
			ScreenRecorderUtil.stopRecord();
			ScreenRecorderUtil.ffmpeg.mergeAudioAndVideo("demo");

			screenRecorderThread.interrupt();
		} else {
			System.out.println("No thread to interrupt");

		}
	}
}