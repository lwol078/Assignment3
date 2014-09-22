package vamix.GUI;

import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;

import vamix.work.*;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;

public class MainFrame extends JFrame implements ActionListener
{
	private JMenuBar menuBar;
    private JMenu menu1,menu2,downloadMenu, audioMenu;
    private JMenuItem openItem,item2, downloadAudio, downloadVideo, extractAudio;
    private JDesktopPane desktop;
    private JPanel playerPanel;
    private PlayOptionsPanel playOptionsPanel;

    private EmbeddedMediaPlayerComponent mediaPlayerComponent;
    private File currentFile;

	public MainFrame() 
	{
		super("Vamix - Title");

        mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
        currentFile = null;

		setLocation(100, 100);
        setSize(1050, 600);
        setMinimumSize(new Dimension(100,100));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        addWindowListener(new WindowListener()
            {
                public void windowClosing(WindowEvent e)
                {
                    mediaPlayerComponent.release();
                }
                public void windowActivated(WindowEvent e){}
                public void windowDeactivated(WindowEvent e){}
                public void windowDeiconified(WindowEvent e){}
                public void windowIconified(WindowEvent e){}
                public void windowClosed(WindowEvent e){}
                public void windowOpened(WindowEvent e){}
            });
        addComponentListener(new ComponentAdapter()
            {
                public void componentResized(ComponentEvent e)
                {
                    Dimension d = getSize();
                    int width = (int)d.getWidth();
                    int height = (int)d.getHeight();
                    playerPanel.setBounds(0, 0, width, height-60);
                    playOptionsPanel.setBounds(0, height-60, width, 40);
                    desktop.moveToBack( playerPanel);
                }
            });

        menuBar = new JMenuBar();

        desktop = new JDesktopPane();
        setContentPane(desktop);
        desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
        
        playerPanel = new JPanel();
        playerPanel.setLayout(new BorderLayout());
        playerPanel.setVisible(true);
        desktop.setLayer( playerPanel, JDesktopPane.DEFAULT_LAYER);
        desktop.add(playerPanel);

        playOptionsPanel = new PlayOptionsPanel(mediaPlayerComponent);
        
        desktop.setLayer( playOptionsPanel, JDesktopPane.DEFAULT_LAYER);
        desktop.add(playOptionsPanel);


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
        
        downloadVideo = new JMenuItem("Download Video");
        downloadVideo.addActionListener(this);
        downloadMenu.add(downloadVideo);
        
        audioMenu = new JMenu("Audio");
        audioMenu.getAccessibleContext().setAccessibleDescription(
                "Menu option for audio functionality");
        menuBar.add(audioMenu);
        
        extractAudio = new JMenuItem("Extract Audio from Video File");
        extractAudio.addActionListener(this);
        audioMenu.add(extractAudio);

        setJMenuBar(menuBar);

        Canvas player = new Canvas();
        player.setBackground(Color.black);
        player.setVisible(true);
        playerPanel.add(player, BorderLayout.CENTER);
        playerPanel.add(mediaPlayerComponent, BorderLayout.CENTER);
	}

    public void actionPerformed(ActionEvent e)
    { 
        if(e.getSource() == openItem)
        {
            OpenCommand open = new OpenCommand(mediaPlayerComponent);
            currentFile = open.Execute();
        }
        else if (e.getSource() == downloadAudio)
        {
        	new DownloadAudio(this);
        } 
        else if (e.getSource() == downloadVideo)
        {
        	new DownloadVideo(this);
        }
        else if (e.getSource() == extractAudio)
        {
        	new ExtractAudio(this);
        }
    }
}
