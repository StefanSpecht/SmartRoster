package dom.company.thesis.application;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;

import org.uncommons.maths.number.ConstantGenerator;
import org.uncommons.maths.random.Probability;
import org.uncommons.swing.SpringUtilities;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.swing.ProbabilityParameterControl;

import dom.company.thesis.ga.AdvancedRosterCrossover;
import dom.company.thesis.ga.ClassicRosterCrossover;
import dom.company.thesis.ga.RosterSwapMutation;
import dom.company.thesis.ga.UniformAdvancedRosterCrossover;
import dom.company.thesis.ga.UniformClassicRosterCrossover;
import dom.company.thesis.model.Roster;

class ProbabilitiesPanel extends JPanel
{
   	private static final long serialVersionUID = 784421494515948311L;

	private static final Probability ONE_TENTH = new Probability(0.1d);    
    private final ProbabilityParameterControl classicCrossoverControl;
    private ProbabilityParameterControl advancedCrossoverControl;
    private ProbabilityParameterControl swapMutationControl;
    
    private ButtonGroup classicCrossOverButtonGroup;
    private ButtonGroup advancedCrossOverButtonGroup;
    private JRadioButton classicCrossOverPointsRadioButton;
    private JRadioButton classicUniformCrossOverRadioButton;
    private JRadioButton advancedCrossOverPointsRadioButton;
    private JRadioButton advancedUniformCrossOverRadioButton;
    private JPanel classicCrossOverButtonPanel;
    private JPanel advancedCrossOverButtonPanel;    
    private JSpinner classicCrossOverPointsSpinner;
    private JSpinner advancedCrossOverPointsSpinner;
        
    ProbabilitiesPanel()
    {
        super(new SpringLayout());
        
        //Classic Crossover        
        classicCrossoverControl = new ProbabilityParameterControl(
        		Probability.ZERO,
        		Probability.ONE,
                2,
                Probability.ZERO);        
        
        classicCrossOverPointsSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 1000, 1));        
        classicCrossOverPointsRadioButton = new JRadioButton("# X-over points");
        classicUniformCrossOverRadioButton = new JRadioButton("Uniform Crossover"); 
        classicCrossOverPointsRadioButton.setSelected(true);
        classicCrossOverPointsRadioButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent event) {  
        		classicCrossOverPointsSpinner.setEnabled(true);
            }
        });
        classicUniformCrossOverRadioButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent event) {  
        		classicCrossOverPointsSpinner.setEnabled(false);
            }
        });
        classicCrossOverButtonGroup = new ButtonGroup();
        classicCrossOverButtonGroup.add(classicCrossOverPointsRadioButton);
        classicCrossOverButtonGroup.add(classicUniformCrossOverRadioButton);
        
        classicCrossOverButtonPanel = new JPanel();
        classicCrossOverButtonPanel.add(classicCrossOverPointsRadioButton);
        classicCrossOverButtonPanel.add(classicCrossOverPointsSpinner);
        classicCrossOverButtonPanel.add(classicUniformCrossOverRadioButton);
                
        add(new JLabel("Classic Crossover: "));
        add(classicCrossoverControl.getControl());
        classicCrossoverControl.setDescription("For each pair of solutions, the probability that "
        		+ "classic crossover is applied.");
        add(classicCrossOverButtonPanel);
        
        // Advanced Cross-over        
        advancedCrossoverControl = new ProbabilityParameterControl(
        		Probability.ZERO,
        		Probability.ONE,
                2,
                new Probability(0.9d));        
        
        advancedCrossOverPointsSpinner = new JSpinner(new SpinnerNumberModel(18, 1, 1000, 1));        
        advancedCrossOverPointsRadioButton = new JRadioButton("# X-over points");
        advancedUniformCrossOverRadioButton = new JRadioButton("Uniform Crossover"); 
        advancedCrossOverPointsRadioButton.setSelected(true);
        advancedCrossOverPointsRadioButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent event) {  
        		advancedCrossOverPointsSpinner.setEnabled(true);
            }
        });
        advancedUniformCrossOverRadioButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent event) {  
        		advancedCrossOverPointsSpinner.setEnabled(false);
            }
        });
        advancedCrossOverButtonGroup = new ButtonGroup();
        advancedCrossOverButtonGroup.add(advancedCrossOverPointsRadioButton);
        advancedCrossOverButtonGroup.add(advancedUniformCrossOverRadioButton);        
        advancedCrossOverButtonPanel = new JPanel();
        advancedCrossOverButtonPanel.add(advancedCrossOverPointsRadioButton);
        advancedCrossOverButtonPanel.add(advancedCrossOverPointsSpinner);
        advancedCrossOverButtonPanel.add(advancedUniformCrossOverRadioButton);
                
        add(new JLabel("Segmented Crossover: "));
        add(advancedCrossoverControl.getControl());
        advancedCrossoverControl.setDescription("For each pair of solutions, the probability that "
        		+ "segmented crossover is applied.");
         add(advancedCrossOverButtonPanel);
        
        // Pull-Push-Mutation (former named "swapMutation")
        swapMutationControl = new ProbabilityParameterControl(Probability.ZERO,
                                                               ONE_TENTH,
                                                               4,
                                                               new Probability(0.001));
        add(new JLabel("Pull-Push-Mutation: "));
        add(swapMutationControl.getControl());
        swapMutationControl.setDescription("For each allel, the probability that "
                                            + "Pull-Push-Mutation is applied");
        add(new JLabel(""));

        SpringUtilities.makeCompactGrid(this, 3, 3, 10, 0, 10, 0);
    }

    public EvolutionaryOperator<Roster> createEvolutionPipeline() {
    	
        List<EvolutionaryOperator<Roster>> operators
            = new LinkedList<EvolutionaryOperator<Roster>>();
        
        //Classic Cross-over
        if (classicCrossOverPointsRadioButton.isSelected()) {
        	operators.add(new ClassicRosterCrossover(new ConstantGenerator<Integer>((Integer)classicCrossOverPointsSpinner.getValue()),classicCrossoverControl.getNumberGenerator()));
        }
        else {
        	operators.add(new UniformClassicRosterCrossover());
        }
        
        //Segmented Crossover (former named "Advanced Crossover")
        if (advancedCrossOverPointsRadioButton.isSelected()) {
        	operators.add(new AdvancedRosterCrossover(new ConstantGenerator<Integer>((Integer)advancedCrossOverPointsSpinner.getValue()),advancedCrossoverControl.getNumberGenerator()));
        }
        else {
        	operators.add(new UniformAdvancedRosterCrossover());
        }
        operators.add(new RosterSwapMutation(swapMutationControl.getNumberGenerator()));
       
        return new EvolutionPipeline<Roster>(operators);
    }
  
    public void setCrossPoints(int i) {
    	this.advancedCrossOverPointsSpinner.setValue((Object) i);
    }
    public void setPc(double i) {
    	this.advancedCrossoverControl = new ProbabilityParameterControl(
        		Probability.ZERO,
        		Probability.ONE,
                2,
                new Probability(i));
    }
    public void setPm(double i) {
    	this.swapMutationControl = new ProbabilityParameterControl(Probability.ZERO,
                ONE_TENTH,
                4,
                new Probability(i));
    }
}
