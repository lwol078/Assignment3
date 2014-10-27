package vamix.GUI;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import vamix.filter.Filter;
import vamix.work.Subtitle;

public class SubtitleFrame extends JFrame
{
	private JPanel timePanel, mainPanel;
	private JTextField text;
	private JLabel labelText, labelTime,labelDuration;
	private JButton btnAdd,btnRemove,btnSave,btnTime;
	private JSpinner spinHr, spinMin, spinSec, spinDur;
	private MainFrame parent;
	private File inFile, srtFile;
	private JScrollPane scroll;
	private ArrayList<Subtitle> subtitles;
	private JList<Subtitle> subtitleList;
	private DefaultListModel<Subtitle> listModel;
	private Subtitle currentSubtitle;
	private boolean valueLock;

	public SubtitleFrame(MainFrame parent, File file)
	{
		this.parent = parent;
		inFile = file;
		setVisible(true);

		subtitleList = new JList<Subtitle>();
		currentSubtitle = null;
		listModel = new DefaultListModel<Subtitle>();
		subtitleList.setModel(listModel);
		String srtStr = inFile.getAbsolutePath();
		srtStr = srtStr.substring(0,srtStr.lastIndexOf('.'));
		srtFile = new File(srtStr+".srt");
		valueLock = false;
		
		subtitles = new ArrayList<Subtitle>();
		if(srtFile.exists())
		{
			//Load existing srt file
			subtitles = Subtitle.Load(srtFile);
			UpdateList(null);
		}
		subtitleList.addListSelectionListener(new ListSelectionListener(){
			@Override
			public void valueChanged(ListSelectionEvent arg0) 
			{
				currentSubtitle = subtitleList.getSelectedValue();
				if(currentSubtitle != null)
					SetOptions(currentSubtitle);
			}
		});

		mainPanel = new JPanel();
		timePanel = new JPanel();
		setContentPane(mainPanel);
		setResizable(false);

		btnAdd = new JButton("Add Subtitle");
		btnAdd.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				Subtitle temp = new Subtitle();
				temp.text = "Sample text";
				temp.endTime = 5;
				listModel.addElement(temp);
				subtitles.add(temp);
				UpdateList(temp);
			}
		});
		
		btnRemove = new JButton("Remove Subtitle");
		btnRemove.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				subtitles.remove(currentSubtitle);
				listModel.removeElement(currentSubtitle);
				UpdateList(null);
			}
		});
		btnSave = new JButton("Save subtitles");
		btnSave.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				try 
				{
					FileWriter writer = new FileWriter(srtFile);
					writer.write("");
					for(int i = 0; i < subtitles.size(); i++)
					{
						writer.append(i+"\n");
						Subtitle s = subtitles.get(i);
						String str = Filter.TimeToString(s.startTime)+",000 --> ";
						str+= Filter.TimeToString(s.endTime)+",000\n";
						writer.append(str);
						writer.append(s.text);
						writer.append("\n\n");
					}
					writer.close();
				} 
				catch (IOException e1) 
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		btnTime = new JButton("Get time from playback");
		btnTime.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				int time = (int)SubtitleFrame.this.parent.GetMediaPlayer().getTime()/1000;
				spinHr.setValue(time/3600);
				spinMin.setValue((time%3600)/60);
				spinSec.setValue(time%60);
			}
		});

		labelText = new JLabel("Text:");
		text = new JTextField();
		text.setPreferredSize(new Dimension(200,60));
		text.setMaximumSize(new Dimension(200,40));
		text.setDocument(new PlainDocument()
		{
			public void insertString(int offs, String str, AttributeSet a) throws BadLocationException
			{
				if (str == null)
					return;

				if ((getLength() + str.length()) <= 40)
					super.insertString(offs, str, a);
				String str2 = text.getText(); 
				if(str2.contains("\n\n"))
					text.setText(str2.substring(0, str2.indexOf("\n\n")));
			}

		});
		text.addCaretListener(new CaretListener(){
			@Override
			public void caretUpdate(CaretEvent arg0) 
			{
				if(currentSubtitle != null)
					currentSubtitle.text = text.getText();
			}
		});
		
		scroll = new JScrollPane(subtitleList);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		labelTime = new JLabel("Time:");
		ChangeListener timeListener = new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) 
			{
				if(currentSubtitle != null && !valueLock)
				{
					int hr = (int)spinHr.getValue();
					int min = (int)spinMin.getValue();
					int sec = (int)spinSec.getValue();
					currentSubtitle.startTime = vamix.filter.Filter.ToSeconds(hr, min, sec);
					currentSubtitle.endTime = (int)spinDur.getValue()+currentSubtitle.startTime;
					UpdateList(currentSubtitle);
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
		spinDur = new JSpinner(new SpinnerNumberModel(0,0,null,1));
		spinDur.setMaximumSize(new Dimension(50,20));
		spinDur.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) 
			{
				if(currentSubtitle != null)
					currentSubtitle.endTime = (int)spinDur.getValue()+currentSubtitle.startTime;
			}
		});

		GroupLayout layout = new GroupLayout(mainPanel);
		mainPanel.setLayout(layout);
		Layout(layout);
		GroupLayout timeLayout = new GroupLayout(timePanel);
		timePanel.setLayout(timeLayout);
		TimeLayout(timeLayout);
		pack();
	}

	private void Layout(GroupLayout layout)
	{
		layout.setHorizontalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(scroll)
						.addGroup(layout.createSequentialGroup()
								.addComponent(btnAdd)
								.addComponent(btnRemove)
								)
								.addComponent(btnSave)
						)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
								.addComponent(timePanel)
								.addGroup(layout.createSequentialGroup()
										.addComponent(labelText)
										.addComponent(text)
										)
										.addComponent(btnTime)
								)
				);
		layout.setVerticalGroup(
				layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
						.addComponent(scroll)
						.addGroup(layout.createParallelGroup()
								.addComponent(btnAdd)
								.addComponent(btnRemove)
								)
								.addComponent(btnSave)
						)
						.addGroup(layout.createSequentialGroup()
								.addComponent(timePanel)
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
										.addComponent(labelText)
										.addComponent(text)
										)
										.addComponent(btnTime)
								)
				);
	}

	private void TimeLayout(GroupLayout layout)
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

	private void SetOptions(Subtitle subtitle)
	{
		if(currentSubtitle != null)
		{
		//prevent options from changing subtitle settings
		valueLock = true;
		spinDur.setValue(subtitle.endTime-subtitle.startTime);
		spinHr.setValue(subtitle.startTime/3600);
		spinMin.setValue((subtitle.startTime%3600)/60);
		spinSec.setValue(subtitle.startTime%60);
		text.setText(subtitle.text);
		valueLock = false;
		}
	}
	
	private void UpdateList(Subtitle newCurrent)
	{
		listModel.clear();
		Collections.sort(subtitles);
		for(Subtitle s : subtitles)
			if(s.text.length() > 0)
				listModel.addElement(s);
		if(listModel.contains(newCurrent))
			{
			currentSubtitle = newCurrent;
			subtitleList.setSelectedValue(newCurrent, true);
			}
		else
			currentSubtitle = null;
	}
}
