package dom.company.thesis.service;

import java.sql.Date;
import java.sql.Time;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
	
	static private final String XML_FILE_PATH = "C:\\Users\\Steff\\CloudStation\\Thesis\\_workspace\\Input.xml";
	
	static Date startDate;
	static Date endDate;	
	static long noOfDays;
	static List<Task> tasks = new ArrayList<Task>();
	static List<Employee> employees = new ArrayList<Employee>();
	static List<ShiftType> shiftTypes = new ArrayList<ShiftType>();
	//Convert to employee, task and shiftType map!!	
	static Map<Integer,Employee> employeeMap = new HashMap<Integer,Employee>();
	static Map<Integer,ShiftType> shiftMap = new HashMap<Integer,ShiftType>();
	static Map<Integer,List<Task>> taskMap = new HashMap<Integer,List<Task>>();
	static List<List<Task>> taskCombinations = new ArrayList<List<Task>>();
	static Map<DayOfWeek,List<ShiftType>> shiftCoverRequirements = new HashMap<DayOfWeek,List<ShiftType>>();
		
	public InputService() {
	}
	
	static public void parse() {	//Later XML String as input
		InputParser inputParser = new InputParser(XML_FILE_PATH);
		
		//Read startDate of planning horizon
		startDate = inputParser.getStartDate();
		
		//Read endDate of planning horizon
		endDate = inputParser.getEndDate();
		
		//calculate number of days
		noOfDays = Math.abs(((endDate.getTime() - startDate.getTime()) / 86400000) + 1);
		
		//Read tasks
		tasks = inputParser.getTasks();
		
		//Read employees
		employees = inputParser.getEmployees();
		
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
		shiftTypes = inputParser.getShiftTypes();
		
		//Read all task-combinations Ids
		List<List<String>> taskCombinationIds = new ArrayList<List<String>>();
		taskCombinationIds = inputParser.getTaskCombinationIds();
		
		//Get Task objects by combination IDs and add it to the task combination list
		for (List<String> taskCombinationId : taskCombinationIds) {
			List<Task> taskCombination = new ArrayList<Task>();
			for (String taskId : taskCombinationId) {
				taskCombination.add(getTask(taskId));
			}
			taskCombinations.add(taskCombination);
		}
		//Add all single tasks to list
		for (Task task : tasks) {
			List<Task> taskCombination = new ArrayList<Task>();
			taskCombination.add(task);
			taskCombinations.add(taskCombination);
		}
		
		//Add TaskCoverRequirements to each ShiftType
		Map<String,Integer> taskIdCoverRequirements = new HashMap<String,Integer>();
		
		for (ShiftType shiftType : shiftTypes) {
			//Get Task Ids
			taskIdCoverRequirements = inputParser.getTaskIdCoverRequirements(shiftType.getId());
			
			//Convert task Ids to tasks and add them to the shifttypes
			for(Entry<String, Integer> taskIdCoverRequirement : taskIdCoverRequirements.entrySet()) {
				String taskId = taskIdCoverRequirement.getKey();
				Task task = getTask(taskId);
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
				ShiftType shiftCover = getShiftType(shiftIdCover);
				shiftCovers.add(shiftCover);
			}
			shiftCoverRequirements.put(dayOfWeek, shiftCovers);
		}
		
		//Get shiftOffRequests and add it to employee objects
		for (Employee employee : employees) {
			Map<Date,List<String>> shiftIdOffRequests= new HashMap<Date,List<String>>();
			Map<Date,List<ShiftType>> shiftOffRequests= new HashMap<Date,List<ShiftType>>();
			
			shiftIdOffRequests = inputParser.getShifIdOffRequests(employee.getId(), startDate, endDate);
			
			//Convert shiftIds to shifts
			
			for(Entry<Date, List<String>> shiftIdOffRequest : shiftIdOffRequests.entrySet()) {
				List<ShiftType> currentShiftOffRequests = new ArrayList<ShiftType>();
				Date date = shiftIdOffRequest.getKey();
				List<String> shiftIds = shiftIdOffRequest.getValue();
				
				for (String shiftId : shiftIds) {
					ShiftType shiftType = getShiftType(shiftId);
					currentShiftOffRequests.add(shiftType);
				}
				
				shiftOffRequests.put(date, currentShiftOffRequests);
			}
			
			
			employee.setShiftOffRequests(shiftOffRequests);
		}
		//generate maps
		generateMaps();
	}
	
	static public void generateMaps() {
		//generate employee map
		for (int i=0; i<employees.size(); i++) {
			employeeMap.put(i, employees.get(i));
		}
		
		//generate shift map
		orderShifts(shiftTypes);
		for (int i=0; i<(shiftTypes.size() * noOfDays); i++) {
			shiftMap.put(i, shiftTypes.get(i % shiftTypes.size()));
		}
		
		//generate task map
		for (int i=0; i<taskCombinations.size(); i++) {
			taskMap.put(i, taskCombinations.get(i));
		}
		
		@SuppressWarnings("unused")
		int g = 4;
		g++;
		
	}
	
	private static void orderShifts(List<ShiftType> shiftTypes) {

	    Collections.sort(shiftTypes, new Comparator<ShiftType>() {

	        public int compare(ShiftType shiftType1, ShiftType shiftType2) {

	            Time startDate1 = shiftType1.getStartTime();
	            Time startDate2 = shiftType2.getStartTime();
	            int startDateComp = startDate1.compareTo(startDate2);

	            if (startDateComp != 0) {
	               return startDateComp;
	            } else {
	            	Time endDate1 = shiftType1.getEndTime();
		            Time endDate2 = shiftType2.getEndTime();
		            return endDate1.compareTo(endDate2);
	            }
	        }
	    });
	}
	private static Task getTask(String taskId) {
		return tasks.stream().filter(task -> task.getId().equals(taskId)).collect(Collectors.toList()).get(0);
	}
	private static ShiftType getShiftType(String shiftTypeId) {
		return shiftTypes.stream().filter(shiftType -> shiftType.getId().equals(shiftTypeId)).collect(Collectors.toList()).get(0);
	}

	public static Map<Integer, Employee> getEmployeeMap() {
		return employeeMap;
	}

	public static Map<Integer, ShiftType> getShiftMap() {
		return shiftMap;
	}

	public static Map<Integer, List<Task>> getTaskMap() {
		return taskMap;
	}
	
	
	
	
	
}
