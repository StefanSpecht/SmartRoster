package dom.company.thesis.gui;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.table.JTableHeader;

public class ShiftListRenderer extends JLabel implements ListCellRenderer<String>{

	private static final long serialVersionUID = -8069134359374125954L;

	ShiftListRenderer(JTable table) {
		JTableHeader tableHeader = table.getTableHeader();
	    setBorder(UIManager.getBorder("TableHeader.cellBorder"));
	    setHorizontalAlignment(CENTER);
	    setForeground(tableHeader.getForeground());
	    setOpaque(true);
	    setFont(tableHeader.getFont());
	}
	
	public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean fSelected, boolean fCellHasFocus) {
		setText((value == null) ? "" : value.toString());
		return this;
}
	
}