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
    /**
     * {@inheritDoc}
     */
    @Override
    public void init()
    {
        configure(this);
    }

    /**
     * Configure the program to display its GUI in the specified container.
     * @param container The container to place the GUI components in.
     */
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


    /**
     * Implemented in sub-classes to initialise and layout the GUI.
     * @param container The container that this method should add components to.
     */
    protected abstract void prepareGUI(Container container);


    /**
     * Display this program using a JFrame as the top-level GUI container (rather
     * than running the program as an applet).
     * @param title The text to use for the frame's title bar.
     */
    protected void displayInFrame(String title)
    {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        configure(frame);
        frame.pack();
        frame.setVisible(true);
    }
}
