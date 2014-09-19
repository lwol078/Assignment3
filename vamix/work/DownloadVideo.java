package vamix.work;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

public class DownloadVideo implements ActionListener {

	JFrame frame;
	JInternalFrame downloadFrame;
	JTextField url;
	JProgressBar progressBar;
	JButton dlBtn;
	JButton cancelDlBtn;
	private DownloadVideoWorker worker;
	private int status;
	private String fileName;
	private String urlString;

	public DownloadVideo(JFrame frame) {
		frame = frame;

		downloadFrame = new JInternalFrame("Download Video", true, true);
		downloadFrame.setSize(600, 400);
		downloadFrame.setVisible(true);
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		downloadFrame.setLayout(layout);
		
		frame.add(downloadFrame);
		downloadFrame.setLocation(225, 100);
		try {
			downloadFrame.setSelected(true);
		} catch (java.beans.PropertyVetoException e) {}
		
		JLabel dlLabel = new JLabel("Enter URL of video to download here:");
		c.weightx = 0.3;
		c.ipadx = 0;
		c.gridx = 0;
		c.gridy = 0;
		downloadFrame.add(dlLabel, c);
		
		url = new JTextField();
		c.weightx = 0.5;
		c.ipadx = 200;
		c.gridx = 1;
		c.gridy = 0;
		downloadFrame.add(url, c);

		dlBtn = new JButton("Download");
		dlBtn.addActionListener(this);
		c.weightx = 0.2;
		c.ipadx = 0;
		c.gridx = 2;
		c.gridy = 0;
		downloadFrame.add(dlBtn);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == dlBtn) {
			SetUpDownload();
		}
		
	}

	public void SetUpDownload() {
		
		worker = new DownloadVideoWorker();
		urlString = url.getText();
		fileName = urlString.substring( urlString.lastIndexOf('/')+1, urlString.length() );
		
		File file = new File(fileName);
		if (file.isFile()) {
			Object[] options = {"Overwrite", "Cancel"};
			int n = JOptionPane.showOptionDialog(downloadFrame, "The file " + fileName + " already exists. Do you want to overwrite the file?", "File Already Exists",
					JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, "Cancel");
			if (n == 0) {
				file.delete();
			} else if (n == 1) {
				return;
			}
		}
		
		dlBtn.setEnabled(false);
		GridBagConstraints c = new GridBagConstraints();
		
		progressBar = new JProgressBar(0, 100);
		progressBar.setStringPainted(true);
		c.ipadx = 150;
		c.gridx = 1;
		c.gridy = 1;
		downloadFrame.add(progressBar, c);
		
		cancelDlBtn = new JButton("Cancel");
		cancelDlBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				worker.cancel(true);
				JOptionPane.showMessageDialog(downloadFrame, "Download Cancelled");
				downloadFrame.remove(progressBar);
				downloadFrame.remove(cancelDlBtn);
				downloadFrame.revalidate();
				downloadFrame.repaint();
				dlBtn.setEnabled(true);
			}
		});
		c.ipadx = 0;
		c.gridx = 2;
		c.gridy = 1;
		downloadFrame.add(cancelDlBtn,c);
		downloadFrame.validate();
		downloadFrame.repaint();
		
		Object[] options = {"Yes", "No"};
		int n = JOptionPane.showOptionDialog(downloadFrame, "Please confirm that " + fileName + " is open source", "Open Source Check",
				JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, "No");
		if (n != 0) {
			JOptionPane.showMessageDialog(downloadFrame, "Can only download open source files, please enter the url of an open source file");
			downloadFrame.remove(progressBar);
			downloadFrame.remove(cancelDlBtn);
			downloadFrame.validate();
			downloadFrame.repaint();
			dlBtn.setEnabled(true);
		} else if (n == 0) {
			worker.execute();
		}
	}
	
	class DownloadVideoWorker extends SwingWorker<Integer, String> {
		
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
					JOptionPane.showMessageDialog(downloadFrame, "An error occurred during download, please ensure to enter a valid url"/*wgetExitMessage(exitStatus)*/);
				}
				fileName = "";
				downloadFrame.remove(progressBar);
				downloadFrame.remove(cancelDlBtn);
				downloadFrame.validate();
				downloadFrame.repaint();
				dlBtn.setEnabled(true);
			} catch (Exception ex) {

			}
		}
	}

	

}