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
package dom.company.thesis.ga;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.List;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.interactive.Renderer;
import org.uncommons.watchmaker.framework.islands.IslandEvolutionObserver;

import dom.company.thesis.application.RosterEvaluator;
import dom.company.thesis.application.SmartRosterApplet;
import dom.company.thesis.model.Roster;

/**
 * {@link EvolutionMonitor} view for displaying a graphical representation
 * of the fittest candidate found so far.  This allows us to monitor the
 * progress of an evolutionary algorithm.
 * @param <T> The type of the evolved entity displayed by this component.
 * @author Daniel Dyer
 */
class FittestCandidateView<T> extends JPanel implements IslandEvolutionObserver<T>
{
    private static final Font BIG_FONT = new Font("Dialog", Font.BOLD, 16);

    private final Renderer<? super T, JComponent> renderer;
    private final JLabel fitnessLabel = new JLabel("N/A", JLabel.CENTER);
    private final JLabel penaltyLabel = new JLabel("ShiftOff: N/A Weekends: N/A MaxAssign: N/A TaskCover: N/A", JLabel.CENTER);
    private final JScrollPane scroller = new JScrollPane();

    private T fittestCandidate = null;
    private JComponent renderedCandidate = null;

    /**
     * Creates a Swing view that uses the specified renderer to display
     * evolved entities.
     * @param renderer A renderer that convert evolved entities of the type
     * recognised by this view into Swing components.
     */
    FittestCandidateView(Renderer<? super T, JComponent> renderer)
    {
        super(new BorderLayout(0, 10));
        this.renderer = renderer;

        JPanel header = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Fitness", JLabel.CENTER);
        header.add(label, BorderLayout.NORTH);
        fitnessLabel.setFont(BIG_FONT);
        header.add(fitnessLabel, BorderLayout.CENTER);
        header.add(penaltyLabel, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        scroller.setBackground(null);
        scroller.getViewport().setBackground(null);
        scroller.setBorder(null);
        add(scroller, BorderLayout.CENTER);

        // Set names for easier indentification in unit tests.
        fitnessLabel.setName("FitnessLabel");
    }

    
    public void populationUpdate(final PopulationData<? extends T> populationData)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                fitnessLabel.setText(String.valueOf(populationData.getBestCandidateFitness()));
                fittestCandidate = populationData.getBestCandidate();
                
                List<Roster> emptyList = new ArrayList<Roster>();
                RosterEvaluator rosterEvaluator = new RosterEvaluator(
                		(Integer) SmartRosterApplet.getShiftOffWeightSpinner().getValue(),
                		(Integer) SmartRosterApplet.getWeekendWeightSpinner().getValue(),
                		(Integer) SmartRosterApplet.getMaxAssignWeightSpinner().getValue(),
                		(Integer) SmartRosterApplet.getCoverWeightSpinner().getValue()            		
                		);
                rosterEvaluator.getFitness((Roster) fittestCandidate, emptyList);
                penaltyLabel.setText(
                		"ShiftOff: " + rosterEvaluator.getPenaltyShiftOffPreferences()
                		+ "Weekends" + rosterEvaluator.getPenaltyCompleteWeekends()
                		+ "MaxAssign" + rosterEvaluator.getPenaltyMaxAssignmentsPerWeek()
                		+ "TaskCover" + rosterEvaluator.getPenaltyCoverRequirements()
                		);
                		
                
                renderedCandidate = renderer.render(fittestCandidate);
                scroller.setViewportView(renderedCandidate);
                
            }
        });
    }


    public void islandPopulationUpdate(int islandIndex, final PopulationData<? extends T> populationData)
    {
        // Do nothing.        
    }
}
