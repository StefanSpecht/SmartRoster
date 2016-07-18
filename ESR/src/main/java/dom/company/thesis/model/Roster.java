package dom.company.thesis.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import dom.company.thesis.service.InputService;

public class Roster {

	private final int noOfTasks;
    private final int noOfShifts;
    private final int noOfEmployees;
    private int[] assignments;
	
	public Roster(int noOfEmployees, int noOfShifts, int noOfTasks, Random rng) {
		this.noOfTasks = noOfTasks;
        this.noOfShifts = noOfShifts;
        this.noOfEmployees = noOfEmployees;
        this.assignments = new int[noOfEmployees * noOfShifts];
        
        smartInit();        
	}
	
	public Roster(int noOfEmployees, int noOfShifts, int noOfTasks, int[] assignments) {
		this.noOfTasks = noOfTasks;
        this.noOfShifts = noOfShifts;
        this.noOfEmployees = noOfEmployees;
        
        this.assignments = assignments;
	}
	
	private void randomInit(Random rng) {
		for (int i = 0; i < noOfEmployees * noOfShifts ; i++) {
        		assignments[i] = rng.nextInt(noOfTasks);
        }
	}
	
	private void zeroInit() {
		for (int i = 0; i < noOfEmployees * noOfShifts ; i++) {
        	assignments[i] = 0;
        }
	}
	
	private void smartInit() {
		
		zeroInit();
		Map<Integer,List<Task>> taskCombinationMap= InputService.getTaskCombinationMap();
		Map<List<Task>, Integer> reverseTaskCombinationMap= InputService.getReverseTaskCombinationMap();
		
		for (int s = 0; s < noOfShifts ; s++) {
			
			int[] uncoveredTasks = InputService.getCoverRequirementMatrix()[s].clone();
			int[] employeeAvailability = InputService.getAvailabilityMatrix()[s];
			
			List<Employee> availableEmployees = new ArrayList<Employee>();
			
			//Generate list of employees that are available for that shift
			for (int i=0; i < employeeAvailability.length; i++) {
				if (employeeAvailability[i] == 1) {
					availableEmployees.add(InputService.getEmployeeMap().get(i));
				}
			}
			
			//
			for (int t = 0; t < uncoveredTasks.length; t++) {
				
				Task task = InputService.getSingleTaskMap().get(t);
				
				//Shuffle list of available employees
				Collections.shuffle(availableEmployees);
				
				Iterator<Employee> empIterator = availableEmployees.iterator();
				
				while (uncoveredTasks[t] != 0 && empIterator.hasNext()) {
					
					//Check employees for task ability
					Employee employee = empIterator.next();						
					int employeeIndex = InputService.getReverseEmployeeMap().get(employee);
					
					//Check general ability
					if (employee.isAble(task)) {
						
						//Check if new task combination would be possible							
						int currentTaskCombinationAssignment = this.getValue(InputService.getReverseEmployeeMap().get(employee), s);
						
						//translate taskCombination to list of tasks
						List<Task> currentTaskAssignments = new ArrayList<Task>(taskCombinationMap.get(currentTaskCombinationAssignment));
						
						//check if currentTaskAssignment new task would be a valid combination
						currentTaskAssignments.add(task);
						if (reverseTaskCombinationMap.get(currentTaskAssignments) != null) {
							
							//assign employee to new task combination
							int taskCombinationIndex = reverseTaskCombinationMap.get(currentTaskAssignments);
							this.setValue(taskCombinationIndex, employeeIndex, s);
							
							uncoveredTasks[t]--;
						}
					}					
				}		
			}
			
		}
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
		
		//Add a random task
		if (rng.nextBoolean()) {
			
			//get employee and shift indices
			int employeeIndex = (int) i / InputService.getNoOfShifts();
			Employee employee = InputService.getEmployeeMap().get(employeeIndex);
			int shiftIndex = i % InputService.getNoOfShifts();
			
			//Check if employee is available
			if (InputService.getAvailabilityMatrix()[shiftIndex][employeeIndex] == 1) {
				
				//list of current task assignments
				List<Task> currentTaskAssignments = InputService.getTaskCombinationMap().get(assignments[i]);
				
				//Possible new assignments
				List<Task> taskQualifications = new ArrayList<Task>(employee.getTaskQualifications());
				taskQualifications.removeAll(currentTaskAssignments);
				
				if (!taskQualifications.isEmpty()) {
					List<Task> taskCombinationToAdd = new ArrayList<Task>();
					taskCombinationToAdd.add(taskQualifications.get(rng.nextInt(taskQualifications.size())));
					if (InputService.getReverseTaskCombinationMap().get(taskCombinationToAdd) != null) {
						assignments[i] = InputService.getReverseTaskCombinationMap().get(taskCombinationToAdd);
					}
				}
			}
					
		}
		//Remove a random task
		else {
			
			if (assignments[i] != 0) {
				
				//list of current task assignments
				List<Task> taskAssignments = new ArrayList<Task>(InputService.getTaskCombinationMap().get(assignments[i]));
				
				//remove a random assignment from list
				taskAssignments.remove(rng.nextInt(taskAssignments.size()));
				
				//Assign new task combination value
				assignments[i] = InputService.getReverseTaskCombinationMap().get(taskAssignments);
			}
		}
	}
	public int[] getAssignmentByEmployee(int employeeIndex) {
		return Arrays.copyOfRange(assignments, employeeIndex * this.getNoOfShifts(), employeeIndex * this.getNoOfShifts() + this.getNoOfShifts());
	}
	public int getValue(int e, int s) {
		return this.assignments[e * this.getNoOfShifts() + s];
	}
	public void setValue(int value, int e, int s) {
		this.assignments[e * this.getNoOfShifts() + s] = value;
	}
	
	
}
