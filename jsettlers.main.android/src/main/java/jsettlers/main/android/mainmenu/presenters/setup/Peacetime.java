package jsettlers.main.android.mainmenu.presenters.setup;

/**
 * Created by tompr on 24/02/2017.
 */

public class Peacetime {
	private final String type;

	public Peacetime(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Peacetime && ((Peacetime) obj).getType() == type;
	}

	@Override
	public String toString() {
		return type;
	}
}
