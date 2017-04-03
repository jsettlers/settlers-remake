package jsettlers.logic.movable;

import jsettlers.common.position.ShortPoint2D;

/**
 * Created by jt-1 on 4/1/2017.
 */

public class PlayerCmdComponent extends Component {
    public static class LeftClickCommand extends Notification {
        ShortPoint2D pos;
        public LeftClickCommand(ShortPoint2D pos) {
            this.pos = pos;
        }
    }
    public static class AltLeftClickCommand extends Notification {
        ShortPoint2D pos;
        public AltLeftClickCommand(ShortPoint2D pos) {
            this.pos = pos;
        }
    }
    public static class StartWorkCommand extends Notification {}

    public void send_LeftClick(ShortPoint2D pos) {
        entity.raiseNotification(new LeftClickCommand(pos));
    }

    public void send_AltLeftClick(ShortPoint2D pos) {
        entity.raiseNotification(new AltLeftClickCommand(pos));
    }

    public void sendStartWorkCommand() {
        entity.raiseNotification(new StartWorkCommand());
    }
}
