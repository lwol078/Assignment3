package vamix.work;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

public class ExtractAudio implements ActionListener {

	JFrame frame;
	JInternalFrame extract;
	JButton openBtn;
	JButton advancedBtn;
	JButton advExtractBtn;
	JButton extractBtn;
	JLabel inFileLabel;
	JFileChooser chooser = new JFileChooser();
	private String startTime;
	private String endTime;
	private File extractFile;
	private String inFileString = "No File Selected";
	private String outFilename;
	private File outDirectory;
	private boolean advanced = false;
	
	JLabel startLabel;
	JTextField startField;
	JLabel endLabel;
	JTextField endField;
	JLabel formatLabel;
	JButton showLess;
	
	JRadioButton mp3Btn;
	JRadioButton wavBtn;
	JRadioButton aacBtn;
	JRadioButton oggBtn;
	JRadioButton flacBtn;
	
	ButtonGroup formatBtns;
	JPanel formatPanel;
	
	private final int MP3 = 0;
	private final int WAV = 1;
	private final int AAC = 2;
	private final int OGG = 3;
	private final int FLAC = 4;
	private int format = MP3;
	
	public ExtractAudio(JFrame frame) {
		this.frame = frame;
		
		extract = new JInternalFrame("Extract Audio", true, true);
		extract.setSize(600,400);
		extract.setVisible(true);
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		extract.setLayout(layout);
		
		frame.add(extract);
		extract.setLocation(225, 100);
		try {
			extract.setSelected(true);
		} catch (java.beans.PropertyVetoException e) {}
		
		inFileLabel = new JLabel("File to extract audio from: " + inFileString);
		c.weightx = 0.7;
		c.weighty = 0.3;
		c.ipadx = 0;
		c.gridx = 0;
		c.gridy = 0;
		extract.add(inFileLabel, c);
		
		openBtn = new JButton("Choose File");
		openBtn.addActionListener(this);
		c.weightx = 0.3;
		c.weighty = 0.3;
		c.ipadx = 50;
		c.gridx = 1;
		c.gridy = 0;
		extract.add(openBtn, c);
		
		advancedBtn = new JButton("Advanced Options");
		advancedBtn.addActionListener(this);
		c.weightx = 0.5;
		c.weighty = 0.3;
		c.ipadx = 0;
		c.gridx = 0;
		c.gridy = 1;
		extract.add(advancedBtn, c);
		
		extractBtn = new JButton("Extract Audio");
		extractBtn.addActionListener(this);
		c.weightx = 0.5;
		c.weighty = 0.3;
		c.ipadx = 50;
		c.gridx = 1;
		c.gridy = 1;
		extract.add(extractBtn, c);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == openBtn) {
			int returnVal = chooser.showDialog(extract, "Select");
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				extractFile = chooser.getSelectedFile();
				try {
					ProcessBuilder builder = new ProcessBuilder("/bin/bash","-c","avconv -i " + "\"" + extractFile.toString() + "\"" + " 2>&1 | grep -q -w Audio:" );
					Process process = builder.start();
					process.waitFor();
					int exitStatus = process.exitValue();
					if (exitStatus != 0) {
						JOptionPane.showMessageDialog(extract, "Please select a video file with an audio stream to extract from.");
						extractFile = null;
						inFileString = "No File Selected";
						inFileLabel.setText("File to extract audio from: " + inFileString);
						inFileLabel.repaint();
						return;
					}

				} catch (IOException ex) {

				} catch (InterruptedException e1) {

				}
			}
			inFileString = extractFile.getName();
			inFileLabel.setText("File to extract audio from: " + inFileString);
			inFileLabel.repaint();
		} 
		else if (e.getSource() == extractBtn) {
			advanced = false;
			if (extractFile == null) {
				JOptionPane.showMessageDialog(extract, "Please select a file to extract audio from.");
				return;
			}
			int returnVal = chooser.showSaveDialog(extract);
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				outFilename = chooser.getSelectedFile().getName();
				outDirectory = chooser.getCurrentDirectory();
			}
			format = MP3;
			if (!outFilename.endsWith(".mp3")) {
				JOptionPane.showMessageDialog(extract, "Please ensure filename ends with default .mp3 suffix");
				return;
			}
			ExtractWorker extractWorker = new ExtractWorker();
			extractWorker.execute();
		}
		else if (e.getSource() == advancedBtn) {
			advanced = true;
			GridBagConstraints c = new GridBagConstraints();
			extract.remove(advancedBtn);
			extract.remove(extractBtn);
			
			startLabel = new JLabel("Start time in format: hh:mm:ss");
			c.weightx = 0.6;
			c.weighty = 0.2;
			c.ipadx = 0;
			c.gridx = 0;
			c.gridy = 1;
			extract.add(startLabel, c);
			
			startField = new JTextField();
			c.weightx = 0.4;
			c.weighty = 0.2;
			c.ipadx = 100;
			c.gridx = 1;
			c.gridy = 1;
			extract.add(startField, c);
			
			endLabel = new JLabel("Output duration in format: hh:mm:ss");
			c.weightx = 0.6;
			c.weighty = 0.2;
			c.ipadx = 0;
			c.gridx = 0;
			c.gridy = 2;
			extract.add(endLabel, c);
			
			endField = new JTextField();
			c.weightx = 0.4;
			c.weighty = 0.2;
			c.ipadx = 100;
			c.gridx = 1;
			c.gridy = 2;
			extract.add(endField, c);
			
			formatLabel = new JLabel("Select format for output file");
			c.weightx = 0.6;
			c.weighty = 0.2;
			c.ipadx = 0;
			c.gridx = 0;
			c.gridy = 3;
			extract.add(formatLabel, c);
			
			mp3Btn = new JRadioButton("mp3");
			mp3Btn.setSelected(true);
			wavBtn = new JRadioButton("wav");
			aacBtn = new JRadioButton("aac");
			oggBtn = new JRadioButton("ogg");
			flacBtn = new JRadioButton("flac");
			
			formatBtns = new ButtonGroup();
			formatBtns.add(mp3Btn);
			formatBtns.add(wavBtn);
			formatBtns.add(aacBtn);
			formatBtns.add(oggBtn);
			formatBtns.add(flacBtn);
			
			formatPanel = new JPanel(new GridLayout(0,1));
			formatPanel.add(mp3Btn);
			formatPanel.add(wavBtn);
			formatPanel.add(aacBtn);
			formatPanel.add(oggBtn);
			formatPanel.add(flacBtn);
			c.weightx = 0.4;
			c.weighty = 0.2;
			c.ipadx = 50;
			c.gridx = 1;
			c.gridy = 3;
			extract.add(formatPanel, c);
			
			showLess = new JButton("Show less");
			showLess.addActionListener(this);
			c.weightx = 0.5;
			c.weighty = 0.2;
			c.ipadx = 0;
			c.gridx = 0;
			c.gridy = 4;
			extract.add(showLess, c);
			
			advExtractBtn = new JButton("Extract Audio");
			advExtractBtn.addActionListener(this);
			c.weightx = 0.5;
			c.weighty = 0.2;
			c.ipadx = 50;
			c.gridx = 1;
			c.gridy = 4;
			extract.add(advExtractBtn, c);
			
			extract.revalidate();
			extract.repaint();
		} 
		else if (e.getSource() == advExtractBtn) {
			advanced = true;
			if (extractFile == null) {
				JOptionPane.showMessageDialog(extract, "Please select a file to extract audio from.");
				return;
			}
			startTime = startField.getText();
			if(!startTime.matches("^(\\d\\d:\\d\\d:\\d\\d)")) {
				JOptionPane.showMessageDialog(extract, "Please use format hh:mm:ss for start time.");
				return;
			}
			endTime = endField.getText();
			if(!endTime.matches("^(\\d\\d:\\d\\d:\\d\\d)")) {
				JOptionPane.showMessageDialog(extract, "Please use format hh:mm:ss for duration time.");
				return;
			}
			int returnVal = chooser.showSaveDialog(extract);
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				outFilename = chooser.getSelectedFile().getName();
				outDirectory = chooser.getCurrentDirectory();
			}
			if (mp3Btn.isSelected()) {
				format = MP3;
				if (!outFilename.endsWith(".mp3")) {
					JOptionPane.showMessageDialog(extract, "Please ensure filename ends with selected .mp3 suffix.");
					return;
				}
			} else if (wavBtn.isSelected()) {
				format = WAV;
				if (!outFilename.endsWith(".wav")) {
					JOptionPane.showMessageDialog(extract, "Please ensure filename ends with selected .wav suffix.");
					return;
				}
			} else if (aacBtn.isSelected()) {
				format = AAC;
				if (!outFilename.endsWith(".aac")) {
					JOptionPane.showMessageDialog(extract, "Please ensure filename ends with selected .aac suffix.");
					return;
				}
			} else if (oggBtn.isSelected()) {
				format = OGG;
				if (!outFilename.endsWith(".ogg")) {
					JOptionPane.showMessageDialog(extract, "Please ensure filename ends with selected .ogg suffix.");
					return;
				}
			} else if (flacBtn.isSelected()) {
				format = FLAC;
				if (!outFilename.endsWith(".flac")) {
					JOptionPane.showMessageDialog(extract, "Please ensure filename ends with selected .flac suffix.");
					return;
				}
			}
			ExtractWorker extractWorker = new ExtractWorker();
			extractWorker.execute();
		}
		else if (e.getSource() == showLess) {
			advanced = false;
			GridBagConstraints c = new GridBagConstraints();
			
			extract.remove(startLabel);
			extract.remove(startField);
			extract.remove(endLabel);
			extract.remove(endField);
			extract.remove(formatLabel);
			extract.remove(formatPanel);
			extract.remove(showLess);
			extract.remove(advExtractBtn);
			
			advancedBtn = new JButton("Advanced Options");
			advancedBtn.addActionListener(this);
			c.weightx = 0.5;
			c.weighty = 0.3;
			c.ipadx = 0;
			c.gridx = 0;
			c.gridy = 1;
			extract.add(advancedBtn, c);
			
			extractBtn = new JButton("Extract Audio");
			extractBtn.addActionListener(this);
			c.weightx = 0.5;
			c.weighty = 0.3;
			c.ipadx = 50;
			c.gridx = 1;
			c.gridy = 1;
			extract.add(extractBtn, c);
			
			extract.revalidate();
			extract.repaint();
		}
	}
	
	class ExtractWorker extends SwingWorker<Integer, String> {

		private int status;
		ProcessBuilder builder;

		@Override
		protected Integer doInBackground() throws Exception {
			
			if (advanced) {
				switch (format) {
				case 1: builder = new ProcessBuilder("avconv", "-v", "quiet","-ss",startTime, "-i",
							extractFile.toString(), "-f", "wav", "-vn", "-y", "-acodec", "copy", "-t", endTime, outFilename);
					break;
				case 2: builder = new ProcessBuilder("avconv", "-v", "quiet","-ss",startTime, "-i",
							extractFile.toString(), "-f", "aac", "-vn", "-y", "-acodec", "copy", "-t", endTime, outFilename);
					break;
				case 3: builder = new ProcessBuilder("avconv", "-v", "quiet","-ss",startTime, "-i",
							extractFile.toString(), "-f", "ogg", "-vn", "-y", "-acodec", "copy", "-t", endTime, outFilename);
					break;
				case 4: builder = new ProcessBuilder("avconv", "-v", "quiet","-ss",startTime, "-i",
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
				if (exitStatus == 0) {
					JOptionPane.showMessageDialog(extract, "Extract from " + inFileString + " to " + outFilename + " completed successfuly");
				} else {
					if (!advanced) {
						JOptionPane.showMessageDialog(extract, "An error occurred with default .mp3 format, try using a different format in the advanced settings.");
					} else {
						if (format == MP3) 
							JOptionPane.showMessageDialog(extract, "An error occurred with selected .mp3 format, please select a valid format.");
						else if (format == WAV)
							JOptionPane.showMessageDialog(extract, "An error occurred with selected .wav format, please select a valid format.");
						else if (format == AAC)
							JOptionPane.showMessageDialog(extract, "An error occurred with selected .aac format, please select a valid format.");
						else if (format == OGG)
							JOptionPane.showMessageDialog(extract, "An error occurred with selected .ogg format, please select a valid format.");
						else if (format == FLAC)
							JOptionPane.showMessageDialog(extract, "An error occurred with selected .flac format, please select a valid format.");
					}
				}
				startTime = "";
				endTime = "";
				outFilename = "";
			} catch (Exception ex) {

			}
		}

	}
}
