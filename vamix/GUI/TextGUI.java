package vamix.GUI;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import vamix.work.*;

public class TextGUI implements ActionListener
{
	private JLabel labelFont,labelColor,labelX,labelY,labelText,labelTime,labelDuration, labelSize;
	private JComboBox<String> comboFont, comboColor;
	private JSpinner spinX, spinY, spinHr, spinMin, spinSec, spinDur, spinSize;
	private JTextArea text;
	private JButton btnSave, btnChoose;
	private JProgressBar progress;

	private JFrame parent;
	private JPanel mainPanel, formatPanel, positionPanel, timePanel;
	private JInternalFrame textFrame;
	private File sourceFile;

	public TextGUI(JFrame frame, File source)
	{
		sourceFile = source;
		parent = frame;
		textFrame = new JInternalFrame("Edit text",false,true);
		textFrame.setSize(400,500);
		textFrame.setVisible(true);
		parent.add(textFrame);
		textFrame.setLocation(0, 0);
		try {
			textFrame.setSelected(true);
		} catch (java.beans.PropertyVetoException e) {}
		mainPanel = new JPanel();
		formatPanel = new JPanel();
		positionPanel = new JPanel();
		timePanel = new JPanel();

		textFrame.setContentPane(mainPanel);

		GroupLayout layout = new GroupLayout(textFrame.getContentPane());
		textFrame.getContentPane().setLayout(layout);
		GroupLayout formatLayout = new GroupLayout(formatPanel);
		formatPanel.setLayout(formatLayout);
		GroupLayout posLayout = new GroupLayout(positionPanel);
		positionPanel.setLayout(posLayout);
		GroupLayout timeLayout = new GroupLayout(timePanel);
		timePanel.setLayout(timeLayout);

		btnChoose = new JButton("Choose");
		btnChoose.addActionListener(this);

		labelFont = new JLabel("Font:");
		Vector<String> fonts = new Vector<String>();
		final File fontFolder = new File("vamix/fonts");
		for (final File file : fontFolder.listFiles())
		{
			if (!file.isDirectory())
			{
				fonts.add(file.getName());
			}
		}

		comboFont = new JComboBox<String>(fonts);
		comboFont.setSelectedIndex(0);
		comboFont.setMaximumSize(new Dimension(100,20));

		labelColor = new JLabel("Color:");
		String[] colors = {"white","green","black","blue"};
		comboColor = new JComboBox<String>(colors);
		comboColor.setSelectedIndex(0);
		comboColor.setMaximumSize(new Dimension(100,20));

		labelSize = new JLabel("Size:");
		spinSize = new JSpinner(new SpinnerNumberModel(10,1,50,1));
		spinSize.setMaximumSize(new Dimension(30,20));

		labelX = new JLabel("x:");
		spinX = new JSpinner(new SpinnerNumberModel(0,0,100,1));
		spinX.setMaximumSize(new Dimension(30,20));

		labelY = new JLabel("y:");
		spinY = new JSpinner(new SpinnerNumberModel(0,0,100,1));
		spinY.setMaximumSize(new Dimension(30,20));

		labelText = new JLabel("Text:");
		text = new JTextArea();
		text.setMaximumSize(new Dimension(300,100));
		text.setLineWrap(true);

		labelTime = new JLabel("Time:");
		spinHr = new JSpinner(new SpinnerNumberModel(0,0,60,1));
		spinHr.setMaximumSize(new Dimension(50,20));
		spinMin = new JSpinner(new SpinnerNumberModel(0,0,60,1));
		spinMin.setMaximumSize(new Dimension(50,20));
		spinSec = new JSpinner(new SpinnerNumberModel(0,0,60,1));
		spinSec.setMaximumSize(new Dimension(50,20));

		labelDuration = new JLabel("Duration (s):");
		spinDur = new JSpinner(new SpinnerNumberModel(0,0,null,1));
		spinDur.setMaximumSize(new Dimension(50,20));

		btnSave = new JButton("Save");
		btnSave.addActionListener(this);

		progress = new JProgressBar(0,100);
		progress.setStringPainted(true);

		//Main panel layout
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(
        	layout.createParallelGroup()
        		.addComponent(formatPanel)
				.addComponent(positionPanel)
				.addComponent(text)
				.addComponent(timePanel)
				.addGroup(layout.createSequentialGroup()
    				.addComponent(btnSave)
    				.addComponent(progress)
    				)

        	);
        layout.setVerticalGroup(
        	layout.createSequentialGroup()
    			.addComponent(formatPanel)
    			.addComponent(positionPanel)
    			.addComponent(text)
    			.addComponent(timePanel)
    			.addGroup(layout.createParallelGroup()
    				.addComponent(btnSave)
    				.addComponent(progress)
    				)

        	);

        //Format panel layout
        formatLayout.setAutoCreateGaps(true);
        formatLayout.setAutoCreateContainerGaps(true);
        formatLayout.setHorizontalGroup(
        	formatLayout.createSequentialGroup()
        		.addGroup(formatLayout.createParallelGroup()
    				.addComponent(labelFont)
    				.addComponent(labelColor)
    				.addComponent(labelSize)
    				)
    			.addGroup(formatLayout.createParallelGroup()
    				.addComponent(comboFont)
    				.addComponent(comboColor)
    				.addComponent(spinSize)
    				)
    		);
        formatLayout.setVerticalGroup(
        	formatLayout.createSequentialGroup()
        		.addGroup(formatLayout.createParallelGroup()
    				.addComponent(labelFont)
    				.addComponent(comboFont)
    				)
    			.addGroup(formatLayout.createParallelGroup()
    				.addComponent(labelColor)
    				.addComponent(comboColor)
    				)
    			.addGroup(formatLayout.createParallelGroup()
    				.addComponent(labelSize)
    				.addComponent(spinSize)
    				)
    		);

        //Position panel layout
        posLayout.setAutoCreateGaps(true);
        posLayout.setAutoCreateContainerGaps(true);
        posLayout.setHorizontalGroup(
        	posLayout.createSequentialGroup()
        		.addComponent(labelX)
        		.addComponent(spinX)
        		.addComponent(labelY)
        		.addComponent(spinY)
        	);
        posLayout.setVerticalGroup(
        	posLayout.createParallelGroup()
        		.addComponent(labelX)
        		.addComponent(spinX)
        		.addComponent(labelY)
        		.addComponent(spinY)
        	);

        JLabel colon1 = new JLabel(":");
        JLabel colon2 = new JLabel(":");
        timeLayout.setAutoCreateGaps(true);
        timeLayout.setAutoCreateContainerGaps(true);
        timeLayout.setHorizontalGroup(
        	timeLayout.createSequentialGroup()
        		.addGroup(timeLayout.createParallelGroup()
        			.addComponent(labelTime)
        			.addComponent(labelDuration)
        			)
        		.addComponent(spinHr)
        		.addComponent(colon1)
        		.addComponent(spinMin)
        		.addComponent(colon2)
        		.addGroup(timeLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
        			.addComponent(spinSec)
        			.addComponent(spinDur)
        			)
        		
        	);
        timeLayout.setVerticalGroup(
        	timeLayout.createSequentialGroup()
	        	.addGroup(timeLayout.createParallelGroup()
	        		.addComponent(labelTime)
	        		.addComponent(spinHr)
	        		.addComponent(colon1)
	        		.addComponent(spinMin)
	        		.addComponent(colon2)
	        		.addComponent(spinSec)
	        		)
        		.addGroup(timeLayout.createParallelGroup()
        			.addComponent(labelDuration)
        			.addComponent(spinDur)
        			)
        		
        	);

        Border etched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        formatPanel.setBorder(BorderFactory.createTitledBorder(etched,"Format"));
        positionPanel.setBorder(BorderFactory.createTitledBorder(etched,"Position"));
        text.setBorder(BorderFactory.createTitledBorder(etched,"Text"));
        timePanel.setBorder(BorderFactory.createTitledBorder(etched,"Time"));


	}

	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == btnSave)
		{
			JFileChooser jFC = new JFileChooser();
			int returnVal = jFC.showSaveDialog(textFrame);
			if(returnVal == JFileChooser.APPROVE_OPTION) 
			{
				DrawCommandArgs args = new DrawCommandArgs();
				args.sourceFile = sourceFile;
				args.text = text.getText();
				args.p = new Point((int)spinX.getValue(),(int)spinX.getValue());
				args.color = (String)comboColor.getSelectedItem();
				args.startTime = ToSeconds((int)spinHr.getValue(),(int)spinMin.getValue(),(int)spinSec.getValue());
				args.duration = (int)spinDur.getValue();
				args.size = (int)spinSize.getValue();
				args.outFile = jFC.getSelectedFile().getAbsolutePath();
				args.fontName = (String)comboFont.getSelectedItem();
				args.gui = this;

				new DrawCommand(args);
			}
		}
	}

	private int ToSeconds(int hr, int min, int sec)
	{
		return 3600*hr+60*min+sec;
	}

	public void addInternalFrameListener(InternalFrameListener listener)
	{
		textFrame.addInternalFrameListener(listener);
	}

	public void setProgress(int pct)
	{
		progress.setValue(pct);
	}
}