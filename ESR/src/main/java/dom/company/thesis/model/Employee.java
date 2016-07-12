package dom.company.thesis.model;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dom.company.thesis.service.InputService;

public class Employee {
	
	private String id;
	private String name;
	private List<Task> taskQualifications = new ArrayList<Task>();
	private Map<Date,List<ShiftType>> shiftUnavailabilities;
	
	//Soft Constraints
	boolean completeWeekendsEnabled;
	boolean maxAssignmentsPerWeekEnabled;
	
	private Map<Date,List<ShiftType>> shiftOffPreferences;
	int[] shiftOffPrefNumbering;
	int maxAssignementsPerWeek;	
	
	public Employee(String id, String name, List<Task> taskQualifications) {
		super();
		this.id = id;
		this.name = name;
		this.taskQualifications = taskQualifications;
		
		completeWeekendsEnabled = false;
		maxAssignmentsPerWeekEnabled = false;
	}
	public Employee(String id, String name) {
		super();
		this.id = id;
		this.name = name;
		
		completeWeekendsEnabled = false;
		maxAssignmentsPerWeekEnabled = false;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Task> getTaskQualifications() {
		return taskQualifications;
	}
	public void setTaskQualifications(List<Task> taskQualifications) {
		this.taskQualifications = taskQualifications;
	}
	
	public Map<Date, List<ShiftType>> getShiftUnavailabilities() {
		return shiftUnavailabilities;
	}
	public void setShiftUnavailabilities(Map<Date, List<ShiftType>> shiftUnavailabilities) {
		this.shiftUnavailabilities = shiftUnavailabilities;
	}	
	
	public Map<Date, List<ShiftType>> getShiftOffPreferences() {
		return shiftOffPreferences;
	}
	public void setShiftOffPreferences(Map<Date, List<ShiftType>> shiftOffPreferences) {
		this.shiftOffPreferences = shiftOffPreferences;
		this.shiftOffPrefNumbering = new int[InputService.getNoOfShifts()];
		
		Map<Integer,ShiftType> shiftMap = InputService.getShiftMap();
		Map<Integer,Date> shiftDatesMap = InputService.getShiftDatesMap();
		
		int number = 0;
		for (int i = 0; i < shiftMap.size(); i++) {
			
			ShiftType shiftType = shiftMap.get(i);
			Date shiftDate = shiftDatesMap.get(i);
			
			if (shiftOffPreferences.get(shiftDate).contains(shiftType)) {
				this.shiftOffPrefNumbering[i] = number++;
			}
			else {
				this.shiftOffPrefNumbering[i] = Integer.MIN_VALUE;
			}
		}
	}
	
	public boolean hasShiftOffPreferences() {
		if (this.shiftOffPreferences.isEmpty()) {
			return false;
		}
		return true;
	}
	public boolean isCompleteWeekendsEnabled() {
		return completeWeekendsEnabled;
	}
	public void setCompleteWeekendsEnabled(boolean completeWeekendsEnabled) {
		this.completeWeekendsEnabled = completeWeekendsEnabled;
	}
	public boolean isMaxAssignmentsPerWeekEnabled() {
		return maxAssignmentsPerWeekEnabled;
	}
	public void setMaxAssignmentsPerWeekEnabled(boolean maxAssignmentsPerWeekEnabled) {
		this.maxAssignmentsPerWeekEnabled = maxAssignmentsPerWeekEnabled;
	}
	public int[] getShiftOffPrefNumbering() {
		return shiftOffPrefNumbering;
	}
	public void setShiftOffPrefNumbering(int[] shiftOffPrefNumbering) {
		this.shiftOffPrefNumbering = shiftOffPrefNumbering;
	}
	public int getMaxAssignementsPerWeek() {
		return maxAssignementsPerWeek;
	}
	public void setMaxAssignementsPerWeek(int maxAssignementsPerWeek) {
		this.maxAssignementsPerWeek = maxAssignementsPerWeek;
	}
	
	
	
	
}
