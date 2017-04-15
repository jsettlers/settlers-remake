package jsettlers.logic.movable.components;

import java.io.IOException;
import java.io.ObjectInputStream;

import jsettlers.algorithms.path.IPathCalculatable;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.logic.movable.MovableDataManager;
import jsettlers.logic.movable.MovableWrapper;
import jsettlers.logic.movable.Requires;
import jsettlers.logic.player.Player;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.constants.Constants;

/**
 * @author homoroselaps
 */

@Requires({GameFieldComponent.class})
public class MovableComponent extends Component implements IPathCalculatable{
    private static final long serialVersionUID = -7615132582559956988L;
    private final EMovableType movableType;
    private Player player;
    private ShortPoint2D position;
    private EDirection viewDirection;
    //TODO: make @aMovableWrapper not necessary
    private MovableWrapper aMovableWrapper;

    private GameFieldComponent gameC;

    public MovableComponent(EMovableType movableType, Player player, ShortPoint2D position, EDirection viewDirection) {
        this.movableType = movableType;
        this.player = player;
        this.position = position;
        this.viewDirection = viewDirection;
    }

    @Override
    public void onAwake() {
        gameC = entity.get(GameFieldComponent.class);
        aMovableWrapper = new MovableWrapper(entity);
    }

    @Override
    public void onEnable() {
        gameC.getMovableMap().put(entity.getID(), aMovableWrapper);
        gameC.getAllMovables().offer(aMovableWrapper);
        gameC.getMovableGrid().enterPosition(position, aMovableWrapper, true);
    }

    private final void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        gameC.getMovableMap().put(entity.getID(), aMovableWrapper);
        gameC.getAllMovables().offer(aMovableWrapper);
    }

    @Override
    public void onDisable() {
        // TODO: refactor leavePosition to not use the instance
        gameC.getMovableGrid().leavePosition(position, gameC.getMovableGrid().getMovableAt(position.x, position.y));

        gameC.getAllMovables().remove(gameC.getMovableMap().get(entity.getID()));
        gameC.getMovableMap().remove(entity.getID());
    }

    @Override
    public void onDestroy() {
        gameC.getMovableGrid().addSelfDeletingMapObject(position, EMapObjectType.GHOST, Constants.GHOST_PLAY_DURATION, player);
    }

    public MovableWrapper getaMovableWrapper() {
        return aMovableWrapper;
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
        gameC.getMovableGrid().leavePosition(this.position, aMovableWrapper);
        this.position = position;
        gameC.getMovableGrid().enterPosition(this.position, aMovableWrapper, false);
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
