package dom.company.thesis.application;

import dom.company.thesis.service.InputService;

public class SmartRoster {

	public static void main(String[] args)
	{
		 InputService.parse();
		 SmartRosterApplet smartRosterApplet = new SmartRosterApplet();
	     smartRosterApplet.displayInFrame("Evonik SmartRoster");
	 }
}
