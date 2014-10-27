package vamix.GUI;

import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.event.*;

import vamix.work.*;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayer;

/** Mainframe
 *  The main frame class for the vamix. It contains a menu to access the editing functions,
 *   a panel containing the play options and the media player
 */
public class MainFrame extends JFrame implements ActionListener
{
	private JMenuBar menuBar;

    private JMenu fileMenu,videoMenu,downloadMenu, audioMenu;
    private JMenuItem openItem,filterItem, subtitleItem, downloadAudio, downloadVideo, extractAudio, replaceAudio, overlayAudio;

    private JDesktopPane desktop;
    private JPanel playerPanel;
    private PlayOptionsPanel playOptionsPanel;

    private EmbeddedMediaPlayerComponent mediaPlayerComponent;
    private File currentFile;
    private TextGUI textGUI;
    private SubtitleFrame subtitles;

    /** Constructor
    *   Sets everything up to run
    */
	public MainFrame() 
	{
		super("Vamix - Title");

        mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
        currentFile = null;
        textGUI = null;
        subtitles = null;

		setLocation(100, 100);
        setSize(1050, 600);
        setMinimumSize(new Dimension(100,100));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        //Release media player at end of program and set size on startup
        addWindowListener(new WindowAdapter()
            {
                public void windowClosing(WindowEvent e)
                {
                    mediaPlayerComponent.release();
                }
            });
        addComponentListener(new ComponentAdapter()
            {
                public void componentResized(ComponentEvent e)
                {
                    Dimension d = getSize();
                    int width = (int)d.getWidth();
                    int height = (int)d.getHeight();
                    playerPanel.setBounds(0, 0, width, height-100);
                    playOptionsPanel.setBounds(0, height-100, width, 80);
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
        desktop.add(playOptionsPanel, BorderLayout.SOUTH);


        fileMenu = new JMenu("File");
        fileMenu.getAccessibleContext().setAccessibleDescription(
            "Menu for selecting files to open");
        menuBar.add(fileMenu);

        openItem = new JMenuItem("Open");
        openItem.addActionListener(this);
        fileMenu.add(openItem);
        
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
        
        replaceAudio = new JMenuItem("Replace Audio Track of Video");
        replaceAudio.addActionListener(this);
        audioMenu.add(replaceAudio);
        
        overlayAudio = new JMenuItem("Overlay Audio Track to Video");
        overlayAudio.addActionListener(this);
        audioMenu.add(overlayAudio);

        videoMenu = new JMenu("Video");
        audioMenu.getAccessibleContext().setAccessibleDescription(
                "Menu option for video functionality");
        menuBar.add(videoMenu);

        filterItem = new JMenuItem("Add Filters to Video");
        filterItem.addActionListener(this);
        videoMenu.add(filterItem);
        
        subtitleItem = new JMenuItem("Add Subtitles to Video");
        subtitleItem.addActionListener(this);
        videoMenu.add(subtitleItem);

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
        	new DownloadAudioGUI(this);
        } 
        else if (e.getSource() == downloadVideo)
        {
        	new DownloadVideoGUI(this);
        }
        else if (e.getSource() == extractAudio)
        {
        	new ExtractAudioGUI(this);
        }
        else if (e.getSource() == replaceAudio) 
        {
        	new ReplaceAudioGUI(this);
        }
        else if (e.getSource() == overlayAudio) 
        {
        	new OverlayAudioGUI(this);
        }
        else if (e.getSource() == filterItem)
        {
            if(textGUI == null)
            {
                textGUI = new TextGUI(this, currentFile);
                textGUI.addWindowListener(new WindowAdapter()
                {
                    public void windowClosing(WindowEvent e)
                    {
                        textGUI = null;
                    }
                });
            }
        }
        else if (e.getSource() == subtitleItem)
        {
        	if(!TextGUI.ValidateVideoFile(currentFile))
        			JOptionPane.showMessageDialog(this, "Begin playing a track to be able to add subtitles");
        	else if(subtitles == null)
            {
                subtitles = new SubtitleFrame(this,currentFile);
                subtitles.addWindowListener(new WindowAdapter()
                {
                    public void windowClosing(WindowEvent e)
                    {
                        subtitles = null;
                    }
                });
            }
        }
    }
    
    public MediaPlayer GetMediaPlayer()
    {
    	return mediaPlayerComponent.getMediaPlayer();
    }
}
