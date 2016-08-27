package dom.company.thesis.service;

import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
	
public List<String> getShiftTypeIds() {
		
		List<String> shiftTypeIds = new ArrayList<String>();		
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
			        shiftTypeIds.add(shiftTypeId);
			    }
			}			
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		} catch (DOMException e) {
			e.printStackTrace();
		}
		return shiftTypeIds;	
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

	public Map<DayOfWeek, Map<String, Integer>> getTaskIdCoverRequirements(String shiftTypeId) {

		Map<DayOfWeek, Map<String, Integer>> taskIdCoverRequirements = new HashMap<DayOfWeek, Map<String, Integer>>();		
		XPath xPath =  XPathFactory.newInstance().newXPath();
		
		
			//Get taskCoverRequirements node for queried shiftType-ID for each DayOfWeek
		for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
			try {
				Map<String,Integer> currTaskIdCoverRequirements = new HashMap<String,Integer>();
				XPathExpression expression = xPath.compile("SchedulingPeriod/TaskCoverRequirements/TaskCoverRequirement[@ShiftType='" + shiftTypeId + "' and (contains(@WeekDays,'" + dayOfWeek.toString() + "'))]");
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
					        currTaskIdCoverRequirements.put(taskId, quantity);
			        	} else {
							throw(new IllegalArgumentException());
						}	        			        	
			        }
			        taskIdCoverRequirements.put(dayOfWeek, currTaskIdCoverRequirements);
				} else {
					throw(new IllegalArgumentException());
				}			
			} catch (XPathExpressionException e) {
				e.printStackTrace();
			}
		}
		
		return taskIdCoverRequirements;
	}

	public Map<DayOfWeek, List<String>> getShiftIdCoverRequirements() {
		
		Map<DayOfWeek,List<String>> shiftIdCoverRequirements = new HashMap<DayOfWeek,List<String>>();		
		XPath xPath =  XPathFactory.newInstance().newXPath();
		
		try {
			//Get DayOfWeek shiftCoverRequirement nodes
			XPathExpression expression = xPath.compile("SchedulingPeriod/ShiftCoverRequirements/DayOfWeekCoverRequirement");
			NodeList dayOfWeekCoverRequirementsNodes = (NodeList)expression.evaluate(document, XPathConstants.NODESET);
					
			
		        //Iterate through all cover requirements
		        for (int i = 0; i < dayOfWeekCoverRequirementsNodes.getLength(); i++) {
		        	
		        	Node dayOfWeekCoverRequirementNode = dayOfWeekCoverRequirementsNodes.item(i);
		        	
		        	if (dayOfWeekCoverRequirementNode != null && dayOfWeekCoverRequirementNode.getNodeType() == Node.ELEMENT_NODE) {

				        Element dayOfWeekCoverRequirementElement = (Element) dayOfWeekCoverRequirementNode;
				        
				        //Get DayOfWeek
				        DayOfWeek dayOfWeek;
				        expression = xPath.compile("Day"); 
				        Node dayOfWeekNode = (Node) expression.evaluate(dayOfWeekCoverRequirementElement,XPathConstants.NODE);
				        dayOfWeek = DayOfWeek.valueOf(dayOfWeekNode.getTextContent().toUpperCase());
				        
				        //Get list of required shifts for that day
				        List<String> shiftCovers = new ArrayList<String>();
				        
				        expression = xPath.compile("Cover"); 
				        NodeList coverNodes = (NodeList) expression.evaluate(dayOfWeekCoverRequirementElement,XPathConstants.NODESET);

				        //Iterate through all cover requirements
				        for (int y = 0; y < coverNodes.getLength(); y++) {
				        	
				        	Node coverNode = coverNodes.item(y);
				        	
				        	if (coverNode != null && coverNode.getNodeType() == Node.ELEMENT_NODE) {

						        Element coverElement = (Element) coverNode;
						        
						        //Get shift Id
						        String shiftId;
						        expression = xPath.compile("@Shift"); 
						        Node shiftIdNode = (Node) expression.evaluate(coverElement,XPathConstants.NODE);
						        shiftId = shiftIdNode.getTextContent();
						        
						        //Add shiftId to List
						        shiftCovers.add(shiftId);
				        	} else {
								throw(new IllegalArgumentException());
							}	        			        	
				        }
				        
				        //Add DayofWeek shiftCoverRequirement to map
				        shiftIdCoverRequirements.put(dayOfWeek, shiftCovers);
		        	}		        			        	
		        }
						
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return shiftIdCoverRequirements;
	}

	public Date getStartDate() {
		
		XPath xPath =  XPathFactory.newInstance().newXPath();
		java.util.Date date = new java.sql.Date(0);
		try {
			//Get startDate node
			XPathExpression expression = xPath.compile("SchedulingPeriod/StartDate");
			Node startDateNode = (Node)expression.evaluate(document, XPathConstants.NODE);
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			date = dateFormat.parse(startDateNode.getTextContent());			
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new java.sql.Date(date.getTime());
	}
	
	public Date getEndDate() {
		
		XPath xPath =  XPathFactory.newInstance().newXPath();
		java.util.Date date = new java.sql.Date(0);
		try {
			//Get endDate node
			XPathExpression expression = xPath.compile("SchedulingPeriod/EndDate");
			Node endDateNode = (Node)expression.evaluate(document, XPathConstants.NODE);
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			date = dateFormat.parse(endDateNode.getTextContent());			
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new java.sql.Date(date.getTime());
	}

	public Map<Date, List<String>> getShifIdUnavailabilities(String employeeId, Date startDate, Date endDate) {
		
		XPath xPath =  XPathFactory.newInstance().newXPath();
		Map<Date,List<String>> shiftIdUnavailabilities = new HashMap<Date,List<String>>();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		long noOfDays = Math.abs(((endDate.getTime() - startDate.getTime()) / 86400000) + 1);
		
		calendar.setTime(startDate);
		calendar.add(Calendar.DAY_OF_YEAR,-1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		//Get all EmployeeGroupIds for the employee
		List<String> employeeGroupIds = this.getEmployeeGroupIds(employeeId);
		
		//get all shiftUnavailabilities and dayUnavailabilities for the employee, or its group
		try {
			
			for (String employeeGroupId : employeeGroupIds) {
				
				//Iterate through all dates
				for (int i=0; i < noOfDays; i++) {
					calendar.add(Calendar.DAY_OF_YEAR,1);
					Date currentDate = new Date(calendar.getTimeInMillis());
					String currentDateString = dateFormat.format(currentDate);
					List<String> currentShiftIds = new ArrayList<String>();
					//List<String> currentShiftUnavailabilityfExceptions = new ArrayList<String>();
					
					//Check, if there is a dayUnavailability for this date. If yes, add all shifts to the list
					XPathExpression expression = xPath.compile("SchedulingPeriod/DayUnavailabilities/DayUnavailability[(EmployeeGroup='" + employeeGroupId + "' or Employee='" + employeeId + "') and Date='" + currentDateString + "']");
					NodeList dayUnavailabilityNodes = (NodeList)expression.evaluate(document, XPathConstants.NODESET);
					
					if (dayUnavailabilityNodes != null && dayUnavailabilityNodes.getLength() > 0) {
						currentShiftIds.addAll(this.getShiftTypeIds());
					} 
					else {
						//If there is no dayUnavailability, check for shiftUnavailabilities
						expression = xPath.compile("SchedulingPeriod/ShiftUnavailabilities/ShiftUnavailability[(EmployeeGroup='" + employeeGroupId + "' or Employee='" + employeeId + "') and (Date='" + currentDateString + "' or not(Date))]/Shift");
						NodeList shiftUnavailabilityNodes = (NodeList)expression.evaluate(document, XPathConstants.NODESET);
						expression = xPath.compile("SchedulingPeriod/ShiftUnavailabilities/ShiftUnavailability[(EmployeeGroup='" + employeeGroupId + "' or Employee='" + employeeId + "') and (Date='" + currentDateString + "' or not(Date))]/ShiftGroup");
						NodeList shiftGroupUnavailabilityNodes = (NodeList)expression.evaluate(document, XPathConstants.NODESET);
						
						if (shiftUnavailabilityNodes != null) {
													
							for (int y = 0; y < shiftUnavailabilityNodes.getLength(); y++) {
								String shiftId = shiftUnavailabilityNodes.item(y).getTextContent();
								currentShiftIds.add(shiftId);
							}
						}
						
						if (shiftGroupUnavailabilityNodes != null) {
							
							for (int y = 0; y < shiftGroupUnavailabilityNodes.getLength(); y++) {
								String shiftGroupId = shiftGroupUnavailabilityNodes.item(y).getTextContent();
								List<String> shiftIdsInGroup = this.getShiftIdsFromGroupId(shiftGroupId);
								currentShiftIds.addAll(shiftIdsInGroup);
							}
						}
						
						//Remove all shiftUnavailabilityExceptions from list
						expression = xPath.compile("SchedulingPeriod/ShiftUnavailabilityfExceptions/ShiftUnavailabilityfException[(EmployeeGroup='" + employeeGroupId + "' or Employee='" + employeeId + "') and (Date='" + currentDateString + "' or not(Date))]/Shift");
						NodeList ShiftUnavailabilityfExceptionNodes = (NodeList)expression.evaluate(document, XPathConstants.NODESET);
						expression = xPath.compile("SchedulingPeriod/ShiftUnavailabilityfExceptions/ShiftUnavailabilityfException[(EmployeeGroup='" + employeeGroupId + "' or Employee='" + employeeId + "') and (Date='" + currentDateString + "' or not(Date))]/ShiftGroup");
						NodeList shiftGroupUnavailabilityfExceptionNodes = (NodeList)expression.evaluate(document, XPathConstants.NODESET);
						
						if (ShiftUnavailabilityfExceptionNodes != null) {							
							for (int y = 0; y < ShiftUnavailabilityfExceptionNodes.getLength(); y++) {
								String shiftId = ShiftUnavailabilityfExceptionNodes.item(y).getTextContent();
								currentShiftIds.remove(shiftId);
							}
						}
						
						if (shiftGroupUnavailabilityfExceptionNodes != null) {
							
							for (int y = 0; y < shiftGroupUnavailabilityfExceptionNodes.getLength(); y++) {
								String shiftGroupId = shiftGroupUnavailabilityfExceptionNodes.item(y).getTextContent();
								List<String> shiftIdsInGroup = this.getShiftIdsFromGroupId(shiftGroupId);
								currentShiftIds.removeAll(shiftIdsInGroup);
							}
						}
						shiftIdUnavailabilities.put(currentDate, currentShiftIds);
					}
					
					// Add all shiftUnavailabilities to the map
					if (!currentShiftIds.isEmpty()) {
						shiftIdUnavailabilities.put(currentDate, currentShiftIds);
					}
					
				}
			}
			
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return shiftIdUnavailabilities;
	}
	
	public boolean isCompleteWeekendsEnabled(String employeeId) {
		XPath xPath =  XPathFactory.newInstance().newXPath();
				
		//Get all EmployeeGroupIds for the employee
		List<String> employeeGroupIds = this.getEmployeeGroupIds(employeeId);
		
		try {
			
			for (String employeeGroupId : employeeGroupIds) {
				
								
				//Check, if there is a ConstraintSet for the employee or it's group that has the CompleteWeekends Constraint enabled
				XPathExpression expression = xPath.compile("SchedulingPeriod/ConstraintSets/ConstraintSet[(EmployeeGroup='" + employeeGroupId + "' or Employee='" + employeeId + "') and CompleteWeekends]");
				NodeList constraintSetNodes = (NodeList)expression.evaluate(document, XPathConstants.NODESET);
				
				if (constraintSetNodes != null && constraintSetNodes.getLength() > 0) {
					return true;
				}
			}				
							
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public int getMaxAssignmentsPerWeek(String employeeId) {
		XPath xPath =  XPathFactory.newInstance().newXPath();
				
		//Get all EmployeeGroupIds for the employee
		List<String> employeeGroupIds = this.getEmployeeGroupIds(employeeId);
		
		try {
			
			for (String employeeGroupId : employeeGroupIds) {
				
								
				//Check, if there is a ConstraintSet for the employee or it's group that has the CompleteWeekends Constraint enabled
				XPathExpression expression = xPath.compile("SchedulingPeriod/ConstraintSets/ConstraintSet[(EmployeeGroup='" + employeeGroupId + "' or Employee='" + employeeId + "') and AssignmentsPerWeek]/AssignmentsPerWeek");
				NodeList constraintSetNodes = (NodeList)expression.evaluate(document, XPathConstants.NODESET);
				
				if (constraintSetNodes != null && constraintSetNodes.getLength() > 0) {
					return Integer.parseInt(constraintSetNodes.item(0).getTextContent());
				}
			}				
							
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return -1;
	}

	private List<String> getShiftIdsFromGroupId(String shiftGroupId) {
		
		XPath xPath =  XPathFactory.newInstance().newXPath();
		List<String> shiftIds = new ArrayList<String>();
		
		try {
			XPathExpression expression = xPath.compile("SchedulingPeriod/ShiftGroups/ShiftGroup[@ID='" + shiftGroupId + "']/Shift");
			NodeList shiftNodes = (NodeList)expression.evaluate(document, XPathConstants.NODESET);
			
			for (int i = 0; i < shiftNodes.getLength(); i++) {
				String shiftId = shiftNodes.item(i).getTextContent();
				shiftIds.add(shiftId);
			}			
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return shiftIds;
	}
	
public Map<Date, List<String>> getShifIdOffPreferences(String employeeId, Date startDate, Date endDate) {
		
		XPath xPath =  XPathFactory.newInstance().newXPath();
		Map<Date,List<String>> shiftIdOffPreferences = new HashMap<Date,List<String>>();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		long noOfDays = Math.abs(((endDate.getTime() - startDate.getTime()) / 86400000) + 1);
		
		calendar.setTime(startDate);
		calendar.add(Calendar.DAY_OF_YEAR,-1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		//Get all EmployeeGroupIds for the employee
		List<String> employeeGroupIds = this.getEmployeeGroupIds(employeeId);
		
		//get all shiftOff and dayOff request for the employee, or its group
		try {
			
			for (String employeeGroupId : employeeGroupIds) {
				
				//Iterate through all dates
				for (int i=0; i < noOfDays; i++) {
					calendar.add(Calendar.DAY_OF_YEAR,1);
					Date currentDate = new Date(calendar.getTimeInMillis());
					String currentDateString = dateFormat.format(currentDate);
					List<String> currentShiftIds = new ArrayList<String>();
					//List<String> currentShiftOffPreferencefExceptions = new ArrayList<String>();
					
					//Check, if there is a dayOffPreference for this date. If yes, add all shifts to the list
					XPathExpression expression = xPath.compile("SchedulingPeriod/DayOffPreferences/DayOffPreference[(EmployeeGroup='" + employeeGroupId + "' or Employee='" + employeeId + "') and Date='" + currentDateString + "']");
					NodeList dayOffPreferenceNodes = (NodeList)expression.evaluate(document, XPathConstants.NODESET);
					
					if (dayOffPreferenceNodes != null && dayOffPreferenceNodes.getLength() > 0) {
						currentShiftIds.addAll(this.getShiftTypeIds());
					} 
					else {
						//If there is no dayOffPreference, check for shiftOffPreferences
						expression = xPath.compile("SchedulingPeriod/ShiftOffPreferences/ShiftOffPreference[(EmployeeGroup='" + employeeGroupId + "' or Employee='" + employeeId + "') and (Date='" + currentDateString + "' or not(Date))]/Shift");
						NodeList shiftOffPreferenceNodes = (NodeList)expression.evaluate(document, XPathConstants.NODESET);
						expression = xPath.compile("SchedulingPeriod/ShiftOffPreferences/ShiftOffPreference[(EmployeeGroup='" + employeeGroupId + "' or Employee='" + employeeId + "') and (Date='" + currentDateString + "' or not(Date))]/ShiftGroup");
						NodeList shiftGroupOffPreferenceNodes = (NodeList)expression.evaluate(document, XPathConstants.NODESET);
						
						if (shiftOffPreferenceNodes != null) {
													
							for (int y = 0; y < shiftOffPreferenceNodes.getLength(); y++) {
								String shiftId = shiftOffPreferenceNodes.item(y).getTextContent();
								currentShiftIds.add(shiftId);
							}
						}
						
						if (shiftGroupOffPreferenceNodes != null) {
							
							for (int y = 0; y < shiftGroupOffPreferenceNodes.getLength(); y++) {
								String shiftGroupId = shiftGroupOffPreferenceNodes.item(y).getTextContent();
								List<String> shiftIdsInGroup = this.getShiftIdsFromGroupId(shiftGroupId);
								currentShiftIds.addAll(shiftIdsInGroup);
							}
						}
						
						//Remove all shiftOffPreferenceExceptions from list
						expression = xPath.compile("SchedulingPeriod/ShiftOffPreferencefExceptions/ShiftOffPreferencefException[(EmployeeGroup='" + employeeGroupId + "' or Employee='" + employeeId + "') and (Date='" + currentDateString + "' or not(Date))]/Shift");
						NodeList ShiftOffPreferencefExceptionNodes = (NodeList)expression.evaluate(document, XPathConstants.NODESET);
						expression = xPath.compile("SchedulingPeriod/ShiftOffPreferencefExceptions/ShiftOffPreferencefException[(EmployeeGroup='" + employeeGroupId + "' or Employee='" + employeeId + "') and (Date='" + currentDateString + "' or not(Date))]/ShiftGroup");
						NodeList shiftGroupOffPreferencefExceptionNodes = (NodeList)expression.evaluate(document, XPathConstants.NODESET);
						
						if (ShiftOffPreferencefExceptionNodes != null) {							
							for (int y = 0; y < ShiftOffPreferencefExceptionNodes.getLength(); y++) {
								String shiftId = ShiftOffPreferencefExceptionNodes.item(y).getTextContent();
								currentShiftIds.remove(shiftId);
							}
						}
						
						if (shiftGroupOffPreferencefExceptionNodes != null) {
							
							for (int y = 0; y < shiftGroupOffPreferencefExceptionNodes.getLength(); y++) {
								String shiftGroupId = shiftGroupOffPreferencefExceptionNodes.item(y).getTextContent();
								List<String> shiftIdsInGroup = this.getShiftIdsFromGroupId(shiftGroupId);
								currentShiftIds.removeAll(shiftIdsInGroup);
							}
						}
						shiftIdOffPreferences.put(currentDate, currentShiftIds);
					}
					
					// Add all shiftOffPreferences to the map
					if (!currentShiftIds.isEmpty()) {
						shiftIdOffPreferences.put(currentDate, currentShiftIds);
					}
					
				}
			}
			
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return shiftIdOffPreferences;
	}

	private List<String> getEmployeeGroupIds(String employeeId) {
		
		List<String> employeeGroupIds = new ArrayList<String>();
		XPath xPath =  XPathFactory.newInstance().newXPath();
		
		try {
			XPathExpression expression = xPath.compile("SchedulingPeriod/EmployeeGroups/EmployeeGroup[Employee='" + employeeId + "']/@ID");
			NodeList employeeGroupNodes = (NodeList)expression.evaluate(document, XPathConstants.NODESET);
			
			for (int i = 0; i < employeeGroupNodes.getLength(); i++) {
				employeeGroupIds.add(employeeGroupNodes.item(i).getTextContent());
			}			
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return employeeGroupIds;
	}

	public int getTerminationSetting() {
		
		XPath xPath =  XPathFactory.newInstance().newXPath();
		
		try {
			XPathExpression expression = xPath.compile("SchedulingPeriod/GlobalSettings/Termination/StagnationLimit");
			Node terminationNode = (Node) expression.evaluate(document,XPathConstants.NODE);
			
			return Integer.parseInt(terminationNode.getTextContent());
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		
		return 50;
	}

	public Map<String, Integer> getExperimentPopulationSizeSettings() {
		Map<String,Integer> experimentPopulationSizeSettings = new HashMap<String,Integer>();
		XPath xPathMin =  XPathFactory.newInstance().newXPath();
		XPath xPathMax =  XPathFactory.newInstance().newXPath();
		XPath xPathGran =  XPathFactory.newInstance().newXPath();
		
		try {
			XPathExpression expressionMin = xPathMin.compile("SchedulingPeriod/GlobalSettings/Experiments/PopulationSize/Min");
			Node minNode = (Node) expressionMin.evaluate(document,XPathConstants.NODE);
			
			XPathExpression expressionMax = xPathMax.compile("SchedulingPeriod/GlobalSettings/Experiments/PopulationSize/Max");
			Node maxNode = (Node) expressionMax.evaluate(document,XPathConstants.NODE);
			
			XPathExpression expressionGran = xPathGran.compile("SchedulingPeriod/GlobalSettings/Experiments/PopulationSize/Granularity");
			Node granNode = (Node) expressionGran.evaluate(document,XPathConstants.NODE);
			
			experimentPopulationSizeSettings.put("min", Integer.parseInt(minNode.getTextContent()));
			experimentPopulationSizeSettings.put("max", Integer.parseInt(maxNode.getTextContent()));
			experimentPopulationSizeSettings.put("gran", Integer.parseInt(granNode.getTextContent()));
			
			return experimentPopulationSizeSettings;
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		
		//default values
		experimentPopulationSizeSettings.put("min", 50);
		experimentPopulationSizeSettings.put("max", 2500);
		experimentPopulationSizeSettings.put("gran", 50);
		return experimentPopulationSizeSettings;
	}
	
	public Map<String, Integer> getExperimentCrossPointsSettings() {
		Map<String,Integer> experimentCrossPointsSettings = new HashMap<String,Integer>();
		XPath xPathMin =  XPathFactory.newInstance().newXPath();
		XPath xPathMax =  XPathFactory.newInstance().newXPath();
		XPath xPathGran =  XPathFactory.newInstance().newXPath();
		
		try {
			XPathExpression expressionMin = xPathMin.compile("SchedulingPeriod/GlobalSettings/Experiments/CrossoverPoints/Min");
			Node minNode = (Node) expressionMin.evaluate(document,XPathConstants.NODE);
			
			XPathExpression expressionMax = xPathMax.compile("SchedulingPeriod/GlobalSettings/Experiments/CrossoverPoints/Max");
			Node maxNode = (Node) expressionMax.evaluate(document,XPathConstants.NODE);
			
			XPathExpression expressionGran = xPathGran.compile("SchedulingPeriod/GlobalSettings/Experiments/CrossoverPoints/Granularity");
			Node granNode = (Node) expressionGran.evaluate(document,XPathConstants.NODE);
			
			experimentCrossPointsSettings.put("min", Integer.parseInt(minNode.getTextContent()));
			experimentCrossPointsSettings.put("max", Integer.parseInt(maxNode.getTextContent()));
			experimentCrossPointsSettings.put("gran", Integer.parseInt(granNode.getTextContent()));
			
			return experimentCrossPointsSettings;
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		
		//default values
		experimentCrossPointsSettings.put("min", 50);
		experimentCrossPointsSettings.put("max", 2500);
		experimentCrossPointsSettings.put("gran", 50);
		return experimentCrossPointsSettings;
	}
	
	public Map<String, Double> getExperimentPcSettings() {
		Map<String,Double> experimentPcSettings = new HashMap<String,Double>();
		XPath xPathMin =  XPathFactory.newInstance().newXPath();
		XPath xPathMax =  XPathFactory.newInstance().newXPath();
		XPath xPathGran =  XPathFactory.newInstance().newXPath();
		
		try {
			XPathExpression expressionMin = xPathMin.compile("SchedulingPeriod/GlobalSettings/Experiments/CrossoverRate/Min");
			Node minNode = (Node) expressionMin.evaluate(document,XPathConstants.NODE);
			
			XPathExpression expressionMax = xPathMax.compile("SchedulingPeriod/GlobalSettings/Experiments/CrossoverRate/Max");
			Node maxNode = (Node) expressionMax.evaluate(document,XPathConstants.NODE);
			
			XPathExpression expressionGran = xPathGran.compile("SchedulingPeriod/GlobalSettings/Experiments/CrossoverRate/Granularity");
			Node granNode = (Node) expressionGran.evaluate(document,XPathConstants.NODE);
			
			experimentPcSettings.put("min", Double.parseDouble(minNode.getTextContent()));
			experimentPcSettings.put("max", Double.parseDouble(maxNode.getTextContent()));
			experimentPcSettings.put("gran", Double.parseDouble(granNode.getTextContent()));
			
			return experimentPcSettings;
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		
		//default values
		experimentPcSettings.put("min", 0.5);
		experimentPcSettings.put("max", 1.0);
		experimentPcSettings.put("gran", 0.1);
		return experimentPcSettings;
	}
	
	public Map<String, Double> getExperimentPmSettings() {
		Map<String,Double> experimentPmSettings = new HashMap<String,Double>();
		XPath xPathMin =  XPathFactory.newInstance().newXPath();
		XPath xPathMax =  XPathFactory.newInstance().newXPath();
		XPath xPathGran =  XPathFactory.newInstance().newXPath();
		
		try {
			XPathExpression expressionMin = xPathMin.compile("SchedulingPeriod/GlobalSettings/Experiments/MutationRate/Min");
			Node minNode = (Node) expressionMin.evaluate(document,XPathConstants.NODE);
			
			XPathExpression expressionMax = xPathMax.compile("SchedulingPeriod/GlobalSettings/Experiments/MutationRate/Max");
			Node maxNode = (Node) expressionMax.evaluate(document,XPathConstants.NODE);
			
			XPathExpression expressionGran = xPathGran.compile("SchedulingPeriod/GlobalSettings/Experiments/MutationRate/Granularity");
			Node granNode = (Node) expressionGran.evaluate(document,XPathConstants.NODE);
			
			experimentPmSettings.put("min", Double.parseDouble(minNode.getTextContent()));
			experimentPmSettings.put("max", Double.parseDouble(maxNode.getTextContent()));
			experimentPmSettings.put("gran", Double.parseDouble(granNode.getTextContent()));
			
			return experimentPmSettings;
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		
		//default values
		experimentPmSettings.put("min", 0.0000);
		experimentPmSettings.put("max", 0.003);
		experimentPmSettings.put("gran", 0.0001);
		return experimentPmSettings;
	}
	
}
