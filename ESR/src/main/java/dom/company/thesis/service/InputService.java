package dom.company.thesis.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.w3c.dom.Document;

import dom.company.thesis.model.Employee;
import dom.company.thesis.model.Task;

public class InputService {
	
	private final String XML_FILE_PATH = "C:\\Users\\Steff\\CloudStation\\Thesis\\_workspace\\Input.xml";
	
	List<Task> tasks = new ArrayList<Task>();
	List<Employee> employees = new ArrayList<Employee>();
		
	public InputService() {
		
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
	}
	
	Task getTask(String taskId) {
		return this.tasks.stream().filter(task -> task.getId().equals(taskId)).collect(Collectors.toList()).get(0);
	}
	
	
	
	
}
