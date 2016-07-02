
package dom.company.thesis.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.uncommons.maths.number.ConstantGenerator;
import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

import dom.company.thesis.model.Roster;

public class RosterMutation implements EvolutionaryOperator<Roster>
{
    private final NumberGenerator<Probability> mutationProbability;
    private final NumberGenerator<Integer> mutationCount;


    /**
     * Creates a mutation operator for bit strings with the specified probability that a given
     * bit string will be mutated, with exactly one bit being flipped.
     * @param mutationProbability The probability of a candidate being mutated.
     */
    public RosterMutation(Probability mutationProbability)
    {
        this(new ConstantGenerator<Probability>(mutationProbability),
             new ConstantGenerator<Integer>(1));
    }


    /**
     * Creates a mutation operator for bit strings, with the probability that any
     * given bit will be flipped governed by the specified number generator.
     * @param mutationProbability The (possibly variable) probability of a candidate
     * bit string being mutated at all.
     * @param mutationCount The (possibly variable) number of bits that will be flipped
     * on any candidate bit string that is selected for mutation.
     */
    public RosterMutation(NumberGenerator<Probability> mutationProbability,
                             NumberGenerator<Integer> mutationCount)
    {
        this.mutationProbability = mutationProbability;
        this.mutationCount = mutationCount;
    }
    public RosterMutation(NumberGenerator<Probability> mutationProbability)
	{
	this(mutationProbability, new ConstantGenerator<Integer>(1));
	}


    public List<Roster> apply(List<Roster> selectedCandidates, Random rng)
    {
        List<Roster> mutatedPopulation = new ArrayList<Roster>(selectedCandidates.size());
        for (Roster roster : selectedCandidates)
        {
            mutatedPopulation.add(mutateRoster(roster, rng));
        }
        return mutatedPopulation;
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
        if (mutationProbability.nextValue().nextEvent(rng))
        {
            Roster mutatedRoster = roster.clone();
            int mutations = mutationCount.nextValue();
            for (int i = 0; i < mutations; i++)
            {
                mutatedRoster.mutateAssignment(rng.nextInt(mutatedRoster.getAssignments().length), rng);
            }
            return mutatedRoster;
        }
        return roster;
    }
}
