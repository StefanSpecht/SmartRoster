package dom.company.thesis.model;

import java.util.List;
import java.util.Map;

import javax.swing.AbstractListModel;

import dom.company.thesis.service.InputService;

public class EmployeeListModel extends AbstractListModel<String>{
	
	private static final long serialVersionUID = 1939037425527217351L;
	
	int noOfEmployees;
	Map<Integer,Employee> employeeMap;
	String[] employeeNames;
	
	public EmployeeListModel() {
		this.noOfEmployees = InputService.getNoOfEmployees();
		this.employeeMap = InputService.getEmployeeMap();
		this.employeeNames = new String[noOfEmployees];
		
		for (int i = 0; i < noOfEmployees; i++) {
			employeeNames[i] = employeeMap.get(i).getId();
		}
	}
	@Override
	public String getElementAt(int index) {
		return employeeNames[index];
	}
	@Override
	public int getSize() {
		return employeeNames.length;
	}
	
	
 
}