import vamix.GUI.MainFrame;

import javax.swing.*;
import javax.swing.UIManager.*;

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
				} catch (Exception e) {}
				MainFrame mainFrame =new MainFrame();
			}
		});
	}
}