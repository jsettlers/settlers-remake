package jsettlers.logic.movable;

import jsettlers.common.position.ShortPoint2D;

/**
 * Created by jt-1 on 4/1/2017.
 */

public class PlayerCommandComponent extends Component {
    public static class MoveToCommand extends Notification {
        ShortPoint2D pos;
        public MoveToCommand(ShortPoint2D pos) {
            this.pos = pos;
        }
    }
    public static class MoveToAndWorkCommand extends Notification {
        ShortPoint2D pos;
        public MoveToAndWorkCommand(ShortPoint2D pos) {
            this.pos = pos;
        }
    }
    public static class StartWorkCommand extends Notification {}

    public void sendMoveToCommand(ShortPoint2D pos) {
        entity.raiseNotification(new MoveToCommand(pos));
    }

    public void sendMoveToAndWorkCommand(ShortPoint2D pos) {
        entity.raiseNotification(new MoveToAndWorkCommand(pos));
    }

    public void sendStartWorkCommand() {
        entity.raiseNotification(new StartWorkCommand());
    }
}
