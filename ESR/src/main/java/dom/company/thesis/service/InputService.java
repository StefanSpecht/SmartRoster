package dom.company.thesis.service;

import java.sql.Date;
import java.sql.Time;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import dom.company.thesis.model.Employee;
import dom.company.thesis.model.ShiftType;
import dom.company.thesis.model.Task;

public class InputService {
	
	static private final String XML_FILE_PATH = "C:\\Users\\Steff\\CloudStation\\Thesis\\_workspace\\Input3.xml";
	
	static Date startDate;
	static Date endDate;	
	static long noOfDays;
	static List<Task> tasks = new ArrayList<Task>();
	static List<Employee> employees = new ArrayList<Employee>();
	static List<ShiftType> shiftTypes = new ArrayList<ShiftType>();
	static Map<Integer,Employee> employeeMap = new HashMap<Integer,Employee>();
	static Map<Integer,ShiftType> shiftMap = new HashMap<Integer,ShiftType>();
	static Map<Integer,Date> shiftDatesMap = new HashMap<Integer,Date>();
	static Map<Integer,List<Task>> taskCombinationMap = new HashMap<Integer,List<Task>>();
	static Map<Integer,Task> taskMap = new HashMap<Integer,Task>();
	static Map<Task, Integer> reverseTaskMap = new HashMap<Task, Integer>();
	static List<List<Task>> taskCombinations = new ArrayList<List<Task>>();
	static Map<DayOfWeek,List<ShiftType>> shiftCoverRequirements = new HashMap<DayOfWeek,List<ShiftType>>();
	
	//Hard Constraints
	static int[][] availabilityMatrix;			// [shift][employee] = {0,1}
	static int[][] abilityMatrix;				// [taskCombination][employee] = {0,1}
	static int[][] coverRequirementMatrix;		// [shift][task]	= {0,1,2,...#combinations}
	
	//Soft Constraints
	static int[][] shiftOffPreferenceMatrix;	// [shift][employee] = {0,1}
		
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
		
		//Add TaskCoverRequirements to each ShiftType for each day
		Map<DayOfWeek,Map<String,Integer>> taskIdCoverRequirements = new HashMap<DayOfWeek,Map<String,Integer>>();
		
