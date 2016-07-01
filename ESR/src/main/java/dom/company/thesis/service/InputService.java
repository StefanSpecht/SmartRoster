package dom.company.thesis.service;

import java.sql.Date;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.w3c.dom.Document;

import dom.company.thesis.model.Employee;
import dom.company.thesis.model.ShiftType;
import dom.company.thesis.model.Task;

public class InputService {
	
	private final String XML_FILE_PATH = "C:\\Users\\Steff\\CloudStation\\Thesis\\_workspace\\Input.xml";
	
	Date startDate;
	Date endDate;	
	List<Task> tasks = new ArrayList<Task>();
	List<Employee> employees = new ArrayList<Employee>();
	List<ShiftType> shiftTypes = new ArrayList<ShiftType>();
	//Convert to employee, task and shiftType map!!	
	List<List<Task>> taskCombinations = new ArrayList<List<Task>>();
	Map<DayOfWeek,List<ShiftType>> shiftCoverRequirements = new HashMap<DayOfWeek,List<ShiftType>>();
		
	public InputService() {
	}
	
	public void parse() {	//Later XML String as input
		InputParser inputParser = new InputParser(XML_FILE_PATH);
		
		//Read startDate of planning horizon
		this.startDate = inputParser.getStartDate();
		
		//Read endDate of planning horizon
		this.endDate = inputParser.getEndDate();
		
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
		
		//Add TaskCoverRequirements to each ShiftType
		Map<String,Integer> taskIdCoverRequirements = new HashMap<String,Integer>();
		
		for (ShiftType shiftType : this.shiftTypes) {
			//Get Task Ids
			taskIdCoverRequirements = inputParser.getTaskIdCoverRequirements(shiftType.getId());
			
			//Convert task Ids to tasks and add them to the shifttypes
			for(Entry<String, Integer> taskIdCoverRequirement : taskIdCoverRequirements.entrySet()) {
				String taskId = taskIdCoverRequirement.getKey();
				Task task = this.getTask(taskId);
				int quantity = taskIdCoverRequirement.getValue();
				shiftType.addTaskCoverRequirement(task, quantity);
			}
		}
		
		//Get ShiftCoverRequirements for each DayOfWeek
		Map<DayOfWeek, List<String>> shiftIdCoverRequirements = new HashMap<DayOfWeek, List<String>>();
		shiftIdCoverRequirements = inputParser.getShiftIdCoverRequirements();
		
		//Convert shift Ids to shiftTypes
		for(Entry<DayOfWeek, List<String>> shiftIdCoverRequirement : shiftIdCoverRequirements.entrySet()) {
			DayOfWeek dayOfWeek = shiftIdCoverRequirement.getKey();
			List<ShiftType> shiftCovers = new ArrayList<ShiftType>();
			
			for(String shiftIdCover : shiftIdCoverRequirement.getValue()) {
				ShiftType shiftCover = this.getShiftType(shiftIdCover);
				shiftCovers.add(shiftCover);
			}
			this.shiftCoverRequirements.put(dayOfWeek, shiftCovers);
		}
		
		//Get shiftOffRequests and add it to employee objects
		for (Employee employee : employees) {
			Map<Date,List<String>> shiftIdOffRequests= new HashMap<Date,List<String>>();
			Map<Date,List<ShiftType>> shiftOffRequests= new HashMap<Date,List<ShiftType>>();
			
			shiftIdOffRequests = inputParser.getShifIdOffRequests(employee.getId(), this.startDate, this.endDate);
			
			//Convert shiftIds to shifts
			
			for(Entry<Date, List<String>> shiftIdOffRequest : shiftIdOffRequests.entrySet()) {
				List<ShiftType> currentShiftOffRequests = new ArrayList<ShiftType>();
				Date date = shiftIdOffRequest.getKey();
				List<String> shiftIds = shiftIdOffRequest.getValue();
				
				for (String shiftId : shiftIds) {
					ShiftType shiftType = this.getShiftType(shiftId);
					currentShiftOffRequests.add(shiftType);
				}
				
				shiftOffRequests.put(date, currentShiftOffRequests);
			}
			
			
			employee.setShiftOffRequests(shiftOffRequests);
			
		}
	}

	public Task getTask(String taskId) {
		return this.tasks.stream().filter(task -> task.getId().equals(taskId)).collect(Collectors.toList()).get(0);
	}
	public ShiftType getShiftType(String shiftTypeId) {
		return this.shiftTypes.stream().filter(shiftType -> shiftType.getId().equals(shiftTypeId)).collect(Collectors.toList()).get(0);
	}
	
	
	
	
}
