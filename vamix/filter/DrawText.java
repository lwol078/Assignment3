package vamix.filter;

import java.awt.Color;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import vamix.GUI.TextGUI;
import vamix.work.*;

public class DrawText extends Filter 
{
	public String text;
	public String fontName;
	public Point p;
	public Color color;
	public int size;
	
	public DrawText(Project project, String name)
	{
		super(project, name, Type.DRAWTEXT);
		text = "wooglywooglywoo";
		fontName = "";
		p = new Point(0,0);
		color = StringToColor("000000");
		size = 20;
	}
	
	@Override
	public String FilterString()
	{
		String filter;
		int end = startTime+duration;
		filter = "drawtext=fontcolor="+ColorToString(color)+":fontfile="+fontName
				+":fontsize="+size+":text='"+text+"':x="+p.x+":y="+p.y+":draw='gt(t,"+startTime+")*lt(t,"+end+")'";
		return filter;
	}

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
	
	public static String ColorToString(Color color)
	{
		String str = Integer.toHexString(color.getRGB());
		String alpha = null;
		if(str.length() > 6)
			{
				alpha = str.substring(0,2);
				str = str.substring(2);
			}
		while(str.length() < 6)
			str = "0"+str;
		if(alpha != null)
			str+=alpha;
		return str;
	}
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
