package vamix.GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import vamix.filter.DrawText;

public class DrawTextPanel extends JPanel 
{
	private TextGUI parent;
	private JLabel labelFont,labelColor,labelX,labelY,labelText,labelTime,labelDuration, labelSize;
	private JComboBox<String> comboFont;
	private JSpinner spinX, spinY, spinHr, spinMin, spinSec, spinDur, spinSize;
	private JTextField text;
	private JButton btnColor;

	private JPanel formatPanel, positionPanel, timePanel;
	private Color selectedColor;

	public DrawTextPanel(TextGUI argParent)
	{
		parent = argParent;
		selectedColor = new Color(0,0,0);

		formatPanel = new JPanel();
		positionPanel = new JPanel();
		timePanel = new JPanel();

		GroupLayout formatLayout = new GroupLayout(formatPanel);
		formatPanel.setLayout(formatLayout);
		GroupLayout posLayout = new GroupLayout(positionPanel);
		positionPanel.setLayout(posLayout);
		GroupLayout timeLayout = new GroupLayout(timePanel);
		timePanel.setLayout(timeLayout);

		labelFont = new JLabel("Font:");
		//Load fonts from file
		Vector<String> fonts = new Vector<String>();
		final File fontFolder = new File("vamix/fonts");
		for (final File file : fontFolder.listFiles())
		{
			if (!file.isDirectory())
			{
				fonts.add(file.getName());
			}
		}

		comboFont = new JComboBox<String>(fonts);
		if(comboFont.getItemCount()!= 0)
			comboFont.setSelectedIndex(0);
		comboFont.setMaximumSize(new Dimension(100,20));
		comboFont.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent arg0) 
			{
				((DrawText)(parent.GetFilter())).fontName = (String)comboFont.getSelectedItem();
			}
		});

		labelColor = new JLabel("Color:");
		btnColor = new JButton("Select");
		btnColor.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				Color newColor = JColorChooser.showDialog(
						null,
						"Choose Text Color",
						selectedColor);
				if(newColor != null)
					selectedColor = newColor;
			}
		});

		labelSize = new JLabel("Size:");
		spinSize = new JSpinner(new SpinnerNumberModel(10,1,50,1));
		spinSize.setMaximumSize(new Dimension(30,20));

		labelX = new JLabel("x:");
		spinX = new JSpinner(new SpinnerNumberModel(0,0,100,1));
		spinX.setMaximumSize(new Dimension(30,20));

		labelY = new JLabel("y:");
		spinY = new JSpinner(new SpinnerNumberModel(0,0,100,1));
		spinY.setMaximumSize(new Dimension(30,20));


		text = new JTextField(40);
		text.setMaximumSize(new Dimension(335,40));
		text.setDocument(new PlainDocument()
		{
			public void insertString(int offs, String str, AttributeSet a) throws BadLocationException
			{
				if (str == null)
					return;

				if ((getLength() + str.length()) <= 40)
					super.insertString(offs, str, a);
			}

		});

		labelTime = new JLabel("Time:");
		spinHr = new JSpinner(new SpinnerNumberModel(0,0,60,1));
		spinHr.setMaximumSize(new Dimension(50,20));
		spinMin = new JSpinner(new SpinnerNumberModel(0,0,60,1));
		spinMin.setMaximumSize(new Dimension(50,20));
		spinSec = new JSpinner(new SpinnerNumberModel(0,0,60,1));
		spinSec.setMaximumSize(new Dimension(50,20));

		labelDuration = new JLabel("Duration (s):");
		spinDur = new JSpinner(new SpinnerNumberModel(0,0,null,1));
		spinDur.setMaximumSize(new Dimension(50,20));

		//Format panel layout
		FormatLayout(formatLayout);

		//Position panel layout
		PositionLayout(posLayout);

		//Time panel layout
		TimeLayout(timeLayout);
		
		//Format overall panel
		GroupLayout layout = new GroupLayout(this);
		Layout(layout);

		Border etched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		formatPanel.setBorder(BorderFactory.createTitledBorder(etched,"Format"));
		positionPanel.setBorder(BorderFactory.createTitledBorder(etched,"Position"));
		text.setBorder(BorderFactory.createTitledBorder(etched,"Text (Max 40 characters):"));
		timePanel.setBorder(BorderFactory.createTitledBorder(etched,"Time"));
	}
	
	public void FormatLayout(GroupLayout layout)
	{
		layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(
        	layout.createSequentialGroup()
        		.addGroup(layout.createParallelGroup()
    				.addComponent(labelFont)
    				.addComponent(labelColor)
    				.addComponent(labelSize)
    				)
    			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
    				.addComponent(comboFont)
    				.addComponent(btnColor)
    				.addComponent(spinSize)
    				)
    		);
        layout.setVerticalGroup(
        	layout.createSequentialGroup()
        		.addGroup(layout.createParallelGroup()
    				.addComponent(labelFont)
    				.addComponent(comboFont)
    				)
    			.addGroup(layout.createParallelGroup()
    				.addComponent(labelColor)
    				.addComponent(btnColor)
    				)
    			.addGroup(layout.createParallelGroup()
    				.addComponent(labelSize)
    				.addComponent(spinSize)
    				)
    		);
	}

	public void PositionLayout(GroupLayout layout)
	{
		layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(
        	layout.createSequentialGroup()
        		.addComponent(labelX)
        		.addComponent(spinX)
        		.addComponent(labelY)
        		.addComponent(spinY)
        	);
        layout.setVerticalGroup(
        	layout.createParallelGroup()
        		.addComponent(labelX)
        		.addComponent(spinX)
        		.addComponent(labelY)
        		.addComponent(spinY)
        	);
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
	
	public void Layout(GroupLayout layout)
	{
		layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(
        	layout.createParallelGroup()
        		.addComponent(formatPanel)
				.addComponent(positionPanel)
				.addComponent(text)
				.addComponent(timePanel)
        	);
        layout.setVerticalGroup(
        	layout.createSequentialGroup()
    			.addComponent(formatPanel)
    			.addComponent(positionPanel)
    			.addComponent(text)
    			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING))
    			.addComponent(timePanel)
        	);
	}
	
	public void SetOptions(DrawText f)
	{
		String name = f.name;
		if(name.length() > 20)
			name = "..."+name.substring(name.length()-20);
		//labelSource.setText(name);
		text.setText(f.text);
		spinX.setValue(f.p.x);
		spinY.setValue(f.p.y);
		selectedColor = DrawText.StringToColor(f.color.substring(2));
		spinHr.setValue(f.startTime/3600);
		spinMin.setValue((f.startTime%3600)/60);
		spinSec.setValue(f.startTime%60);
		spinDur.setValue(f.duration);
		spinSize.setValue(f.size);
		comboFont.setSelectedItem(f.fontName);
	}
}
