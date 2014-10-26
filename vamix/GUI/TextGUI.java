package vamix.GUI;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import vamix.filter.Filter;
import vamix.filter.DrawText;
import vamix.work.*;

/**	TextGUI
*	Contains all the gui elements that pop up when Edit Text is selected
*/
public class TextGUI extends JFrame implements ActionListener
{
	private final int MAXLENGTH = 40;

	private JLabel labelSource;
	private JButton btnSave, btnChoose, btnOpen, btnDo, btnAddFilter,btnRemoveFilter;
	private JProgressBar progress;
	private JTabbedPane tabbedPane;

	private JFrame parent;
	private JPanel mainPanel, sourcePanel, leftPanel, rightPanel;
	private File sourceFile;
	
	private Project currentProject;
	private JScrollPane filterScroll;
	private JList<Filter> filterList;
	private DefaultListModel<Filter> filterModel;
	private Filter currentFilter;

	public TextGUI(JFrame frame, File source)
	{
		super("Edit Text");
		sourceFile = source;
		currentProject = new Project("NewProject");
		currentFilter = null;
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
		tabbedPane = new JTabbedPane();
		

		setContentPane(mainPanel);

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		GroupLayout srcLayout = new GroupLayout(sourcePanel);
		sourcePanel.setLayout(srcLayout);
		GroupLayout leftLayout = new GroupLayout(leftPanel);
		leftPanel.setLayout(leftLayout);
		GroupLayout rightLayout = new GroupLayout(rightPanel);
		rightPanel.setLayout(rightLayout);

		btnChoose = new JButton("Choose file");
		btnChoose.addActionListener(this);

		btnOpen = new JButton("Open project");
		btnOpen.addActionListener(this);

		

		btnSave = new JButton("Save Project");
		btnSave.addActionListener(this);

		btnDo = new JButton("Process");
		btnDo.addActionListener(this);

		progress = new JProgressBar(0,100);
		progress.setString("");
		progress.setStringPainted(true);
		progress.setMaximumSize(new Dimension(160,25));
		
		filterModel = new DefaultListModel<Filter>();
		filterList = new JList<Filter>(filterModel);
		filterList.addListSelectionListener(new ListSelectionListener(){
			@Override
			public void valueChanged(ListSelectionEvent arg0) 
			{
				currentFilter = filterList.getSelectedValue();
				Set(currentFilter);
			}
		});
		filterScroll = new JScrollPane(filterList);
		filterScroll.setMaximumSize(new Dimension(250,300));
		filterScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		btnAddFilter = new JButton("Add Filter");
        btnAddFilter.addActionListener(this);
        
        btnRemoveFilter = new JButton("Remove Filter");
        btnRemoveFilter.addActionListener(this);
        
		//Main panel layout
        MainLayout(layout);

        //Source panel layout
        SourceLayout(srcLayout);

        //Right layout
        RightLayout(rightLayout);

        //Left layout
        //LeftLayout(leftLayout);

        
        sourcePanel.setBorder(BorderFactory.createTitledBorder("Source"));
        filterScroll.setBorder(BorderFactory.createTitledBorder("Filters"));
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
				jFC.setSelectedFile(new File(currentProject.name));
				int returnVal = jFC.showSaveDialog(this);
				if(returnVal == JFileChooser.APPROVE_OPTION) 
				{
					
					/*DrawCommandArgs args = new DrawCommandArgs();
					args.sourceFile = sourceFile;
					args.text = text.getText();
					args.p = new Point((int)spinX.getValue(),(int)spinX.getValue());
					args.color = "0x"+ColorToString(selectedColor);
					args.startTime = ToSeconds((int)spinHr.getValue(),(int)spinMin.getValue(),(int)spinSec.getValue());
					args.duration = (int)spinDur.getValue();
					args.size = (int)spinSize.getValue();
					
					args.fontName = (String)comboFont.getSelectedItem();
					args.gui = this;*/
					File outputFile = jFC.getSelectedFile();
					if(!outputFile.getName().endsWith(".vam"))
						outputFile = new File(outputFile.getAbsolutePath()+".vam");
					if(outputFile.exists())
						{
						Object[] options = { "OVERWRITE", "CANCEL" };
						int value = JOptionPane.showOptionDialog(null, "Project file already exists. Overwrite?", "Warning",JOptionPane.DEFAULT_OPTION, 
								JOptionPane.WARNING_MESSAGE,null, options, options[0]);
						if(value == JOptionPane.CANCEL_OPTION)
							return;
						}
					
					
					try
					{
						FileWriter saveWriter = new FileWriter(outputFile.getAbsolutePath());
						saveWriter.write(currentProject.SaveText());
						saveWriter.close();
					}
					catch(Exception err)
					{
						JOptionPane.showMessageDialog(null,"Error "+err.getMessage());
					}
				}
			}
		}
		else if(e.getSource() == btnDo)
		{
			/*if(sourceFile == null)
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

					new DrawCommand2(args).Execute();
					btnDo.setEnabled(false);
				}
			}*/
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
			/*JFileChooser jFC = new JFileChooser();
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
			}*/
		}
		else if(e.getSource() == btnAddFilter)
		{
			String name = JOptionPane.showInputDialog("New filter name:");
			Filter toAdd = new DrawText(currentProject,name);
			filterModel.addElement(toAdd);
		}
		else if(e.getSource() == btnRemoveFilter)
		{
			Filter toRemove = filterList.getSelectedValue();
			filterModel.removeElement(toRemove);
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
	        	.addComponent(filterScroll)
	        	.addGroup(layout.createSequentialGroup()
	    			.addComponent(btnAddFilter)
	    			.addComponent(btnRemoveFilter)
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
	        	.addComponent(filterScroll)
	        	.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
    				.addComponent(btnAddFilter)
    				.addComponent(btnRemoveFilter)
    				)
    			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
    				.addComponent(btnDo)
    				.addComponent(progress)
    				)
        	);
	}

	/*public void Set(DrawCommandArgs args)
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
	}*/
	
	public void Set(Filter f)
	{
		switch(f.type)
		{
		case DrawText: SetDrawText((DrawText) f); break;
			
		}
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

	public void Completed()
	{
		progress.setValue(0);
		progress.setString("");
		btnDo.setEnabled(true);
	}
	
	public Filter GetFilter()
	{
		return currentFilter;
	}
}