package vamix.GUI;

import java.lang.*;
import javax.swing.*;
import javax.swing.SwingUtilities;

//import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;

public class MainFrame extends JFrame
{
	private JPanel panel;

	public MainFrame()
	{
		super("Vamix - Title");
		setLocation(100, 100);
        setSize(1050, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        panel = new JPanel();
        this.add(panel);
	}
}
