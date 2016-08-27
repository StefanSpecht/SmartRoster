package dom.company.thesis.application;

import java.awt.Container;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public abstract class AbstractApplet extends JApplet
{
	private static final long serialVersionUID = -4427649975260993256L;

    @Override
    public void init()
    {
        configure(this);
    }

    private void configure(final Container container)
    {
        try
        {
            // Use invokeAndWait so that we can be sure that initialisation is complete
            // before continuing.
            SwingUtilities.invokeAndWait(new Runnable()
            {
                public void run()
                {
                    try
                    {
                        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    }
                    catch (Exception ex)
                    {
                        // This should never happen as we are installing a known look-and-feel.
                        System.err.println("Failed to load System look-and-feel.");
                    }
                    prepareGUI(container);
                }
            });
        }
        catch (InterruptedException ex)
        {
            // Restore interrupt flag.
            Thread.currentThread().interrupt();
        }
        catch (InvocationTargetException ex)
        {
            ex.getCause().printStackTrace();
            JOptionPane.showMessageDialog(container, ex.getCause(), "Error Occurred", JOptionPane.ERROR_MESSAGE);
        }
    }

    protected abstract void prepareGUI(Container container);

    protected void displayInFrame(String title)
    {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        configure(frame);
        frame.pack();
        frame.setVisible(true);
    }
}
