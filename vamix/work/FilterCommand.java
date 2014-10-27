package vamix.work;

import javax.swing.*;
import java.io.*;
import java.util.*;
import java.awt.Point;
import vamix.GUI.TextGUI;
import vamix.filter.Filter;
import vamix.filter.Project;

/**
 * FilterCommand
 * @author luke
 *
 */
public class FilterCommand
{
	private Project project;
	private TextGUI gui; 

	private String ext;
	private DrawCommandWorker worker;

	public FilterCommand(Project p, TextGUI gui)
	{
		project = p;
		this.gui = gui;

		if(project.sourceFile != null)
		{
			worker = new DrawCommandWorker();
		}
		else
		{
			JOptionPane.showMessageDialog(null,"Please select a valid source file");
		}
	}

	private class DrawCommandWorker extends SwingWorker<Integer,Integer>
	{
		@Override
		protected Integer doInBackground()
		{
			int status = 0;
			String fileName = project.sourceFile.getName();
			int i = fileName.lastIndexOf('.');
			//Get extension of file
			if (i > 0)
				ext = fileName.substring(i+1);

			status = step1();
			publish(1);
			return status;


		}

		@Override
		protected void done()
		{
			try
			{
				int exitStatus = get();
				if(exitStatus == 0)
					JOptionPane.showMessageDialog(null,"Done");
				else
					JOptionPane.showMessageDialog(null,"Error: exit status "+ exitStatus);
				gui.Completed();
			}
			catch (Exception ex)
			{}
		}
		@Override
		protected void process(List<Integer> chunks)
		{
			if(gui != null)
				gui.setProgress(chunks.get(0)*25);
		}
	}
	/**	Step 1 of the process
	 */
	private int step1()
	{
		int status = 0;
		ProcessBuilder builder;
		List<String> processString = new ArrayList<String>();
		processString.add("avconv");
		processString.add("-i");
		processString.add(project.sourceFile.getAbsolutePath());
		processString.add("-vf");
		String str = "";
		for(int i = 0; i < project.NumFilters(); i++)
		{	
			
			if(i == 0)
				str = "[in]";
			else
				str += "[temp"+i+"]";
			str += project.GetFilter(i).FilterString();
			if(i < project.NumFilters()-1)
				str = str+"[temp"+(i+1)+"];";
			else
				str += "[out]";
			
		}
		processString.add(str);
		processString.add("-strict");
		processString.add("experimental");
		processString.add("-y");
		processString.add(project.outFile+"."+ext);
		try 
		{
			builder = new ProcessBuilder(processString);
			builder.redirectError(new File("log.txt"));
			Process process = builder.start();
			status = process.waitFor();
			process.destroy();
		}
		catch(Exception ex)
		{
			JOptionPane.showMessageDialog(null,"An error has occured\n"+ex.getMessage());
			return -1;
		}
		return status;
	}
	
	public void Execute()
	{
		if(worker != null)
			worker.execute();
	}
	
	public void kill()
	{
		
	}
}