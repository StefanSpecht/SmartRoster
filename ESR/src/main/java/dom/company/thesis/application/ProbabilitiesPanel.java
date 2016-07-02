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
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
import org.uncommons.watchmaker.swing.ProbabilityParameterControl;

import dom.company.thesis.model.Roster;

class ProbabilitiesPanel extends JPanel
{
    private static final Probability ONE_TENTH = new Probability(0.1d);
    
    private final ProbabilityParameterControl crossoverControl;
    private final ProbabilityParameterControl mutationControl;
        
    ProbabilitiesPanel()
    {
        super(new SpringLayout());
        
        crossoverControl = new ProbabilityParameterControl(
        		Probability.ZERO,
        		Probability.ONE,
                2,
                Probability.ONE);
        
        add(new JLabel("Cross-over: "));
        add(crossoverControl.getControl());
        crossoverControl.setDescription("For each PAIR of solutions, the probability that "
        		+ "2-point cross-over is applied.");
        
        mutationControl = new ProbabilityParameterControl(Probability.ZERO,
                                                               ONE_TENTH,
                                                               3,
                                                               new Probability(0.02));
        add(new JLabel("Mutation: "));
        add(mutationControl.getControl());
        mutationControl.setDescription("For each solution, the probability that a "
                                            + "randomly-selected allel will be mutated.");

       
        // Set component names for easy look-up from tests.
        crossoverControl.getControl().setName("Crossover");
        mutationControl.getControl().setName("Mutation");
       
        SpringUtilities.makeCompactGrid(this, 1, 4, 10, 0, 10, 0);
    }


    public EvolutionaryOperator<Roster> createEvolutionPipeline() {
    	
        List<EvolutionaryOperator<Roster>> operators
            = new LinkedList<EvolutionaryOperator<Roster>>();
        operators.add(new RosterCrossover(120));
        //operators.add(new UniformRosterCrossover());
        //operators.add(new StringMutation(getAlphabet(), mutationControl.getNumberGenerator()));
       
        return new EvolutionPipeline<Roster>(operators);
    }

}
