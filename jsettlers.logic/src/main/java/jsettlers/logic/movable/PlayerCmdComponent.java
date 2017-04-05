package jsettlers.logic.movable;

import jsettlers.common.position.ShortPoint2D;

/**
 * @author homoroselaps
 */

public class PlayerCmdComponent extends Component {
    private static final long serialVersionUID = -3188445864619388414L;

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
