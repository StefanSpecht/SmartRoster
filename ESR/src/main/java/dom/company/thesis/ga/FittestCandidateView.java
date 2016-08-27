package dom.company.thesis.ga;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

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

class FittestCandidateView<T> extends JPanel implements IslandEvolutionObserver<T>
{
    private static final long serialVersionUID = 9089425083038226481L;

	private static final Font BIG_FONT = new Font("Dialog", Font.BOLD, 16);
    private final Renderer<? super T, JComponent> renderer;
    private final JLabel fitnessLabel = new JLabel("N/A", JLabel.CENTER);
    private final JScrollPane scroller = new JScrollPane();

    private T fittestCandidate = null;
    private JComponent renderedCandidate = null;

    FittestCandidateView(Renderer<? super T, JComponent> renderer)
    {
        super(new BorderLayout(0, 10));
        this.renderer = renderer;

        JPanel header = new JPanel(new BorderLayout());
        JLabel label = new JLabel((rightPadding("Fitness",60) + (rightPadding("Penalty",30))), JLabel.CENTER);
        label.setFont(BIG_FONT);
        header.add(label, BorderLayout.NORTH);
        header.add(fitnessLabel, BorderLayout.CENTER);
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
                fittestCandidate = populationData.getBestCandidate();
                
                List<Roster> emptyList = new ArrayList<Roster>();
                RosterEvaluator rosterEvaluator = new RosterEvaluator(
                		(Integer) SmartRosterApplet.getShiftOffWeightSpinner().getValue(),
                		(Integer) SmartRosterApplet.getWeekendWeightSpinner().getValue(),
                		(Integer) SmartRosterApplet.getMaxAssignWeightSpinner().getValue(),
                		(Integer) SmartRosterApplet.getCoverWeightSpinner().getValue()            		
                		);
                rosterEvaluator.getFitness((Roster) fittestCandidate, emptyList);
                
                fitnessLabel.setText(rightPadding(String.valueOf(populationData.getBestCandidateFitness()), 40)
                		+ rightPadding("ShiftOff: " + String.valueOf(rosterEvaluator.getPenaltyShiftOffPreferences()), 15)
                		+ rightPadding("Weekends: " + String.valueOf(rosterEvaluator.getPenaltyCompleteWeekends()), 15)
                		+ rightPadding("MaxAssign: " + String.valueOf(rosterEvaluator.getPenaltyMaxAssignmentsPerWeek()), 15)
                		+ rightPadding("CoverReq: " + String.valueOf(rosterEvaluator.getPenaltyCoverRequirements()), 3)
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
    
    private String rightPadding(String str, int num) {
        return String.format("%1$-" + num + "s", str);
      }
}
