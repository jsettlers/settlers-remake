/*******************************************************************************
 * Copyright (c) 2015
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
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
			JComboBox<String> box = new JComboBox<>(allowed.toArray(new String[allowed.size()]));
			return new DefaultCellEditor(box);
		}
		return null;
	}

}
