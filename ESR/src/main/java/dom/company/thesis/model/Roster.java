package dom.company.thesis.model;

import java.util.Random;

public class Roster {

	private final int noOfTasks;
    private final int noOfShifts;
    private final int noOfEmployees;
    private int[][] assignments;
	
	public Roster(int noOfEmployees, int noOfShifts, int noOfTasks, Random rng) {
		this.noOfTasks = noOfTasks;
        this.noOfShifts = noOfShifts;
        this.noOfEmployees = noOfEmployees;
        
        int[][] assignments = new int[noOfShifts][noOfEmployees];
        for (int i = 0; i < noOfShifts; i++) {
            for (int y = 0; y < noOfEmployees; ++y) {
            	assignments[i][y] = rng.nextInt(noOfTasks+1);
            }
        }
	}
}
