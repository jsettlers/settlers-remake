package jsettlers.main.android.mainmenu.presenters.setup.playeritem;

/**
 * Created by tompr on 24/02/2017.
 */

public class StartPosition {
	private final byte positionByte;

	public StartPosition(byte positionByte) {
		this.positionByte = positionByte;
	}

	public byte asByte() {
		return positionByte;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof StartPosition && ((StartPosition) obj).asByte() == positionByte;
	}

	@Override
	public String toString() {
		return "Position " + (positionByte + 1);
	}
}
