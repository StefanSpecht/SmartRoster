package dom.company.thesis.application;

import java.util.List;

import org.uncommons.maths.binary.BitString;
import org.uncommons.watchmaker.framework.FitnessEvaluator;

import dom.company.thesis.model.Roster;
import dom.company.thesis.service.InputService;

public class RosterEvaluator implements FitnessEvaluator<Roster>
{

 public RosterEvaluator() {    
 }

public double getFitness(Roster candidate,
                          List<? extends Roster> population)
 {
    int panalty = 0;
     for (int s = 0; s < candidate.getNoOfShifts(); ++s) {
    	 for (int e = 0; e < candidate.getNoOfEmployees(); ++e) {
    		 
    		 if (candidate.getAssignments()[s * candidate.getNoOfEmployees() + e] != 0 && InputService.getAvailabilityMatrix()[s][e] == 0) {
    			 panalty++;
    		 }
    	 }
     }
     return panalty;
 }


 /**
  * {@inheritDoc}
  */
 public boolean isNatural()
 {
     return false;
 }
}
