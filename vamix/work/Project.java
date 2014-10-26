package vamix.work;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JList;
import javax.swing.JOptionPane;

import vamix.filter.Filter;

public class Project 
{
	private List<Filter> filterList;
	public String name;
	private File sourceFile, outFile;
	
	public Project(String name)
	{
		this.name = name;
		filterList = new ArrayList<Filter>();
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
	
	public void Save()
	{
		JOptionPane.showMessageDialog(null, "Save");
	}
	
	public Filter GetFilter(int index)
	{
		return filterList.get(index);
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
}
