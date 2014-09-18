package vamix.GUI;

import java.lang.*;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.event.*;

import javax.swing.*;

import vamix.work.*;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;

public class MainFrame extends JFrame implements ActionListener
{
	private JMenuBar menuBar;
    private JMenu menu1,menu2,downloadMenu;
    private JMenuItem openItem,item2, downloadAudio;
    private JDesktopPane desktop;
    private JPanel playerPanel;

    private EmbeddedMediaPlayerComponent mediaPlayerComponent;

	public MainFrame() 
	{
		super("Vamix - Title");
		setLocation(100, 100);
        setSize(1050, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        menuBar = new JMenuBar();
        desktop = new JDesktopPane();
        setContentPane(desktop);
        desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
        
        playerPanel = new JPanel();
        playerPanel.setBounds(0, 0, 1050, 600);
        playerPanel.setLayout(new BorderLayout());
        playerPanel.setVisible(true);
        desktop.setLayer( playerPanel, JDesktopPane.DEFAULT_LAYER );
        desktop.add(playerPanel);

        menu1 = new JMenu("Menu1");
        menu1.getAccessibleContext().setAccessibleDescription(
            "The only menu in this program that has menu items");
        menuBar.add(menu1);

        menu2 = new JMenu("Menu2");
         menu1.getAccessibleContext().setAccessibleDescription(
            "Look, a submenu!");
        menu1.add(menu2);

        openItem = new JMenuItem("Open");
        openItem.addActionListener(this);
        menu1.add(openItem);

        item2 = new JMenuItem("How exciting, I'm in a submenu!");
        menu2.add(item2);
        
        downloadMenu = new JMenu("Download");
        downloadMenu.getAccessibleContext().setAccessibleDescription(
                "Menu option for downloading files");
        menuBar.add(downloadMenu);
        
        downloadAudio = new JMenuItem("Download Audio");
        downloadAudio.addActionListener(this);
        downloadMenu.add(downloadAudio);

        setJMenuBar(menuBar);

        mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
        
        Canvas player = new Canvas();
        player.setBackground(Color.black);
        player.setVisible(true);
        playerPanel.add(player, BorderLayout.CENTER);
        playerPanel.add(mediaPlayerComponent);
	}

    public void actionPerformed(ActionEvent e)
    { 
        if(e.getSource() == openItem)
        {
            new OpenCommand(mediaPlayerComponent);
        }
        else if (e.getSource() == downloadAudio)
        {
        	new DownloadAudio(this);
        }
    }
}
