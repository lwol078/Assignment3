package vamix.GUI;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import vamix.work.*;

/**	TextGUI
*	Contains all the gui elements that pop up when Edit Text is selected
*/
public class TextGUI extends JFrame implements ActionListener
{
	private final int MAXLENGTH = 40;

	private JLabel labelFont,labelColor,labelX,labelY,labelText,labelTime,labelDuration, labelSize, labelSource;
	private JComboBox<String> comboFont;
	private JSpinner spinX, spinY, spinHr, spinMin, spinSec, spinDur, spinSize;
	private JTextField text;
	private JButton btnSave, btnChoose, btnOpen, btnDo, btnColor;
	private JProgressBar progress;

	private JFrame parent;
	private JPanel mainPanel, formatPanel, positionPanel, timePanel, sourcePanel, leftPanel, rightPanel;
	private File sourceFile;
	private Color selectedColor;

	public TextGUI(JFrame frame, File source)
	{
		super("Edit Text");
		sourceFile = source;
		selectedColor = new Color(0,0,0);
		if(sourceFile == null)
			labelSource = new JLabel("none selected");
		else if (!ValidateVideoFile(sourceFile))
		{
			sourceFile = null;
			labelSource = new JLabel("none selected");
		}
		else
		{
			//Set max length to show of filename
			String name = sourceFile.getName();
			if(name.length() > 20)
				name = "..."+name.substring(name.length()-20);
			labelSource = new JLabel(name);
		}
		parent = frame;
		setSize(800,500);
		setVisible(true);
		setLocation(125, 30);
		mainPanel = new JPanel();
		leftPanel = new JPanel();
		rightPanel = new JPanel();
		sourcePanel = new JPanel();
		formatPanel = new JPanel();
		positionPanel = new JPanel();
		timePanel = new JPanel();

		setContentPane(mainPanel);

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
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
		//Load fonts from file
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
		if(comboFont.getItemCount()!= 0)
			comboFont.setSelectedIndex(0);
		comboFont.setMaximumSize(new Dimension(100,20));

		labelColor = new JLabel("Color:");
		btnColor = new JButton("Select");
		btnColor.addActionListener(this);

		labelSize = new JLabel("Size:");
		spinSize = new JSpinner(new SpinnerNumberModel(10,1,50,1));
		spinSize.setMaximumSize(new Dimension(30,20));

		labelX = new JLabel("x:");
		spinX = new JSpinner(new SpinnerNumberModel(0,0,100,1));
		spinX.setMaximumSize(new Dimension(30,20));

		labelY = new JLabel("y:");
		spinY = new JSpinner(new SpinnerNumberModel(0,0,100,1));
		spinY.setMaximumSize(new Dimension(30,20));


		text = new JTextField(40);
		text.setMaximumSize(new Dimension(335,40));
		text.setDocument(new PlainDocument()
		{
			public void insertString(int offs, String str, AttributeSet a) throws BadLocationException
			{
				if (str == null)
			      return;

			    if ((getLength() + str.length()) <= 40)
			      super.insertString(offs, str, a);
			}

		});

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

		btnDo = new JButton("Process");
		btnDo.addActionListener(this);

		progress = new JProgressBar(0,100);
		progress.setString("");
		progress.setStringPainted(true);
		progress.setMaximumSize(new Dimension(160,25));

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
        text.setBorder(BorderFactory.createTitledBorder(etched,"Text (Max 40 characters):"));
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
				int returnVal = jFC.showSaveDialog(this);
				if(returnVal == JFileChooser.APPROVE_OPTION) 
				{
					DrawCommandArgs args = new DrawCommandArgs();
					args.sourceFile = sourceFile;
					args.text = text.getText();
					args.p = new Point((int)spinX.getValue(),(int)spinX.getValue());
					args.color = "0x"+ColorToString(selectedColor);
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
				int returnVal = jFC.showSaveDialog(this);
				if(returnVal == JFileChooser.APPROVE_OPTION) 
				{
					DrawCommandArgs args = new DrawCommandArgs();
					args.sourceFile = sourceFile;
					args.text = text.getText();
					args.p = new Point((int)spinX.getValue(),(int)spinX.getValue());
					args.color = "0x"+ColorToString(selectedColor);
					args.startTime = ToSeconds((int)spinHr.getValue(),(int)spinMin.getValue(),(int)spinSec.getValue());
					args.duration = (int)spinDur.getValue();
					args.size = (int)spinSize.getValue();
					args.outFile = jFC.getSelectedFile().getAbsolutePath();
					args.fontName = (String)comboFont.getSelectedItem();
					args.gui = this;

					new DrawCommand(args).Execute();
					btnDo.setEnabled(false);
				}
			}
		}
		else if(e.getSource() == btnChoose)
		{
			JFileChooser jFC = new JFileChooser();
			int returnVal = jFC.showOpenDialog(this);
			if(returnVal == JFileChooser.APPROVE_OPTION)
			{
				sourceFile = jFC.getSelectedFile();
				if(!ValidateVideoFile(sourceFile))
				{
					JOptionPane.showMessageDialog(null,"Please select a valid video file");
					sourceFile = null;
				}
				String name;
				if(sourceFile == null)
					name = "No file selected";
				else
					name = sourceFile.getName();
				if(name.length() > 20)
					name = "..."+name.substring(name.length()-20);
				labelSource.setText(name);
			}

		}
		else if(e.getSource() == btnOpen)
		{
			JFileChooser jFC = new JFileChooser();
			int returnVal = jFC.showOpenDialog(this);
			if(returnVal == JFileChooser.APPROVE_OPTION)
			{
				File project = jFC.getSelectedFile();
				if(!project.exists() || !ValidateProjectFile(project))
					JOptionPane.showMessageDialog(null,"Project not found");
				else
				{
					Set(new DrawCommandArgs(project));
				}
			}
		}
		else if(e.getSource() == btnColor)
		{
		Color newColor = JColorChooser.showDialog(
                    null,
                    "Choose Text Color",
                    selectedColor);
		if(newColor != null)
			selectedColor = newColor;
		}
	}

	private int ToSeconds(int hr, int min, int sec)
	{
		return 3600*hr+60*min+sec;
	}

	public void setProgress(int pct)
	{
		progress.setValue(pct);
		progress.setString("Processing: " + pct + "%");
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
    				.addComponent(btnColor)
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
    				.addComponent(btnColor)
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
		String name;
		if(sourceFile == null)
			name = "No file selected";
		else
			name = sourceFile.getName();
		if(name.length() > 20)
			name = "..."+name.substring(name.length()-20);
		labelSource.setText(name);
		text.setText(args.text);
		spinX.setValue(args.p.x);
		spinY.setValue(args.p.y);
		selectedColor = StringToColor(args.color.substring(2));
		spinHr.setValue(args.startTime/3600);
		spinMin.setValue((args.startTime%3600)/60);
		spinSec.setValue(args.startTime%60);
		spinDur.setValue(args.duration);
		spinSize.setValue(args.size);
		comboFont.setSelectedItem(args.fontName);
	}

	private boolean ValidateVideoFile(File file)
	{
		try 
		{
			ProcessBuilder builder = new ProcessBuilder("/bin/bash","-c","avconv -i " + "\"" + file.getAbsolutePath() + "\"" + " 2>&1 | grep -q -w Video:" );
			Process process = builder.start();
			process.waitFor();
			int exitStatus = process.exitValue();
			if (exitStatus != 0)
				return false;
			return true;
		}
		catch(Exception err)
		{ return false;}
	}

	private boolean ValidateProjectFile(File file)
	{
		return file.getName().endsWith(".vam");
	}

	private String ColorToString(Color color)
	{
		String str = Integer.toHexString(color.getRGB());
		String alpha = null;
		if(str.length() > 6)
			{
				alpha = str.substring(0,2);
				str = str.substring(2);
			}
		while(str.length() < 6)
			str = "0"+str;
		if(alpha != null)
			str+=alpha;
		return str;
	}
	private Color StringToColor(String str)
	{
		String alpha = null;
		if(str.length() > 6)
			{
				alpha = str.substring(6);
				str = str.substring(0,6);
			}
		
		if(alpha != null)
			str = alpha+str;
		return new Color((int)Long.parseLong(str,16), true);
	}

	public void Completed()
	{
		progress.setValue(0);
		progress.setString("");
		btnDo.setEnabled(true);
	}
}