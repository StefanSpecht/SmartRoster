package dom.company.thesis.application;

import java.util.List;

import org.uncommons.maths.binary.BitString;
import org.uncommons.watchmaker.framework.FitnessEvaluator;

import dom.company.thesis.model.Employee;
import dom.company.thesis.model.Numbering;
import dom.company.thesis.model.Roster;
import dom.company.thesis.model.Task;
import dom.company.thesis.service.InputService;

public class RosterEvaluator implements FitnessEvaluator<Roster>
{

 public RosterEvaluator() {    
 }

public double getFitness(Roster candidate, List<? extends Roster> population) {
	
	int penaltyShiftOffPreferences = 0;
	int penaltyCompleteWeekends = 0;
	int penaltyMaxAssignmentsPerWeek = 0;
	int penaltyCoverRequirements = 0;
	int costCoverRequirements = 2;
	
	//Evaluate each employee
	for (int e = 0; e < InputService.getNoOfEmployees(); e++) {
		
		Employee employee = InputService.getEmployeeMap().get(e);
		int[] assignmentPattern = candidate.getAssignmentByEmployee(e);
		
		//Evaluate ShiftOffPreferences
		Numbering shiftOffNumbering = new Numbering(employee.getshiftOffPrefNumberingPattern());
		shiftOffNumbering.setAssignmentPattern(assignmentPattern);
		shiftOffNumbering.setMaxTotal(0);
		shiftOffNumbering.evaluate();
		penaltyShiftOffPreferences += shiftOffNumbering.getPenaltyMaxTotal();
		
		//Evaluate CompleteWeekends, if enabled
		if (employee.isCompleteWeekendsEnabled()) {
			Numbering completeWeekendsNumbering = new Numbering(InputService.getWeekendNumberingPattern());
			completeWeekendsNumbering.setAssignmentPattern(assignmentPattern);
			completeWeekendsNumbering.setMinConsecutive(2);
			completeWeekendsNumbering.evaluate();
			penaltyCompleteWeekends += completeWeekendsNumbering.getPenaltyMinConsecutive();
			
		}
		
		//Evaluate MaxAssignmentsPerWeek, if enabled
		if (employee.isMaxAssignmentsPerWeekEnabled()) {
			Numbering maxAssignmentsPerWeekNumbering = new Numbering(InputService.getMaxAssignmentsPerWeekNumberingPattern());
			maxAssignmentsPerWeekNumbering.setAssignmentPattern(assignmentPattern);
			maxAssignmentsPerWeekNumbering.setMaxPert(employee.getMaxAssignementsPerWeek());
			maxAssignmentsPerWeekNumbering.evaluate();
			penaltyMaxAssignmentsPerWeek += maxAssignmentsPerWeekNumbering.getPenaltyMaxPert();
		}
		
	}
	
	
	//Evaluate Cover Requirements
	for (int s = 0; s < candidate.getNoOfShifts(); s++) {
		
		int[] uncoveredTasks = InputService.getCoverRequirementMatrix()[s].clone();
		 
		for (int e = 0; e < candidate.getNoOfEmployees(); e++) {
			Integer assignedTaskCombination = candidate.getAssignments()[e * candidate.getNoOfShifts() + s];
			 
			/**
			//check for availability
    		if (assignedTaskCombination != 0 && InputService.getAvailabilityMatrix()[s][e] == 0) {
    			penalty++;
    		}
    		 
    		//check for ability
    		if (InputService.getAbilityMatrix()[assignedTaskCombination][e] != 1) {
    			penalty++;
    		}
    		 **/
    		for (Task task : InputService.getTaskCombinationMap().get(assignedTaskCombination)) {
    			int taskNr = InputService.getReverseTaskMap().get(task);
    			if (uncoveredTasks[taskNr] > 0) {
    				uncoveredTasks[taskNr]--;
    			}
    			else {
    				penaltyCoverRequirements++;
    			}
    		}
    		 
		}
		for (int i = 0; i < uncoveredTasks.length; i++) {
			penaltyCoverRequirements += uncoveredTasks[i];
		}
	}
	
     return penaltyShiftOffPreferences 
    		 + penaltyCompleteWeekends 
    		 + penaltyMaxAssignmentsPerWeek 
    		 + (costCoverRequirements * penaltyCoverRequirements);
 }


 /**
  * {@inheritDoc}
  */
 public boolean isNatural()
 {
     return false;
 }
}
