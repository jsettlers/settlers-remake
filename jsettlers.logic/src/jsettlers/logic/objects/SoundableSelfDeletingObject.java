package jsettlers.logic.objects;

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.sound.ISoundable;

public class SoundableSelfDeletingObject extends SelfDeletingMapObject implements ISoundable {
	private static final long serialVersionUID = -3103648926788895100L;
	private boolean soundPlayed;

	public SoundableSelfDeletingObject(ShortPoint2D pos, EMapObjectType type, float duration) {
		super(pos, type, duration);
	}

	public SoundableSelfDeletingObject(ShortPoint2D pos, EMapObjectType type, float duration, byte player) {
		super(pos, type, duration, player);
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
