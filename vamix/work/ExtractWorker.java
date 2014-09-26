package vamix.work;

import java.io.File;

import javax.swing.SwingWorker;

import vamix.GUI.ExtractAudioGUI;

public class ExtractWorker extends SwingWorker<Integer, String> {
	/**
	 * ExtractWorker creates a terminal process that extracts audio from a file. The worker takes as constructor
	 * arguments all of the relevant arguments to perform the required extract. The arguments depend on
	 * the ExtractAudioGUI being in advanced mode or not. not advanced extracts the full audio file in mp3
	 * format while advanced selects from the supported formats and the duration and start time entered in
	 * the gui. Processes the current number of frames to update the progress bar in the gui.
	 */
	private ExtractAudioGUI gui;

	private int status;
	ProcessBuilder builder;
	private boolean advanced;
	private String startTime;
	private String endTime;
	private File extractFile;
	private String outFilename;
	private File outDirectory;
	
	private final int MP3 = 0;
	private final int WAV = 1;
	private final int AAC = 2;
	private final int OGG = 3;
	private final int FLAC = 4;
	private int format = MP3;

	public ExtractWorker(ExtractAudioGUI gui, boolean advanced, String startTime, String endTime,
			File extractFile, String outFilename, File outDirectory, int format) {
		this.gui = gui;
		this.advanced = advanced;
		this.startTime = startTime;
		this.endTime = endTime;
		this.extractFile = extractFile;
		this.outFilename = outFilename;
		this.outDirectory = outDirectory;
		this.format = format;
	}
	
	public ExtractWorker(ExtractAudioGUI gui, File extractFile, String outFilename, File outDirectory) {
		this.gui = gui;
		this.extractFile = extractFile;
		this.outFilename = outFilename;
		this.outDirectory = outDirectory;
	}
	
	@Override
	protected Integer doInBackground() throws Exception {
		
		if (advanced) {
			switch (format) {
			case WAV: builder = new ProcessBuilder("avconv", "-v", "quiet","-ss",startTime, "-i",
						extractFile.toString(), "-f", "wav", "-vn", "-y", "-acodec", "copy", "-t", endTime, outFilename);
				break;
			case AAC: builder = new ProcessBuilder("avconv", "-v", "quiet","-ss",startTime, "-i",
						extractFile.toString(), "-f", "aac", "-vn", "-y", "-acodec", "copy", "-t", endTime, outFilename);
				break;
			case OGG: builder = new ProcessBuilder("avconv", "-v", "quiet","-ss",startTime, "-i",
						extractFile.toString(), "-f", "ogg", "-vn", "-y", "-acodec", "copy", "-t", endTime, outFilename);
				break;
			case FLAC: builder = new ProcessBuilder("avconv", "-v", "quiet","-ss",startTime, "-i",
						extractFile.toString(), "-f", "flac", "-vn", "-y", "-acodec", "copy", "-t", endTime, outFilename);
				break;
			default: builder = new ProcessBuilder("avconv", "-v", "quiet","-ss", startTime, "-i",
						extractFile.toString(), "-f", "mp3", "-vn", "-y", "-acodec", "mp3", "-t", endTime, outFilename);
				break;
			}
				
		} else {
			builder = new ProcessBuilder("avconv", "-v", "quiet", "-i",
					extractFile.toString(), "-f", "mp3", "-vn", "-y", "-acodec", "mp3", outFilename);
		}
		builder.directory(outDirectory);
		Process process = builder.start();
		
		process.waitFor();
		status = process.exitValue();
		return status;
	}

	@Override
	protected void done() {
		try {
			int exitStatus = get();
			gui.extractDone(format, exitStatus, advanced);
		} catch (Exception ex) {

		}
	}

}