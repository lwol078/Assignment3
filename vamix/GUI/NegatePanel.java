package vamix.GUI;

import javax.swing.JPanel;

import vamix.filter.DrawText;
import vamix.filter.Negate;

public class NegatePanel extends JPanel 
{
	private TextGUI parent;
	private TimePanel timePanel;
	
	public NegatePanel(TextGUI gui)
	{
		parent = gui;
		timePanel = new TimePanel(gui);
		add(timePanel);
	}
	
	public void SetValues(Negate n)
	{
		int hr = (int)timePanel.spinHr.getValue();
		int min = (int)timePanel.spinMin.getValue();
		int sec = (int)timePanel.spinSec.getValue();
		n.startTime = vamix.filter.Filter.ToSeconds(hr, min, sec);
		n.duration = (int)timePanel.spinDur.getValue();
	}
	
	public void SetOptions(Negate f)
	{
		timePanel.Update(f.startTime, f.duration);
	}
}
