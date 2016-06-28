package dom.company.thesis.model;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class ShiftType {
	
	private String id;
	private String description;
	private Time startTime;
	private Time endTime;
	
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

	
}
