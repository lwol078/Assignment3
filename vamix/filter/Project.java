package vamix.filter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;


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
					project.AddFilter(DrawText.Load(project,openReader));
				}
				else if(str.equals("NEGATE"))
				{
					project.AddFilter(Negate.Load(project,openReader));
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

	public void ReplaceFilter(Filter toReplace, Filter replacer)
	{
		if(filterList.contains(toReplace))
			filterList.set(filterList.indexOf(toReplace), replacer);
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
		
		//TODO fix
		if(sourceFile == null)
			return 0;
		String cmd = "avprobe '"+sourceFile.getAbsolutePath()+"' 2>&1 | grep -o '[[:digit:]]*.[[:digit:]]* fps'";
		System.out.println(cmd);
		ArrayList<String> pS = new ArrayList<String>();
		pS.add("avprobe");
		pS.add(sourceFile.getAbsolutePath());
		pS.add("2>&1");
		pS.add("|");
		pS.add("grep");
		pS.add("-o");
		pS.add("'[[:digit:]]*.[[:digit:]]* fps'");

		try {
			ProcessBuilder pB = new ProcessBuilder("/bin/bash","-c",cmd);
			Process p;
			p = pB.start();
			p.waitFor();
			BufferedReader bR = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String frames;
			while((frames = bR.readLine()) != null)
				System.err.println(frames);
			if(frames == null)
				return 0;
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
