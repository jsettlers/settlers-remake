package jsettlers.buildingcreator.editor.jobeditor;

import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;

public class PersonJobEditorModel extends AbstractTableModel {

	/**
     * 
     */
    private static final long serialVersionUID = -1752201140282382752L;
	
	private final BuildingPersonJobProperties bpa;
	
	private List<String> keys;

	public PersonJobEditorModel(BuildingPersonJobProperties bpa) {
		this.bpa = bpa;
		reloadKeys();
    }

	private void reloadKeys() {
	    keys = bpa.getAllowedKeys();
    }

	@Override
	public String getColumnName(int column) {
	    if (column == 0) {
	    	return "Schlüssel";
	    } else {
	    	return "Wert";
	    }
	}
	
	@Override
    public int getColumnCount() {
	    return 2;
    }

	@Override
    public int getRowCount() {
	    return keys.size();
    }

	@Override
    public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			return keys.get(rowIndex);
		} else {
			return bpa.getProperty(keys.get(rowIndex));
		}
    }
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
	    return columnIndex == 1;
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (aValue instanceof String && columnIndex == 1) {
			bpa.setProperty(keys.get(rowIndex), (String) aValue);
		}
		reloadKeys();
		fireTableDataChanged();
	}

	public TableCellEditor getCellEditor(int row, int column) {
	    if (column == 1) {
	    	List<String> allowed = bpa.getAllowedValues(keys.get(row));
	    	JComboBox box = new JComboBox(allowed.toArray(new String[allowed.size()]));
	    	return new DefaultCellEditor(box);
	    }
	    return null;
    }
	
	
}
