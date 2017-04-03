package jsettlers.logic.movable;

import jsettlers.common.position.ShortPoint2D;

/**
 * Created by jt-1 on 2/6/2017.
 */

public class SpecialistComponent extends Component {
    private boolean isWorking = false;
    private ShortPoint2D centerOfWork;

    public boolean isWorking() {
        return isWorking;
    }
    public void setIsWorking(boolean isWorking) {
        this.isWorking = isWorking;
    }

    public ShortPoint2D getCenterOfWork() { return centerOfWork; }
    public void setCenterOfWork(ShortPoint2D centerOfWork) { this.centerOfWork = centerOfWork; }
}
