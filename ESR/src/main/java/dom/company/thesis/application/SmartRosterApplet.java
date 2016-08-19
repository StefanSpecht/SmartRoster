package dom.company.thesis.application;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
import org.uncommons.watchmaker.framework.selection.RankSelection;
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection;
import org.uncommons.watchmaker.framework.selection.StochasticUniversalSampling;
import org.uncommons.watchmaker.framework.selection.TournamentSelection;
import org.uncommons.watchmaker.framework.termination.Stagnation;
import org.uncommons.watchmaker.framework.termination.TargetFitness;
import org.uncommons.watchmaker.swing.AbortControl;
import org.uncommons.watchmaker.swing.ProbabilityParameterControl;
import org.uncommons.watchmaker.swing.evolutionmonitor.EvolutionMonitor;

import dom.company.thesis.ga.CustomEvolutionMonitor;
import dom.company.thesis.gui.RosterRenderer;
import dom.company.thesis.model.Roster;
import dom.company.thesis.service.InputParser;
import dom.company.thesis.service.InputService;

public class SmartRosterApplet extends AbstractApplet
{
	private static final long serialVersionUID = 9082082587988054878L;
	private final int TERMINATION_STAGNATION = 50;
	private final int EXPERIMENT_POPULATION_MIN = 50;
	private final int EXPERIMENT_POPULATION_MAX = 2000;
	private final int EXPERIMENT_POPULATION_GRAN = 50;
	
	private JButton startButton;
	private AbortControl abort;
	private JLabel populationLabel;
	private JLabel elitismLabel;
	private JLabel selectionLabel;
	private JSpinner populationSpinner;
	private JSpinner elitismSpinner;
	
	private JLabel weekendWeightLabel;
	private static JSpinner weekendWeightSpinner;
	private JLabel shiftOffWeightLabel;
	private static JSpinner shiftOffWeightSpinner;
	private JLabel maxAssignWeightLabel;
	private static JSpinner maxAssignWeightSpinner;	
	private JLabel coverWeightLabel;
	private static JSpinner coverWeightSpinner;
	//private ProbabilityParameterControl selectionPressureControl;
	private JPanel selectionButtonPanel;
	private JRadioButton rankSelectionRadioButton;
	private JRadioButton rouletteWheelSelectionRadioButton;
	private JRadioButton susSelectionRadioButton;
	private ButtonGroup selectionButtonGroup;
	private ProbabilitiesPanel probabilitiesPanel;
	private CustomEvolutionMonitor<Roster> evolutionMonitor;

	//Experiments
	private JCheckBox experimentModeCheckBox;
	//private JLabel populationSizeExperimentLabel;
	private JRadioButton populationSizeExperimentRadioButton;
	//private JLabel selectionExperimentLabel;
	private JRadioButton selectionExperimentRadioButton;
	private ButtonGroup experimentButtonGroup;
	private JLabel experimentLabel;
	private JPanel experimentPanel;
	private JLabel experimentIterationsLabel;
	private JSpinner experimentIterationsSpinner;
	private JButton startExperimentButton;
	

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
		 abort = new AbortControl(); 
		 
		 probabilitiesPanel = new ProbabilitiesPanel();
	     probabilitiesPanel.setBorder(BorderFactory.createTitledBorder("Evolution Probabilities")); 
		 JPanel controlsPanel = new JPanel(new BorderLayout());
	     controlsPanel.add(createParametersPanel(), BorderLayout.NORTH);
	     controlsPanel.add(probabilitiesPanel, BorderLayout.SOUTH);
	     container.add(controlsPanel, BorderLayout.NORTH);
	     	     
	     Renderer<Roster, JComponent> renderer = new RosterRenderer();
	     
	     evolutionMonitor = new CustomEvolutionMonitor<Roster>(renderer,
	    		 false, 
	    		 (TerminationCondition) new TargetFitness(0, false),
	    		 new Stagnation(TERMINATION_STAGNATION, false),
	    		 abort.getTerminationCondition());
	     
