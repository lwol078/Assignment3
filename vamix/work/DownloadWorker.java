package vamix.work;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.swing.SwingWorker;

import vamix.GUI.DownloadGUI;

public class DownloadWorker extends SwingWorker<Integer, String>{
	
	private DownloadGUI gui;
	private String urlString;
	private int status;
	
	public DownloadWorker(DownloadGUI gui, String urlString) {
		this.gui = gui;
		this.urlString = urlString;
	}
	@Override
	protected Integer doInBackground() {
		try {

			ProcessBuilder builder;
			builder = new ProcessBuilder("wget", "--progress=dot", urlString);

			Process process = builder.start();

			InputStream err = process.getErrorStream();
			BufferedReader stderr = new BufferedReader(new InputStreamReader(err));


			String line = null;
			while ((line = stderr.readLine()) != null) {
				if (isCancelled()) {
					process.destroy();
					publish("0%");
					break;
				}
				publish(line);
			}

			process.waitFor();
			status = process.exitValue();
			process.destroy();

		} catch (Exception ex) {

		}

		return status;
	}

	protected void process(List<String> chunks) {
		String[] chunksSplit = chunks.get(0).split(" ");
		for (String i : chunksSplit) {
			if (i.contains("%")) {
				i = i.replaceAll("\\%", "");
				int progress = Integer.parseInt(i);
				gui.setProgressBar(progress);
			}
		}
	}

	@Override
	protected void done() {
		try {
			int exitStatus = get();
			gui.downloadDone(exitStatus);
		} catch (Exception ex) {

		}
	}
}
