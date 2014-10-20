package jsettlers.mapcreator.stat;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import jsettlers.mapcreator.data.MapData;

public class StatisticsWindow {
	public StatisticsWindow(MapData data) {
		JTabbedPane root = new JTabbedPane();
		JTable table = new JTable(new StatisticsTable(data));
		root.add("Table", new JScrollPane(table));
		
		for (int i = 0; i < data.getPlayerCount(); i++) {
			root.add("player " + i, new PlayerDiagram(data, i));
		}

		JFrame frame = new JFrame("statistics");
		frame.add(root);
		frame.pack();
		frame.setVisible(true);
	}
}
