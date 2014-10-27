package vamix.filter;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;


public class Negate extends Filter {

	public Negate(Project p, String name)
	{
		super(p,name,Type.NEGATE);
	}
	
	@Override
	public String FilterString() 
	{
		int end = startTime+duration;
		//Step One: split video into 3 
		String str = "split=3[split_main][split_neg][split_end];";
		//Step Two: trim negate portion"
		str+="[split_neg]select=gt(t\\,"+startTime+")*lt(t\\,"+end+"),negate[neg];";
		//Step Three: tverlay negation over main
		str+="[split_main][neg]overlay[neg2];";
		//Step Four: trim end portion
		str+="[split_end]select=gt(t\\,"+end+")[end2];";
		//Step Five: overlay end portion
		str+="[neg2][end2]overlay";
		return str;
	}

	@Override
	public String SaveText() 
	{
		String str = "NEGATE\n";
		str += name+"\n";
		str += TimeToString(startTime)+"\n";
		str += duration+"\n";
		return str;
	}
	
	public static Negate Load(Project p, BufferedReader openReader) throws IOException
	{
		String name = openReader.readLine();
		Negate n = new Negate(p,name);
		n.startTime = StringToTime(openReader.readLine());
		n.duration = Integer.parseInt(openReader.readLine());
		
		return n;
	}

}
