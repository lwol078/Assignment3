package vamix.work;

import java.awt.Point;
import java.io.File;

import vamix.GUI.TextGUI;

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
}