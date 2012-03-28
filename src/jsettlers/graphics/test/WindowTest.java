package jsettlers.graphics.test;

import jsettlers.TestWindow;
import jsettlers.graphics.map.MapInterfaceConnector;
import jsettlers.input.SelectionSet;

public class WindowTest {

	private WindowTest() {
		TestMap map = new TestMap();

		MapInterfaceConnector connector = TestWindow.openTestWindow(map);

		connector.setSelection(new SelectionSet(map.getAllSettlers()));
	}

	public static void main(String[] args) {
		new WindowTest();
	}
}
