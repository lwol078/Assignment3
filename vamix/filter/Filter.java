package vamix.filter;

import vamix.work.*;

/**
 * Filter
 * @author luke
 *Abstract class to be extended by filters such as DrawText
 */
public abstract class Filter 
{
	private Project parent;
	public String name;
	public int startTime, duration;
	public enum Type {DRAWTEXT, /*FADE,*/ NEGATE};
	public final Type type;
	
	public Filter(Project project, String name, Type type)
	{
		parent = project;
		this.name = name;
		startTime = 0;
		duration = 10;
		this.type = type;
	}
	
	/**
	 * FilterString
	 * @return String which represents filter, to be given to FilterCommand
	 * Should be overwritten by extending classes
	 */
	public abstract String FilterString();
	
	/**
	 * toString()
	 * return name for JList
	 */
	@Override
	public String toString()
	{
		return name;
	}
	
	/**
	 * SaveText()
	 * @return String representing filter that can be written to a file, then read later
	 */
	public abstract String SaveText();
	
	/**
	 * ToSeconds(int hr, int min, int sec)
	 * @param hr hours
	 * @param min minutes
	 * @param sec seconds
	 * @return time in seconds form
	 */
	public static int ToSeconds(int hr, int min, int sec)
	{
		return 3600*hr+60*min+sec;
	}
	
	/**String TimeToString(int time)
	 * @param time time to convert in seconds
	 * @return time converted to string form hh:mm:ss
	 */
	public static String TimeToString(int time)
	{
		//Time must be in hh:mm:ss format
		String hrs = ""+(time/3600);
		String mins = ""+((time % 3600)/60);
		String secs = ""+(time % 60);
		while(hrs.length() < 2)
			hrs= "0"+hrs;
		while(mins.length() < 2)
			mins= "0"+mins;
		while(secs.length() < 2)
			secs= "0"+secs;
		return hrs+":"+mins+":"+secs;
	}
	
	/**StringToTime(String time)
	 * @param time String of form hh:mm:ss
	 * @return time in seconds
	 */
	public static int StringToTime(String time)
	{
		//Time must be in hh:mm:ss format
		String[] str = time.split(":");
		return Integer.parseInt(str[0])*3600+Integer.parseInt(str[1])*60+Integer.parseInt(str[2]);
	}
}
