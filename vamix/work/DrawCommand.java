package vamix.work;

import javax.swing.*;
import java.io.*;
import java.util.*;
import java.awt.Point;
import vamix.GUI.TextGUI;

/**	DrawCommand
*	Contains the functionality to overlay text over a portion of a video file. A DrawCommandArgs object must be passed
*	to the constructor. Call Execute() to execute the command and Save() to save the command into a text file
*/
public class DrawCommand
{

	private File sourceFile;
	private String text;
	private Point p;
	private String color;
	private int startTime, duration, size;
	private String outFile;
	private String font;
	private TextGUI gui; 

	private String ext;
	private DrawCommandWorker worker;

	public DrawCommand(DrawCommandArgs args)
	{
		sourceFile = args.sourceFile;
		text = args.text;
		p = args.p;
		color = args.color;
		size = args.size;
		startTime = args.startTime;
		duration = args.duration;
		outFile = args.outFile;
		font = args.fontName;
		gui = args.gui;

		if(sourceFile != null)
		{
			worker = new DrawCommandWorker();
		}
		else
		{
			JOptionPane.showMessageDialog(null,"Please select a valid source file");
		}
	}

	private class DrawCommandWorker extends SwingWorker<Integer,Integer>
	{
		@Override
		protected Integer doInBackground()
		{
			int status = 0;
			String fileName = sourceFile.getName();
			int i = fileName.lastIndexOf('.');
			//Get extension of file
			if (i > 0)
    			ext = fileName.substring(i+1);

			status = step1();
			publish(1);

			if(status != 0)
				return status;
			status = step2();
			publish(2);
			if(status != 0)
				return status;
			status = step3();
			publish(3);
			if(status != 0)
				return status;
			status = step4();
			publish(4);
			return status;
			

		}

		@Override
		protected void done()
		{
			try
			{
				int exitStatus = get();
				if(exitStatus == 0)
					JOptionPane.showMessageDialog(null,"Done");
				else
					JOptionPane.showMessageDialog(null,"Error: exit status "+ exitStatus);
				gui.Completed();
			}
			catch (Exception ex)
			{}
		}
		@Override
		protected void process(List<Integer> chunks)
		{
			if(gui != null)
				gui.setProgress(chunks.get(0)*25);
		}
	}
	/**	Step 1 of the process: get the video before the text is to be overlaid.
	*/
	private int step1()
	{
		int status = 0;
		ProcessBuilder builder;
		List<String> processString = new ArrayList<String>();
		processString.add("avconv");
		processString.add("-i");
		processString.add(sourceFile.getAbsolutePath());
		processString.add("-strict");
		processString.add("experimental");
		processString.add("-y");
		processString.add("-t");
		processString.add(""+startTime);
		processString.add(".out1."+ext);
		try 
		{
			builder = new ProcessBuilder(processString);


			builder.redirectError(new File("log.txt"));
			Process process = builder.start();
			status = process.waitFor();
			process.destroy();
		}
		catch(Exception ex)
		{
			JOptionPane.showMessageDialog(null,"An error has occured\n"+ex.getMessage());
			return -1;
		}
		return status;
	}

	/**	Step 2 of the process: get the video while the text is to be overlaid.
	*/
	private int step2()
	{
		int status = 0;
		ProcessBuilder builder;
		List<String> processString = new ArrayList<String>();
		processString.add("avconv");
		processString.add("-ss");
		processString.add(DrawCommandArgs.TimeToString(startTime));
		processString.add("-i");
		processString.add(sourceFile.getAbsolutePath());
		processString.add("-vf");
		processString.add("drawtext=fontcolor="+color+":fontfile=vamix/fonts/"+font
							+":fontsize="+size+":text='"+text+"':x="+p.x+":y="+p.y);
		processString.add("-strict");
		processString.add("experimental");
		processString.add("-y");
		processString.add("-t");
		processString.add(""+duration);
		processString.add(".out2."+ext);
		try 
		{
			builder = new ProcessBuilder(processString);


			builder.redirectError(new File("log.txt"));
			Process process = builder.start();
			status = process.waitFor();
			process.destroy();
		}
		catch(Exception ex)
		{
			JOptionPane.showMessageDialog(null,"An error has occured\n"+ex.getMessage());
			return -1;
		}
		return status;
	}

	/**	Step 3 of the process: get the video after the text is to be overlaid.
	*/
	private int step3()
	{
		int status = 0;
		ProcessBuilder builder;
		List<String> processString = new ArrayList<String>();
		processString = new ArrayList<String>();

		processString.add("avconv");
		processString.add("-ss");
		processString.add(DrawCommandArgs.TimeToString(duration+startTime));
		processString.add("-i");
		processString.add(sourceFile.getAbsolutePath());
		processString.add("-y");
		processString.add("-strict");
		processString.add("experimental");
		processString.add(".out3."+ext);

		try 
		{
			builder = new ProcessBuilder(processString);


			builder.redirectError(new File("log.txt"));
			Process process = builder.start();
			status = process.waitFor();
			process.destroy();
		}
		catch(Exception ex)
		{
			JOptionPane.showMessageDialog(null,"An error has occured\n"+ex.getMessage());
			return -1;
		}
		return status;
	}

	/**	Step 4 of the process: concatenate the videos from steps 1-3.
	*/
	private int step4()
	{
		int status = 0;
		String cmd = "";
		ProcessBuilder builder;

		if(ext.equals("mp4"))
		{
			cmd = "avconv -y -i .out1.mp4 -c copy -bsf:v h264_mp4toannexb -f mpegts .intermediate1.ts\n"
					+"avconv -y -i .out2.mp4 -c copy -bsf:v h264_mp4toannexb -f mpegts .intermediate2.ts\n"
					+"avconv -y -i .out3.mp4 -c copy -bsf:v h264_mp4toannexb -f mpegts .intermediate3.ts\n"
					+"avconv -y -i \"concat:.intermediate1.ts|.intermediate2.ts|.intermediate3.ts\" -c copy -bsf:a aac_adtstoasc '"+outFile+".mp4'";
		}
		else
		{
			cmd = "avconv -y -i concat:.out1."+ext+"\\|.out2."+ext+"\\|.out3."+ext+" -c copy '"+outFile+"."+ext+"'";
		}

		try 
		{
			builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			builder.redirectError(new File("log.txt"));
			Process process = builder.start();
			status = process.waitFor();
			process.destroy();
		}
		catch(Exception ex)
		{
			JOptionPane.showMessageDialog(null,"An error has occured\n"+ex.getMessage());
			return -1;
		}
		return status;
	}

	public void Save(DrawCommandArgs args, File file)
	{
		try
		{
			FileWriter saveWriter = new FileWriter(file.getAbsolutePath());
			saveWriter.write(args.ToText());
			saveWriter.close();
		}
		catch(Exception err)
		{
			JOptionPane.showMessageDialog(null,"Error "+err.getMessage());
		}
	}
	public void Execute()
	{
		if(worker != null)
			worker.execute();
	}
}