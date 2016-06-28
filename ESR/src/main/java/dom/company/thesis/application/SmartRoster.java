package dom.company.thesis.application;

import dom.company.thesis.service.InputService;

public class SmartRoster {

	public static void main(String[] args)
	{
	     SmartRosterApplet smartRosterApplet = new SmartRosterApplet();
	     InputService inputService = new InputService();
	     inputService.parse();
	     smartRosterApplet.displayInFrame("Evonik SmartRoster");
	 }
}
