package com.mindfire.poc.screenrecorder;

import static org.monte.media.AudioFormatKeys.ByteOrderKey;
import static org.monte.media.AudioFormatKeys.ChannelsKey;
import static org.monte.media.AudioFormatKeys.ENCODING_QUICKTIME_TWOS_PCM;
import static org.monte.media.AudioFormatKeys.SampleRateKey;
import static org.monte.media.AudioFormatKeys.SampleSizeInBitsKey;
import static org.monte.media.AudioFormatKeys.SignedKey;
import static org.monte.media.FormatKeys.EncodingKey;
import static org.monte.media.FormatKeys.FrameRateKey;
import static org.monte.media.FormatKeys.KeyFrameIntervalKey;
import static org.monte.media.FormatKeys.MIME_QUICKTIME;
import static org.monte.media.FormatKeys.MediaTypeKey;
import static org.monte.media.FormatKeys.MimeTypeKey;
import static org.monte.media.VideoFormatKeys.COMPRESSOR_NAME_QUICKTIME_ANIMATION;
import static org.monte.media.VideoFormatKeys.CompressorNameKey;
import static org.monte.media.VideoFormatKeys.DepthKey;
import static org.monte.media.VideoFormatKeys.ENCODING_QUICKTIME_ANIMATION;
import static org.monte.media.VideoFormatKeys.QualityKey;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;

import org.monte.media.Format;
import org.monte.media.FormatKeys.MediaType;
import org.monte.media.Registry;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;

public class ScreenRecorderUtil extends ScreenRecorder {

	// declaring variables
	private static ScreenRecorder screenRecorder;
	private static String fileName;
	private static String path;
	private static SoxUtility sox;
	private static ffmpegUtility ffmpeg;

	// initilization for MonteScreenRecording
	public ScreenRecorderUtil(GraphicsConfiguration cfg, Rectangle captureArea, Format fileFormat, Format screenFormat,
			Format mouseFormat, Format audioFormat, File movieFolder, String name) throws IOException, AWTException {
		super(cfg, captureArea, fileFormat, screenFormat, mouseFormat, audioFormat, movieFolder);
		fileName = name;

	}

	// creation of file
	@Override
	protected File createMovieFile(Format fileFormat) throws IOException {

		if (!movieFolder.exists()) {
			movieFolder.mkdirs();
		} else if (!movieFolder.isDirectory()) {
			throw new IOException("\"" + movieFolder + "\" is not a directory.");
		}
		return new File(movieFolder, fileName + "." + Registry.getInstance().getExtension(fileFormat));
	}

	// start recording
	public static void startRecord(String folderName) throws Exception {

		System.out.println("Recording started!");

		/**
		 * reading path from config.ini and setting to the sox and ffmpeg objects.
		 */
		ConfigurationReader reader = new ConfigurationReader();
		String recordingPath = reader.prop.getProperty("PrimaryRecordingPath") + folderName + File.separator;
		File file = new File(recordingPath);
		sox = new SoxUtility(recordingPath);
		ffmpeg = new ffmpegUtility(recordingPath);

		/**
		 * MonteScreenRecording configuration settings
		 */
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = screenSize.width;
		int height = screenSize.height;
		Rectangle captureSize = new Rectangle(0, 0, width, height);
		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getDefaultConfiguration();
		screenRecorder = new ScreenRecorderUtil(gc, captureSize,
				// file format
				new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_QUICKTIME),
				// screen capture
				new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, ENCODING_QUICKTIME_ANIMATION, CompressorNameKey,
						COMPRESSOR_NAME_QUICKTIME_ANIMATION, DepthKey, 24, FrameRateKey, Rational.valueOf(15),
						QualityKey, 1.0f, KeyFrameIntervalKey, 30 * 60),
				// cursor capture
				new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, "black", FrameRateKey, Rational.valueOf(30)),
				// audio capture
				new Format(MediaTypeKey, MediaType.AUDIO, EncodingKey, ENCODING_QUICKTIME_TWOS_PCM, FrameRateKey,
						new Rational(48000, 1), SampleSizeInBitsKey, 16, ChannelsKey, 2, SampleRateKey,
						new Rational(48000, 1), SignedKey, true, ByteOrderKey, ByteOrder.BIG_ENDIAN),
				file, "monteOutput");

		// MonteScreenRecording starts
		screenRecorder.start();

		/**
		 * sox recording starts , used to record the stereo output
		 */
		sox.start();

	}

	public static void stopRecord() throws Exception {

		// stop monte recording
		screenRecorder.stop();

		// stop sox output recording
		sox.destroy();
		System.out.println("Recording ends!");

		// merge the video with audio output and delete the original files
		ffmpeg.mergeAudioAndVideo();
	}

	public static void main(String[] args) throws Exception {

		String folderName = getArg(args, 0);
		String action = getArg(args, 1);

		if (action.equals("start")) {

			startRecord(folderName);
		}

		ConfigurationReader reader = new ConfigurationReader();
		String recordingPath = reader.prop.getProperty("PrimaryRecordingPath") + folderName + File.separator;
		File file = new File(recordingPath);

		File[] filesInsideRecordingPath = file.listFiles();

		Thread fileWatcherThread = new FileWatcher(folderName);

		fileWatcherThread.run();

		while (!fileWatcherThread.isAlive()) {

			stopRecord();
			System.exit(0);
		}

	}

	private static String getArg(String[] args, int argnum) {

		if (args.length > argnum) {

			return args[argnum];

		} else {

			return null;
		}
	}
}
