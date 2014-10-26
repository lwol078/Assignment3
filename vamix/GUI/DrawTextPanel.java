package vamix.GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
	private Vector<String> fonts;
	private File fontFile;

	public DrawTextPanel(TextGUI argParent)
	{
		//setMaximumSize(new Dimension(300,600));
		parent = argParent;
		selectedColor = new Color(0,0,0);

		formatPanel = new JPanel();
		positionPanel = new JPanel();
		timePanel = new JPanel();

		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		GroupLayout formatLayout = new GroupLayout(formatPanel);
		formatPanel.setLayout(formatLayout);
		GroupLayout posLayout = new GroupLayout(positionPanel);
		positionPanel.setLayout(posLayout);
		GroupLayout timeLayout = new GroupLayout(timePanel);
		timePanel.setLayout(timeLayout);

		labelFont = new JLabel("Font:");

		fonts = new Vector<String>();
		fontFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getFile());
		if(fontFile.getName().endsWith(".jar"))
			fontFile = new File(fontFile.getParent());
		fontFile = new File(fontFile.getAbsolutePath()+"/vamix/fonts");
		for(File f : fontFile.listFiles())
		{
			fonts.add(f.getName());
		}

		comboFont = new JComboBox<String>(fonts);
		comboFont.setSelectedIndex(comboFont.getItemCount()-1);
		comboFont.setMaximumSize(new Dimension(100,20));
		comboFont.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent arg0) 
			{

				Filter().fontName = (String)comboFont.getSelectedItem();
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
				{
					selectedColor = newColor;
					Filter().color = selectedColor;
					text.setForeground(selectedColor);
					text.setBackground(Inverse(selectedColor));
				}
			}
		});

		labelSize = new JLabel("Size:");
		spinSize = new JSpinner(new SpinnerNumberModel(10,1,50,1));
		spinSize.setMaximumSize(new Dimension(30,20));

		labelX = new JLabel("x:");
		spinX = new JSpinner(new SpinnerNumberModel(0,0,100,1));
		spinX.setMaximumSize(new Dimension(30,20));
		spinX.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) 
			{
				Filter().p.x = (int)spinX.getValue();
			}
		});

		labelY = new JLabel("y:");
		spinY = new JSpinner(new SpinnerNumberModel(0,0,100,1));
		spinY.setMaximumSize(new Dimension(30,20));
		spinY.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) 
			{
				Filter().p.y = (int)spinY.getValue();
			}
		});


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
		text.addCaretListener(new CaretListener(){
			@Override
			public void caretUpdate(CaretEvent arg0) 
			{
				Filter().text = text.getText();
			}
		});
		text.setForeground(selectedColor);
		text.setBackground(Inverse(selectedColor));

		labelTime = new JLabel("Time:");
		ChangeListener timeListener = new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) 
			{
				int hr = (int)spinHr.getValue();
				int min = (int)spinHr.getValue();
				int sec = (int)spinHr.getValue();
				Filter().startTime = vamix.filter.Filter.ToSeconds(hr, min, sec);}
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
				Filter().duration = (int)spinDur.getValue();
			}
		});

		//Format panel layout
		FormatLayout(formatLayout);

		//Position panel layout
		PositionLayout(posLayout);

		//Time panel layout
		TimeLayout(timeLayout);

		//Format overall panel
		Layout(layout);

		formatPanel.setBorder(BorderFactory.createTitledBorder("Format"));
		positionPanel.setBorder(BorderFactory.createTitledBorder("Position"));
		text.setBorder(BorderFactory.createTitledBorder("Text (Max 40 characters):"));
		timePanel.setBorder(BorderFactory.createTitledBorder("Time"));
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
				//.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING))
				.addComponent(timePanel)
				);
	}

	public void SetOptions(DrawText f)
	{
		text.setText(f.text);
		spinX.setValue(f.p.x);
		spinY.setValue(f.p.y);
		selectedColor = f.color;
		if(selectedColor != null)
		{
			text.setForeground(selectedColor);
			text.setBackground(Inverse(selectedColor));
		}
		spinHr.setValue(f.startTime/3600);
		spinMin.setValue((f.startTime%3600)/60);
		spinSec.setValue(f.startTime%60);
		spinDur.setValue(f.duration);
		spinSize.setValue(f.size);
		comboFont.setSelectedItem(f.fontName);
	}
	public DrawText Filter()
	{
		return ((DrawText)(parent.GetFilter()));
	}

	public static Color Inverse(Color c)
	{
		float[] rgb = new float[4];
		c.getRGBComponents(rgb);
		for(int i = 0; i < 3; i++)
			rgb[i] = 1-rgb[i];
		//if middle grey, creates the same grey, so return black instead
		if(Math.abs(rgb[0]-0.5) < 0.1 && Math.abs(rgb[1]-0.5) < 0.1 && Math.abs(rgb[2]-0.5) < 0.1)
			return new Color(0,0,0);
		return new Color(rgb[0],rgb[1],rgb[2]);
	}

}
