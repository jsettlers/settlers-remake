package jsettlers.main.android.mainmenu.presenters.setup;

import jsettlers.graphics.localization.Labels;
import jsettlers.logic.map.loading.EMapStartResources;

/**
 * Created by tompr on 24/02/2017.
 */

public class StartResources {
	private final EMapStartResources type;

	public StartResources(EMapStartResources type) {
		this.type = type;
	}

	public EMapStartResources getType() {
		return type;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof StartResources && ((StartResources) obj).getType() == type;
	}

	@Override
	public String toString() {
		return Labels.getString("map-start-resources-" + type.name());
	}
}
