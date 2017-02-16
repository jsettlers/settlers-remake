package jsettlers.logic.movable;

/**
 * Created by jt-1 on 2/6/2017.
 */

public class WorkComponent extends Component {
    private boolean isWorking = false;
    public void setIsWorking(boolean isWorking) {
        this.isWorking = isWorking;
    }

    public boolean isWorking() {
        return isWorking;
    }
}
