package vamix.work;

import javax.swing.*;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;

public class JHoldButton extends JButton
{
	private Timer timer;

	public JHoldButton(String s)
	{
		super(s);
	}

	private class HoldTask extends TimerTask
	{
		@Override
		public void run()
		{
			doClick();
		}
	}
	
	@Override
	protected void processMouseEvent(MouseEvent e)
	{
		if(e.getID() == MouseEvent.MOUSE_PRESSED)
		{
			timer = new Timer();
			timer.schedule(new HoldTask(), 0l, 10l);
		}
		else if(e.getID() == MouseEvent.MOUSE_RELEASED)
		{
			timer.cancel();
		}
	}

}
