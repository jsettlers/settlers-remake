package jsettlers.logic.movable;

import jsettlers.common.position.ShortPoint2D;

/**
 * @author homoroselaps
 */

public class SpecialistComponent extends Component {
    private static final long serialVersionUID = -5944104465410121876L;
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
