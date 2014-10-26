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

/**	PlayOptionsPanel
*	Panel containing the play options for the media
*/
public class PlayOptionsPanel extends JPanel implements ActionListener, ChangeListener
{
	private EmbeddedMediaPlayerComponent mediaPlayerComponent;
	private JLabel labelTimer;
	private JButton btnPlay, btnPause, btnMute;
	private JButton btnSkipBack, btnSkipForward;
	private JSlider volumeSlider, playSlider;
	private int volume;
	private boolean muted;
	private Timer timerFastForward, timerRewind, timerSetPosSlider;
	private Icon playIcon, pauseIcon, ffIcon, rwIcon, muteIcon, unmuteIcon;
	private final Dimension BUTTON_DIMENSION = new Dimension(20,20);
	private boolean playSliderLock, mediaLock;

	public PlayOptionsPanel(EmbeddedMediaPlayerComponent mPC) 
	{
		
		playSliderLock = false;
		mediaLock = false;
		volume = 50;
		muted = false;
		mediaPlayerComponent = mPC;
		
		//fetch icons
		
		playIcon = resizeIcon(new ImageIcon(getClass().getResource("/vamix/icons/playBtn.png")));
		pauseIcon = resizeIcon(new ImageIcon(getClass().getResource("/vamix/icons/pauseBtn.png")));
		ffIcon = resizeIcon(new ImageIcon(getClass().getResource("/vamix/icons/ffBtn.png")));
		rwIcon = resizeIcon(new ImageIcon(getClass().getResource("/vamix/icons/rwBtn.png")));
		muteIcon = resizeIcon(new ImageIcon(getClass().getResource("/vamix/icons/muteBtn.png")));
		unmuteIcon = resizeIcon(new ImageIcon(getClass().getResource("/vamix/icons/unmuteBtn.png")));

		mPC.getMediaPlayer().addMediaPlayerEventListener(new MediaPlayerEventAdapter()
		{
			public void finished(MediaPlayer mP)
			{
				mediaPlayerComponent.getMediaPlayer().stop();
				setEnableAll(true);
				timerFastForward.cancel();
				timerRewind.cancel();
			}
			public void stopped(MediaPlayer mP)
			{
				setEnableAll(false);
				btnPlay.setEnabled(true);
				timerFastForward.cancel();
				timerRewind.cancel();
			}
			public void paused(MediaPlayer mP)
			{
				setEnableAll(false);
				btnPlay.setEnabled(true);
				timerFastForward.cancel();
				timerRewind.cancel();
			}
			public void playing(MediaPlayer mP)
			{
				setEnableAll(true);
				btnPlay.setEnabled(false);
			}
			public void positionChanged(MediaPlayer mP, float newPosition)
			{
				if(!mediaLock)
				{
					playSliderLock = true;
					int pos = (int)(100*newPosition);
					if(pos < 0)
						pos = 0;
					playSlider.setValue(pos);
					playSliderLock = false;
				}
				labelTimer.setText(LongToTime(mP.getTime())+"/"+LongToTime(mP.getLength()));
			}
		});
		timerFastForward = new Timer();
		timerRewind = new Timer();
		timerSetPosSlider = new Timer();

		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		setVisible(true);

		btnPlay = new JButton(playIcon);
		btnPlay.setPreferredSize(new Dimension(BUTTON_DIMENSION.width,BUTTON_DIMENSION.height));
		btnPlay.addActionListener(this);

		btnPause = new JButton(pauseIcon);
		btnPause.setPreferredSize(new Dimension(BUTTON_DIMENSION.width,BUTTON_DIMENSION.height));
		btnPause.addActionListener(this);

		btnSkipBack = new JButton(rwIcon);
		btnSkipBack.setPreferredSize(new Dimension(BUTTON_DIMENSION.width,BUTTON_DIMENSION.height));
		btnSkipBack.addActionListener(this);

		btnSkipForward = new JButton(ffIcon);
		btnSkipForward.setPreferredSize(new Dimension(BUTTON_DIMENSION.width,BUTTON_DIMENSION.height));
		btnSkipForward.addActionListener(this);

		btnMute = new JButton(unmuteIcon);
		btnMute.setPreferredSize(new Dimension(BUTTON_DIMENSION.width,BUTTON_DIMENSION.height));
		btnMute.addActionListener(this);

		volumeSlider = new JSlider(JSlider.HORIZONTAL,0,100,volume);
		volumeSlider.addChangeListener(this);
		playSlider = new JSlider(JSlider.HORIZONTAL,0,100,0);
		playSlider.addChangeListener(this);

		JLabel labelVolume = new JLabel(" Volume: ");
		labelTimer = new JLabel();

		layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(
        	layout.createParallelGroup()
        		.addGroup(layout.createSequentialGroup()
        			.addComponent(playSlider)
        			.addComponent(labelTimer)
        			)
	        	.addGroup(layout.createSequentialGroup()
		        	.addComponent(btnPlay)
		        	.addComponent(btnPause)
		        	.addComponent(btnSkipBack)
		        	.addComponent(btnSkipForward)
		        	.addComponent(btnSkipBack)
		        	.addComponent(btnMute)
		        	.addComponent(labelVolume)
		        	.addComponent(volumeSlider)
		        	)
        	);
        layout.setVerticalGroup(
        	layout.createSequentialGroup()
        		.addGroup(layout.createParallelGroup()
        			.addComponent(playSlider)
        			.addComponent(labelTimer)
        			)
        		.addGroup(layout.createParallelGroup()
	    			.addComponent(btnPlay)
		        	.addComponent(btnPause)
		        	.addComponent(btnSkipBack)
		        	.addComponent(btnSkipForward)
		        	.addComponent(btnSkipBack)
		        	.addComponent(btnMute)
		        	.addComponent(labelVolume)
		        	.addComponent(volumeSlider)
		        	)
        	);
        setEnableAll(false);
	}

	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == btnPlay)
		{
			mediaPlayerComponent.getMediaPlayer().play();
			timerFastForward.cancel();
			timerRewind.cancel();
		}
		else if(e.getSource() == btnPause)
		{
			mediaPlayerComponent.getMediaPlayer().setPause(true);
		}
		else if(e.getSource() == btnSkipForward)
		{
			setEnableAll(true);
			btnSkipForward.setEnabled(false);
			//Skip forward 1s every 50ms
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
			//Skip back 1s every 50ms
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

	/**	Sets Play/Pause/Fastforward/back to the argument
	*/
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
		if(e.getSource() == playSlider)
		{
			if (!playSliderLock)
			{
				mediaLock = true;
				MediaPlayer mP = mediaPlayerComponent.getMediaPlayer();
				mP.setPosition((float)playSlider.getValue()/100.0f);
				mP.setPause(true);
				labelTimer.setText(LongToTime(mP.getTime())+"/"+LongToTime(mP.getLength()));
				mediaLock = false;
			}
		}
	}
	
	public ImageIcon resizeIcon(ImageIcon icon) {
		Image img = icon.getImage() ;  
		Image newimg = img.getScaledInstance( 20, 20,  java.awt.Image.SCALE_SMOOTH ) ;  
	    icon = new ImageIcon( newimg );
		return icon;
	}

	private String LongToTime(long time)
	{
		long hrs =  time / (1000*60*60);
		long mins = (time % (1000*60*60)) / (1000*60);
		long secs = (time % (1000*60) / 1000);
		String sHrs = ""+hrs;
		if(sHrs.length() < 2)
			sHrs = "0"+sHrs;
		String sMins = ""+mins;
		if(sMins.length() < 2)
			sMins = "0"+sMins;
		String sSecs = ""+secs;
		if(sSecs.length() < 2)
			sSecs = "0"+sSecs;
		return sHrs+":"+sMins+":"+sSecs;
	}
}