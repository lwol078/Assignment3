package vamix.work;

import java.awt.Point;
import java.io.File;

public class DrawCommandArgs
{
	public File sourceFile;
	public String text;
	public Point p;
	public String color;
	public int startTime, duration, size;
	public String outFile;

	public DrawCommandArgs()
	{
		sourceFile = null;
		text = "";
		p = new Point(0,0);
		color = "white";
		startTime = 0;
		duration = 0;
		size = 0;
		outFile = "output";
	}
}