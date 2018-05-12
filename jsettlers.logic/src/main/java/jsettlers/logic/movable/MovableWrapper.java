package jsettlers.logic.movable;

import java.io.Serializable;

import jsettlers.algorithms.path.Path;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableAction;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.selectable.ESelectionType;
import jsettlers.logic.buildings.military.IBuildingOccupyableMovable;
import jsettlers.logic.buildings.military.occupying.IOccupyableBuilding;
import jsettlers.logic.movable.components.AnimationComponent;
import jsettlers.logic.movable.components.AttackableComponent;
import jsettlers.logic.movable.components.MaterialComponent;
import jsettlers.logic.movable.components.MovableComponent;
import jsettlers.logic.movable.components.PlayerCmdComponent;
import jsettlers.logic.movable.components.SelectableComponent;
import jsettlers.logic.movable.components.SpecialistComponent;
import jsettlers.logic.movable.components.SteeringComponent;
import jsettlers.logic.movable.interfaces.IAttackable;
import jsettlers.logic.movable.interfaces.ILogicMovable;
import jsettlers.logic.player.Player;

/**
 * @author homoroselaps
 */
public final class MovableWrapper implements ILogicMovable, Serializable {
	private static final long serialVersionUID = -2853861825853788354L;

	private final Entity entity;

	public MovableWrapper(Entity entity) {
		this.entity = entity;
	}

	//region Interface Implementations
	@Override
	public short getViewDistance() {
		return entity.getComponentOptional(MovableComponent.class).map(MovableComponent::getViewDistance).orElse((short) 0);
	}

	@Override
	public boolean needsPlayersGround() {
		return entity.getComponentOptional(MovableComponent.class).map(MovableComponent::needsPlayersGround).orElse(false);
	}

	@Override
	public EDirection getDirection() {
		return entity.getComponentOptional(MovableComponent.class).map(MovableComponent::getViewDirection).orElse(EDirection.NORTH_EAST);
	}

	@Override
	public EMovableAction getAction() {
		return entity.getComponentOptional(AnimationComponent.class).map(AnimationComponent::getAnimation).orElse(EMovableAction.NO_ACTION);
	}

	@Override
	public float getMoveProgress() {
		return entity.getComponentOptional(AnimationComponent.class).map(AnimationComponent::getAnimationProgress).orElse(0f);
	}

	@Override
	public EMaterialType getMaterial() {
		return entity.getComponentOptional(MaterialComponent.class).map(MaterialComponent::getMaterial).orElse(EMaterialType.NO_MATERIAL);
	}

	@Override
	public void receiveHit(float strength, ShortPoint2D attackerPos, byte attackingPlayer) {
		entity.getComponentOptional(AttackableComponent.class).ifPresent(component -> component.receiveHit(strength, attackerPos, attackingPlayer));
	}

	@Override
	public float getHealth() {
		return entity.getComponent(AttackableComponent.class).getHealth();
	}

	@Override
	public boolean isRightstep() {
		return entity.getComponent(AnimationComponent.class).isRightStep();
	}

	@Override
	public void stopOrStartWorking(boolean stop) {
		entity.getComponentOptional(SpecialistComponent.class).ifPresent(component -> component.setIsWorking(!stop));
	}

	@Override
	public EBuildingType getGarrisonedBuildingType() {
		return null; // TODO implement this for building workers to return the type of their building
	}

	@Override
	public ShortPoint2D getPos() {
		return entity.getComponent(MovableComponent.class).getPos();
	}

	@Override
	public boolean isSelected() {
		return entity.getComponentOptional(SelectableComponent.class).map(SelectableComponent::isSelected).orElse(false);
	}

	@Override
	public void setSelected(boolean selected) {
		entity.getComponentOptional(SelectableComponent.class).ifPresent(component -> component.setSelected(selected));
	}

	@Override
	public ESelectionType getSelectionType() {
		return entity.getComponentOptional(SelectableComponent.class).map(SelectableComponent::getSelectionType).orElse(ESelectionType.PEOPLE);
	}

	@Override
	public boolean isAttackable() {
		return entity.getComponentOptional(AttackableComponent.class).map(AttackableComponent::isAttackable).orElse(false);
	}

	@Override
	public void setSoundPlayed() {
		entity.getComponent(AnimationComponent.class).setSoundPlayed();
	}

	@Override
	public boolean isSoundPlayed() {
		return entity.getComponent(AnimationComponent.class).isSoundPlayed();
	}

	@Override
	public EMovableType getMovableType() {
		return entity.getComponent(MovableComponent.class).getMovableType();
	}

	@Override
	public boolean isTower() {
		return false;
	}

	@Override
	public void debug() {
		System.out.println("debug: " + entity);
		entity.toggleDebug();
	}

	@Override
	public void informAboutAttackable(IAttackable attackable) {
		entity.getComponent(AttackableComponent.class).informAboutAttackable((ILogicMovable) attackable);
	}

	@Override
	public boolean push(ILogicMovable pushingMovable) {
		entity.raiseNotification(new SteeringComponent.LeavePositionRequest(pushingMovable));
		return false;
	}

	@Override
	public Path getPath() {
		return null;
	}

	@Override
	public void goSinglePathStep() {
		assert false : "not implemented";
	}

	@Override
	public ShortPoint2D getPosition() {
		return entity.movableComponent().getPos();
	}

	@Override
	public ILogicMovable getPushedFrom() {
		assert false : "not implemented";
		return null;
	}

	@Override
	public boolean isProbablyPushable(ILogicMovable pushingMovable) {
		//assert false: "not implemented";
		return false;
	}

	@Override
	public void leavePosition() {
		// The same as push - request to leave the place
		//TODO: call to new implementation of push
	}

	@Override
	public boolean canOccupyBuilding() {
		//TODO: this method has no right to exist, refactor together with @setOccupyableBuilding
		return false;
		//return entity.getComponent(SelectableComponent.class).getSelectionType() == ESelectionType.SOLDIERS;
	}

	@Override
	public IBuildingOccupyableMovable setOccupyableBuilding(IOccupyableBuilding building) {
		//TODO: rename to occupyBuilding
		return null;
	}

	@Override
	public void checkPlayerOfPosition(Player playerOfPosition) {
		//TODO: rename to: player of current position changed
		//TODO: implement event
	}

	@Override
	public void convertTo(EMovableType newMovableType) {
		//TODO: support switching between Movable types
	}

	@Override
	public Player getPlayer() {
		//TODO: switch to playerID or player everywhere
		return entity.getComponent(MovableComponent.class).getPlayer();
	}

	@Override
	public void moveTo(ShortPoint2D targetPosition) {
		entity.getComponentOptional(PlayerCmdComponent.class).ifPresent(component -> component.moveTo(targetPosition));
	}

	@Override
	public int getID() {
		return entity.getID();
	}

	@Override
	public int timerEvent() {
		return entity.timerEvent();
	}

	@Override
	public void kill() {
		entity.kill();
	}

	//endregion
}
