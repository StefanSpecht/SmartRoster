package dom.company.thesis.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.w3c.dom.Document;

import dom.company.thesis.model.Employee;
import dom.company.thesis.model.ShiftType;
import dom.company.thesis.model.Task;

public class InputService {
	
	private final String XML_FILE_PATH = "C:\\Users\\Steff\\CloudStation\\Thesis\\_workspace\\Input.xml";
	
	List<Task> tasks = new ArrayList<Task>();
	List<Employee> employees = new ArrayList<Employee>();
	List<ShiftType> shiftTypes = new ArrayList<ShiftType>();
	//Convert to employee, task and shiftType map!!	
	List<List<Task>> taskCombinations = new ArrayList<List<Task>>();
		
	public InputService() {
	}
	
	public void parse() {	//Later XML String as input
		InputParser inputParser = new InputParser(XML_FILE_PATH);
		
		//Read tasks
		this.tasks = inputParser.getTasks();
		
		//Read employees
		this.employees = inputParser.getEmployees();
		
		//Assign task qualifications to employees
		for (Employee employee : employees) {
			List<Task> taskQualifications = new ArrayList<Task>();
			List<String> taskQualificationIds = inputParser.getTaskQualificationIds(employee.getId());
			
			for (String taskQualificationId : taskQualificationIds) {
				taskQualifications.add(getTask(taskQualificationId));
			}
			
			employee.setTaskQualifications(taskQualifications);			
		}
		
		//Read shift types
		this.shiftTypes = inputParser.getShiftTypes();
		
		//Read all task-combinations Ids
		List<List<String>> taskCombinationIds = new ArrayList<List<String>>();
		taskCombinationIds = inputParser.getTaskCombinationIds();
		
		//Get Task objects by combination IDs and add it to the task combination list
		for (List<String> taskCombinationId : taskCombinationIds) {
			List<Task> taskCombination = new ArrayList<Task>();
			for (String taskId : taskCombinationId) {
				taskCombination.add(this.getTask(taskId));
			}
			this.taskCombinations.add(taskCombination);
		}
		
	}

	public Task getTask(String taskId) {
		return this.tasks.stream().filter(task -> task.getId().equals(taskId)).collect(Collectors.toList()).get(0);
	}
	
	
	
	
}
