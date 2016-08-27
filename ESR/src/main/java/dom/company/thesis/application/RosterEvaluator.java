package dom.company.thesis.application;

import java.util.List;

import org.uncommons.watchmaker.framework.FitnessEvaluator;

import dom.company.thesis.model.Employee;
import dom.company.thesis.model.Numbering;
import dom.company.thesis.model.Roster;
import dom.company.thesis.model.Task;
import dom.company.thesis.service.InputService;

public class RosterEvaluator implements FitnessEvaluator<Roster>
{

	private int costShiftOffPreferences;
	private int costCompleteWeekends;
	private int costMaxAssignmentsPerWeek;
	private int costCoverRequirements;
	private int globalPenaltyShiftOffPreferences;
	private int globalPenaltyCompleteWeekends;
	private int globalPenaltyMaxAssignmentsPerWeek;
	private int globalPenaltyCoverRequirements;
	
	 public RosterEvaluator() {    
	 }
	 
	 public RosterEvaluator(int shiftOffWeight, int weekendWeight, int maxAssignWeight, int coverWeight) { 
		 this.costShiftOffPreferences = shiftOffWeight;
		 this.costCompleteWeekends = weekendWeight;
		 this.costMaxAssignmentsPerWeek = maxAssignWeight;
		 this.costCoverRequirements = coverWeight;
	 }
	
	public double getFitness(Roster candidate, List<? extends Roster> population) {
		
		int penaltyShiftOffPreferences = 0;
		int penaltyCompleteWeekends = 0;
		int penaltyMaxAssignmentsPerWeek = 0;
		int penaltyCoverRequirements = 0;
		
		//Evaluate each employee
		for (int e = 0; e < InputService.getNoOfEmployees(); e++) {
			
			Employee employee = InputService.getEmployeeMap().get(e);
			int[] assignmentPattern = candidate.getAssignmentByEmployee(e);
			
			//Evaluate ShiftOffPreferences
			Numbering shiftOffNumbering = new Numbering(employee.getshiftOffPrefNumberingPattern());
			shiftOffNumbering.setAssignmentPattern(assignmentPattern);
			shiftOffNumbering.setMaxTotal(0);
			shiftOffNumbering.evaluate();
			penaltyShiftOffPreferences += shiftOffNumbering.getPenaltyMaxTotal() * costShiftOffPreferences;
			
			//Evaluate CompleteWeekends, if enabled
			if (employee.isCompleteWeekendsEnabled()) {
				Numbering completeWeekendsNumbering = new Numbering(InputService.getWeekendNumberingPattern());
				completeWeekendsNumbering.setAssignmentPattern(assignmentPattern);
				completeWeekendsNumbering.setMinConsecutive(2);
				completeWeekendsNumbering.evaluate();
				penaltyCompleteWeekends += completeWeekendsNumbering.getPenaltyMinConsecutive() * costCompleteWeekends;
				
			}
			
			//Evaluate MaxAssignmentsPerWeek, if enabled
			if (employee.isMaxAssignmentsPerWeekEnabled()) {
				Numbering maxAssignmentsPerWeekNumbering = new Numbering(InputService.getMaxAssignmentsPerWeekNumberingPattern());
				maxAssignmentsPerWeekNumbering.setAssignmentPattern(assignmentPattern);
				maxAssignmentsPerWeekNumbering.setMaxPert(employee.getMaxAssignementsPerWeek());
				maxAssignmentsPerWeekNumbering.evaluate();
				penaltyMaxAssignmentsPerWeek += maxAssignmentsPerWeekNumbering.getPenaltyMaxPert() * costMaxAssignmentsPerWeek;
			}
		}
		
		//Evaluate Cover Requirements
		for (int s = 0; s < candidate.getNoOfShifts(); s++) {
			
			int[] uncoveredTasks = InputService.getCoverRequirementMatrix()[s].clone();
			 
			for (int e = 0; e < candidate.getNoOfEmployees(); e++) {
				//Integer assignedTaskCombination = candidate.getAssignments()[e * candidate.getNoOfShifts() + s];
				Integer assignedTaskCombination = candidate.getValue(e, s);
				 
	    		for (Task task : InputService.getTaskCombinationMap().get(assignedTaskCombination)) {
	    			int taskNr = InputService.getReverseTaskMap().get(task);
	    			if (uncoveredTasks[taskNr] > 0) {
	    				uncoveredTasks[taskNr]--;
	    			}
	    			else {
	    				penaltyCoverRequirements += costCoverRequirements;
	    			}
	    		}
	    		 
			}
			for (int i = 0; i < uncoveredTasks.length; i++) {
				penaltyCoverRequirements += uncoveredTasks[i] * costCoverRequirements;
			}
		}
		
		this.globalPenaltyShiftOffPreferences = penaltyShiftOffPreferences;
		this.globalPenaltyCompleteWeekends = penaltyCompleteWeekends;
		this.globalPenaltyMaxAssignmentsPerWeek = penaltyMaxAssignmentsPerWeek;
		this.globalPenaltyCoverRequirements = penaltyCoverRequirements;
		
	     return penaltyShiftOffPreferences 
	    		 + penaltyCompleteWeekends 
	    		 + penaltyMaxAssignmentsPerWeek 
	    		 + penaltyCoverRequirements;
	 }
	
	 public boolean isNatural()
	 {
	     return false;
	 }

	public int getPenaltyShiftOffPreferences() {
		return globalPenaltyShiftOffPreferences;
	}

	public int getPenaltyCompleteWeekends() {
		return globalPenaltyCompleteWeekends;
	}

	public int getPenaltyMaxAssignmentsPerWeek() {
		return globalPenaltyMaxAssignmentsPerWeek;
	}

	public int getPenaltyCoverRequirements() {
		return globalPenaltyCoverRequirements;
	}
	 
	 
}
