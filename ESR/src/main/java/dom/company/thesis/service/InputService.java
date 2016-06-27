package dom.company.thesis.service;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;

import dom.company.thesis.model.Employee;
import dom.company.thesis.model.Task;

public class InputService {
	
	private final String XML_FILE_PATH = "C:\\Users\\Steff\\CloudStation\\Thesis\\_workspace\\Input.xml";
	
	List<Task> tasks = new ArrayList<Task>();
	List<Employee> employees = new ArrayList<Employee>();
		
	public InputService() {
		
		InputParser inputParser = new InputParser(XML_FILE_PATH);
		
		this.tasks = inputParser.getTasks();
	}
	
	
	
	
}
