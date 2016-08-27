package dom.company.thesis.ga;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.operators.AbstractCrossover;

import dom.company.thesis.model.Roster;

public class ClassicRosterCrossover extends AbstractCrossover<Roster>
{
    public ClassicRosterCrossover()
    {
        this(1);
    }

    public ClassicRosterCrossover(int crossoverPoints)
    {
        super(crossoverPoints);
    }

    public ClassicRosterCrossover(int crossoverPoints, Probability crossoverProbability)
    {
        super(crossoverPoints, crossoverProbability);
    }

    public ClassicRosterCrossover(NumberGenerator<Integer> crossoverPointsVariable)
    {
        super(crossoverPointsVariable);
    }

    public ClassicRosterCrossover(NumberGenerator<Integer> crossoverPointsVariable,
                             NumberGenerator<Probability> crossoverProbabilityVariable)
    {
        super(crossoverPointsVariable, crossoverProbabilityVariable);
    }

    @Override
    protected List<Roster> mate(Roster parent1,
                               Roster parent2,
                               int numberOfCrossoverPoints,
                               Random rng) {
    	
    	int[] parent1vector = parent1.getAssignments();	  	
    	int[] parent2vector = parent2.getAssignments();
    	
    	if (parent1vector.length != parent2vector.length) {
            throw new IllegalArgumentException("Cannot perform cross-over with different length parents.");
        }
        int[] offspring1vector = new int[parent1vector.length];
        System.arraycopy(parent1vector, 0, offspring1vector, 0, parent1vector.length);
        int[] offspring2vector = new int[parent2vector.length];
        System.arraycopy(parent2vector, 0, offspring2vector, 0, parent2vector.length);
        // Apply as many cross-overs as required.
        int[] temp = new int[parent1vector.length];
        for (int i = 0; i < numberOfCrossoverPoints; i++) {
            // Cross-over index is always greater than zero and less than
            // the length of the parent so that we always pick a point that
            // will result in a meaningful cross-over.
            int crossoverIndex = (1 + rng.nextInt(parent1vector.length - 1));
            System.arraycopy(offspring1vector, 0, temp, 0, crossoverIndex);
            System.arraycopy(offspring2vector, 0, offspring1vector, 0, crossoverIndex);
            System.arraycopy(temp, 0, offspring2vector, 0, crossoverIndex);
        }
        List<Roster> result = new ArrayList<Roster>(2);
        result.add(new Roster(parent1.getNoOfEmployees(), parent1.getNoOfShifts(), parent1.getNoOfTasks(), offspring1vector));
        result.add(new Roster(parent2.getNoOfEmployees(), parent2.getNoOfShifts(), parent2.getNoOfTasks(), offspring2vector));
        return result;
    }
}