		for (ShiftType shiftType : shiftTypes) {
			//Get Task Ids
			taskIdCoverRequirements = inputParser.getTaskIdCoverRequirements(shiftType.getId());
			
			//Convert task Ids to tasks and add them to the shifttypes
			for(Entry<DayOfWeek, Map<String, Integer>> taskIdCoverRequirement : taskIdCoverRequirements.entrySet()) {
				DayOfWeek dayOfWeek = taskIdCoverRequirement.getKey();
				Map<String,Integer> currIdCoverRequirements = taskIdCoverRequirement.getValue();
				
				Map<Task,Integer> currCoverRequirements = new HashMap<Task,Integer>();
				for(Entry<String,Integer> currIdCoverRequirement : currIdCoverRequirements.entrySet()) {
					
					String taskId = currIdCoverRequirement.getKey();
					Task task = getTask(taskId);
					int quantity = currIdCoverRequirement.getValue();
					currCoverRequirements.put(task, quantity);
				}
				shiftType.addTaskCoverRequirement(dayOfWeek, currCoverRequirements);				
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
		
		//Get shiftUnavailabilities and add it to employee objects
				for (Employee employee : employees) {
					Map<Date,List<String>> shiftIdUnavailabilities= new HashMap<Date,List<String>>();
					Map<Date,List<ShiftType>> shiftUnavailabilities= new HashMap<Date,List<ShiftType>>();
					
					shiftIdUnavailabilities = inputParser.getShifIdUnavailabilities(employee.getId(), startDate, endDate);
					
					//Convert shiftIds to shifts
					
					for(Entry<Date, List<String>> shiftIdUnavailability : shiftIdUnavailabilities.entrySet()) {
						List<ShiftType> currentShiftUnavailabilities = new ArrayList<ShiftType>();
						Date date = shiftIdUnavailability.getKey();
						List<String> shiftIds = shiftIdUnavailability.getValue();
						
						for (String shiftId : shiftIds) {
							ShiftType shiftType = getShiftType(shiftId);
							currentShiftUnavailabilities.add(shiftType);
						}
						
						shiftUnavailabilities.put(date, currentShiftUnavailabilities);
					}
					
					
					employee.setShiftUnavailabilities(shiftUnavailabilities);
				}
				
				//generate maps
				generateMaps();
				
				//Get shiftOffPreferences and add it to employee objects
				for (Employee employee : employees) {
					Map<Date,List<String>> shiftIdOffPreferences= new HashMap<Date,List<String>>();
					Map<Date,List<ShiftType>> shiftOffPreferences= new HashMap<Date,List<ShiftType>>();
					
					shiftIdOffPreferences = inputParser.getShifIdOffPreferences(employee.getId(), startDate, endDate);
					
					//Convert shiftIds to shifts			
					for(Entry<Date, List<String>> shiftIdOffPreference : shiftIdOffPreferences.entrySet()) {
						List<ShiftType> currentShiftOffPreferences = new ArrayList<ShiftType>();
						Date date = shiftIdOffPreference.getKey();
						List<String> shiftIds = shiftIdOffPreference.getValue();
						
						for (String shiftId : shiftIds) {
							ShiftType shiftType = getShiftType(shiftId);
							currentShiftOffPreferences.add(shiftType);
						}
						
						shiftOffPreferences.put(date, currentShiftOffPreferences);
					}
					
					
					employee.setShiftOffPreferences(shiftOffPreferences);
				}
				
				//generate matrices
				generateMatrices();
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
		
		//generate shiftDates map
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		long dayIndex = getNoOfShifts() / noOfDays;
		
		for (int i=0; i<(shiftTypes.size() * noOfDays); i++) {
			calendar.setTime(startDate);
			calendar.add(Calendar.DAY_OF_YEAR, (int) (i / (int)dayIndex));
			shiftDatesMap.put(i, new Date(calendar.getTimeInMillis()));
		}
		
		//generate task-combinations map
		taskCombinationMap.put(0, new ArrayList<Task>());
		for (int i=0; i<taskCombinations.size(); i++) {
			taskCombinationMap.put(i+1, taskCombinations.get(i));
		}	
		
		//generate task map and reverse task map
		for (int i=0; i<tasks.size(); i++) {
			taskMap.put(i, tasks.get(i));
			reverseTaskMap.put(tasks.get(i), i);
		}
	}
	
	static public void generateMatrices() {
		
		//generate availability matrix
		availabilityMatrix = new int[getNoOfShifts()][getNoOfEmployees()];		
		for (int s = 0; s < getNoOfShifts(); s++) {
            for (int e = 0; e < getNoOfEmployees(); e++) {
            	
            	//Check, if employee no e is available for shift no s            	
            	availabilityMatrix[s][e] = isAvailable(s,e);
            }
        }
		
		//generate ability matrix
		abilityMatrix = new int[getNoOfTasks()][getNoOfEmployees()];		
		for (int tc = 0; tc < getNoOfTasks(); tc++) {
            for (int e = 0; e < getNoOfEmployees(); e++) {
            	
            	//Check, if employee no e is able to do task combination tc        	
            	abilityMatrix[tc][e] = isAble(tc,e);
            }
        }
		
		//generate coverRequirementsMatrix
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		coverRequirementMatrix = new int[getNoOfShifts()][tasks.size()];
		
		for (int s = 0; s < getNoOfShifts(); s++) {

			DayOfWeek dayOfWeek = DayOfWeek.of((calendar.get(Calendar.DAY_OF_WEEK) == 1 ? 7 : calendar.get(Calendar.DAY_OF_WEEK) - 1));
			ShiftType shiftType = shiftTypes.get(s % shiftTypes.size());
			
			//Check if shift is required today. If yes, check which tasks must be covered.
			if (shiftCoverRequirements.get(dayOfWeek).contains(shiftType)) {
				Map<Task,Integer> coverRequirements = shiftType.getTaskCoverRequirements().get(dayOfWeek);
				
				for (int t = 0; t < taskMap.size(); t++) {
					Integer taskCoverRequirement = coverRequirements.get(taskMap.get(t));
					
					if (taskCoverRequirement != null) {
						coverRequirementMatrix[s][t] = coverRequirements.get(taskMap.get(t));
					}
					else {
						coverRequirementMatrix[s][t] = 0;
					}
					
				}
			}
			if (s % shiftTypes.size() == shiftTypes.size() -1) {
				calendar.add(Calendar.DAY_OF_YEAR,1);
			}
        }
		
		//generate ShiftOffPreferences matrix
		shiftOffPreferenceMatrix = new int[getNoOfShifts()][getNoOfEmployees()];		
		for (int s = 0; s < getNoOfShifts(); s++) {
            for (int e = 0; e < getNoOfEmployees(); e++) {
            	
            	//Check, if employee  e prefers to have a shift off            	
            	shiftOffPreferenceMatrix[s][e] = hasShiftOffPreference(s,e);
            }
        }
    
	}
	
	private static int isAvailable(int s, int e) {
		
		long dayIndex = getNoOfShifts() / noOfDays;
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		calendar.add(Calendar.DAY_OF_YEAR, (int) (s / (int)dayIndex));
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		Date date = new Date(calendar.getTimeInMillis());
		ShiftType shiftType = shiftMap.get(s);
		Employee employee = employeeMap.get(e);
		
		if (employee.getShiftUnavailabilities().get(date).contains(shiftType)) {
			return 0;
		}		
		return 1;
	}
	
	private static int isAble(int tc, int e) {
		List<Task> taskCombination = getTaskMap().get(tc);
		Employee employee = getEmployeeMap().get(e);
		
		for (Task task : taskCombination) {
			if (!employee.getTaskQualifications().contains(task)) {
				return 0;
			}
		}
		return 1;
	
	}
	
	private static int hasShiftOffPreference(int s, int e) {
		
		long dayIndex = getNoOfShifts() / noOfDays;
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		calendar.add(Calendar.DAY_OF_YEAR, (int) (s / (int)dayIndex));
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		Date date = new Date(calendar.getTimeInMillis());
		ShiftType shiftType = shiftMap.get(s);
		Employee employee = employeeMap.get(e);
		
		if (employee.getShiftOffPreferences().get(date).contains(shiftType)) {
			return 1;
		}		
		return 0;
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
		return taskCombinationMap;
	}

	public static Date getStartDate() {
		return startDate;
	}

	public static Date getEndDate() {
		return endDate;
	}

	public static long getNoOfDays() {
		return noOfDays;
	}

	public static Map<DayOfWeek, List<ShiftType>> getShiftCoverRequirements() {
		return shiftCoverRequirements;
	}
	public static int getNoOfEmployees() {
		return employeeMap.size();
	}
	public static int getNoOfShifts() {
		return shiftMap.size();
	}
	public static int getNoOfTasks() {
		return taskCombinationMap.size();
	}

	public static int[][] getAvailabilityMatrix() {
		return availabilityMatrix;
	}
	
	public static int[][] getShiftOffPreferenceMatrix() {
		return shiftOffPreferenceMatrix;
	}

	public static List<Employee> getEmployees() {
		return employees;
	}
	
	public static int getNoOfShiftTypes() {
		return shiftTypes.size();
	}

	public static Map<Integer, Date> getShiftDatesMap() {
		return shiftDatesMap;
	}

	public static int[][] getAbilityMatrix() {
		return abilityMatrix;
	}

	public static int[][] getCoverRequirementMatrix() {
		return coverRequirementMatrix;
	}

	public static Map<Integer, List<Task>> getTaskCombinationMap() {
		return taskCombinationMap;
	}

	public static Map<Task, Integer> getReverseTaskMap() {
		return reverseTaskMap;
	}
	
	
	
	
}
