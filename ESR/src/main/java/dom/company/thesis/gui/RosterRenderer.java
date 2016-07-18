package dom.company.thesis.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.uncommons.watchmaker.framework.interactive.Renderer;
import dom.company.thesis.model.RosterTableModel;
import dom.company.thesis.service.InputService;
import dom.company.thesis.model.EmployeeListModel;
import dom.company.thesis.model.Roster;

public class RosterRenderer implements Renderer<Roster,JComponent> {

	private RosterTableModel rosterTableModel = new RosterTableModel();
	private EmployeeListModel employeeListModel = new EmployeeListModel();
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
			JTable rosterTable = new JTable(rosterTableModel) {
				private static final long serialVersionUID = 609460242668215696L;
				protected JTableHeader createDefaultTableHeader() {
			          return new GroupableTableHeader(columnModel);
			      }
			};
						
			//Add row headers (employee names)
			JList<String> rowHeader = new JList<String>(employeeListModel);
			//rowHeader.setFixedCellWidth(30);
		    rowHeader.setFixedCellHeight(rosterTable.getRowHeight());
		    rowHeader.setCellRenderer(new EmployeeListRenderer(rosterTable));
		    
		    //Group column headers (employee names)
		    Map<Integer,Date> shiftDatesMap = InputService.getShiftDatesMap();
		    TableColumnModel columnModel = rosterTable.getColumnModel();
		    GroupableTableHeader header = (GroupableTableHeader)rosterTable.getTableHeader();
		    
		    
		    for (int i = 0; i < InputService.getNoOfDays(); i++) {
		    	
		    	String dayLabel = new SimpleDateFormat("dd.MM").format(shiftDatesMap.get(i * InputService.getNoOfShiftTypes()));
		    	ColumnGroup dayGroup = new ColumnGroup(dayLabel);
		    	for (int j = 0; j < InputService.getNoOfShiftTypes(); j++) {
		    		dayGroup.add(columnModel.getColumn(i* InputService.getNoOfShiftTypes() + j));
		      	}
		    	header.addColumnGroup(dayGroup);
		    }
		    
		    //Resize Cells
		    for (int column = 0; column < rosterTable.getColumnCount(); column++) {
		    	TableColumn tableColumn = rosterTable.getColumnModel().getColumn(column);
		        int preferredWidth = tableColumn.getMinWidth();
		        int maxWidth = tableColumn.getMaxWidth();
		     
		        for (int row = 0; row < rosterTable.getRowCount(); row++) {
		            TableCellRenderer cellRenderer = rosterTable.getCellRenderer(row, column);
		            Component c = rosterTable.prepareRenderer(cellRenderer, row, column);
		            int width = c.getPreferredSize().width + rosterTable.getIntercellSpacing().width;
		            preferredWidth = Math.max(preferredWidth, width);
		     
		            if (preferredWidth >= maxWidth){
		                preferredWidth = maxWidth;
		                break;
		            }
		        }		     
		        tableColumn.setPreferredWidth( preferredWidth );
		     }
		   			
			//Add table and row headers to JScrollPane
			JScrollPane scrollPane = new JScrollPane(rosterTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			scrollPane.setRowHeaderView(rowHeader);
			
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
