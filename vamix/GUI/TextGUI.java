package vamix.GUI;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.border.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import vamix.filter.*;
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

	private MainFrame parent;
	private JPanel mainPanel, sourcePanel, leftPanel;
	private DrawTextPanel drawTextPanel;
	private NegatePanel negatePanel;

	private Project currentProject;
	private JScrollPane filterScroll;
	private JList<Filter> filterList;
	private DefaultListModel<Filter> filterModel;
	private Filter currentFilter;
	private GroupLayout layout;
	private boolean viewLock;

	public TextGUI(MainFrame frame, File source)
	{
		super("Edit Text");
		viewLock = false;
		currentProject = new Project("NewProject");
		currentFilter = null;
		currentProject.sourceFile = source;
		if(source == null)
			labelSource = new JLabel("none selected");
		else if (!ValidateVideoFile(source))
		{
			source = null;
			labelSource = new JLabel("none selected");
		}
		else
		{
			labelSource = new JLabel("");
			SetSource(source);
		}

		parent = frame;
		setVisible(true);
		setMaximumSize(new Dimension(600,600));
		setMinimumSize(new Dimension(250,300));
		setLocation(125, 30);
		mainPanel = new JPanel();

		leftPanel = new JPanel();
		sourcePanel = new JPanel();

		tabbedPane = new JTabbedPane();

		drawTextPanel = new DrawTextPanel(this);
		tabbedPane.addTab("Text Overlay", drawTextPanel);
		negatePanel = new NegatePanel(this);
		tabbedPane.addTab("Negate", negatePanel);

		tabbedPane.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0)
			{
				if(currentFilter != null)
				{
					if(tabbedPane.getSelectedComponent() == drawTextPanel)
					{
						Filter temp = currentFilter;
						currentFilter = new DrawText(currentProject,currentFilter.name);
						currentProject.ReplaceFilter(temp,currentFilter);
						drawTextPanel.SetValues((DrawText) currentFilter);

					}
					else if(tabbedPane.getSelectedComponent() == negatePanel)
					{
						Filter temp = currentFilter;
						currentFilter = new Negate(currentProject,currentFilter.name);
						currentProject.ReplaceFilter(temp,currentFilter);
						negatePanel.SetValues((Negate) currentFilter);
					}
					FilterListUpdate();
				}
			}

		});

		setContentPane(mainPanel);

		layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		GroupLayout srcLayout = new GroupLayout(sourcePanel);
		sourcePanel.setLayout(srcLayout);
		GroupLayout rightLayout = new GroupLayout(leftPanel);
		leftPanel.setLayout(rightLayout);

		btnChoose = new JButton("Choose file");
		btnChoose.addActionListener(this);

		btnOpen = new JButton("Open project");
		btnOpen.addActionListener(this);

		btnSave = new JButton("Save Project");
		btnSave.addActionListener(this);

		btnDo = new JButton("Apply Filters");
		btnDo.addActionListener(this);
		btnDo.setBackground(Color.blue);
		btnDo.setForeground(Color.white);

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
				SetFilter(filterList.getSelectedValue());
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
		MainLayout(layout, false);

		//Source panel layout
		SourceLayout(srcLayout);

		//Right layout
		RightLayout(rightLayout);

		//Left layout
		//LeftLayout(leftLayout);

		sourcePanel.setBorder(BorderFactory.createTitledBorder("Source"));
		filterScroll.setBorder(BorderFactory.createTitledBorder("Filters"));
		tabbedPane.setBorder(BorderFactory.createTitledBorder("Filter Options"));
		pack();
	}

	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == btnSave)
		{
			if(currentProject.sourceFile == null)
				JOptionPane.showMessageDialog(null,"Select a valid source file");
			else
			{
				JFileChooser jFC = new JFileChooser();
				jFC.setSelectedFile(new File(currentProject.name));
				int returnVal = jFC.showSaveDialog(this);
				if(returnVal == JFileChooser.APPROVE_OPTION) 
				{
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

			{
				if(currentProject.sourceFile == null)
					JOptionPane.showMessageDialog(null,"Select a valid source file");
				else
				{
					JFileChooser jFC = new JFileChooser();
					if(currentProject.outFile != null)
						jFC.setSelectedFile(currentProject.outFile);
					int returnVal = jFC.showSaveDialog(null);
					if(returnVal == JFileChooser.APPROVE_OPTION) 
					{
						currentProject.outFile = jFC.getSelectedFile();
						new FilterCommand(currentProject, this).Execute();
						btnDo.setEnabled(false);
					}
				}
			}
		}
		else if(e.getSource() == btnChoose)
		{
			JFileChooser jFC = new JFileChooser();
			int returnVal = jFC.showOpenDialog(this);
			if(returnVal == JFileChooser.APPROVE_OPTION)
			{
				SetSource(jFC.getSelectedFile());
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
					Project temp = Project.Load(project);
					if(temp != null)
					{
						currentProject = temp;
						SetSource(currentProject.sourceFile);
						FilterListUpdate();
					}
				}
			}
		}
		else if(e.getSource() == btnAddFilter)
		{
			String name = JOptionPane.showInputDialog("New filter name:");
			Filter toAdd = new DrawText(currentProject,name);
			currentProject.AddFilter(toAdd);
			filterModel.addElement(toAdd);
		}
		else if(e.getSource() == btnRemoveFilter)
		{
			Filter toRemove = filterList.getSelectedValue();
			filterModel.removeElement(toRemove);
			currentProject.RemoveFilter(toRemove);
		}
	}

	public void setProgress(boolean bool)
	{
		progress.setIndeterminate(bool);
	}

	public void MainLayout(GroupLayout layout, boolean showFilter)
	{
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		if(showFilter)
		{
			layout.setHorizontalGroup(
					layout.createSequentialGroup()
					.addComponent(leftPanel)
					.addComponent(tabbedPane)

					);
			layout.setVerticalGroup(
					layout.createParallelGroup()
					.addComponent(leftPanel)
					.addComponent(tabbedPane)
					);
		}
		else
		{
			remove(tabbedPane);
			layout.setHorizontalGroup(
					layout.createSequentialGroup()
					.addComponent(leftPanel)
					);
			layout.setVerticalGroup(
					layout.createParallelGroup()
					.addComponent(leftPanel)
					);
		}

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

	public void SetFilter(Filter f)
	{
		currentFilter = f;
		if(f == null)
		{
			if(!viewLock)
			{
				//Remove filter part
				MainLayout(layout,false);
			}
			else
			{
				//prevent flickering
				MainLayout(layout,true);
			}
			
		}
			
		else
		{
			//Show filter part
			MainLayout(layout,true);
			switch(f.type)
			{
			case DRAWTEXT: 
			{
				drawTextPanel.SetOptions((DrawText) f);
				tabbedPane.setSelectedComponent(drawTextPanel);
				break;
			}

			case NEGATE: 
			{
				negatePanel.SetOptions((Negate) f);
				tabbedPane.setSelectedComponent(negatePanel);
				break;
			}
			}
		}


		pack();
	}

	public static boolean ValidateVideoFile(File file)
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

	private void SetSource(File f)
	{
		if(!ValidateVideoFile(f))
		{
			JOptionPane.showMessageDialog(null,"Please select a valid video file");
			return;
		}
		currentProject.sourceFile = f;
		String name;
		if(currentProject.sourceFile == null)
			name = "No file selected";
		else
			name = currentProject.sourceFile.getName();
		if(name.length() > 15)
			name = "..."+name.substring(name.length()-12);
		labelSource.setText(name);
	}

	public Project GetProject()
	{
		return currentProject;
	}
	
	private void FilterListUpdate()
	{
		viewLock = true;
		Filter temp= currentFilter;
		filterModel.clear();
		for(int i = 0 ; i < currentProject.NumFilters(); i++)
		{
			filterModel.addElement(currentProject.GetFilter(i));
		}
		currentFilter = temp;
		filterList.setSelectedValue(currentFilter, true);
		viewLock = false;
	}
}