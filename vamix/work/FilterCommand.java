package vamix.work;

import javax.swing.*;
import java.io.*;
import java.util.*;
import java.awt.Point;
import vamix.GUI.FilterGUI;
import vamix.filter.Filter;
import vamix.filter.Project;

/**
 * FilterCommand
 * @author luke
 * Runs a command which makes a chain if filters based on a project
 * and applies them to a video 
 */
public class FilterCommand
{
	private Project project;
	private FilterGUI gui; 

	private String ext;
	private DrawCommandWorker worker;

	public FilterCommand(Project p, FilterGUI gui)
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

	private class DrawCommandWorker extends SwingWorker<Integer,Void>
	{
		@Override
		protected Integer doInBackground()
		{
			gui.setProgress(true);
			int status = 0;
			String fileName = project.sourceFile.getName();
			int index = fileName.lastIndexOf('.');
			//Get extension of file
			if (index > 0)
				ext = fileName.substring(index+1);

			ProcessBuilder builder;
			
			List<String> processString = new ArrayList<String>();
			processString.add("avconv");
			processString.add("-i");
			processString.add(project.sourceFile.getAbsolutePath());
			processString.add("-vf");
			String str = "";
			for(int i = 0; i < project.NumFilters(); i++)
			{	
				//format: [in]filter1[temp1];[temp1]filter2[temp2];...filterLast[out];
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
			finally
			{
				gui.setProgress(false);
			}
		}
		@Override
		protected void process(List<Void> chunks)
		{
			//Tried having a progress bar but slowed computer too much
		}
	}
	public void Execute()
	{
		if(worker != null)
			worker.execute();
	}
}