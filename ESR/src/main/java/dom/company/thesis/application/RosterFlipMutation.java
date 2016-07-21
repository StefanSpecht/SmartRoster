
package dom.company.thesis.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.uncommons.maths.number.ConstantGenerator;
import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

import dom.company.thesis.model.Roster;
import dom.company.thesis.service.InputService;

public class RosterFlipMutation implements EvolutionaryOperator<Roster>
{
    private final NumberGenerator<Probability> mutationProbability;
    private double mutationProbabilityValue;
    private int nextMutation;


    /**
     * Creates a mutation operator for bit strings with the specified probability that a given
     * bit string will be mutated, with exactly one bit being flipped.
     * @param mutationProbability The probability of a candidate being mutated.
     */
    public RosterFlipMutation(Probability mutationProbability)
    {
        this.mutationProbability = new ConstantGenerator<Probability>(mutationProbability);
        this.mutationProbabilityValue = mutationProbability.doubleValue();
    }
    
    public RosterFlipMutation(NumberGenerator<Probability> mutationProbability)
    {
        this.mutationProbability = mutationProbability;
        this.mutationProbabilityValue = mutationProbability.nextValue().doubleValue();
    }

    public List<Roster> apply(List<Roster> selectedCandidates, Random rng)
    {
    	List<Roster> mutatedPopulation = new ArrayList<Roster>(selectedCandidates.size());
    	this.mutationProbabilityValue = mutationProbability.nextValue().doubleValue();
    	
    	if (mutationProbabilityValue > 0) {
    		nextMutation = getNextMutationIndex(0,rng);
            for (Roster roster : selectedCandidates)
            {
                mutatedPopulation.add(mutateRoster(roster, rng));
            }
    	}
    	else {
    		for (Roster roster : selectedCandidates)
            {
                mutatedPopulation.add(roster.clone());
            }
    	}
        return mutatedPopulation;
    }

    int getNextMutationIndex(int current, Random rng) {
    	double random = rng.nextDouble();
    	int next = current + (int)(Math.log(random) / Math.log(1 - mutationProbabilityValue));
    	return next;
    }

    /**
     * Mutate a single bit string.  Zero or more bits may be flipped.  The
     * probability of any given bit being flipped is governed by the probability
     * generator configured for this mutation operator.
     * @param bitString The bit string to mutate.
     * @param rng A source of randomness.
     * @return The mutated bit string.
     */
    private Roster mutateRoster(Roster roster, Random rng)
    {
        Roster mutatedRoster = roster.clone();
        
        while (nextMutation < mutatedRoster.getAssignments().length) {
        	mutatedRoster.mutateAssignment(nextMutation,rng);
        	nextMutation = getNextMutationIndex(nextMutation, rng);
        }
        nextMutation = nextMutation - mutatedRoster.getAssignments().length;
    	
        return roster;
    }
}

