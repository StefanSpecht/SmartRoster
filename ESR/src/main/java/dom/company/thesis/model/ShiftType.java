package dom.company.thesis.model;

import java.sql.Time;
import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.Map;

public class ShiftType {
	
	private String id;
	private String description;
	private Time startTime;
	private Time endTime;
	private Map<DayOfWeek,Map<Task,Integer>> taskCoverRequirements = new HashMap<DayOfWeek,Map<Task,Integer>>();
	
	public ShiftType(String id, String description, Time startTime, Time endTime) {
		super();
		this.id = id;
		this.description = description;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	public ShiftType(String id, Time startTime, Time endTime) {
		super();
		this.id = id;
		this.description = id;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Time getStartTime() {
		return startTime;
	}
	public void setStartTime(Time startTime) {
		this.startTime = startTime;
	}
	public Time getEndTime() {
		return endTime;
	}
	public void setEndTime(Time endTime) {
		this.endTime = endTime;
	}
	public Map<DayOfWeek,Map<Task,Integer>> getTaskCoverRequirements() {
		return taskCoverRequirements;
	}
	public void setTaskCoverRequirements(Map<DayOfWeek,Map<Task,Integer>> taskCoverRequirements) {
		this.taskCoverRequirements = taskCoverRequirements;
	}
	public void addTaskCoverRequirement(DayOfWeek dayOfWeek, Map<Task,Integer> coverMap) {
		this.taskCoverRequirements.put(dayOfWeek, coverMap);
	}	
}
