package jsettlers.main.components.openpanel;

import jsettlers.graphics.localization.Labels;
import jsettlers.logic.map.MapLoader;
import jsettlers.logic.map.original.OriginalMapLoader;
import jsettlers.logic.map.save.loader.FreshMapLoader;

/**
 * Map filter for OpenPanel
 * 
 * @author Andreas Butti
 */
public enum EMapFilter {

	/**
	 * No filtering
	 */
	ALL {
		@Override
		public boolean filter(MapLoader loader) {
			return true;
		}
	},

	/**
	 * JSettler Maps
	 */
	JSETTLER {
		@Override
		public boolean filter(MapLoader loader) {
			return loader instanceof FreshMapLoader;
		}
	},

	/**
	 * Mapps imported from Original settler
	 */
	SETTLER_IMPORTED {
		@Override
		public boolean filter(MapLoader loader) {
			return loader instanceof OriginalMapLoader;
		}
	};

	/**
	 * Translated name
	 */
	private final String name;

	/**
	 * Constructor
	 */
	private EMapFilter() {
		this.name = Labels.getString("mapfilter." + name());
	}

	/**
	 * Filter a single map entry
	 * 
	 * @param loader
	 *            Map to filter
	 * @return true if it should be displayed
	 */
	public abstract boolean filter(MapLoader loader);

	/**
	 * @return Translated name
	 */
	public String getName() {
		return name;
	}
}
