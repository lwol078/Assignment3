package vamix.GUI;

import java.awt.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.Timer;
import java.util.TimerTask;

import java.util.Random;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.*;

public class PlayOptionsPanel extends JPanel implements ActionListener, ChangeListener
{
	private EmbeddedMediaPlayerComponent mediaPlayerComponent;
	private JButton btnPlay, btnPause, btnMute;
	private JButton btnSkipBack, btnSkipForward;
	private JSlider volumeSlider;
	private int volume;
	private int mutedVolume;
	private boolean muted;
	private Timer timerFastForward, timerRewind;
	private Icon playIcon, pauseIcon, ffIcon, rwIcon, muteIcon, unmuteIcon;
	//private TimerTask timertaskFastForward, timertaskRewind;

	public PlayOptionsPanel(EmbeddedMediaPlayerComponent mPC) 
	{
		volume = 50;
		mutedVolume = volume;
		muted = false;
		mediaPlayerComponent = mPC;
		
		playIcon = resizeIcon(new ImageIcon("vamix/icons/playBtn.jpg"));
		pauseIcon = resizeIcon(new ImageIcon("vamix/icons/pauseBtn.jpg"));
		ffIcon = resizeIcon(new ImageIcon("vamix/icons/ffBtn.jpg"));
		rwIcon = resizeIcon(new ImageIcon("vamix/icons/rwBtn.jpg"));
		muteIcon = resizeIcon(new ImageIcon("vamix/icons/muteBtn.jpg"));
		unmuteIcon = resizeIcon(new ImageIcon("vamix/icons/unmuteBtn.jpg"));
		mPC.getMediaPlayer().addMediaPlayerEventListener(new MediaPlayerEventAdapter()
		{
			public void finished(MediaPlayer mP)
			{
				mediaPlayerComponent.getMediaPlayer().stop();
				setEnableAll(true);
				timerFastForward.cancel();
				timerRewind.cancel();
			}
		});
		timerFastForward = new Timer();
		timerRewind = new Timer();

		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		setVisible(true);

		btnPlay = new JButton(playIcon);
		btnPlay.setPreferredSize(new Dimension(100,60));
		add(btnPlay);
		btnPlay.addActionListener(this);

		btnPause = new JButton(pauseIcon);
		btnPause.setPreferredSize(new Dimension(100,60));
		add(btnPause);
		btnPause.addActionListener(this);

		btnSkipBack = new JButton(rwIcon);
		btnSkipBack.setPreferredSize(new Dimension(100,60));
		add(btnSkipBack);
		btnSkipBack.addActionListener(this);

		btnSkipForward = new JButton(ffIcon);
		btnSkipForward.setPreferredSize(new Dimension(100,60));
		add(btnSkipForward);
		btnSkipForward.addActionListener(this);

		btnMute = new JButton(unmuteIcon);
		btnMute.setPreferredSize(new Dimension(100,60));
		add(btnMute);
		btnMute.addActionListener(this);

		volumeSlider = new JSlider(JSlider.HORIZONTAL,0,100,volume);
		volumeSlider.addChangeListener(this);
		add(new JLabel(" Volume: "));
		add(volumeSlider);
	}

	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == btnPlay)
		{
			setEnableAll(true);
			btnPlay.setEnabled(false);
			mediaPlayerComponent.getMediaPlayer().play();
		}
		else if(e.getSource() == btnPause)
		{
			setEnableAll(true);
			btnPause.setEnabled(false);
			mediaPlayerComponent.getMediaPlayer().pause();
		}
		else if(e.getSource() == btnSkipForward)
		{
			setEnableAll(true);
			btnSkipForward.setEnabled(false);
			timerFastForward.schedule(new TimerTask()
			{
				@Override public void run()
				{
					long time = mediaPlayerComponent.getMediaPlayer().getTime();
					long length = mediaPlayerComponent.getMediaPlayer().getLength();
					if((time+1000) > length)
						mediaPlayerComponent.getMediaPlayer().setPosition(1.0f);
					else
						mediaPlayerComponent.getMediaPlayer().skip(1000);

				}
			}, 0l, 50l);
		}
		else if(e.getSource() == btnSkipBack)
		{
			setEnableAll(true);
			btnSkipBack.setEnabled(false);

			timerFastForward.schedule(new TimerTask()
			{
				@Override public void run()
				{
					mediaPlayerComponent.getMediaPlayer().skip(-1000);
				}
			}, 0l, 50l);

		}
		else if (e.getSource() == btnMute) 
		{
			if (!muted){
				mediaPlayerComponent.getMediaPlayer().mute(true);
				muted = true;
				btnMute.setIcon(muteIcon);
			} else if (muted) {
				mediaPlayerComponent.getMediaPlayer().mute(false);
				muted = false;
				btnMute.setIcon(unmuteIcon);
			}
		}
	}

	private void setEnableAll(boolean bool)
	{
		btnPlay.setEnabled(bool);
		btnPause.setEnabled(bool);
		btnSkipForward.setEnabled(bool);
		btnSkipBack.setEnabled(bool);

		timerFastForward.cancel();
		timerFastForward = new Timer();
		timerRewind.cancel();
		timerRewind = new Timer();
	}

	public void stateChanged(ChangeEvent e)
	{
		if(e.getSource() == volumeSlider)
		{
			mediaPlayerComponent.getMediaPlayer().mute(false);
			muted = false;
			volume = volumeSlider.getValue();
			mediaPlayerComponent.getMediaPlayer().setVolume(volume);
			btnMute.setIcon(unmuteIcon);
		}
	}
	
	public ImageIcon resizeIcon(ImageIcon icon) {
		Image img = icon.getImage() ;  
		Image newimg = img.getScaledInstance( 30, 30,  java.awt.Image.SCALE_SMOOTH ) ;  
	    icon = new ImageIcon( newimg );
		return icon;
	}
}