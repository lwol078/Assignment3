package vamix.GUI;

import java.lang.*;
import javax.swing.*;
import javax.swing.SwingUtilities;

//import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;

public class MainFrame extends JFrame
{
	private JMenuBar menuBar;
    private JMenu menu1,menu2;
    private JMenuItem item1,item2;

	public MainFrame()
	{
		super("Vamix - Title");
		setLocation(100, 100);
        setSize(1050, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        menuBar = new JMenuBar();

        menu1 = new JMenu("Menu1");
        menu1.getAccessibleContext().setAccessibleDescription(
            "The only menu in this program that has menu items");
        menuBar.add(menu1);

        menu2 = new JMenu("Menu2");
         menu1.getAccessibleContext().setAccessibleDescription(
            "Look, a submenu!");
        menu1.add(menu2);

        item1 = new JMenuItem("Look, an item!");
        menu1.add(item1);

        item2 = new JMenuItem("How exciting, I'm in a submenu!");
        menu2.add(item2);

        setJMenuBar(menuBar);
	}
}
