package vamix.work;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

public class DownloadAudio extends SwingWorker<Integer, String> {
	
	JFrame frame;
	JInternalFrame downloadFrame;
	JTextField url;
	JProgressBar progressBar;
	JButton dlBtn;
	JButton cancelDlBtn;
	private int status;
	private String fileName;
	private String urlString;

	public DownloadAudio(JFrame frame) {
		frame = frame;
		
		downloadFrame = new JInternalFrame("Download Audio", true, true);
		downloadFrame.setSize(200, 200);
		downloadFrame.setVisible(true);
		frame.add(downloadFrame);
		
		dlBtn = new JButton("Download");
		downloadFrame.add(dlBtn);
	}

	public void SetUpDownload() {
		urlString = url.getText();
		fileName = urlString.substring( urlString.lastIndexOf('/')+1, urlString.length() );
	}
	
	@Override
	protected Integer doInBackground() {
		try {

			ProcessBuilder builder;
			builder = new ProcessBuilder("wget", "--progress=dot", url.getText());

			Process process = builder.start();

			InputStream out = process.getInputStream();
			InputStream err = process.getErrorStream();
			BufferedReader stdout = new BufferedReader(new InputStreamReader(out));
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
				progressBar.setValue(progress);
			}
		}
	}
	
	@Override
	protected void done() {
		try {
			int exitStatus = get();
			progressBar.setValue(progressBar.getMinimum());
			if (exitStatus == 0) {
				JOptionPane.showMessageDialog(downloadFrame, "Download of " + fileName + " completed successfuly");
//				createLogFile();
//				Date date = new Date();
//				String toLog = logLineCount() + " DOWNLOAD " + dt.format(date) + "\n";
//				try (BufferedWriter writer = Files.newBufferedWriter(logPath, charset, StandardOpenOption.APPEND)) {
//					writer.write(toLog, 0, toLog.length());
//				} catch (IOException x) {
//
//				}
			} else {
				JOptionPane.showMessageDialog(downloadFrame, "oops"/*wgetExitMessage(exitStatus)*/);
		}
			fileName = "";
		} catch (Exception ex) {

		}
	}
	
	
}
