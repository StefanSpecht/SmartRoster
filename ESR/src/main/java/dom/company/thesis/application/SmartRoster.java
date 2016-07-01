package dom.company.thesis.application;

import java.util.Map;

import dom.company.thesis.model.Employee;
import dom.company.thesis.service.InputService;

public class SmartRoster {

	public static void main(String[] args)
	{
		 InputService.parse();
		 SmartRosterApplet smartRosterApplet = new SmartRosterApplet();
	     smartRosterApplet.displayInFrame("Evonik SmartRoster");
	 }
}
