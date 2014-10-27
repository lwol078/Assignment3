package vamix.GUI;

import java.awt.Dimension;

import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * TimePanel
 * @author luke
 * Useful reusable panel for use in FilterGUI
 * Every filter needs one, as all have start/duration 
 */
public class TimePanel extends JPanel 
{
	public boolean valueLock;
	private JLabel labelTime,labelDuration;
	private FilterGUI gui;
	public JSpinner spinHr, spinMin, spinSec, spinDur;

	public TimePanel(FilterGUI gui)
	{
		valueLock = false;
		this.gui = gui;
		labelTime = new JLabel("Time:");
		ChangeListener timeListener = new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) 
			{
				if(!valueLock)
				{
					int hr = (int)spinHr.getValue();
					int min = (int)spinMin.getValue();
					int sec = (int)spinSec.getValue();
					TimePanel.this.gui.GetFilter().startTime = vamix.filter.Filter.ToSeconds(hr, min, sec);
				}
			}
		};
		spinHr = new JSpinner(new SpinnerNumberModel(0,0,60,1));
		spinHr.setMaximumSize(new Dimension(50,20));
		spinHr.addChangeListener(timeListener);
		spinMin = new JSpinner(new SpinnerNumberModel(0,0,60,1));
		spinMin.setMaximumSize(new Dimension(50,20));
		spinMin.addChangeListener(timeListener);
		spinSec = new JSpinner(new SpinnerNumberModel(0,0,60,1));
		spinSec.setMaximumSize(new Dimension(50,20));
		spinSec.addChangeListener(timeListener);




		labelDuration = new JLabel("Duration (s):");
		spinDur = new JSpinner(new SpinnerNumberModel(10,0,null,1));
		spinDur.setMaximumSize(new Dimension(50,20));
		spinDur.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) 
			{
				TimePanel.this.gui.GetFilter().duration = (int)spinDur.getValue();
			}
		});

		//Time panel layout
		GroupLayout timeLayout = new GroupLayout(this);
		setLayout(timeLayout);
		TimeLayout(timeLayout);
	}

	public void TimeLayout(GroupLayout layout)
	{
		JLabel colon1 = new JLabel(":");
		JLabel colon2 = new JLabel(":");
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(labelTime)
						.addComponent(labelDuration)
						)
						.addComponent(spinHr)
						.addComponent(colon1)
						.addComponent(spinMin)
						.addComponent(colon2)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
								.addComponent(spinSec)
								.addComponent(spinDur)
								)

				);
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(labelTime)
						.addComponent(spinHr)
						.addComponent(colon1)
						.addComponent(spinMin)
						.addComponent(colon2)
						.addComponent(spinSec)
						)
						.addGroup(layout.createParallelGroup()
								.addComponent(labelDuration)
								.addComponent(spinDur)
								)

				);
	}
	
	/**
	 * Update(int startTime, int duration)
	 * @param startTime
	 * @param duration
	 * Updates spinners to proper values
	 */
	public void Update(int startTime, int duration)
	{
		//Lock valueLock so spinners dont send msg to update
		//	filter values
		valueLock = true;
		spinHr.setValue(startTime/3600);
		spinMin.setValue((startTime%3600)/60);
		spinSec.setValue(startTime%60);
		spinDur.setValue(duration);
		valueLock = false;
	}
}
