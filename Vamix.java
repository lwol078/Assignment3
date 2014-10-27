import vamix.GUI.MainFrame;

import javax.swing.*;
import javax.swing.UIManager.*;

/**
 * Vamix
 * @author luke
 *Main class, simply sets look and feel to nimbus if possible then runs the program
 */
public class Vamix
{
	public static void main(final String[] args) 
	{
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try 
				{
					for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) 
					{
						if ("Nimbus".equals(info.getName())) 
						{
							UIManager.setLookAndFeel(info.getClassName());
							break;
						}
					}
				} catch (Exception e) 
				{
					//Otherwise just use default look and feel
				}
				MainFrame mainFrame =new MainFrame();
			}
		});
	}
}