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
        
        assignments = new int[noOfEmployees * noOfShifts];
        for (int i = 0; i < noOfEmployees * noOfShifts ; i++) {
        	// 33:66 chance dass kein Task assigned wird
        	//int assignTask = rng.nextInt(3);
        	//if (assignTask == 0) {
        		assignments[i] = rng.nextInt(noOfTasks);
        	//} else {
        	//	assignments[i] = 0;
        	//}
            
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
	public Roster clone() {
		return new Roster(noOfEmployees, noOfShifts, noOfTasks, assignments);
	}
	public void mutateAssignment(int i, Random rng) {
		// 33:66 chance dass kein Task assigned wird
    	//int assignTask = rng.nextInt(3);
    	//if (assignTask == 0) {
    		assignments[i] = rng.nextInt(noOfTasks);
    	//} else {
    	//	assignments[i] = 0;
    	//}
	}
	public int getValue(int e, int s) {
		return this.assignments[e * this.getNoOfShifts() + s];
	}
	public void setValue(int value, int e, int s) {
		this.assignments[e * this.getNoOfShifts() + s] = value;
	}
	
}
