package vamix.GUI;

import javax.swing.JPanel;

import vamix.filter.DrawText;
import vamix.filter.Negate;

/**NegatePanel
 * @author luke
 * Panel which contains components to set/get Negate options
 */
public class NegatePanel extends JPanel 
{
	private FilterGUI parent;
	private TimePanel timePanel;
	
	public NegatePanel(FilterGUI gui)
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
