package jsettlers.logic.movable;

import jsettlers.algorithms.path.IPathCalculatable;
import jsettlers.algorithms.path.Path;
import jsettlers.common.material.ESearchType;
import jsettlers.logic.player.Player;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.constants.Constants;

/**
 * Created by jt-1 on 2/6/2017.
 */

@Requires({GameFieldComponent.class})
public class MovableComponent extends Component implements IPathCalculatable{
    private final EMovableType movableType;
    private Player player;
    private ShortPoint2D position;
    private EDirection viewDirection;

    private GameFieldComponent gameC;

    public MovableComponent(EMovableType movableType, Player player, ShortPoint2D position, EDirection viewDirection) {
        this.movableType = movableType;
        this.player = player;
        this.position = position;
        this.viewDirection = viewDirection;
    }

    @Override
    public void OnAwake() {
        gameC = entity.get(GameFieldComponent.class);
    }

    @Override
    public void OnStart() {
        gameC.getMovableMap().put(entity.getID(), new MovableWrapper(entity));
        gameC.getAllMovables().offer(new MovableWrapper(entity));
        gameC.getMovableGrid().enterPosition(position, new MovableWrapper(entity), true);
    }

    public void setViewDirection(EDirection viewDirection) {
        this.viewDirection = viewDirection;
    }

    public EDirection getViewDirection() {
        return viewDirection;
    }

    public short getViewDistance() {
        return Constants.MOVABLE_VIEW_DISTANCE;
    }

    @Override
    public boolean needsPlayersGround() {
        return movableType.needsPlayersGround();
    }

    @Override
    public ShortPoint2D getPos() {
        return position;
    }

    public void setPos(ShortPoint2D position) {
        this.position = position;
    }

    @Override
    public byte getPlayerId() {
        return player.playerId;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {return this.player;}

    public EMovableType getMovableType() {
        return movableType;
    }
}
