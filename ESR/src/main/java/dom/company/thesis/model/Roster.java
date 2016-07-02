package dom.company.thesis.model;

import java.util.Random;

public class Roster {

	private final int noOfTasks;
    private final int noOfShifts;
    private final int noOfEmployees;
    private int[] assignments;
	
	public Roster(int noOfEmployees, int noOfShifts, int noOfTasks, Random rng) {
		this.noOfTasks = noOfTasks;
        this.noOfShifts = noOfShifts;
        this.noOfEmployees = noOfEmployees;
        
        assignments = new int[noOfShifts * noOfEmployees];
        for (int i = 0; i < noOfShifts * noOfEmployees ; i++) {
        	// 33:66 chance dass kein Task assigned wird
        	int assignTask = rng.nextInt(3);
        	if (assignTask == 0) {
        		assignments[i] = rng.nextInt(noOfTasks+1);
        	} else {
        		assignments[i] = 0;
        	}
            
        }
	}
	public Roster(int noOfEmployees, int noOfShifts, int noOfTasks, int[] assignments) {
		this.noOfTasks = noOfTasks;
        this.noOfShifts = noOfShifts;
        this.noOfEmployees = noOfEmployees;
        
        this.assignments = assignments;
	}

	public int getNoOfTasks() {
		return noOfTasks;
	}

	public int getNoOfShifts() {
		return noOfShifts;
	}

	public int getNoOfEmployees() {
		return noOfEmployees;
	}

	public int[] getAssignments() {
		return assignments;
	}
	
}
