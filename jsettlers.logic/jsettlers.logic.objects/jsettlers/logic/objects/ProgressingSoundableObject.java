package jsettlers.logic.objects;

import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.sound.ISoundable;

public abstract class ProgressingSoundableObject extends ProgressingObject implements ISoundable {
	private static final long serialVersionUID = -7740838546551477874L;

	private boolean soundPlayed;

	protected ProgressingSoundableObject(ShortPoint2D pos) {
		super(pos);
	}

	@Override
	public void setSoundPlayed() {
		this.soundPlayed = true;
	}

	@Override
	public boolean isSoundPlayed() {
		return this.soundPlayed;
	}
}
