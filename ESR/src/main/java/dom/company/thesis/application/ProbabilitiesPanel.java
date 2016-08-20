//=============================================================================
// Copyright 2006-2010 Daniel W. Dyer
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//=============================================================================
package dom.company.thesis.application;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import org.uncommons.maths.number.ConstantGenerator;
import org.uncommons.maths.random.GaussianGenerator;
import org.uncommons.maths.random.Probability;
import org.uncommons.swing.SpringUtilities;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.factories.StringFactory;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.operators.ListCrossover;
import org.uncommons.watchmaker.framework.operators.ListOperator;
import org.uncommons.watchmaker.framework.operators.StringCrossover;
import org.uncommons.watchmaker.framework.operators.StringMutation;
import org.uncommons.watchmaker.framework.termination.TargetFitness;
import org.uncommons.watchmaker.swing.ProbabilityParameterControl;

import dom.company.thesis.application.SmartRosterApplet.EvolutionTask;
import dom.company.thesis.gui.RosterRenderer;
import dom.company.thesis.model.Roster;

class ProbabilitiesPanel extends JPanel
{
    private static final Probability ONE_TENTH = new Probability(0.1d);
    //private static final Probability ONE_HUNDREDTH = new Probability(0.01d);
    
    private final ProbabilityParameterControl classicCrossoverControl;
    private ProbabilityParameterControl advancedCrossoverControl;
    //private final ProbabilityParameterControl flipMutationControl;
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
                Probability.ONE);
        
        
        classicCrossOverPointsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        
        classicCrossOverPointsRadioButton = new JRadioButton("# X-over points");
        classicUniformCrossOverRadioButton = new JRadioButton("Uniform Cross-over"); 
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
                
        add(new JLabel("Classic Cross-over: "));
        add(classicCrossoverControl.getControl());
        classicCrossoverControl.setDescription("For each pair of solutions, the probability that "
        		+ "classic cross-over is applied.");
        
        add(classicCrossOverButtonPanel);
        
        // Advanced Cross-over
        
        advancedCrossoverControl = new ProbabilityParameterControl(
        		Probability.ZERO,
        		Probability.ONE,
                2,
                Probability.ONE);
        
        
        advancedCrossOverPointsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        
        advancedCrossOverPointsRadioButton = new JRadioButton("# X-over points");
        advancedUniformCrossOverRadioButton = new JRadioButton("Uniform Cross-over"); 
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
                
        add(new JLabel("segmented Cross-over: "));
        add(advancedCrossoverControl.getControl());
        advancedCrossoverControl.setDescription("For each pair of solutions, the probability that "
        		+ "advanced cross-over is applied.");
        
        add(advancedCrossOverButtonPanel);
        
        /*
        // flip Mutation
        flipMutationControl = new ProbabilityParameterControl(Probability.ZERO,
                                                               ONE_TENTH,
                                                               5,
                                                               new Probability(0.01));
        add(new JLabel("Flip Mutation: "));
        add(flipMutationControl.getControl());
        flipMutationControl.setDescription("For each allel, the probability that "
                                            + "it is changed randomly");
        add(new JLabel(""));
        */
        
        // swap Mutation
        swapMutationControl = new ProbabilityParameterControl(Probability.ZERO,
                                                               ONE_TENTH,
                                                               4,
                                                               new Probability(0.01));
        add(new JLabel("Pull-Push-Mutation: "));
        add(swapMutationControl.getControl());
        swapMutationControl.setDescription("For each allel, the probability that "
                                            + "it is swapped with another allel");
        add(new JLabel(""));

       
        /**
        // Set component names for easy look-up from tests.
        crossoverControl.getControl().setName("Crossover");
        flipMutationControl.getControl().setName("flipMutation");
       **/
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
        
        //Advanced Cross-over
        if (advancedCrossOverPointsRadioButton.isSelected()) {
        	operators.add(new AdvancedRosterCrossover(new ConstantGenerator<Integer>((Integer)advancedCrossOverPointsSpinner.getValue()),advancedCrossoverControl.getNumberGenerator()));
        }
        else {
        	operators.add(new UniformAdvancedRosterCrossover());
        }
        //operators.add(new ClassicRosterCrossover(new ConstantGenerator<Integer>(2),crossoverControl.getNumberGenerator()));
        //operators.add(new UniformRosterCrossover());
        //operators.add(new StringMutation(getAlphabet(), mutationControl.getNumberGenerator()));
        //operators.add(new RosterFlipMutation(flipMutationControl.getNumberGenerator()));
        operators.add(new RosterSwapMutation(swapMutationControl.getNumberGenerator()));
       
        return new EvolutionPipeline<Roster>(operators);
    }
    /*
    public void setCrossPointParametersEnabled(boolean enabled) {
    	classicCrossoverControl.getControl().setEnabled(enabled);
    	classicCrossOverButtonPanel.setEnabled(enabled);
    	advancedCrossOverButtonPanel.setEnabled(enabled);
	 }
	 */
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
