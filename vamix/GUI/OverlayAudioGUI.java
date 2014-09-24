package vamix.GUI;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

import vamix.work.OverlayWorker;

public class OverlayAudioGUI implements ActionListener {

	JFrame frame;
	JInternalFrame overlay;
	JLabel inVideoLabel, inAudioLabel;
	JButton getVideoBtn, getAudioBtn, overlayBtn, cancelBtn;
	JProgressBar progressBar;
	JFileChooser chooser = new JFileChooser();
	String inVideoString = "No Video Selected";
	String inAudioString = "No Audio Selected";
	Insets cInsets = new Insets(20, 20, 20, 20);

	private File videoFile, audioFile, outFile;
	private String outFilename;
	private File outDirectory;
	private OverlayWorker worker;

	public OverlayAudioGUI (JFrame frame) {
		this.frame = frame;

		overlay = new JInternalFrame("Overlay Audio", true, true);
		overlay.setSize(600,400);
		overlay.setVisible(true);
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		c.insets = cInsets;
		overlay.setLayout(layout);

		frame.add(overlay);
		overlay.setLocation(225, 100);
		try {
			overlay.setSelected(true);
		} catch (java.beans.PropertyVetoException e) {}

		inVideoLabel = new JLabel("Video to overlay audio of: " + inVideoString);
		c.weightx = 0.7;
		c.weighty = 0.3;
		c.ipadx = 0;
		c.gridx = 0;
		c.gridy = 0;
		overlay.add(inVideoLabel, c);

		getVideoBtn = new JButton("Select Video File");
		getVideoBtn.addActionListener(this);
		c.weightx = 0.3;
		c.weighty = 0.3;
		c.ipadx = 40;
		c.gridx = 1;
		c.gridy = 0;
		overlay.add(getVideoBtn, c);

		inAudioLabel = new JLabel("Audio Track to Overlay: " + inAudioString);
		c.weightx = 0.7;
		c.weighty = 0.3;
		c.ipadx = 0;
		c.gridx = 0;
		c.gridy = 1;
		overlay.add(inAudioLabel, c);

		getAudioBtn = new JButton("Select Audio File");
		getAudioBtn.addActionListener(this);
		c.weightx = 0.3;
		c.weighty = 0.3;
		c.ipadx = 40;
		c.gridx = 1;
		c.gridy = 1;
		overlay.add(getAudioBtn, c);

		overlayBtn = new JButton("Overlay Audio");
		overlayBtn.addActionListener(this);
		c.weightx = 0.3;
		c.weighty = 0.3;
		c.ipadx = 40;
		c.gridx = 1;
		c.gridy = 2;
		overlay.add(overlayBtn, c);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == getVideoBtn) {
			int returnVal = chooser.showDialog(overlay, "Select");
			if(returnVal == JFileChooser.CANCEL_OPTION) {
				return;
			} else if(returnVal == JFileChooser.APPROVE_OPTION) {
				videoFile = chooser.getSelectedFile();
				try {
					ProcessBuilder builder = new ProcessBuilder("/bin/bash","-c","avconv -i " + "\"" + videoFile + "\"" + " 2>&1 | grep -q -w Video:" );
					Process process = builder.start();
					process.waitFor();
					int exitStatus = process.exitValue();
					if (exitStatus != 0) {
						JOptionPane.showMessageDialog(overlay, "Please select a valid video file.");
						videoFile = null;
						inVideoString = "No Video Selected";
						inVideoLabel.setText("Video to overlay audio of: " + inVideoString);
						inVideoLabel.repaint();
						return;
					}

				} catch (IOException ex) {

				} catch (InterruptedException e1) {

				}
			}
			inVideoString = videoFile.getName();
			inVideoLabel.setText("Video to overlay audio of: " + inVideoString);
			inVideoLabel.repaint();
		}
		else if (e.getSource() == getAudioBtn) {
			int returnVal = chooser.showDialog(overlay, "Select");
			if (returnVal == JFileChooser.CANCEL_OPTION) {
				return;
			} else if(returnVal == JFileChooser.APPROVE_OPTION) {
				audioFile = chooser.getSelectedFile();
				try {
					ProcessBuilder builder = new ProcessBuilder("/bin/bash","-c","avconv -i " + "\"" + audioFile.toString() + "\"" +" 2>&1 | grep -q -w Audio:" );
					Process process = builder.start();
					process.waitFor();
					int exitStatus = process.exitValue();
					if (exitStatus != 0) {
						JOptionPane.showMessageDialog(overlay, "Please select a valid audio file.");
						audioFile = null;
						inAudioString = "No Audio Selected";
						inAudioLabel.setText("Audio Track to Overlay: " + inAudioString);
						inAudioLabel.repaint();
						return;
					}

				} catch (IOException ex) {

				} catch (InterruptedException e1) {

				}
			}
			inAudioString = audioFile.getName();
			inAudioLabel.setText("Audio Track to Overlay: " + inAudioString);
			inAudioLabel.repaint();
		}
		else if (e.getSource() == overlayBtn) {
			if (videoFile == null) {
				JOptionPane.showMessageDialog(overlay, "Please select a video file.");
				return;
			}
			if (audioFile == null) {
				JOptionPane.showMessageDialog(overlay, "Please select an audio file.");
				return;
			}
			int returnVal = chooser.showSaveDialog(overlay);
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				outFile = chooser.getSelectedFile();
				outFilename = chooser.getSelectedFile().getName();
				outDirectory = chooser.getCurrentDirectory();
			}
			if (!outFilename.endsWith(".mp4")) {
				JOptionPane.showMessageDialog(overlay, "Please ensure filename ends with .mp4 suffix");
				return;
			}
			if (outFile.exists()) {
				Object[] options = {"Overwrite", "Cancel"};
				int n = JOptionPane.showOptionDialog(overlay, "The file " + outFilename + " already exists. Do you want to overwrite the file?", "File Already Exists",
						JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, "Cancel");
				if (n == 1)
					return;
			}

