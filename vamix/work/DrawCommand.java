package vamix.work;

import javax.swing.*;
import java.io.*;
import java.util.*;
import java.awt.Point;

public class DrawCommand
{

	private File sourceFile;
	private String text;
	private Point p;
	private String color;
	private int startTime, duration, size;
	private String outFile;

	private String ext;
	private DrawCommandWorker worker;

	public DrawCommand(DrawCommandArgs args)
	{
		sourceFile = args.sourceFile;
		text = args.text;//"blahblah";
		p = args.p;//new Point(0,100);
		color = args.color;//"green";
		size = args.size;//40;
		startTime = args.startTime;//5;
		duration = args.duration;//12;
		if(sourceFile != null)
		{
			worker = new DrawCommandWorker();
			worker.execute();
		}
	}

	private class DrawCommandWorker extends SwingWorker<Integer,Void>
	{
		@Override
		protected Integer doInBackground()
		{
			int status = 0;
			outFile = "Output file";
			String fileName = sourceFile.getName();
			int i = fileName.lastIndexOf('.');
			if (i > 0)
    			ext = fileName.substring(i+1);

			status = step1();

			if(status != 0)
				return status;
			status = step2();
			if(status != 0)
				return status;
			status = step3();
			if(status != 0)
				return status;
			status = step4();
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
			}
			catch (Exception ex)
			{}
		}
	}

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

	private int step2()
	{
		int status = 0;
		ProcessBuilder builder;
		List<String> processString = new ArrayList<String>();
		processString.add("avconv");
		processString.add("-ss");
		processString.add(TimeToString(startTime));
		processString.add("-i");
		processString.add(sourceFile.getAbsolutePath());
		processString.add("-vf");
		processString.add("drawtext=fontcolor="+color+":fontfile=/usr/share/fonts/truetype/ubuntu-font-family/Ubuntu-L.ttf"
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

	private int step3()
	{
		int status = 0;
		ProcessBuilder builder;
		List<String> processString = new ArrayList<String>();
		processString = new ArrayList<String>();

		processString.add("avconv");
		processString.add("-ss");
		processString.add(TimeToString(duration+startTime));
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

	public String TimeToString(int time)
	{
		return (time / 3600)+":"+((time % 3600)/60)+":"+(time % 60);
	}
	public int StringToTime(String time)
	{
		String[] str = time.split(":");

		return Integer.parseInt(str[0])*3600+Integer.parseInt(str[1])*60+Integer.parseInt(str[3]);
	}
}