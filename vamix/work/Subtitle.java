package vamix.work;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import vamix.filter.Filter;

/**
 * Subtitle
 * @author luke
 * Simple class to represent subtitle
 * Plus saving/loading
 */
public class Subtitle implements Comparable<Subtitle>
{
	public int startTime;
	public int endTime;
	public String text;
	
	@Override
	public String toString()
	{
		//set max length to show in JList
		if(text.length() > 15)
			return text.substring(0,12)+"...";
		else
			return text;
	}
	
	/**
	 * SaveString()
	 * @return String that can be written to srt file to save subtitle
	 */
	public String SaveString()
	{
		String str = Filter.TimeToString(startTime)+",000 --> ";
		str += Filter.TimeToString(startTime)+",000\n";
		str += text;
		return str;
	}
	public static ArrayList<Subtitle> Load(File inFile)
	{
		ArrayList<Subtitle> list = new ArrayList<Subtitle>();
		try 
		{
			BufferedReader openReader = new BufferedReader(new FileReader(inFile));
			String str;
			while((str = openReader.readLine()) != null)
			{
				//Ignore number above subtitle
				str= openReader.readLine();
				//format hh:mm:ss,nnn -->hh:mm:ss,nn
				if(!str.matches("\\d\\d:\\d\\d:\\d\\d,\\d\\d\\d --> \\d\\d:\\d\\d:\\d\\d,\\d\\d\\d"))
					{
					JOptionPane.showMessageDialog(null, "File not formatted correctly");
					return new ArrayList<Subtitle>();
					}
				String[] strings = str.split(" ");
				Subtitle subtitle = new Subtitle();
				
				//ignore milliseconds
				subtitle.startTime = Filter.StringToTime(strings[0].substring(0,8));
				subtitle.endTime = Filter.StringToTime(strings[2].substring(0,8));
				
				subtitle.text = openReader.readLine();
				while((str = openReader.readLine()) != null && str.length() > 0)
				{
					subtitle.text += "\n"+str;
				}
				list.add(subtitle);
			}
			openReader.close();
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
		return list;
		
	}

	@Override
	public int compareTo(Subtitle other) 
	{
		//order by startTime
		return ((Integer)this.startTime).compareTo(other.startTime);
	}
}
