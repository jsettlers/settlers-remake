package jsettlers.main.android.mainmenu.presenters.setup.playeritem;

import jsettlers.common.player.ECivilisation;
import jsettlers.graphics.localization.Labels;

/**
 * Created by tompr on 24/02/2017.
 */

public class Civilisation {
	private final ECivilisation type;

	public Civilisation(ECivilisation type) {
		this.type = type;
	}

	public ECivilisation getType() {
		return type;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Civilisation && ((Civilisation) obj).getType() == type;
	}

	@Override
	public String toString() {
		return Labels.getString("civilisation-" + type.name());
	}
}
