package vamix.GUI;

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

import vamix.work.DownloadWorker;

public class DownloadAudioGUI extends JFrame implements ActionListener, DownloadGUI {
	/**
	 * This GUI creates and shows the internal frame gui for download audio functionality. 
	 * It extends the DownloadGUI interface to allow it to use download worker. It handles sending error
	 * messages to the user and progress updates of downloads in progress.
	 */

	JFrame frame;
	JTextField url;
	JProgressBar progressBar;
	JButton dlBtn;
	JButton cancelDlBtn;
	private DownloadWorker worker;
	private int status;
	private String fileName;
	private String urlString;

	public DownloadAudioGUI(JFrame frame) {
		super("Download Audio");
		this.frame = frame;

		setSize(600, 400);
		setVisible(true);
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		setLayout(layout);
		
		
		setLocation(225, 100);
		
		
		JLabel dlLabel = new JLabel("Enter URL of audio to download here:");
		c.weightx = 0.3;
		c.ipadx = 0;
		c.gridx = 0;
		c.gridy = 0;
		add(dlLabel, c);
		
		url = new JTextField();
		c.weightx = 0.5;
		c.ipadx = 200;
		c.gridx = 1;
		c.gridy = 0;
		add(url, c);

		dlBtn = new JButton("Download");
		dlBtn.addActionListener(this);
		c.weightx = 0.2;
		c.ipadx = 0;
		c.gridx = 2;
		c.gridy = 0;
		add(dlBtn);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == dlBtn) {
			SetUpDownload();
		}
		
	}

	public void SetUpDownload() {
		
		worker = new DownloadWorker(this, url.getText());
		urlString = url.getText();
		fileName = urlString.substring( urlString.lastIndexOf('/')+1, urlString.length() );
		
		File file = new File(fileName);
		if (file.isFile()) {
			Object[] options = {"Overwrite", "Cancel"};
			int n = JOptionPane.showOptionDialog(this, "The file " + fileName + " already exists. Do you want to overwrite the file?", "File Already Exists",
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
		add(progressBar, c);
		
		cancelDlBtn = new JButton("Cancel");
		cancelDlBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				worker.cancel(true);
				JOptionPane.showMessageDialog(null, "Download Cancelled");
				remove(progressBar);
				remove(cancelDlBtn);
				revalidate();
				repaint();
				dlBtn.setEnabled(true);
			}
		});
		c.ipadx = 0;
		c.gridx = 2;
		c.gridy = 1;
		add(cancelDlBtn,c);
		validate();
		repaint();
		
		Object[] options = {"Yes", "No"};
		int n = JOptionPane.showOptionDialog(this, "Please confirm that " + fileName + " is open source", "Open Source Check",
				JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, "No");
		if (n != 0) {
			JOptionPane.showMessageDialog(this, "Can only download open source files, please enter the url of an open source file");
			remove(progressBar);
			remove(cancelDlBtn);
			validate();
			repaint();
			dlBtn.setEnabled(true);
		} else if (n == 0) {
			worker.execute();
		}
	}
	
	public void downloadDone(int exitStatus) {
		progressBar.setValue(progressBar.getMinimum());
		if (exitStatus == 0) {
			JOptionPane.showMessageDialog(this, "Download of " + fileName + " completed successfuly");
		} else {
			JOptionPane.showMessageDialog(this, "An error occurred during download, please ensure to enter a valid url");
		}
		fileName = "";
		remove(progressBar);
		remove(cancelDlBtn);
		validate();
		repaint();
		dlBtn.setEnabled(true);
	}

	@Override
	public void setProgressBar(int progress) {
		progressBar.setValue(progress);
		
	}
}
