package dom.company.thesis.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
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
	
	/**
	private void randomInit(Random rng) {
		for (int i = 0; i < noOfEmployees * noOfShifts ; i++) {
        		assignments[i] = rng.nextInt(noOfTasks);
        }
	}
	**/
	
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
		
		if (assignments[i] == 0 && isAvailable(i)) {
			
			int s = getShiftIndex(i);
			List<Integer> assignedEmployees = getAssignedEmployeeByShift(s);
			Collections.shuffle(assignedEmployees);
			boolean isSwapped = false;
			Iterator<Integer> empIndexIterator = assignedEmployees.iterator();
			
			while (!isSwapped && empIndexIterator.hasNext()) {
				int e = empIndexIterator.next();
				int taskCombinationAssignment = getValue(e,s);
				
				List<Task> sourceTaskList = new ArrayList<Task>(
						InputService.getTaskCombinationMap()
						.get(taskCombinationAssignment));
				Collections.shuffle(sourceTaskList);
				Iterator<Task> taskIterator = sourceTaskList.iterator();
				
				while (!isSwapped && taskIterator.hasNext()) {
					List<Task> singleTaskList = new ArrayList<Task>();
					singleTaskList.add(taskIterator.next());
					int taskCombination = InputService.getReverseTaskCombinationMap().get(singleTaskList);
					
					if (isAble(i, taskCombination)) {
						//un-assign task from source
						List<Task> newSourceTaskList = new ArrayList<Task>(sourceTaskList);
						newSourceTaskList.removeAll(singleTaskList);
						
						if (newSourceTaskList.isEmpty()) {
							setValue(0, e,s);
							assignments[i] = taskCombination;
							isSwapped = true;
						}
						else {
							Set<Task> sourceTaskCombinationSet = new HashSet<Task>();
							sourceTaskCombinationSet.addAll(newSourceTaskList);					
							List<List<Task>> validTaskCombinations = InputService.getTaskCombinations();
							
							for (List<Task> validTaskCombination : validTaskCombinations) {
								Set<Task> validTaskCombinationSet = new HashSet<Task>();
								validTaskCombinationSet.addAll(validTaskCombination);
								
								if (sourceTaskCombinationSet.equals(validTaskCombinationSet)) {
									int newSourceTaskCombination = InputService.getReverseTaskCombinationMap().get(validTaskCombination);
									setValue(newSourceTaskCombination, e,s);
									
									//assign task to destination
									assignments[i] = taskCombination;
									isSwapped = true;
									
									break;
								}
							}
						}
					}
					
				}
			}
		}

		
		
		
		
		
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
	
	private boolean isAvailable(int i) {
		int employeeIndex = getEmployeeIndex(i);
		int shiftIndex =  getShiftIndex(i);
		
		if (InputService.getAvailabilityMatrix()[shiftIndex][employeeIndex] == 1) {
			return true;
		}
		return false;
	}
	
	private boolean isAble(int i, int tc) {
		int employeeIndex = getEmployeeIndex(i);
		
		if (InputService.getAbilityMatrix()[tc][employeeIndex] == 1) {
			return true;
		}
		return false;
	}
	
	private int getEmployeeIndex(int i) {
		return i % InputService.getNoOfEmployees();
	}
	
	private int getShiftIndex(int i) {
		return (int) i / InputService.getNoOfEmployees();
	}
	
	private int getAdjustedMutationIndex(int i) {
		int employeeIndex = getEmployeeIndex(i);
		int shiftIndex =  getShiftIndex(i);
		
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
	
	private List<Integer> getAssignedEmployeeByShift(int s) {
		
		List<Integer> assignedEmployees = new ArrayList<Integer>();
		for (int e = 0; e < noOfEmployees; e++) {
			if (getValue(e, s) != 0) {
				assignedEmployees.add(e);
			}
		}
		return assignedEmployees;
	}
	
	public int getValue(int e, int s) {
		return this.assignments[s * this.getNoOfEmployees() + e];
	}
	public void setValue(int value, int e, int s) {
		this.assignments[s * this.getNoOfEmployees() + e] = value;
	}
	
	
}
