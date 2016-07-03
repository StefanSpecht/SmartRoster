package dom.company.thesis.model;

import javax.swing.table.AbstractTableModel;

import dom.company.thesis.service.InputService;

/**
 * {@link javax.swing.table.TableModel} for displaying a Roster
 * grid in a {@link javax.swing.JTable}.
 * @author Daniel Dyer
 */
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
    
    public int getRowCount() {
        return InputService.getNoOfEmployees();
    }


    public int getColumnCount() {
        return InputService.getNoOfShifts();
    }


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
