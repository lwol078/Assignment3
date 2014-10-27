package vamix.filter;

import java.awt.Color;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import vamix.GUI.FilterGUI;
import vamix.work.*;

/**
 * DrawText
 * @author luke
 *Drawtext filter - draws text at a specified position with a certain format
 *Also contains some utility functions
 */
public class DrawText extends Filter 
{
	public String text;
	public String fontName;
	public Point p;
	public Color color;
	public int size;
	public static String defaultFont = "";
	
	public DrawText(Project project, String name)
	{
		super(project, name, Type.DRAWTEXT);
		text = "wooglywooglywoo";
		this.fontName = defaultFont;
		p = new Point(0,0);
		color = StringToColor("000000");
		size = 20;
	}
	
	/**
	 * FilterString()
	 * Returns the filter string to be added to the filter chain by a FilterCommand
	 */
	@Override
	public String FilterString()
	{
		String filter;
		int end = startTime+duration;
		filter = "drawtext=fontcolor="+ColorToString(color)+":fontfile="+fontName
				+":fontsize="+size+":text='"+text+"':x="+p.x+":y="+p.y+":draw='gt(t,"+startTime+")*lt(t,"+end+")'";
		return filter;
	}
	
	/**
	 * SaveText()
	 * Returns a string representing this filter which can be saved to a project file
	 */
	@Override
	public String SaveText() 
	{
		String str = "DRAWTEXT\n";
		str += name+"\n";
		str += text+"\n";
		str += p.x+" "+p.y+"\n";
		str += fontName +"\n";
		str += ColorToString(color)+"\n";
		str += size+"\n";
		str += TimeToString(startTime)+"\n";
		str += duration+"\n";
		return str;
	}
	
	/**
	 * ColorToString(Color color)
	 * @param color the color to be converted
	 * @return a string representation of color that can be used by avconv (RRGGBBFF)
	 */
	public static String ColorToString(Color color)
	{
		String str = Integer.toHexString(color.getRGB());
		String alpha = null;
		if(str.length() > 6)
			{
				//has alpha bits, so is FFRRGGBB
				//alpha = FF, str = RRGGBB
				alpha = str.substring(0,2);
				str = str.substring(2);
			}
		//Append necessary 0s
		while(str.length() < 6)
			str = "0"+str;
		if(alpha != null)
			str+=alpha;
		return str;
	}
	/**
	 * StringToColor(String str)
	 * @param str string to be converted: form RRGGBBFF (avconv form)
	 * @return Color representation of string
	 */
	public static Color StringToColor(String str)
	{
		String alpha = null;
		if(str.length() > 6)
			{
				alpha = str.substring(6);
				str = str.substring(0,6);
			}
		str = "ff"+str;
		if(alpha != null)
			str = alpha+str;
		return new Color((int)Long.parseLong(str,16), true);
	}
	
	/**
	 * Load(Project p, BufferedReader openReader)
	 * @param p filter's parent project
	 * @param openReader reader to get lines from
	 * @return a new DrawText filter read from the lines of the file 
	 * @throws IOException
	 */
	public static DrawText Load(Project p, BufferedReader openReader) throws IOException
	{
		String name = openReader.readLine();
		DrawText d = new DrawText(p,name);
		d.text = openReader.readLine();
		String str = openReader.readLine();
		String[] point = str.split(" ");
		d.p = new Point(Integer.parseInt(point[0]), Integer.parseInt(point[1]));
		d.fontName = openReader.readLine();
		d.color = StringToColor(openReader.readLine());
		d.size = Integer.parseInt(openReader.readLine());
		d.startTime = StringToTime(openReader.readLine());
		d.duration = Integer.parseInt(openReader.readLine());
		
		return d;
	}
}
