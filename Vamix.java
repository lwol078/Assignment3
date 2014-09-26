import vamix.GUI.MainFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Vamix
{
	public static void main(final String[] args) 
	{
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainFrame mainFrame =new MainFrame();
			}
		});
	}
}