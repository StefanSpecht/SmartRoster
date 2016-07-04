package dom.company.thesis.model;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import dom.company.thesis.service.InputService;

public class RosterTableModel extends AbstractTableModel {
  	private static final long serialVersionUID = 2957859970865531519L;
  	private Roster roster;

    public void setRoster(Roster roster) {
        this.roster = roster;
        fireTableRowsUpdated(0, getRowCount() - 1);
    }

    public Roster getRoster() {
        return roster;
    }
    
    @Override
    public int getRowCount() {
        return InputService.getNoOfEmployees();
    }

    @Override
    public int getColumnCount() {
        return InputService.getNoOfShifts();
    }
    
    @Override
	public String getColumnName(int column) {
		return InputService.getShiftMap().get(column).getId();
	}
    
    @Override
	public Object getValueAt(int row, int column) {
       //return roster == null ? null : roster.getValue(row, column);
    	
    	List<Task> tasks = InputService.getTaskMap().get(roster.getValue(row, column));
    	String tasksString = "";
    	
    	if (tasks != null) {
	    	for (Task task : tasks) {
	    		tasksString +=  ";" + task.getId();
	    	}
    	}
    	
    	return tasksString.equals("") ? "-" : tasksString.substring(1);
    }

    @Override
    public void setValueAt(Object object, int row, int column) {
        Integer value = (Integer) object;
        if (!(value == null || (value >= 0 && value < InputService.getNoOfTasks()))) {
            throw new IllegalArgumentException("Invalid task no: " + value);
        }
        roster.setValue(value, row, column);
        fireTableCellUpdated(row, column);
    }
}
