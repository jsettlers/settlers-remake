package jsettlers.mapcreator.main.window;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import jsettlers.logic.map.MapLoader;

/**
 * Panel to open one of the last used maps
 * 
 * @author Andreas Butti
 *
 */
public class LastUsedPanel extends OpenPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 */
	public LastUsedPanel(ActionListener doubleclickListener) {
		super(doubleclickListener);
	}

	/**
	 * Order the maps
	 */
	@Override
	protected void sortMaps() {
		LastUsedHandler lastUsed = new LastUsedHandler();

		List<MapLoader> mapsCopy = maps;
		maps = new ArrayList<>();

		for (String id : lastUsed.getLastUsed()) {
			for (MapLoader m : mapsCopy) {
				if (m.getMapId() != null && m.getMapId().equals(id)) {
					maps.add(m);
					break;
				}
			}
		}
	}

	/**
	 * @return true if there is at least one file
	 */
	public boolean hasFiles() {
		return !maps.isEmpty();
	}
}