			overlayBtn.setEnabled(false);
			GridBagConstraints c = new GridBagConstraints();
			c.insets = cInsets;

			progressBar = new JProgressBar(0, 100);
			progressBar.setStringPainted(true);
			c.weightx = 0.5;
			c.weighty = 0.3;
			c.ipadx = 150;
			c.gridx = 0;
			c.gridy = 2;
			overlay.add(progressBar, c);

			overlay.remove(overlayBtn);
			cancelBtn = new JButton("Cancel");
			cancelBtn.addActionListener(this);
			c.weightx = 0.3;
			c.weighty = 0.3;
			c.ipadx = 40;
			c.gridx = 1;
			c.gridy = 2;
			overlay.add(cancelBtn, c);

			overlay.revalidate();
			overlay.repaint();

			worker = new OverlayWorker(this, videoFile, audioFile, outDirectory, outFilename);
			worker.execute();
		}
		else if (e.getSource() == cancelBtn) {
			GridBagConstraints c = new GridBagConstraints();
			c.insets = cInsets;

			worker.cancel(true);
			JOptionPane.showMessageDialog(overlay, "Overlay Audio Cancelled");
			overlay.remove(progressBar);
			overlay.remove(cancelBtn);

			overlayBtn = new JButton("Overlay Audio");
			overlayBtn.addActionListener(this);
			c.weightx = 0.3;
			c.weighty = 0.3;
			c.ipadx = 40;
			c.gridx = 1;
			c.gridy = 2;
			overlay.add(overlayBtn, c);
			overlayBtn.setEnabled(true);

			overlay.revalidate();
			overlay.repaint();
		}
	}
	
	public void setProgressBar(int progress) {
		progressBar.setValue(progress);
	}
	
	public void overlayDone(int exitStatus) {
		if (exitStatus == 0) {
			JOptionPane.showMessageDialog(overlay, "Overlay Audio of " + inVideoString + " with " + inAudioString + " completed successfully.");
		} else {
			JOptionPane.showMessageDialog(overlay, "An error occurred during audio track overlay.");
		}
		outFilename = "";
		outFile = null;
		overlay.remove(progressBar);
		overlay.remove(cancelBtn);
		GridBagConstraints c = new GridBagConstraints();
		c.insets = cInsets;
		overlayBtn = new JButton("Overlay Audio");
		overlayBtn.addActionListener(this);
		c.weightx = 0.3;
		c.weighty = 0.3;
		c.ipadx = 40;
		c.gridx = 1;
		c.gridy = 2;
		overlay.add(overlayBtn, c);
		overlayBtn.setEnabled(true);

		overlay.revalidate();
		overlay.repaint();
	}
}