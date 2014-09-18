import vamix.GUI.MainFrame;
import javax.swing.SwingUtilities;

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