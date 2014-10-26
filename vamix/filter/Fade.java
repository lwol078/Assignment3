package vamix.filter;

import vamix.work.Project;

public class Fade extends Filter 
{
	private int fps;
	
	public Fade(Project project, String name) 
	{
		super(project, name, Type.FADE);
		fps = project.GetFPS();
		System.out.print(fps);
	}

	public enum FadeType {IN, OUT};
	public FadeType fadeType;
	@Override
	public String FilterString() 
	{
		String filter = "";
		int end = startTime+duration;
		return filter;
	}

	@Override
	public String SaveText() 
	{
		return null;
	}

}
