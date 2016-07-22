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
	
	private void smartInit() {
		
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
	
	public void swapMutateAssignment(int i, Random rng) {
		
		//adjust i to correspond to an available employee
		i = getAdjustedMutationIndex(i);
		
		
	}
	
	
	public void mutateAssignment(int i, Random rng) {
		
		//adjust i to correspond to an available employee
		i = getAdjustedMutationIndex(i);
				
		//Add a random task
		if (assignments[i] == 0) {
			addRandomTask(i, rng);					
		}
		
		//Remove a random task
		else {
			if (rng.nextBoolean()) {
				addRandomTask(i, rng);
			}
			else {
				removeRandomTask(i, rng);
			}
		}
	}
	
	private int getAdjustedMutationIndex(int i) {
		int employeeIndex = (int) i / InputService.getNoOfShifts();
		int shiftIndex =  i % InputService.getNoOfShifts();
		
		while (InputService.getAvailabilityMatrix()[shiftIndex][employeeIndex] != 1)  {
			i++;
			
			if (i >= assignments.length) {
				i -= assignments.length;
			}
			
			employeeIndex = (int) i / InputService.getNoOfShifts();
			shiftIndex = i % InputService.getNoOfShifts();		
		} 
		return i;
	}
	
	private void addRandomTask(int i, Random rng) {
		//get employee and shift indices
		int employeeIndex = (int) i / InputService.getNoOfShifts();
		Employee employee = InputService.getEmployeeMap().get(employeeIndex);
		int shiftIndex = i % InputService.getNoOfShifts();
		
		//list of current task assignments
		List<Task> currentTaskAssignments = InputService.getTaskCombinationMap().get(assignments[i]);
		
		//Possible new assignments
		List<Task> taskQualifications = new ArrayList<Task>(employee.getTaskQualifications());
		taskQualifications.removeAll(currentTaskAssignments);
		
		//remove task that are not required for that shift
		int[] coverRequirements = InputService.getCoverRequirementMatrix()[shiftIndex];
		for (int j = 0; j < coverRequirements.length; j++) {
			if (coverRequirements[j] == 0 && taskQualifications.contains(InputService.getSingleTaskMap().get(j))) {
				taskQualifications.remove(InputService.getSingleTaskMap().get(j));
			}
		}		
		
		if (!taskQualifications.isEmpty()) {
			List<Task> taskCombinationToAdd = new ArrayList<Task>();
			taskCombinationToAdd.add(taskQualifications.get(rng.nextInt(taskQualifications.size())));
			if (InputService.getReverseTaskCombinationMap().get(taskCombinationToAdd) != null) {
				assignments[i] = InputService.getReverseTaskCombinationMap().get(taskCombinationToAdd);
			}
		}
		
	}
	private void removeRandomTask(int i, Random rng) {
		if (assignments[i] != 0) {
			
			//list of current task assignments
			List<Task> taskAssignments = new ArrayList<Task>(InputService.getTaskCombinationMap().get(assignments[i]));
			
			//remove a random assignment from list
			taskAssignments.remove(rng.nextInt(taskAssignments.size()));
			
			//Assign new task combination value
			assignments[i] = InputService.getReverseTaskCombinationMap().get(taskAssignments);
		}		
	}
	
	public int[] getAssignmentByEmployee(int employeeIndex) {
		int[] employeeAssignment = new int[noOfShifts];
		
		for (int s = 0; s < noOfShifts; s++) {
			employeeAssignment[s] = getValue(employeeIndex, s);
		}
		
		return employeeAssignment;
	}
	public int getValue(int e, int s) {
		return this.assignments[s * this.getNoOfEmployees() + e];
	}
	public void setValue(int value, int e, int s) {
		this.assignments[s * this.getNoOfEmployees() + e] = value;
	}
	
	
}
