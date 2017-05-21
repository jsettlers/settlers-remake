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
package jsettlers.mapcreator.stat;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import jsettlers.mapcreator.data.MapData;
import jsettlers.mapcreator.localization.EditorLabels;

/**
 * Dialog to display statistics
 * 
 * @author Andreas Butti
 *
 */
public class StatisticsDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            Parent frame to center on
	 * 
	 * @param data
	 *            Map data to display
	 */
	public StatisticsDialog(JFrame parent, MapData data) {
		super(parent);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle(EditorLabels.getLabel("statistics.header"));

		StatisticsTableModel model = new StatisticsTableModel(data);
		JTabbedPane tabs = new JTabbedPane();
		JTable table = new JTable(model);
		TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
		table.setRowSorter(sorter);

		tabs.add(EditorLabels.getLabel("statistics.overview"), new JScrollPane(table));

		for (int i = 0; i < data.getPlayerCount(); i++) {
			tabs.add("player " + i, new PlayerDiagram(data, i));
		}

		setLayout(new BorderLayout());
		add(tabs, BorderLayout.CENTER);

		pack();
		setModal(true);
		setLocationRelativeTo(parent);
	}
}
