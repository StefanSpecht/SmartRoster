package dom.company.thesis.gui;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.uncommons.watchmaker.framework.interactive.Renderer;
import dom.company.thesis.model.RosterTableModel;
import dom.company.thesis.model.Roster;

public class RosterRenderer implements Renderer<Roster,JComponent> {

	private RosterTableModel rosterTableModel = new RosterTableModel();
	Roster roster;
	private static boolean isEnabled;
	
	public RosterRenderer() {
		isEnabled = false;
	}
	
	@Override
	public JComponent render(Roster roster) {
		if (isEnabled) {
			//create JTable
			this.roster = roster;
			rosterTableModel.setRoster(roster);
			JTable rosterTable = new JTable(rosterTableModel);
			
			//Put it into JScrollPane
			JScrollPane scrollPane = new JScrollPane(rosterTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			
			//AutoResize = off
			rosterTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			
			return scrollPane;
		}
		else return new JPanel();
	}
	
	static public void enable() {
		isEnabled = true;
	}
	static public void disable() {
		isEnabled = false;
	}

}
