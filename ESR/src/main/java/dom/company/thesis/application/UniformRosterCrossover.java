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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.operators.AbstractCrossover;

import dom.company.thesis.model.Roster;
import dom.company.thesis.service.InputService;

/**
 * Cross-over with a configurable number of points (fixed or random) for
 * arrays of primitive ints.
 * @author Daniel Dyer
 */
public class UniformRosterCrossover extends AbstractCrossover<Roster>
{
    /**
     * Default is single-point cross-over, applied to all parents.
     */
    public UniformRosterCrossover()
    {
        this(1);
    }


    /**
     * Cross-over with a fixed number of cross-over points.
     * @param crossoverPoints The constant number of cross-over points
     * to use for all cross-over operations.
     */
    public UniformRosterCrossover(int crossoverPoints)
    {
        super(crossoverPoints);
    }


    /**
     * Cross-over with a fixed number of cross-over points.  Cross-over
     * may or may not be applied to a given pair of parents depending on
     * the {@code crossoverProbability}.
     * @param crossoverPoints The constant number of cross-over points
     * to use for all cross-over operations.
     * @param crossoverProbability The probability that, once selected,
     * a pair of parents will be subjected to cross-over rather than
     * being copied, unchanged, into the output population.
     */
    public UniformRosterCrossover(int crossoverPoints, Probability crossoverProbability)
    {
        super(crossoverPoints, crossoverProbability);
    }


    /**
     * Cross-over with a variable number of cross-over points.
     * @param crossoverPointsVariable A random variable that provides a number
     * of cross-over points for each cross-over operation.
     */
    public UniformRosterCrossover(NumberGenerator<Integer> crossoverPointsVariable)
    {
        super(crossoverPointsVariable);
    }


    /**
     * Sets up a cross-over implementation that uses a variable number of cross-over
     * points.  Cross-over is applied to a proportion of selected parent pairs, with
     * the remainder copied unchanged into the output population.  The size of this
     * evolved proportion is controlled by the {@code crossoverProbabilityVariable}
     * parameter.
     * @param crossoverPointsVariable A variable that provides a (possibly constant,
     * possibly random) number of cross-over points for each cross-over operation.
     * @param crossoverProbabilityVariable A variable that controls the probability
     * that, once selected, a pair of parents will be subjected to cross-over rather
     * than being copied, unchanged, into the output population.
     */
    public UniformRosterCrossover(NumberGenerator<Integer> crossoverPointsVariable,
                             NumberGenerator<Probability> crossoverProbabilityVariable)
    {
        super(crossoverPointsVariable, crossoverProbabilityVariable);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected List<Roster> mate(Roster parent1,
                               Roster parent2,
                               int numberOfCrossoverPoints,
                               Random rng)
    {
    	
    	int[] parent1vector = parent1.getAssignments();	  	
    	int[] parent2vector = parent2.getAssignments();
    	
    	if (parent1vector.length != parent2vector.length)
        {
            throw new IllegalArgumentException("Cannot perform cross-over with different length parents.");
        }
        int[] offspring1vector = new int[parent1vector.length];
        System.arraycopy(parent1vector, 0, offspring1vector, 0, parent1vector.length);
        int[] offspring2vector = new int[parent2vector.length];
        System.arraycopy(parent2vector, 0, offspring2vector, 0, parent2vector.length);
        // Apply as many cross-overs as required.
        int[] temp = new int[parent1vector.length];
        for (int i = 0; i < parent1vector.length-1; i++)
        {
            // Cross-over index is always greater than zero and less than
            // the length of the parent so that we always pick a point that
            // will result in a meaningful cross-over.
            int crossoverIndex = (1 + i);
            System.arraycopy(offspring1vector, 0, temp, 0, crossoverIndex);
            System.arraycopy(offspring2vector, 0, offspring1vector, 0, crossoverIndex);
            System.arraycopy(temp, 0, offspring2vector, 0, crossoverIndex);
        }
        List<Roster> result = new ArrayList<Roster>(2);
        result.add(new Roster(parent1.getNoOfEmployees(), parent1.getNoOfShifts(), parent1.getNoOfTasks(), offspring1vector));
        result.add(new Roster(parent2.getNoOfEmployees(), parent2.getNoOfShifts(), parent2.getNoOfTasks(), offspring2vector));
        return result;
    }
    /*
    private int[] convertTo1d(int[][] matrix, int rows, int columns) {
    	int vector[] = new int[rows * columns];
    	for (int i = 0; i < rows; i++) {
    		int row[] = matrix[i];
    		for (int j = 0; j < row.length; j++) {
    			int value = matrix[i][j];
    			vector[i * row.length + j] = value;
    		}
    	}
    	return vector;
    }
    */
}
