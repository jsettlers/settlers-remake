package jsettlers.logic.movable.components;

import jsettlers.common.movable.EMovableAction;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.movable.Notification;

/**
 * @author homoroselaps
 */
public class AnimationComponent extends Component {
	private static final long serialVersionUID = 8064683552580286008L;

	private EMovableAction animation     = EMovableAction.NO_ACTION;
	private int            animationStartTime;
	private short          animationDuration;
	private boolean        isSoundPlayed = false;
	private boolean        isRightStep   = false;

	public static class AnimationFinishedNotification extends Notification {}

	public AnimationComponent() { }

	@Override
	protected void onUpdate() {
		if (animation != EMovableAction.NO_ACTION && isAnimationFinished()) {
			stopAnimation();
		}
	}

	@Override
	protected void onLateUpdate() {
		if (!isAnimationFinished()) { entity.setInvocationDelay(getRemainingTime()); }
	}

	public EMovableAction getAnimation() {
		return animation;
	}

	public float getAnimationProgress() {
		return ((float) (MatchConstants.clock().getTime() - animationStartTime)) / animationDuration;
	}

	public boolean isAnimationFinished() {
		return animationStartTime + animationDuration <= MatchConstants.clock().getTime();
	}

	public void startAnimation(EMovableAction animation, short duration) {
		this.animationStartTime = MatchConstants.clock().getTime();
		this.animationDuration = duration;
		this.animation = animation;
		isSoundPlayed = false;
	}

	private void stopAnimation() {
		this.animation = EMovableAction.NO_ACTION;
		this.entity.raiseNotification(new AnimationFinishedNotification());
	}

	public boolean isRightStep() {
		return isRightStep;
	}

	public void switchStep() {
		isRightStep = !isRightStep;
	}

	public void setSoundPlayed() {
		isSoundPlayed = true;
	}

	public boolean isSoundPlayed() {
		return isSoundPlayed;
	}

	public int getRemainingTime() {
		return animationStartTime + animationDuration - MatchConstants.clock().getTime();
	}
}
