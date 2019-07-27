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
	private boolean        isChained     = false;

	public static class AnimationFinishedNotification<T extends EMovableAction> extends Notification {
		public final EMovableAction type;

		public AnimationFinishedNotification(EMovableAction animationType) {
			this.type = animationType;
		}
	}

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

	public void startAnimation(EMovableAction animation, short duration, boolean isChained) {
		this.animationStartTime = MatchConstants.clock().getTime();
		this.animationDuration = duration;
		this.animation = animation;
		this.isSoundPlayed = false;
		this.isChained = isChained;
	}

	private void stopAnimation() {
		this.entity.raiseNotification(new AnimationFinishedNotification(this.animation));
		if (!isChained) this.animation = EMovableAction.NO_ACTION;
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
