package dom.company.thesis.model;

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
       return roster == null ? null : roster.getValue(row, column);
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
