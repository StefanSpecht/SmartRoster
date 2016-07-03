package dom.company.thesis.application;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.maths.random.Probability;
import org.uncommons.maths.random.XORShiftRNG;
import org.uncommons.swing.SwingBackgroundTask;
import org.uncommons.watchmaker.framework.CachingFitnessEvaluator;
import org.uncommons.watchmaker.framework.EvolutionEngine;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.watchmaker.framework.GenerationalEvolutionEngine;
import org.uncommons.watchmaker.framework.SelectionStrategy;
import org.uncommons.watchmaker.framework.TerminationCondition;
import org.uncommons.watchmaker.framework.factories.BitStringFactory;
import org.uncommons.watchmaker.framework.factories.StringFactory;
import org.uncommons.watchmaker.framework.interactive.Renderer;
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection;
import org.uncommons.watchmaker.framework.selection.TournamentSelection;
import org.uncommons.watchmaker.framework.termination.Stagnation;
import org.uncommons.watchmaker.framework.termination.TargetFitness;
import org.uncommons.watchmaker.swing.AbortControl;
import org.uncommons.watchmaker.swing.ProbabilityParameterControl;
import org.uncommons.watchmaker.swing.evolutionmonitor.EvolutionMonitor;

import dom.company.thesis.gui.RosterRenderer;
import dom.company.thesis.model.Roster;
import dom.company.thesis.service.InputParser;
import dom.company.thesis.service.InputService;

public class SmartRosterApplet extends AbstractApplet
{
	private static final long serialVersionUID = 9082082587988054878L;
	
	private JButton startButton;
	private AbortControl abort;
	private JSpinner populationSpinner;
	private JSpinner elitismSpinner;
	private ProbabilityParameterControl selectionPressureControl;
	private ProbabilitiesPanel probabilitiesPanel;
	private EvolutionMonitor<Roster> evolutionMonitor;	
	

	/**Delete if no additional initialization needed**/	
	 @Override
	 public void init()
	 {
	     super.init();
	 }
	
	
	 /**
	  * Initialize and layout the GUI.
	  * @param container The Swing component that will contain the GUI controls.
	  */
	 @Override
	 protected void prepareGUI(Container container)
	 {
		 probabilitiesPanel = new ProbabilitiesPanel();
	     probabilitiesPanel.setBorder(BorderFactory.createTitledBorder("Evolution Probabilities")); 
		 JPanel controlsPanel = new JPanel(new BorderLayout());
	     controlsPanel.add(createParametersPanel(), BorderLayout.NORTH);
	     controlsPanel.add(probabilitiesPanel, BorderLayout.SOUTH);
	     container.add(controlsPanel, BorderLayout.NORTH);
	     
	     
	     
	     Renderer<Roster, JComponent> renderer = new RosterRenderer();
	     evolutionMonitor = new EvolutionMonitor<Roster>(renderer,false);
	     container.add(evolutionMonitor.getGUIComponent(), BorderLayout.CENTER);
		 
	 }
	 
	 private JComponent createParametersPanel() {
	 
	 		 
        Box parameterBox = Box.createHorizontalBox();
        parameterBox.add(Box.createHorizontalStrut(10));
        final JLabel populationLabel = new JLabel("Population Size: ");
        parameterBox.add(populationLabel);
        parameterBox.add(Box.createHorizontalStrut(10));
        
        populationSpinner = new JSpinner(new SpinnerNumberModel(10, 2, 10000, 1));
        populationSpinner.setMaximumSize(populationSpinner.getMinimumSize());
        parameterBox.add(populationSpinner);
        parameterBox.add(Box.createHorizontalStrut(10));
        final JLabel elitismLabel = new JLabel("Elitism: ");
        parameterBox.add(elitismLabel);
        parameterBox.add(Box.createHorizontalStrut(10));
        
        elitismSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 1000, 1));
        elitismSpinner.setMaximumSize(elitismSpinner.getMinimumSize());
        parameterBox.add(elitismSpinner);
        parameterBox.add(Box.createHorizontalStrut(10));

        parameterBox.add(new JLabel("Selection Pressure: "));
        parameterBox.add(Box.createHorizontalStrut(10));
        selectionPressureControl = new ProbabilityParameterControl(Probability.EVENS,
                                                                   Probability.ONE,
                                                                   2,
                                                                   new Probability(0.7));
        parameterBox.add(selectionPressureControl.getControl());
        parameterBox.add(Box.createHorizontalStrut(10));

        startButton = new JButton("Start");
        abort = new AbortControl(); 
        
        startButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent event) {        		
                abort.getControl().setEnabled(true);
                populationLabel.setEnabled(false);
                populationSpinner.setEnabled(false);
                elitismLabel.setEnabled(false);
                elitismSpinner.setEnabled(false);
                startButton.setEnabled(false);
                new EvolutionTask((Integer) populationSpinner.getValue(),
                                  (Integer) elitismSpinner.getValue(),
                                 // abort.getTerminationCondition())
                                  //.execute();
                                  abort.getTerminationCondition(),
                                  new TargetFitness(0, false)).execute();
            }
        });
        abort.getControl().setEnabled(false);
        
        parameterBox.add(startButton);
        parameterBox.add(abort.getControl());
        parameterBox.add(Box.createHorizontalStrut(10));

        parameterBox.setBorder(BorderFactory.createTitledBorder("Parameters"));
        return parameterBox;
     }
	
	 private class EvolutionTask extends SwingBackgroundTask<Roster> {
		 		 
		 private final int populationSize;
	     private final int eliteCount;
	     private final TerminationCondition[] terminationConditions;


        EvolutionTask(int populationSize, int eliteCount, TerminationCondition... terminationConditions) {
        	
            this.populationSize = populationSize;
            this.eliteCount = eliteCount;
            this.terminationConditions = terminationConditions;
        }


        @Override
        protected Roster performTask() throws Exception
        {
            //String target = "GUDE TOBI DIESE NACHRICHT WIRD PER GENETISCHEM ALGORITHMUS ERZEUGT GUDE TOBI DIESE NACHRICHT WIRD PER GENETISCHEM ALGORITHMUS ERZEUGT GUDE TOBI DIESE NACHRICHT WIRD PER GENETISCHEM ALGORITHMUS ERZEUGT GUDE TOBI DIESE NACHRICHT WIRD PER GENETISCHEM ALGORITHMUS ERZEUGT";
        	Random rng = new MersenneTwisterRNG();
            RosterEvaluator fitnessEvaluator = new RosterEvaluator();
            RosterFactory rosterFactory = new RosterFactory(InputService.getNoOfEmployees(), InputService.getNoOfShifts(), InputService.getNoOfTasks());
            
            EvolutionaryOperator<Roster> pipeline = probabilitiesPanel.createEvolutionPipeline();

            SelectionStrategy<Object> selection = new RouletteWheelSelection();
            //selectionPressureControl.getNumberGenerator()
            EvolutionEngine<Roster> engine = new GenerationalEvolutionEngine<Roster>(
            		rosterFactory,
            		pipeline,
            		fitnessEvaluator,
            		selection,
            		rng);
            engine.addEvolutionObserver(evolutionMonitor);

            return engine.evolve(populationSize, eliteCount, terminationConditions);
        }


        @Override
        protected void postProcessing(Roster result)
        {
            abort.reset();
            abort.getControl().setEnabled(false);
            populationSpinner.setEnabled(true);
            elitismSpinner.setEnabled(true);
            startButton.setEnabled(true);
        }


        @Override
        protected void onError(Throwable throwable)
        {
            super.onError(throwable);
            postProcessing(null);
        }
	 }
}
