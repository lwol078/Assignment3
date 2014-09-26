package vamix.work;

import javax.swing.*;
import java.io.*;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;

/**	OpenCommand
*	Call execute to open the file in the media
*/
public class OpenCommand
{
	private EmbeddedMediaPlayerComponent player;
	
	public OpenCommand(EmbeddedMediaPlayerComponent eMPC)
	{
		player = eMPC;
	}

	public File Execute()
	{
		JFileChooser jFC = new JFileChooser();
		jFC.showOpenDialog(null);
		if(jFC.getSelectedFile() != null)
			player.getMediaPlayer().startMedia(jFC.getSelectedFile().getAbsolutePath());
		return jFC.getSelectedFile();
	}
}