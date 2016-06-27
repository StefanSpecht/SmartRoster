package dom.company.thesis.model;

import java.util.ArrayList;
import java.util.List;

public class Employee {
	
	private String id;
	private String name;
	private List<Task> taskQualifications = new ArrayList<Task>();
	
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
}
