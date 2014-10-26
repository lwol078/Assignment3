package vamix.work;

import java.awt.Point;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;

import vamix.filter.DrawText;
import vamix.filter.Filter;

public class Project 
{
	private List<Filter> filterList;
	public String name;
	public File sourceFile, outFile;
	
	public Project(String name)
	{
		this.name = name;
		filterList = new ArrayList<Filter>();
	}
	/**Load project from a project file
	 * @param inFile
	 */
	public static Project Load(File file)
	{
		try
		{
			//Check file exists
			if(!file.exists())
			{
				JOptionPane.showMessageDialog(null,"File does not exist");
				return null;
			}

			//Initialise
			String str = "";
			BufferedReader openReader = new BufferedReader(new FileReader(file));

			//Check format
			str = openReader.readLine();
			if(!str.equals("PROJECT"))
			{
				JOptionPane.showMessageDialog(null,"File not formatted correctly");
				return null;
			}

			//Read name, source file, out file
			str = openReader.readLine();
			Project project = new Project(str);
			str = openReader.readLine();
			project.sourceFile = new File(str);
			if(!project.sourceFile.exists())
			{
				project.sourceFile = null;
				JOptionPane.showMessageDialog(null,"Source file not found");
			}
			str = openReader.readLine();
			project.outFile = new File(str);

			while((str = openReader.readLine()) != null)
			{
				if(str.equals("DRAWTEXT"))
				{
					DrawText.Load(project,openReader);
				}
				else
				{
					JOptionPane.showMessageDialog(null,"File not formatted correctly");
					return null;
				}
			}
			openReader.close();
			return project;
			
		}
		catch(Exception err)
		{
			JOptionPane.showMessageDialog(null,err.getMessage());
			err.printStackTrace();
		}
		return null;
	}

	public void AddFilter(Filter f)
	{
		if(!filterList.contains(f))
			filterList.add(f);
	}

	public void RemoveFilter(Filter f)
	{
		filterList.remove(f);
	}

	public Filter GetFilter(int index)
	{
		return filterList.get(index);
	}
	
	public int NumFilters()
	{
		return filterList.size();
	}
	
	public String SaveText()
	{
		String str = "PROJECT\n";
		str += name+"\n";
		str += sourceFile+"\n";
		str += outFile+"\n";
		for(Filter f : filterList)
		{
			if(f != null)
				str += f.SaveText();
		}
		return str;
	}
	
	public int GetFPS()
	{
		ArrayList<String> pS = new ArrayList<String>();
		pS.add("avprobe");
		pS.add(sourceFile.getAbsolutePath());
		pS.add("2>&1");
		pS.add("|");
		pS.add("grep");
		pS.add("-o");
		pS.add("'[[:digit:]]*.[[:digit:]]* fps'");
		
		try {
		ProcessBuilder pB = new ProcessBuilder(pS);
		Process p;
		p = pB.start();
		p.waitFor();
		BufferedReader bR = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String frames = bR.readLine();
		frames = frames.split(" ")[0];
		return (int)Float.parseFloat(frames);
		} 
		catch (IOException e) 
		{

			e.printStackTrace();
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		return 0;
	}
}
