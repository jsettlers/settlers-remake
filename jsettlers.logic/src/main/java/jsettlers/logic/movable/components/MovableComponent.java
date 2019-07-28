package jsettlers.logic.movable.components;

import java.io.IOException;
import java.io.ObjectInputStream;

import jsettlers.algorithms.path.IPathCalculatable;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.movable.EntityFactory;
import jsettlers.logic.movable.MovableWrapper;
import jsettlers.logic.movable.Requires;
import jsettlers.logic.player.Player;

/**
 * @author homoroselaps
 */
@Requires({GameFieldComponent.class})
public class MovableComponent extends Component implements IPathCalculatable {
	private static final long serialVersionUID = -7615132582559956988L;

	private       EMovableType   movableType;
	private       Player         player;
	private       ShortPoint2D   position;
	private       EDirection     viewDirection;
	private 	  boolean        visible = true;
	//TODO: make @movableWrapper not necessary
	private       MovableWrapper movableWrapper;

	private GameFieldComponent gameComponent;

	public MovableComponent(EMovableType movableType, Player player, ShortPoint2D position, EDirection viewDirection) {
		this.movableType = movableType;
		this.player = player;
		this.position = position;
		this.viewDirection = viewDirection;
	}

	@Override
	protected void onWakeUp() {
		movableWrapper = new MovableWrapper(entity);
	}

	@Override
	protected void onEnable() {
		gameComponent = entity.getComponent(GameFieldComponent.class);
		gameComponent.addNewMovable(movableWrapper);
		gameComponent.movableGrid.enterPosition(position, movableWrapper, true);
	}

	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		ois.defaultReadObject();
	}

	@Override
	protected void onDisable() {
		// TODO: refactor leavePosition to not use the instance
		gameComponent.movableGrid.leavePosition(position, gameComponent.movableGrid.getMovableAt(position.x, position.y));
		gameComponent.removeMovable(getMovableWrapper());
	}

	@Override
	protected void onDestroy() {
		gameComponent.movableGrid.addSelfDeletingMapObject(position, EMapObjectType.GHOST, Constants.GHOST_PLAY_DURATION, player);
	}

	public MovableWrapper getMovableWrapper() {
		return movableWrapper;
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
	public boolean isShip() {
		return this.getMovableType() == EMovableType.FERRY;
	}

	@Override
	public ShortPoint2D getPosition() {
		return position;
	}

	public void setPos(ShortPoint2D position) {
		gameComponent.movableGrid.leavePosition(this.position, movableWrapper);
		this.position = position;
		gameComponent.movableGrid.enterPosition(this.position, movableWrapper, false);
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Player getPlayer() {return this.player;}

	public EMovableType getMovableType() {
		return movableType;
	}

	final void setVisible(boolean visible) {
		if (this.visible == visible) return; // nothing to change

		if (this.visible) { // is visible and gets invisible
			gameComponent.movableGrid.leavePosition(position, movableWrapper);
		} else {
			gameComponent.movableGrid.enterPosition(position, movableWrapper, true);
		}

		this.visible = visible;
	}

	public void convertTo(EMovableType newMovableType) {
		if (newMovableType == EMovableType.BEARER && !player.equals(gameComponent.movableGrid.getPlayerAt(position))) {
			return; // can't convert to bearer if the ground does not belong to the player
		}
		if (!(movableType == EMovableType.BEARER || (movableType == EMovableType.PIONEER && newMovableType == EMovableType.BEARER) || movableType == newMovableType)) {
			System.err.println("Tried invalid conversion from " + movableType + " to " + newMovableType);
			return; // can't convert between this types
		}
		entity.getComponentOptional(AttackableComponent.class).ifPresent(c ->
			c.setHealth(c.getHealth() / movableType.getHealth() * newMovableType.getHealth())
		);
		movableType = newMovableType;
		setVisible(true); // ensure the movable is visible
		entity.convertTo(EntityFactory.createEntity(gameComponent.movableGrid, newMovableType, position, player));
	}
}
