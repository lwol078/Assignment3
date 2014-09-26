package vamix.work;

import java.awt.Point;
import java.io.*;
import java.util.*;
import javax.swing.*;

import vamix.GUI.TextGUI;

/**	DrawCommandArgs
*	Contains the information to pass to the draw command
*	Simplifies passing info
*/
public class DrawCommandArgs
{
	public File sourceFile;
	public String text;
	public String fontName;
	public Point p;
	public String color;
	public int startTime, duration, size;
	public String outFile;
	public TextGUI gui;

	public DrawCommandArgs()
	{
		gui = null;
		sourceFile = null;
		text = "";
		p = new Point(0,0);
		color = "white";
		startTime = 0;
		duration = 0;
		size = 0;
		outFile = "output";
		fontName = "Ubuntu-C.tff";
	}
	/** Construct from reading a project file
	*/
	public DrawCommandArgs(File file)
	{
		try
		{
			if(!file.exists())
			{
				JOptionPane.showMessageDialog(null,"File does not exist");
				return;
			}
			String str = "";
			BufferedReader saveReader = new BufferedReader(new FileReader(file));
			str = saveReader.readLine();
			sourceFile = new File(str);
			if(!sourceFile.exists())
			{
				sourceFile = null;
				JOptionPane.showMessageDialog(null,"Source file not found");
			}
			text = saveReader.readLine();
			fontName = saveReader.readLine();
			str = saveReader.readLine();
			String[] point = str.split(" ");
			p = new Point(Integer.parseInt(point[0]), Integer.parseInt(point[1]));
			color = saveReader.readLine();

			startTime = StringToTime(saveReader.readLine());
			
			duration = Integer.parseInt(saveReader.readLine());
			size = Integer.parseInt(saveReader.readLine());

			saveReader.close();
		}
		catch(Exception err)
		{
			JOptionPane.showMessageDialog(null,err.getMessage());
			err.printStackTrace();
		}
	}
	/** Converts arguments to text format
	*/
	public String ToText()
	{
		String str = "";
		str += sourceFile.getAbsolutePath()+"\n";
		str += text+"\n";
		str += fontName+"\n";
		str += p.x+" "+p.y+"\n";
		str += color+"\n";
		str += TimeToString(startTime)+"\n";
		str += duration+"\n";
		str += size+"\n";
		
		return str;
	}
	public static String TimeToString(int time)
	{
		//Time must be in hh:mm:ss format
		return (time / 3600)+":"+((time % 3600)/60)+":"+(time % 60);
	}
	public static int StringToTime(String time)
	{
		//Time must be in hh:mm:ss format
		String[] str = time.split(":");
		return Integer.parseInt(str[0])*3600+Integer.parseInt(str[1])*60+Integer.parseInt(str[2]);
	}
}