package vamix.filter;

import vamix.work.*;

public abstract class Filter 
{
	private Project parent;
	public String name;
	public int startTime, duration;
	public enum Type {DrawText};
	public final Type type;
	
	public Filter(Project project, String name, Type type)
	{
		parent = project;
		parent.AddFilter(this);
		this.name = name;
		startTime = 0;
		duration = 0;
		this.type = type;
	}
	
	public abstract String FilterString();
	
	@Override
	public String toString()
	{
		return name;
	}
	
	public abstract String SaveText();
}
