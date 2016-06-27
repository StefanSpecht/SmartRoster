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
import org.uncommons.watchmaker.framework.factories.StringFactory;
import org.uncommons.watchmaker.framework.interactive.Renderer;
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection;
import org.uncommons.watchmaker.framework.selection.TournamentSelection;
import org.uncommons.watchmaker.framework.termination.Stagnation;
import org.uncommons.watchmaker.framework.termination.TargetFitness;
import org.uncommons.watchmaker.swing.AbortControl;
import org.uncommons.watchmaker.swing.ProbabilityParameterControl;
import org.uncommons.watchmaker.swing.evolutionmonitor.EvolutionMonitor;

public class SmartRosterApplet extends AbstractApplet
{
	private static final long serialVersionUID = 9082082587988054878L;
	
	private JButton startButton;
	private AbortControl abort;
	private JSpinner populationSpinner;
	private JSpinner elitismSpinner;
	private ProbabilityParameterControl selectionPressureControl;
	private ProbabilitiesPanel probabilitiesPanel;
	private EvolutionMonitor<String> evolutionMonitor;	
	

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
	     
	     
	     
	     //Renderer<List<ColouredPolygon>, JComponent> renderer = new PolygonImageSwingRenderer(targetImage);
	     evolutionMonitor = new EvolutionMonitor<String>(false);
	     container.add(evolutionMonitor.getGUIComponent(), BorderLayout.CENTER);
		 
	 }
	 
	 private JComponent createParametersPanel() {
	 
	 		 
        Box parameters = Box.createHorizontalBox();
        parameters.add(Box.createHorizontalStrut(10));
        final JLabel populationLabel = new JLabel("Population Size: ");
        parameters.add(populationLabel);
        parameters.add(Box.createHorizontalStrut(10));
        
        populationSpinner = new JSpinner(new SpinnerNumberModel(10, 2, 10000, 1));
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

        parameters.add(new JLabel("Selection Pressure: "));
        parameters.add(Box.createHorizontalStrut(10));
        selectionPressureControl = new ProbabilityParameterControl(Probability.EVENS,
                                                                   Probability.ONE,
                                                                   2,
                                                                   new Probability(0.7));
        parameters.add(selectionPressureControl.getControl());
        parameters.add(Box.createHorizontalStrut(10));

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
        
        parameters.add(startButton);
        parameters.add(abort.getControl());
        parameters.add(Box.createHorizontalStrut(10));

        parameters.setBorder(BorderFactory.createTitledBorder("Parameters"));
        return parameters;
     }
	
	 private class EvolutionTask extends SwingBackgroundTask<String> {
		 		 
		 private final int populationSize;
	     private final int eliteCount;
	     private final TerminationCondition[] terminationConditions;


        EvolutionTask(int populationSize, int eliteCount, TerminationCondition... terminationConditions) {
        	
            this.populationSize = populationSize;
            this.eliteCount = eliteCount;
            this.terminationConditions = terminationConditions;
        }


        @Override
        protected String performTask() throws Exception
        {
            String target = "GUDE TOBI DIESE NACHRICHT WIRD PER GENETISCHEM ALGORITHMUS ERZEUGT GUDE TOBI DIESE NACHRICHT WIRD PER GENETISCHEM ALGORITHMUS ERZEUGT GUDE TOBI DIESE NACHRICHT WIRD PER GENETISCHEM ALGORITHMUS ERZEUGT GUDE TOBI DIESE NACHRICHT WIRD PER GENETISCHEM ALGORITHMUS ERZEUGT";
        	Random rng = new MersenneTwisterRNG();
            FitnessEvaluator<String> fitnessEvaluator = new StringEvaluator(target);
            StringFactory stringFactory = new StringFactory(ProbabilitiesPanel.getAlphabet(), target.length());
            EvolutionaryOperator<String> pipeline = probabilitiesPanel.createEvolutionPipeline();

            SelectionStrategy<Object> selection = new RouletteWheelSelection();
            //selectionPressureControl.getNumberGenerator()
            EvolutionEngine<String> engine = new GenerationalEvolutionEngine<String>(
            		stringFactory,
            		pipeline,
            		fitnessEvaluator,
            		selection,
            		rng);
            engine.addEvolutionObserver(evolutionMonitor);

            return engine.evolve(populationSize, eliteCount, terminationConditions);
        }


        @Override
        protected void postProcessing(String result)
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
