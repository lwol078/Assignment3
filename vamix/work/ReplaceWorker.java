package vamix.work;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.swing.SwingWorker;

import vamix.GUI.ReplaceAudioGUI;

public class ReplaceWorker extends SwingWorker<Integer, String> {
	/**
	 * ReplaceWorker runs a terminal process using avconv that replaces the audio track of a video with an
	 * audio file. The constructor is passed the neccessary information to create the process and the class
	 * returns the exit status of the process along with intermittent progress updates to the progress bar
	 * in the gui.
	 */
	private ReplaceAudioGUI gui;

	private int status;
	private ProcessBuilder builder;
	private int totalFrames = 0;
	private File videoFile, audioFile, outDirectory;
	private String outFilename;
	
	public ReplaceWorker(ReplaceAudioGUI gui, File videoFile, File audioFile, File outDirectory, String outFilename) {
		this.gui = gui;
		this.videoFile = videoFile;
		this.audioFile = audioFile;
		this.outDirectory = outDirectory;
		this.outFilename = outFilename;
	}

	@Override
	protected Integer doInBackground() throws Exception {

		builder = new ProcessBuilder("avconv", "-i", videoFile.toString(), "-i", audioFile.toString(),
				"-c:v", "copy", "-c:a", "copy", "-map", "0:v", "-map", "1:a", "-y" , outFilename); 
		builder.directory(outDirectory);

		totalFrames = videoFrameCount(videoFile);
		Process process = builder.start();

		InputStream err = process.getErrorStream();
		BufferedReader stderr = new BufferedReader(new InputStreamReader(err));

		String line = null;
		while ((line = stderr.readLine()) != null) {
			if (isCancelled()) {
				process.destroy();
				break;
			}
			if (line.contains("frame=")) {
				publish(line);
			}
		}

		process.waitFor();
		status = process.exitValue();
		return status;
	}

	protected void process(List<String> chunks) {
		String frames = chunks.get(0).replace("frame=", "").trim();
		frames = frames.split(" ")[0];
		int frameCount = Integer.parseInt(frames);
		int progress = frameCount*100/totalFrames;
		gui.setProgressBar(progress);
	}

	protected void done() {
		try {
			int exitStatus = get();
			gui.replaceDone(exitStatus);
		} catch (Exception ex) {

		}
	}

	public int videoFrameCount(File videoFile) {

		int frameCount = 0;
		try {
			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "avconv -i " + videoFile.toString() + " -c:v copy -c:a copy -f null /dev/null 2>&1 | grep 'frame='");

			Process process = builder.start();

			InputStream out = process.getInputStream();
			BufferedReader stdout = new BufferedReader(new InputStreamReader(out));

			String line = null;
			while ((line = stdout.readLine()) != null) {
				if (line.contains("frame=")) {
					String frames = line.replace("frame=", "").trim();
					frames = frames.split(" ")[0];
					frameCount = Integer.parseInt(frames);
				}
			}
		} catch (IOException e) {

		}

		return frameCount;
	}

}
