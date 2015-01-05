package jsettlers.buildingcreator.editor.jobeditor;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

public class PersonJobEditor extends JPanel {
	/**
     * 
     */
	private static final long serialVersionUID = 9078199835094146817L;

	public PersonJobEditor(String name, BuildingPersonJobProperties bpa) {
		final PersonJobEditorModel model = new PersonJobEditorModel(bpa);
		JTable table = new JTable(model) {
			/**
             * 
             */
			private static final long serialVersionUID = -5466542307017507020L;

			@Override
			public TableCellEditor getCellEditor(int row, int column) {
				return model.getCellEditor(row, column);
			}
		};
		this.setBorder(BorderFactory.createTitledBorder(name));
		this.add(table);
	}
}
