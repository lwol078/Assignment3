package vamix.work;

import javax.swing.*;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
public class OpenCommand
{
	public OpenCommand(EmbeddedMediaPlayerComponent player)
	{
		JFileChooser jFC = new JFileChooser();
		jFC.showOpenDialog(null);
		if(jFC.getSelectedFile() != null)
			player.getMediaPlayer().startMedia(jFC.getSelectedFile().getAbsolutePath());
	}
}