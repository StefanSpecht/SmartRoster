package dom.company.thesis.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import dom.company.thesis.model.Employee;
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
			        expression = xPath.compile("Description"); 
			        Node taskDescriptionNode = (Node) expression.evaluate(taskElement,XPathConstants.NODE);
			        String taskDescription = taskDescriptionNode.getTextContent();
			        
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
}
