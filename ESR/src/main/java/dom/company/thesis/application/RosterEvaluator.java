package dom.company.thesis.application;

import java.util.List;

import org.uncommons.maths.binary.BitString;
import org.uncommons.watchmaker.framework.FitnessEvaluator;

import dom.company.thesis.model.Roster;
import dom.company.thesis.model.Task;
import dom.company.thesis.service.InputService;

public class RosterEvaluator implements FitnessEvaluator<Roster>
{

 public RosterEvaluator() {    
 }

public double getFitness(Roster candidate, List<? extends Roster> population) {
	
	int penalty = 0;
	for (int s = 0; s < candidate.getNoOfShifts(); s++) {
		
		int[] uncoveredTasks = InputService.getCoverRequirementMatrix()[s];
		 
		for (int e = 0; e < candidate.getNoOfEmployees(); e++) {
			Integer assignedTaskCombination = candidate.getAssignments()[e * candidate.getNoOfShifts() + s];
			 
			//check for availability
    		if (assignedTaskCombination != 0 && InputService.getAvailabilityMatrix()[s][e] == 0) {
    			penalty++;
    			break;
    		}
    		 
    		//check for ability
    		if (InputService.getAbilityMatrix()[assignedTaskCombination][e] != 1) {
    			penalty++;
    			break;
    		}
    		 
    		for (Task task : InputService.getTaskCombinationMap().get(assignedTaskCombination)) {
    			int taskNr = InputService.getReverseTaskMap().get(task);
    			if (uncoveredTasks[taskNr] > 0) {
    				uncoveredTasks[taskNr] --;
    			}
    			else {
    				//penalty++;
    			}
    		}
    		 
		}
		for (int i = 0; i < uncoveredTasks.length; i++) {
			penalty += uncoveredTasks[i];
		}
	}
     return penalty;
 }


 /**
  * {@inheritDoc}
  */
 public boolean isNatural()
 {
     return false;
 }
}