	     container.add(evolutionMonitor.getGUIComponent(), BorderLayout.CENTER);
	 }
	 
	 private JComponent createParametersPanel() {
		 Box parameterBox = Box.createVerticalBox();
		 parameterBox.add(createGeneralParametersPanel());
		 parameterBox.add(createPenaltyWeightParametersPanel());
		 parameterBox.add(createExperimentParametersPanel());
		 
		 return parameterBox;
	 }
	 
	 
	 private JComponent createGeneralParametersPanel() {
	 	 		 
        Box parameterBox = Box.createHorizontalBox();
        parameterBox.add(Box.createHorizontalStrut(10));
        populationLabel = new JLabel("Population Size: ");
        parameterBox.add(populationLabel);
        parameterBox.add(Box.createHorizontalStrut(5));
        populationSpinner = new JSpinner(new SpinnerNumberModel(100, 2, 10000, 1));
        populationSpinner.setMaximumSize(populationSpinner.getMinimumSize());
        parameterBox.add(populationSpinner);
        parameterBox.add(Box.createHorizontalStrut(10));
        
        elitismLabel = new JLabel("Elitism: ");
        parameterBox.add(elitismLabel);
        parameterBox.add(Box.createHorizontalStrut(5));        
        elitismSpinner = new JSpinner(new SpinnerNumberModel(10, 0, 10000, 1));
        elitismSpinner.setMaximumSize(elitismSpinner.getMinimumSize());
        parameterBox.add(elitismSpinner);
        parameterBox.add(Box.createHorizontalStrut(10));

        /**
        parameterBox.add(new JLabel("Selection Pressure: "));
        parameterBox.add(Box.createHorizontalStrut(10));
        selectionPressureControl = new ProbabilityParameterControl(Probability.EVENS,
                                                                   Probability.ONE,
                                                                   2,
                                                                   new Probability(0.7));
        parameterBox.add(selectionPressureControl.getControl());
        parameterBox.add(Box.createHorizontalStrut(10));
		**/
        selectionLabel = new JLabel("Selection: ");
        parameterBox.add(selectionLabel);
        parameterBox.add(Box.createHorizontalStrut(5));        
        rouletteWheelSelectionRadioButton = new JRadioButton("Roulette");
        rankSelectionRadioButton = new JRadioButton("Rank");
        susSelectionRadioButton = new JRadioButton("SUS");
        
        selectionButtonGroup = new ButtonGroup();
        selectionButtonGroup.add(rouletteWheelSelectionRadioButton);
        selectionButtonGroup.add(rankSelectionRadioButton);
        selectionButtonGroup.add(susSelectionRadioButton);
        
        selectionButtonPanel = new JPanel();
        rouletteWheelSelectionRadioButton.setSelected(true);
        selectionButtonPanel.add(rouletteWheelSelectionRadioButton);
        selectionButtonPanel.add(rankSelectionRadioButton);
        selectionButtonPanel.add(susSelectionRadioButton);
        parameterBox.add(selectionButtonPanel);
        parameterBox.add(Box.createHorizontalStrut(10));  
        
        coverWeightLabel = new JLabel("Panalty weight");
        
        startButton = new JButton("Start");        
        startButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent event) {  
        		RosterRenderer.disable();
                abort.getControl().setEnabled(true);
                populationLabel.setEnabled(false);
                populationSpinner.setEnabled(false);
                elitismLabel.setEnabled(false);
                elitismSpinner.setEnabled(false);
                selectionLabel.setEnabled(false);
                selectionButtonPanel.setEnabled(false);
                rouletteWheelSelectionRadioButton.setEnabled(false);
                rankSelectionRadioButton.setEnabled(false);
                susSelectionRadioButton.setEnabled(false);
                weekendWeightLabel.setEnabled(false);
            	weekendWeightSpinner.setEnabled(false);
            	shiftOffWeightLabel.setEnabled(false);
            	shiftOffWeightSpinner.setEnabled(false);
            	maxAssignWeightLabel.setEnabled(false);
            	maxAssignWeightSpinner.setEnabled(false);	
            	coverWeightLabel.setEnabled(false);
            	coverWeightSpinner.setEnabled(false);
                startButton.setEnabled(false);
                new EvolutionTask((Integer) populationSpinner.getValue(),
                                  (Integer) elitismSpinner.getValue(),
                                  abort.getTerminationCondition(),
                                  new Stagnation(TERMINATION_STAGNATION, false),
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
	 
	 private JComponent createPenaltyWeightParametersPanel() {
	 		 
	        Box parameterBox = Box.createHorizontalBox();
	        parameterBox.add(Box.createHorizontalStrut(10));
	        
	        shiftOffWeightLabel = new JLabel("Shift-off preference: ");
	        parameterBox.add(shiftOffWeightLabel);
	        parameterBox.add(Box.createHorizontalStrut(10));
	        shiftOffWeightSpinner = new JSpinner(new SpinnerNumberModel(1, 0, 100, 1));
	        shiftOffWeightSpinner.setMaximumSize(shiftOffWeightSpinner.getMinimumSize());
	        parameterBox.add(shiftOffWeightSpinner);
	        parameterBox.add(Box.createHorizontalStrut(10));
	        
	        weekendWeightLabel = new JLabel("Complete Weekends: ");
	        parameterBox.add(weekendWeightLabel);
	        parameterBox.add(Box.createHorizontalStrut(10));
	        weekendWeightSpinner = new JSpinner(new SpinnerNumberModel(1, 0, 100, 1));
	        weekendWeightSpinner.setMaximumSize(weekendWeightSpinner.getMinimumSize());
	        parameterBox.add(weekendWeightSpinner);
	        parameterBox.add(Box.createHorizontalStrut(10));
	        
	        maxAssignWeightLabel = new JLabel("Maximum Assignments: ");
	        parameterBox.add(maxAssignWeightLabel);
	        parameterBox.add(Box.createHorizontalStrut(10));
	        maxAssignWeightSpinner = new JSpinner(new SpinnerNumberModel(1, 0, 100, 1));
	        maxAssignWeightSpinner.setMaximumSize(maxAssignWeightSpinner.getMinimumSize());
	        parameterBox.add(maxAssignWeightSpinner);
	        parameterBox.add(Box.createHorizontalStrut(10));
	        
	        coverWeightLabel = new JLabel("Cover Requirements: ");
	        parameterBox.add(coverWeightLabel);
	        parameterBox.add(Box.createHorizontalStrut(10));
	        coverWeightSpinner = new JSpinner(new SpinnerNumberModel(5, 0, 100, 1));
	        coverWeightSpinner.setMaximumSize(coverWeightSpinner.getMinimumSize());
	        parameterBox.add(coverWeightSpinner);
	        parameterBox.add(Box.createHorizontalStrut(10));
	        
	        parameterBox.setBorder(BorderFactory.createTitledBorder("Penalty weights"));
	        return parameterBox;
	 }
	 private JComponent createExperimentParametersPanel() {
		 
		 	experimentModeCheckBox = new JCheckBox("Experiment Mode");	   
		 	experimentLabel = new JLabel("Experiment:");
	        populationSizeExperimentRadioButton = new JRadioButton("Population Size");
	        populationSizeExperimentRadioButton.setSelected(true);
	    	selectionExperimentRadioButton = new JRadioButton("Selection");
	    	experimentButtonGroup = new ButtonGroup();
	    	experimentPanel = new JPanel();	    	
	    	experimentButtonGroup.add(populationSizeExperimentRadioButton);
	    	experimentButtonGroup.add(selectionExperimentRadioButton);
	    	experimentPanel.add(populationSizeExperimentRadioButton);
	    	experimentPanel.add(selectionExperimentRadioButton);
	    	experimentIterationsLabel = new JLabel("Iterations per setting");
	    	experimentIterationsSpinner = new JSpinner(new SpinnerNumberModel(1, 0, 1000, 1));
	    	experimentIterationsSpinner.setMaximumSize(shiftOffWeightSpinner.getMinimumSize());
	    	startExperimentButton = new JButton("Start Experiment");
	    	
	    	experimentLabel.setEnabled(false);
	    	populationSizeExperimentRadioButton.setEnabled(false);
	    	selectionExperimentRadioButton.setEnabled(false);
	    	experimentIterationsLabel.setEnabled(false);
	    	experimentIterationsSpinner.setEnabled(false);
	    	startExperimentButton.setEnabled(false);
	    	
	    	experimentModeCheckBox.addActionListener(new ActionListener() {
	        	public void actionPerformed(ActionEvent event) {  
	        		if (((JCheckBox)event.getSource()).isSelected()) {
	        			setExperimentParametersEnabled(true);
	        	    	
	        	    	if (populationSizeExperimentRadioButton.isSelected()) {
	        	    		setPopulationParametersEnabled(false);
	        	    	}
	        	    	if (selectionExperimentRadioButton.isSelected()) {
	        	    		setSelectionParametersEnabled(false);
	        	    	}
	        		}
	        		else {
	        			setExperimentParametersEnabled(false);
	        			setGeneralParametersEnabled(true);
	        			//setPopulationParametersEnabled(true);
	        			//setSelectionParametersEnabled(true);
	        		}
	            }
	        });
	    	
	    	populationSizeExperimentRadioButton.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent event) {
					if (event.getStateChange() == ItemEvent.SELECTED) {
	        			setPopulationParametersEnabled(false);
	        		}
	        		else {
	        			setPopulationParametersEnabled(true);
	        		}
				}
	        });
	    	
	    	selectionExperimentRadioButton.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent event) {
					if (event.getStateChange() == ItemEvent.SELECTED) {
						setSelectionParametersEnabled(false);
	        		}
	        		else {
	        			setSelectionParametersEnabled(true);
	        		}
				}
	        });
	    	
	    	startExperimentButton = new JButton("Start Experiment");        
	        startExperimentButton.addActionListener(new ActionListener() {
	        	public void actionPerformed(ActionEvent event) {  
	        		RosterRenderer.disable();
	                setGeneralParametersEnabled(false);
	                setExperimentParametersEnabled(false);
	                
	                if(populationSizeExperimentRadioButton.isSelected()) {
	                	startPopulationSizeExperiment((int)experimentIterationsSpinner.getValue(),
	                			EXPERIMENT_POPULATION_MIN,
	                			EXPERIMENT_POPULATION_MAX,
	                			EXPERIMENT_POPULATION_GRAN);
	                }
	             }
	        });
	   		 
	        Box parameterBox = Box.createHorizontalBox();
	        parameterBox.add(Box.createHorizontalStrut(10));
	        
	        parameterBox.add(experimentModeCheckBox);
	        parameterBox.add(Box.createHorizontalStrut(10));
	        
	        parameterBox.add(experimentPanel);
	        parameterBox.add(Box.createHorizontalStrut(10));
	        
	        parameterBox.add(experimentIterationsLabel);
	        parameterBox.add(Box.createHorizontalStrut(10));
	        
	        parameterBox.add(experimentIterationsSpinner);
	        parameterBox.add(Box.createHorizontalStrut(10));
	        
	        parameterBox.add(startExperimentButton);
	        parameterBox.add(Box.createHorizontalStrut(10));
	        
	        parameterBox.setBorder(BorderFactory.createTitledBorder("Experiments"));
	        return parameterBox;
	        
	 }
	 protected void startPopulationSizeExperiment(int iterations, int populationMin,
			int populationMax, int populationGran) {
		 
		 	List<Integer> populationSizes = new ArrayList<Integer>();
		 
		 for (int i = populationMin; i <= populationMax; i += populationGran) {
			 for (int j = 0; j < iterations; j++) {
				 populationSizes.add(i);
			 }
		 }
		 
		 EvolutionTask evolutionTask = new EvolutionTask((Integer) populationSpinner.getValue(),
                 (Integer) elitismSpinner.getValue(),
                 abort.getTerminationCondition(),
                 new Stagnation(TERMINATION_STAGNATION, false),
                 new TargetFitness(0, false));
		 evolutionTask.setPopulationSizeExperimentParams(populationSizes);
		 evolutionTask.execute();
	}
		

	private void setGeneralParametersEnabled(boolean enabled) {
		 if (enabled) {
            abort.getControl().setEnabled(false);
            populationLabel.setEnabled(true);
            populationSpinner.setEnabled(true);
            elitismLabel.setEnabled(true);
            elitismSpinner.setEnabled(true);
            selectionLabel.setEnabled(true);
            selectionButtonPanel.setEnabled(true);
            rouletteWheelSelectionRadioButton.setEnabled(true);
            rankSelectionRadioButton.setEnabled(true);
            susSelectionRadioButton.setEnabled(true);
            weekendWeightLabel.setEnabled(true);
         	weekendWeightSpinner.setEnabled(true);
         	shiftOffWeightLabel.setEnabled(true);
         	shiftOffWeightSpinner.setEnabled(true);
         	maxAssignWeightLabel.setEnabled(true);
         	maxAssignWeightSpinner.setEnabled(true);	
         	coverWeightLabel.setEnabled(true);
         	coverWeightSpinner.setEnabled(true);
            startButton.setEnabled(true);
		 }
		 else {
			 abort.getControl().setEnabled(true);
	            populationLabel.setEnabled(false);
	            populationSpinner.setEnabled(false);
	            elitismLabel.setEnabled(false);
	            elitismSpinner.setEnabled(false);
	            selectionLabel.setEnabled(false);
	            selectionButtonPanel.setEnabled(false);
	            rouletteWheelSelectionRadioButton.setEnabled(false);
	            rankSelectionRadioButton.setEnabled(false);
	            susSelectionRadioButton.setEnabled(false);
	            weekendWeightLabel.setEnabled(false);
	         	weekendWeightSpinner.setEnabled(false);
	         	shiftOffWeightLabel.setEnabled(false);
	         	shiftOffWeightSpinner.setEnabled(false);
	         	maxAssignWeightLabel.setEnabled(false);
	         	maxAssignWeightSpinner.setEnabled(false);	
	         	coverWeightLabel.setEnabled(false);
	         	coverWeightSpinner.setEnabled(false);
	            startButton.setEnabled(false);
		 }
	 }
	 private void setExperimentParametersEnabled(boolean enabled) {
		 if (enabled) {
			 experimentLabel.setEnabled(true);
			 populationSizeExperimentRadioButton.setEnabled(true);
			 selectionExperimentRadioButton.setEnabled(true);
			 experimentIterationsLabel.setEnabled(true);
			 experimentIterationsSpinner.setEnabled(true);
			 startExperimentButton.setEnabled(true);
			 startButton.setEnabled(false);
		}
		else {
			
			experimentLabel.setEnabled(false);
			populationSizeExperimentRadioButton.setEnabled(false);
			selectionExperimentRadioButton.setEnabled(false);
			experimentIterationsLabel.setEnabled(false);
			experimentIterationsSpinner.setEnabled(false);
			startExperimentButton.setEnabled(false);
			//startButton.setEnabled(true);
		}
	 }
	 
	 private void setPopulationParametersEnabled(boolean enabled) {
		 if (enabled) {
			populationLabel.setEnabled(true);
		 	populationSpinner.setEnabled(true);
		 }
		 else {
			populationLabel.setEnabled(false);
		 	populationSpinner.setEnabled(false);
		 }
	 }
	 private void setSelectionParametersEnabled(boolean enabled) {
		 if (enabled) {
			 selectionLabel.setEnabled(true);
	    	rankSelectionRadioButton.setEnabled(true);
	    	rouletteWheelSelectionRadioButton.setEnabled(true);
	    	susSelectionRadioButton.setEnabled(true);
		}
		else {
			selectionLabel.setEnabled(false);
 	    	rankSelectionRadioButton.setEnabled(false);
 	    	rouletteWheelSelectionRadioButton.setEnabled(false);
 	    	susSelectionRadioButton.setEnabled(false);
		 }
	 }
	
	 public class EvolutionTask extends SwingBackgroundTask<Roster> {
		 		 
		 private int populationSize;
	     private int eliteCount;
	     private List<Integer> populationSizeExperimentParams;
	     private boolean isPopulationSizeExperiment;
	     private SelectionStrategy<Object> selection;
	     private TerminationCondition[] terminationConditions;


        EvolutionTask(int populationSize, int eliteCount, TerminationCondition... terminationConditions) {
        	
            this.populationSize = populationSize;
            this.eliteCount = eliteCount;
            this.terminationConditions = terminationConditions;
            this.isPopulationSizeExperiment = false;
        }
        @Override
        protected Roster performTask() throws Exception
        {
           	if(isPopulationSizeExperiment) {
           		this.populationSize = populationSizeExperimentParams.get(0);
           	}           	
        	RosterRenderer.disable();
        	Random rng = new MersenneTwisterRNG();
            RosterEvaluator fitnessEvaluator = new RosterEvaluator(
            		(Integer) shiftOffWeightSpinner.getValue(),
            		(Integer) weekendWeightSpinner.getValue(),
            		(Integer) maxAssignWeightSpinner.getValue(),
            		(Integer) coverWeightSpinner.getValue()            		
            		);
            RosterFactory rosterFactory = new RosterFactory(InputService.getNoOfEmployees(), InputService.getNoOfShifts(), InputService.getNoOfTasks());            
            EvolutionaryOperator<Roster> pipeline = probabilitiesPanel.createEvolutionPipeline();
            
            
            if (rouletteWheelSelectionRadioButton.isSelected()) {
            	selection = new RouletteWheelSelection();
            }
            else if (rankSelectionRadioButton.isSelected()) {
            	selection = new RankSelection();
            }
            else {
            	selection = new StochasticUniversalSampling();
            }

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
            populationLabel.setEnabled(true);
            populationSpinner.setEnabled(true);
            elitismLabel.setEnabled(true);
            elitismSpinner.setEnabled(true);
            selectionLabel.setEnabled(true);
            selectionButtonPanel.setEnabled(true);
            rouletteWheelSelectionRadioButton.setEnabled(true);
            rankSelectionRadioButton.setEnabled(true);
            susSelectionRadioButton.setEnabled(true);
            weekendWeightLabel.setEnabled(true);
        	weekendWeightSpinner.setEnabled(true);
        	shiftOffWeightLabel.setEnabled(true);
        	shiftOffWeightSpinner.setEnabled(true);
        	maxAssignWeightLabel.setEnabled(true);
        	maxAssignWeightSpinner.setEnabled(true);	
        	coverWeightLabel.setEnabled(true);
        	coverWeightSpinner.setEnabled(true);            
            startButton.setEnabled(true);
            
            if (isPopulationSizeExperiment) {
            	this.populationSizeExperimentParams.remove(0);
            	if (!populationSizeExperimentParams.isEmpty()) {
            		
            		if (populationSizeExperimentParams.size() == 1 || (populationSize == populationSizeExperimentParams.get(0) && populationSize != populationSizeExperimentParams.get(1))) {
            			evolutionMonitor.setRollup(true);
            		}
            		
            		RosterRenderer.disable();
	                setGeneralParametersEnabled(false);
	                setExperimentParametersEnabled(false);
            		
            		EvolutionTask evolutionTask = new EvolutionTask(this.populationSize,
                            this.eliteCount,
                            abort.getTerminationCondition(),
                            new Stagnation(TERMINATION_STAGNATION, false),
                            new TargetFitness(0, false));
           		 evolutionTask.setPopulationSizeExperimentParams(populationSizeExperimentParams);
           		 evolutionTask.execute();
            	}
            	else {
            		setExperimentParametersEnabled(true);
        	    	
        	    	if (populationSizeExperimentRadioButton.isSelected()) {
        	    		setPopulationParametersEnabled(false);
        	    	}
        	    	if (selectionExperimentRadioButton.isSelected()) {
        	    		setSelectionParametersEnabled(false);
        	    	}
            	}
            }
        }
        @Override
        protected void onError(Throwable throwable)
        {
            super.onError(throwable);
            postProcessing(null);
        }
        protected void setPopulationSizeExperimentParams(List<Integer> populationSizes) {
        	this.populationSizeExperimentParams = populationSizes;
        	this.isPopulationSizeExperiment = true;
        }
	 }

	public static JSpinner getWeekendWeightSpinner() {
		return weekendWeightSpinner;
	}


	public static JSpinner getShiftOffWeightSpinner() {
		return shiftOffWeightSpinner;
	}


	public static JSpinner getMaxAssignWeightSpinner() {
		return maxAssignWeightSpinner;
	}


	public static JSpinner getCoverWeightSpinner() {
		return coverWeightSpinner;
	}
	 
	 
}
