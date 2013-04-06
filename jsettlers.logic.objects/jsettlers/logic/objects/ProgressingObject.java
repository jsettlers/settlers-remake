package jsettlers.logic.objects;

import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.newGrid.objects.AbstractObjectsManagerObject;
import random.RandomSingleton;
import synchronic.timer.NetworkTimer;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public abstract class ProgressingObject extends AbstractObjectsManagerObject {
	private static final long serialVersionUID = 62117014829414034L;

	private int startTime;
	private int duration;

	protected ProgressingObject(ShortPoint2D pos) {
		super(pos);
	}

	@Override
	public float getStateProgress() {
		float progress = (NetworkTimer.get().getGameTime() - startTime) / ((float) duration);
		if (progress < 1) {
			return progress;
		} else {
			return 1;
		}
	}

	/**
	 * 
	 * @param duration
	 *            in seconds<br>
	 *            NOTE: duration MAY NEVER BE LESS OR EQUAL TO 0.0f
	 */
	protected final void setDuration(float duration) {
		// assert duration > 0 : "duration may never be less or equal to 0.0f"; TODO @Andreas Eberle enable this assertion again!

		this.duration = (int) (duration * 1000);
		this.startTime = NetworkTimer.get().getGameTime();
	}

	/**
	 * 
	 * @param duration
	 *            in seconds<br>
	 *            NOTE: the duration can vary up to +- 5% <br>
	 *            NOTE: duration MAY NEVER BE 0.0f
	 */
	protected final void setDurationWithVariation(float duration) {
		this.duration = (int) (duration * 1000);
		this.startTime = NetworkTimer.get().getGameTime() - RandomSingleton.getInt(0, (int) (duration * 100));
	}

	/**
	 * @return the time when this object's getStateProgress() method will return 1.0 (in seconds)
	 */
	public float getEndTime() {
		return ((float) duration) / 1000;
	}
}
