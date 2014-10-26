package vamix.filter;

import vamix.work.*;

public abstract class Filter 
{
	private Project parent;
	public String name;
	public int startTime, duration;
	public enum Type {DRAWTEXT, FADE, FLIP};
	public final Type type;
	
	public Filter(Project project, String name, Type type)
	{
		parent = project;
		parent.AddFilter(this);
		this.name = name;
		startTime = 0;
		duration = 10;
		this.type = type;
	}
	
	public abstract String FilterString();
	
	@Override
	public String toString()
	{
		return name;
	}
	
	public abstract String SaveText();
	
	public static int ToSeconds(int hr, int min, int sec)
	{
		return 3600*hr+60*min+sec;
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
