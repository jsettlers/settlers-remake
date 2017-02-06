package jsettlers.logic.movable;

import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableAction;

/**
 * Created by jt-1 on 2/6/2017.
 */

public class AnimationComponent implements Component {
    public EMovableAction getAnimation() {
        //GraphicComponent
        return null;
    }

    public EDirection getDirection() {
        //GraphicCompontent
        return null;
    }

    public float getAnimationProgress() {
        //GraphicComponent
        return 0;
    }

    public boolean isRightstep() {
        //GraphicComponent
        return false;
    }

    public void setSoundPlayed() {
        //SoundComponent
    }

    public boolean isSoundPlayed() {
        //SoundComponent
        return false;
    }
}
