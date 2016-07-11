package dom.company.thesis.model;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Employee {
	
	private String id;
	private String name;
	private List<Task> taskQualifications = new ArrayList<Task>();
	private Map<Date,List<ShiftType>> shiftUnavailabilities;
	private Map<Date,List<ShiftType>> shiftOffPreferences;
	
	
	public Employee(String id, String name, List<Task> taskQualifications) {
		super();
		this.id = id;
		this.name = name;
		this.taskQualifications = taskQualifications;
	}
	public Employee(String id, String name) {
		super();
		this.id = id;
		this.name = name;
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
	}	
	
	
}
