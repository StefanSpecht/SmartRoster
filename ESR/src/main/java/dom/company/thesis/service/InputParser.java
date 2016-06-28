package dom.company.thesis.service;

import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import dom.company.thesis.model.Employee;
import dom.company.thesis.model.ShiftType;
import dom.company.thesis.model.Task;

public class InputParser {
	
	private Document document;
	
	public InputParser(String xmlFilePath){
		
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

		try {
			//Using factory get an instance of document builder
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

			//parse using builder to get DOM representation of the XML file
			document = documentBuilder.parse(xmlFilePath);
		} catch(ParserConfigurationException e) {
			e.printStackTrace();
		} catch(SAXException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public Document getDocument() {
		return document;
	}

	public List<Task> getTasks() {
		
		List<Task> taskList = new ArrayList<Task>();
		
		XPath xPath =  XPathFactory.newInstance().newXPath();
		try {
			//Get all task nodes
			XPathExpression expression = xPath.compile("SchedulingPeriod/Tasks/Task");
			NodeList taskNodes = (NodeList)expression.evaluate(document, XPathConstants.NODESET);
			
			//iterate through all task nodes
			for (int i = 0; i < taskNodes.getLength(); i++) {
				
				Node taskNode = taskNodes.item(i);				
				if (taskNode != null && taskNode.getNodeType() == Node.ELEMENT_NODE) {

			        Element taskElement = (Element) taskNode;
			      
			        //Get ID of each task
			        expression = xPath.compile("@ID"); 
			        Node taskIdNode = (Node) expression.evaluate(taskElement,XPathConstants.NODE);
			        String taskId = taskIdNode.getTextContent();
			        
			        //Get description of each task
			        String taskDescription;
			        expression = xPath.compile("Description"); 
			        Node taskDescriptionNode = (Node) expression.evaluate(taskElement,XPathConstants.NODE);
			        if (taskDescriptionNode != null) {
			        	taskDescription = taskDescriptionNode.getTextContent();
			        } else {
			        	taskDescription = taskId;
			        }
			        
			        Task task = new Task(taskId, taskDescription);
			        taskList.add(task);
			    }
			}			
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return taskList;		
	}	

public List<Employee> getEmployees() {
		
		List<Employee> employeeList = new ArrayList<Employee>();
		
		XPath xPath =  XPathFactory.newInstance().newXPath();
		try {
			//Get all employee nodes
			XPathExpression expression = xPath.compile("SchedulingPeriod/Employees/Employee");
			NodeList employeeNodes = (NodeList)expression.evaluate(document, XPathConstants.NODESET);
			
			//iterate through all employee nodes
			for (int i = 0; i < employeeNodes.getLength(); i++) {
				
				Node employeeNode = employeeNodes.item(i);				
				if (employeeNode != null && employeeNode.getNodeType() == Node.ELEMENT_NODE) {

			        Element employeeElement = (Element) employeeNode;
			      
			        //Get ID of each employee
			        String employeeId;
			        expression = xPath.compile("@ID"); 
			        Node employeeIdNode = (Node) expression.evaluate(employeeElement,XPathConstants.NODE);
			        employeeId = employeeIdNode.getTextContent();
			        
			        //Get name of each employee. If not defined use ID value
			        String employeeName;
			        expression = xPath.compile("Name"); 
			        Node employeeNameNode = (Node) expression.evaluate(employeeElement,XPathConstants.NODE);
			        if (employeeNameNode != null) {
			        	employeeName = employeeNameNode.getTextContent();
			        } else {
			        	employeeName = employeeId;
			        }
			        
			        Employee employee = new Employee(employeeId, employeeName);
			        employeeList.add(employee);
			    }
			}			
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return employeeList;		
	}

	public List<String> getTaskQualificationIds(String employeeId) {
		
		List<String> taskIds = new ArrayList<String>();
		
		XPath xPath =  XPathFactory.newInstance().newXPath();
		try {
			//Get employee with queried ID
			XPathExpression expression = xPath.compile("SchedulingPeriod/Employees/Employee[@ID='" + employeeId + "']");
			Node employeeNode = (Node)expression.evaluate(document, XPathConstants.NODE);
					
			if (employeeNode != null) {

		        Element employeeElement = (Element) employeeNode;
		      
		        //Get tasks ID references for this employee
		        expression = xPath.compile("TaskQualifications/TaskQualification/@ID"); 
		        NodeList taskQualificationNodes = (NodeList) expression.evaluate(employeeElement,XPathConstants.NODESET);
		        
		        for (int i = 0; i < taskQualificationNodes.getLength(); i++) {
		        	Node taskQualificationNode = taskQualificationNodes.item(i);
		        	taskIds.add(taskQualificationNode.getTextContent());		        	
		        }
			} else {
				throw(new IllegalArgumentException());
			}			
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return taskIds;
	}

	public List<ShiftType> getShiftTypes() {
		
		List<ShiftType> shiftTypeList = new ArrayList<ShiftType>();		
		XPath xPath =  XPathFactory.newInstance().newXPath();
		
		try {
			//Get all Shift nodes
			XPathExpression expression = xPath.compile("SchedulingPeriod/ShiftTypes/Shift");
			NodeList shiftTypeNodes = (NodeList)expression.evaluate(document, XPathConstants.NODESET);
			
			//iterate through all Shift nodes
			for (int i = 0; i < shiftTypeNodes.getLength(); i++) {
				
				Node shiftTypeNode = shiftTypeNodes.item(i);				
				if (shiftTypeNode != null && shiftTypeNode.getNodeType() == Node.ELEMENT_NODE) {

			        Element shiftTypeElement = (Element) shiftTypeNode;
			      
			        //Get ID of each shiftType
			        expression = xPath.compile("@ID"); 
			        Node shiftTypeIdNode = (Node) expression.evaluate(shiftTypeElement,XPathConstants.NODE);
			        String shiftTypeId = shiftTypeIdNode.getTextContent();
			        
			        //Get description of each shiftType
			        String shiftTypeDescription;
			        expression = xPath.compile("Description"); 
			        Node shiftTypeDescriptionNode = (Node) expression.evaluate(shiftTypeElement,XPathConstants.NODE);
			        if (shiftTypeDescriptionNode != null) {
			        	shiftTypeDescription = shiftTypeDescriptionNode.getTextContent();
			        } else {
			        	shiftTypeDescription = shiftTypeId;
			        }
			        
			        //Get StartTime of each shift
			        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
			        expression = xPath.compile("StartTime"); 
			        Node shiftTypeStartTimeNode = (Node) expression.evaluate(shiftTypeElement,XPathConstants.NODE);
			        Time shiftTypeStartTime = new Time(dateFormat.parse(shiftTypeStartTimeNode.getTextContent()).getTime());
			        
			      //Get EndTime of each shift
			        expression = xPath.compile("EndTime"); 
			        Node shiftTypeEndTimeNode = (Node) expression.evaluate(shiftTypeElement,XPathConstants.NODE);
			        Time shiftTypeEndTime = new Time(dateFormat.parse(shiftTypeEndTimeNode.getTextContent()).getTime());
			        
			        
			        ShiftType shiftType = new ShiftType(shiftTypeId, shiftTypeDescription, shiftTypeStartTime, shiftTypeEndTime);
			        shiftTypeList.add(shiftType);
			    }
			}			
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		} catch (DOMException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return shiftTypeList;	
	}

	public List<List<String>> getTaskCombinationIds() {
		
		List<List<String>> taskCombinations = new ArrayList<List<String>>();		
		XPath xPath =  XPathFactory.newInstance().newXPath();
		
		try {
			//Get all Task nodes
			XPathExpression expression = xPath.compile("SchedulingPeriod/TaskCombinations/TaskCombination");
			NodeList taskCombinationNodes = (NodeList)expression.evaluate(document, XPathConstants.NODESET);
			
			//iterate through all TaskCombination nodes
			for (int i = 0; i < taskCombinationNodes.getLength(); i++) {
				
				Node taskCombinationNode = taskCombinationNodes.item(i);				
				if (taskCombinationNode != null && taskCombinationNode.getNodeType() == Node.ELEMENT_NODE) {

			        Element taskCombinationElement = (Element) taskCombinationNode;
			      
			        //Get all tasks included in this combination entry
			        expression = xPath.compile("Task"); 
			        NodeList taskNodes = (NodeList) expression.evaluate(taskCombinationElement,XPathConstants.NODESET);
			        
			        //iterate through all included tasks and add it to the tasklist
			        List<String> tasks = new ArrayList<String>();
					for (int y = 0; y < taskNodes.getLength(); y++) {
						tasks.add(taskNodes.item(y).getTextContent());
					}
					
					//Finally, add the task list to the task-combination list
					taskCombinations.add(tasks);
				}
			}			
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		} catch (DOMException e) {
			e.printStackTrace();
		}
		return taskCombinations;
	}

	public Map<String, Integer> getTaskIdCoverRequirements(String shiftTypeId) {

		Map<String,Integer> taskIdCoverRequirements = new HashMap<String,Integer>();		
		XPath xPath =  XPathFactory.newInstance().newXPath();
		
		try {
			//Get taskCoverRequirements node for queried shiftType-ID
			XPathExpression expression = xPath.compile("SchedulingPeriod/TaskCoverRequirements/TaskCoverRequirement[@ShiftType='" + shiftTypeId + "']");
			Node taskCoverRequirementsNode = (Node)expression.evaluate(document, XPathConstants.NODE);
					
			if (taskCoverRequirementsNode != null) {

		        Element taskCoverRequirements = (Element) taskCoverRequirementsNode;
		      
		        //Get all Cover nodes
		        expression = xPath.compile("Cover"); 
		        NodeList coverNodes = (NodeList) expression.evaluate(taskCoverRequirements,XPathConstants.NODESET);
		        
		        //Iterate through all cover requirements
		        for (int i = 0; i < coverNodes.getLength(); i++) {
		        	
		        	Node coverNode = coverNodes.item(i);
		        	
		        	if (coverNode != null && coverNode.getNodeType() == Node.ELEMENT_NODE) {

				        Element coverElement = (Element) coverNode;
				        
				        //Get Task Id
				        String taskId;
				        expression = xPath.compile("Task"); 
				        Node taskIdNode = (Node) expression.evaluate(coverElement,XPathConstants.NODE);
				        taskId = taskIdNode.getTextContent();
				        
				        //Get quantity
				        int quantity;
				        expression = xPath.compile("Quantity"); 
				        Node quantityNode = (Node) expression.evaluate(coverElement,XPathConstants.NODE);
				        quantity = Integer.parseInt(quantityNode.getTextContent());
				        
				        //Add to map
				        taskIdCoverRequirements.put(taskId, quantity);
		        	}		        			        	
		        }
			} else {
				throw(new IllegalArgumentException());
			}			
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return taskIdCoverRequirements;
	}
}
