package util;

import java.util.ArrayList;

import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

public class ServerTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	
	public final String[] columnNames = {
		"Server Name",
		"Status",
		"Latency"
	};
	
	private ArrayList<Object[]> rows = new ArrayList<Object[]>();
	private JList list = new JList();

	public void addServer(ServerChannel server, HealthReport report) {
		// Last row is object itself.
		Object[] row = new Object[columnNames.length + 1]; 
		row[0] = server.getHost();
		
		// TODO evaluate report.
		if (report == null) {
			
		}
		row[1] = "Swell"; //TODO
		row[2] = Integer.toString(report.getResponseTime()) + "ms";
		
		row[3] = server;
		
		rows.add(row);
		list.setListData(rows.toArray());
		
		fireTableDataChanged();
	}
	
	/**
	 * Set an event handler for the list model.
	 * @param e
	 */
	public void setListListener(ListSelectionListener e) {
		list.getSelectionModel().addListSelectionListener(e);
	}
	
	public ListSelectionModel getListModel() {
		return list.getSelectionModel();
	}
	
	public ServerChannel getRowObject(int row) {
		return (ServerChannel)rows.get(row)[getColumnCount()];
	}
	
	/**
	 * Remove all rows from the table.
	 */
	public void clear() {
		rows.clear();
		list.setListData(rows.toArray());
	}
	
	@Override public java.lang.Class<?> getColumnClass(int column) {
		return getValueAt(0, column).getClass();
	}
	
	@Override public int getColumnCount() {
		return columnNames.length;
	}

	@Override public String getColumnName(int column) {
		return columnNames[column];
	}
	
	@Override public int getRowCount() {
		return rows.size();
	}

	@Override public Object getValueAt(int rowIndex, int columnIndex) {
		return rows.get(rowIndex)[columnIndex];
	}
}
