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
	private JLabel labelFont,labelColor,labelX,labelY,labelText,labelTime,labelDuration, labelSize, labelSource;
	private JComboBox<String> comboFont, comboColor;
	private JSpinner spinX, spinY, spinHr, spinMin, spinSec, spinDur, spinSize;
	private JTextField text;
	private JButton btnSave, btnChoose, btnOpen, btnDo;
	private JProgressBar progress;

	private JFrame parent;
	private JPanel mainPanel, formatPanel, positionPanel, timePanel, sourcePanel, leftPanel, rightPanel;
	private JInternalFrame textFrame;
	private File sourceFile;

	public TextGUI(JFrame frame, File source)
	{
		sourceFile = source;
		if(sourceFile == null)
			labelSource = new JLabel("none selected");
		else
		{
			String name = sourceFile.getName();
			if(name.length() > 20)
				name = "..."+name.substring(name.length()-20);
			labelSource = new JLabel(name);
		}
		parent = frame;
		textFrame = new JInternalFrame("Edit text",false,true);
		textFrame.setSize(800,500);
		textFrame.setVisible(true);
		parent.add(textFrame);
		textFrame.setLocation(125, 30);
		try {
			textFrame.setSelected(true);
		} catch (java.beans.PropertyVetoException e) {}
		mainPanel = new JPanel();
		leftPanel = new JPanel();
		rightPanel = new JPanel();
		sourcePanel = new JPanel();
		formatPanel = new JPanel();
		positionPanel = new JPanel();
		timePanel = new JPanel();

		textFrame.setContentPane(mainPanel);

		GroupLayout layout = new GroupLayout(textFrame.getContentPane());
		textFrame.getContentPane().setLayout(layout);
		GroupLayout srcLayout = new GroupLayout(sourcePanel);
		sourcePanel.setLayout(srcLayout);
		GroupLayout formatLayout = new GroupLayout(formatPanel);
		formatPanel.setLayout(formatLayout);
		GroupLayout posLayout = new GroupLayout(positionPanel);
		positionPanel.setLayout(posLayout);
		GroupLayout timeLayout = new GroupLayout(timePanel);
		timePanel.setLayout(timeLayout);
		GroupLayout leftLayout = new GroupLayout(leftPanel);
		leftPanel.setLayout(leftLayout);
		GroupLayout rightLayout = new GroupLayout(rightPanel);
		rightPanel.setLayout(rightLayout);

		btnChoose = new JButton("Choose file");
		btnChoose.addActionListener(this);

		btnOpen = new JButton("Open project");
		btnOpen.addActionListener(this);

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
		text = new JTextField();

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

		btnSave = new JButton("Save Project");
		btnSave.addActionListener(this);

		btnDo = new JButton("Do");
		btnDo.addActionListener(this);

		progress = new JProgressBar(0,100);
		progress.setStringPainted(true);

		//Main panel layout
        MainLayout(layout);

        //Source panel layout
        SourceLayout(srcLayout);


        //Format panel layout
        FormatLayout(formatLayout);

        //Position panel layout
        PositionLayout(posLayout);

        //Time panel layout
        TimeLayout(timeLayout);

        //Right layout
        RightLayout(rightLayout);

        //Left layout
        LeftLayout(leftLayout);

        Border etched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        sourcePanel.setBorder(BorderFactory.createTitledBorder(etched,"Source"));
        formatPanel.setBorder(BorderFactory.createTitledBorder(etched,"Format"));
        positionPanel.setBorder(BorderFactory.createTitledBorder(etched,"Position"));
        text.setBorder(BorderFactory.createTitledBorder(etched,"Text"));
        timePanel.setBorder(BorderFactory.createTitledBorder(etched,"Time"));


	}

	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == btnSave)
		{
			if(sourceFile == null)
				JOptionPane.showMessageDialog(null,"Select a valid source file");
			else
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
					
					args.fontName = (String)comboFont.getSelectedItem();
					args.gui = this;
					File outputFile = jFC.getSelectedFile();
					if(!outputFile.getName().endsWith(".vam"))
						outputFile = new File(outputFile.getAbsolutePath()+".vam");
					new DrawCommand(args).Save(args, outputFile);
				}
			}
		}
		else if(e.getSource() == btnDo)
		{
			if(sourceFile == null)
				JOptionPane.showMessageDialog(null,"Select a valid source file");
			else
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

					new DrawCommand(args).Execute();
				}
			}
		}
		else if(e.getSource() == btnChoose)
		{
			JFileChooser jFC = new JFileChooser();
			int returnVal = jFC.showOpenDialog(textFrame);
			if(returnVal == JFileChooser.APPROVE_OPTION)
			{
				sourceFile = jFC.getSelectedFile();
				String name = sourceFile.getName();
				if(name.length() > 20)
					name = "..."+name.substring(name.length()-20);
				labelSource.setText(name);
			}

		}
		else if(e.getSource() == btnOpen)
		{
			JFileChooser jFC = new JFileChooser();
			int returnVal = jFC.showOpenDialog(textFrame);
			if(returnVal == JFileChooser.APPROVE_OPTION)
			{
				File project = jFC.getSelectedFile();
				if(!project.exists())
					JOptionPane.showMessageDialog(null,"Project not found");
				else
				{
					Set(new DrawCommandArgs(project));
				}
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

	public void MainLayout(GroupLayout layout)
	{
		layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(
        	layout.createSequentialGroup()
	        	.addComponent(leftPanel)
	        	.addComponent(rightPanel)
        	);
        layout.setVerticalGroup(
        	layout.createParallelGroup()
    			.addComponent(leftPanel)
        		.addComponent(rightPanel)
        	);
	}

	public void FormatLayout(GroupLayout layout)
	{
		layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(
        	layout.createSequentialGroup()
        		.addGroup(layout.createParallelGroup()
    				.addComponent(labelFont)
    				.addComponent(labelColor)
    				.addComponent(labelSize)
    				)
    			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
    				.addComponent(comboFont)
    				.addComponent(comboColor)
    				.addComponent(spinSize)
    				)
    		);
        layout.setVerticalGroup(
        	layout.createSequentialGroup()
        		.addGroup(layout.createParallelGroup()
    				.addComponent(labelFont)
    				.addComponent(comboFont)
    				)
    			.addGroup(layout.createParallelGroup()
    				.addComponent(labelColor)
    				.addComponent(comboColor)
    				)
    			.addGroup(layout.createParallelGroup()
    				.addComponent(labelSize)
    				.addComponent(spinSize)
    				)
    		);
	}

	public void PositionLayout(GroupLayout layout)
	{
		layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(
        	layout.createSequentialGroup()
        		.addComponent(labelX)
        		.addComponent(spinX)
        		.addComponent(labelY)
        		.addComponent(spinY)
        	);
        layout.setVerticalGroup(
        	layout.createParallelGroup()
        		.addComponent(labelX)
        		.addComponent(spinX)
        		.addComponent(labelY)
        		.addComponent(spinY)
        	);
	}

	public void TimeLayout(GroupLayout layout)
	{
		JLabel colon1 = new JLabel(":");
        JLabel colon2 = new JLabel(":");
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(
        	layout.createSequentialGroup()
        		.addGroup(layout.createParallelGroup()
        			.addComponent(labelTime)
        			.addComponent(labelDuration)
        			)
        		.addComponent(spinHr)
        		.addComponent(colon1)
        		.addComponent(spinMin)
        		.addComponent(colon2)
        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
        			.addComponent(spinSec)
        			.addComponent(spinDur)
        			)
        		
        	);
        layout.setVerticalGroup(
        	layout.createSequentialGroup()
	        	.addGroup(layout.createParallelGroup()
	        		.addComponent(labelTime)
	        		.addComponent(spinHr)
	        		.addComponent(colon1)
	        		.addComponent(spinMin)
	        		.addComponent(colon2)
	        		.addComponent(spinSec)
	        		)
        		.addGroup(layout.createParallelGroup()
        			.addComponent(labelDuration)
        			.addComponent(spinDur)
        			)
        		
        	);
	}

	public void SourceLayout(GroupLayout layout)
	{
		layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(
        	layout.createSequentialGroup()
        		.addComponent(btnChoose)
        		.addComponent(labelSource)
    		);
        layout.setVerticalGroup(
        	layout.createParallelGroup()
        		.addComponent(btnChoose)
        		.addComponent(labelSource)
    		);
	}

	public void LeftLayout(GroupLayout layout)
	{
		layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(
        	layout.createParallelGroup()
        		.addComponent(formatPanel)
				.addComponent(positionPanel)
				.addComponent(text)
				.addComponent(timePanel)
        	);
        layout.setVerticalGroup(
        	layout.createSequentialGroup()
    			.addComponent(formatPanel)
    			.addComponent(positionPanel)
    			.addComponent(text)
    			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING))
    			.addComponent(timePanel)
        	);
	}

	public void RightLayout(GroupLayout layout)
	{
		layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(
        	layout.createParallelGroup()
		        .addComponent(sourcePanel)
		        .addGroup(layout.createSequentialGroup()
		        	.addComponent(btnOpen)
			        .addComponent(btnSave)
	        		)
		        .addGroup(layout.createSequentialGroup()
	    			.addComponent(btnDo)
	    			.addComponent(progress)
	        		)
        	);
        layout.setVerticalGroup(
        	layout.createSequentialGroup()
        		.addComponent(sourcePanel)
        		.addGroup(layout.createParallelGroup()
		        	.addComponent(btnOpen)
			        .addComponent(btnSave)
	        		)
    			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
    				.addComponent(btnDo)
    				.addComponent(progress)
    				)
        	);
	}

	public void Set(DrawCommandArgs args)
	{
		sourceFile = args.sourceFile;
		labelSource.setText(sourceFile.getName());
		text.setText(args.text);
		spinX.setValue(args.p.x);
		spinY.setValue(args.p.y);
		comboColor.setSelectedItem(args.color);
		spinHr.setValue(args.startTime/3600);
		spinMin.setValue((args.startTime%3600)/60);
		spinSec.setValue(args.startTime%60);
		spinDur.setValue(args.duration);
		spinSize.setValue(args.size);
		comboFont.setSelectedItem(args.fontName);
	}
}