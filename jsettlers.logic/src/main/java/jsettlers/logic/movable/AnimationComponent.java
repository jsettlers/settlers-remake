package jsettlers.logic.movable;

import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableAction;
import jsettlers.common.movable.EMovableType;
import jsettlers.logic.constants.MatchConstants;

/**
 * Created by jt-1 on 2/6/2017.
 */

public class AnimationComponent extends Component {
    private EMovableAction anmiation = EMovableAction.NO_ACTION;
    private int animationStartTime;
    private short animationDuration;
    private boolean isSoundPlayed = false;
    private boolean isRightStep = false;
    private EDirection viewDirection;

    public AnimationComponent(EDirection viewDirection) {
        this.viewDirection = viewDirection;
    }

    public EMovableAction getAnimation() {
        return anmiation;
    }

    public EDirection getViewDirection() {
        return viewDirection;
    }

    public float getAnimationProgress() {
        return ((float) (MatchConstants.clock().getTime() - animationStartTime)) / animationDuration;
    }

    public void startAnimation(EMovableAction animation, float duration) {
        this.animationStartTime = MatchConstants.clock().getTime();
        this.animationDuration = (short)(duration*1000);
        this.anmiation = animation;
    }

    public boolean isRightstep() {
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
}
