package jsettlers.logic.objects;

import jsettlers.common.position.ISPosition2D;
import random.RandomSingleton;
import synchronic.timer.NetworkTimer;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public abstract class ProgressingObject extends AbstractObjectsManagerObject {

	private int startTime;
	private int duration;

	protected ProgressingObject(ISPosition2D pos) {
		super(pos);
	}

	@Override
	public final float getStateProgress() {
		float progress = (NetworkTimer.getGameTime() - startTime) / ((float) duration);
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
	 *            NOTE: duration MAY NEVER BE 0.0f
	 */
	protected final void setDuration(float duration) {
		this.duration = (int) (duration * 1000);
		this.startTime = NetworkTimer.getGameTime();
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
		this.startTime = NetworkTimer.getGameTime() - RandomSingleton.getInt(0, (int) (duration * 100));
	}

	/**
	 * @return the time when this object's getStateProgress() method will return 1.0 (in seconds)
	 */
	public float getEndTime() {
		return ((float) duration) / 1000;
	}
}
