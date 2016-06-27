package dom.company.thesis.application;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;

import org.uncommons.maths.random.Probability;
import org.uncommons.swing.SpringUtilities;
import org.uncommons.watchmaker.framework.termination.Stagnation;
import org.uncommons.watchmaker.swing.AbortControl;
import org.uncommons.watchmaker.swing.ProbabilityParameterControl;
import org.uncommons.watchmaker.swing.evolutionmonitor.EvolutionMonitor;

public class ControlPanel extends JPanel{
	
	private static final Probability ONE_TENTH = new Probability(0.1d);
	
    private EvolutionMonitor<String> monitor;
    private JButton startButton;
    private AbortControl abort;
    private JSpinner populationSpinner;
    private JSpinner elitismSpinner;
    private ProbabilityParameterControl selectionPressureControl;
    private ProbabilityParameterControl mutationControl;
    private ProbabilityParameterControl crossoverControl;
    
    private Box parameters;
    
    ControlPanel() {
       	
    	Box parameters = Box.createHorizontalBox();
    	
        parameters.add(Box.createHorizontalStrut(10));
        final JLabel populationLabel = new JLabel("Population Size: ");
        parameters.add(populationLabel);
        parameters.add(Box.createHorizontalStrut(10));
        populationSpinner = new JSpinner(new SpinnerNumberModel(10, 2, 1000, 1));
        populationSpinner.setMaximumSize(populationSpinner.getMinimumSize());
        parameters.add(populationSpinner);
        
        parameters.add(Box.createHorizontalStrut(10));
        final JLabel elitismLabel = new JLabel("Elitism: ");
        parameters.add(elitismLabel);
        parameters.add(Box.createHorizontalStrut(10));
        elitismSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 1000, 1));
        elitismSpinner.setMaximumSize(elitismSpinner.getMinimumSize());
        parameters.add(elitismSpinner);
        parameters.add(Box.createHorizontalStrut(10));

        startButton = new JButton("Start");
        //abort = new AbortControl();        
        startButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                //abort.getControl().setEnabled(true);
                populationLabel.setEnabled(false);
                populationSpinner.setEnabled(false);
                elitismLabel.setEnabled(false);
                elitismSpinner.setEnabled(false);
                startButton.setEnabled(false);
                /*new EvolutionTask((Integer) populationSpinner.getValue(),
                                  (Integer) elitismSpinner.getValue(),
                                  abort.getTerminationCondition(),
                                  new Stagnation(1000, false)).execute();
               */
            }
        });
        //abort.getControl().setEnabled(false);
        parameters.add(startButton);
        //parameters.add(abort.getControl());
        parameters.add(Box.createHorizontalStrut(10));

        parameters.setBorder(BorderFactory.createTitledBorder("Parameters"));
        
        //SpringUtilities.makeCompactGrid(this, 4, 4, 10, 0, 10, 0);
       
    }

	public Box getParameters() {
		return parameters;
	}
    
    

}
