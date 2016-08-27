package dom.company.thesis.ga;

import java.util.Random;

import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;

import dom.company.thesis.model.Roster;

public class RosterFactory extends AbstractCandidateFactory<Roster>
{
    private final int noOfTasks;
    private final int noOfShifts;
    private final int noOfEmployees;

    public RosterFactory(int noOfEmployees, int noOfShifts, int noOfTasks) {
        this.noOfTasks = noOfTasks;
        this.noOfShifts = noOfShifts;
        this.noOfEmployees = noOfEmployees;
    }

    public Roster generateRandomCandidate(Random rng) {
    	return new Roster(noOfEmployees, noOfShifts, noOfTasks, rng); 
    }
}
