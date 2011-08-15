package jsettlers.graphics;

import go.area.Area;
import go.area.AreaWindow;
import go.region.Region;
import jsettlers.common.map.IHexMap;
import jsettlers.graphics.map.MapContent;
import jsettlers.graphics.map.MapInterfaceConnector;

public class Window {

	private Region region;
	private AreaWindow window;

	public Window() {
		Area area = new Area();
		this.region = new Region(Region.POSITION_CENTER);
		area.add(this.region);
		this.window = new AreaWindow(area, false);
		this.window.setTitle("jsettlers");
	}

	/**
	 * Sets the content of the panel to be amap.
	 * <p>
	 * This method also sets up the draw context of the map and returns a
	 * {@link MapInterfaceConnector} that can be accessed to change the view.
	 * 
	 * @param map
	 *            The map to display.
	 * @return The connector to access the view and add event listenrs
	 * @see MapInterfaceConnector
	 */
	public MapInterfaceConnector setHexMap(final IHexMap map) {
		MapContent content = new MapContent(map);
		this.region.setContent(content);
		return content.getInterfaceConnector();
	}

}
