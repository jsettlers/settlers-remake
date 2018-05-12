package jsettlers.logic.movable.components;

import jsettlers.algorithms.path.Path;
import jsettlers.common.map.shapes.HexGridArea;
import jsettlers.common.material.ESearchType;
import jsettlers.common.movable.EMovableAction;
import jsettlers.common.position.MutablePoint2D;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.mutables.MutableDouble;
import jsettlers.logic.movable.Context;
import jsettlers.logic.movable.Requires;
import jsettlers.logic.movable.simplebehaviortree.Node;
import jsettlers.logic.movable.simplebehaviortree.NodeStatus;
import jsettlers.logic.movable.simplebehaviortree.nodes.Action;
import jsettlers.logic.movable.simplebehaviortree.nodes.DynamicGuardSelector;

import static jsettlers.logic.movable.BehaviorTreeHelper.action;
import static jsettlers.logic.movable.BehaviorTreeHelper.debug;
import static jsettlers.logic.movable.BehaviorTreeHelper.guard;
import static jsettlers.logic.movable.BehaviorTreeHelper.memSequence;
import static jsettlers.logic.movable.BehaviorTreeHelper.selector;
import static jsettlers.logic.movable.BehaviorTreeHelper.sequence;
import static jsettlers.logic.movable.BehaviorTreeHelper.startAnimation;
import static jsettlers.logic.movable.BehaviorTreeHelper.succeeder;
import static jsettlers.logic.movable.BehaviorTreeHelper.triggerGuard;
import static jsettlers.logic.movable.BehaviorTreeHelper.waitForNotification;
import static jsettlers.logic.movable.BehaviorTreeHelper.waitForTargetReached;
import static jsettlers.logic.movable.BehaviorTreeHelper.waitForTargetReachedAndFailIfNotReachable;

/**
 * @author homoroselaps
 */

@Requires({
	SpecialistComponent.class,
	SteeringComponent.class,
	AttackableComponent.class,
	GameFieldComponent.class,
	AnimationComponent.class,
	MovableComponent.class,
	PlayerCmdComponent.class
})
public final class GeologistBehaviorComponent extends BehaviorComponent {
	private static final long serialVersionUID = -4157235942699928852L;

	private static final short ACTION1_DURATION = 1400;
	private static final short ACTION2_DURATION = 1500;

	private boolean goingToPlayerCommandLocation = false;

	@Override
	protected Node<Context> createBehaviorTree() {
		return new DynamicGuardSelector<>(
			triggerGuard(PlayerCmdComponent.MoveToCommand.class,
				debug("MoveToCommand", action(c -> {
					c.component.forFirstNotificationOfType(PlayerCmdComponent.MoveToCommand.class, command -> c.entity.steeringComponent().setTarget(command.pos));
					goingToPlayerCommandLocation = true;
				}))
			),
			triggerGuard(PlayerCmdComponent.StartWorkCommand.class,
				debug("StartWorkCommand",
					setIsWorkingAction(true)
				)
			),
			guard(c -> goingToPlayerCommandLocation, true,
				sequence(
					waitForTargetReached(setIsWorkingAction(true), setIsWorkingAction(false)),
					action(c -> {
						goingToPlayerCommandLocation = false;
					})
				)
			),
			guard(c -> c.entity.specialistComponent().isWorking(), true,
				debug("isWorking",
					selector(
						debug("find a place and work there",
							memSequence(
								debug("FindGoToWorkablePosition", new FindGoToWorkablePosition()),
								debug("waitForTargetReachedAndFailIfNotReachable", waitForTargetReachedAndFailIfNotReachable()),
								debug("markOnCurrentPositionIfWorkingIsPossible", markOnCurrentPositionIfWorkingIsPossible()),
								debug("startAnimation", startAnimation(EMovableAction.ACTION1, ACTION1_DURATION)),
								debug("waitForNotification", waitForNotification(AnimationComponent.AnimationFinishedNotification.class, true)),
								debug("startAnimation", startAnimation(EMovableAction.ACTION2, ACTION2_DURATION)),
								debug("waitForNotification", waitForNotification(AnimationComponent.AnimationFinishedNotification.class, true)),
								debug("placeSign", placeSign())
							)
						),
						debug("on failure: stop working", setIsWorkingAction(false))
					)
				)
			),
			guard(c -> true, debug("no action", succeeder()))
		);
	}

	private Action<Context> setIsWorkingAction(boolean isWorking) {
		return action(c -> {
			c.entity.specialistComponent().setIsWorking(isWorking);
		});
	}

	private Node<Context> placeSign() {
		return new Action<>(c -> {
			ShortPoint2D position = c.entity.movableComponent().getPos();

			c.entity.gameFieldComponent().movableGrid.setMarked(position, false);
			c.entity.gameFieldComponent().movableGrid.executeSearchType(c.entity.movableComponent(), position, ESearchType.RESOURCE_SIGNABLE);
		});
	}

	private static Action<Context> markOnCurrentPositionIfWorkingIsPossible() {
		return new Action<>(c -> {
			ShortPoint2D position = c.entity.movableComponent().getPos();

			if (c.entity.specialistComponent().getCenterOfWork() == null) {
				c.entity.specialistComponent().setCenterOfWork(position);
			}

			c.entity.gameFieldComponent().movableGrid.setMarked(position, false); // unmark the pos for the following check
			boolean canWorkOnPos = c.entity.gameFieldComponent().movableGrid.fitsSearchType(c.entity.movableComponent(), position.x, position.y, ESearchType.RESOURCE_SIGNABLE);

			if (canWorkOnPos) {
				c.entity.gameFieldComponent().movableGrid.setMarked(position, true);
				return NodeStatus.SUCCESS;
			}
			return NodeStatus.FAILURE;
		}
		);
	}

	private static class FindGoToWorkablePosition extends Action<Context> {
		private static final long serialVersionUID = -5393050237159114345L;

		FindGoToWorkablePosition() {
			super(FindGoToWorkablePosition::run);
		}

		public static NodeStatus run(Context c) {
			MovableComponent movableComponent = c.entity.movableComponent();
			GameFieldComponent gameFieldComponent = c.entity.gameFieldComponent();
			SpecialistComponent specialistComponent = c.entity.specialistComponent();
			SteeringComponent steeringComponent = c.entity.steeringComponent();

			if (specialistComponent.getCenterOfWork() == null) {
				specialistComponent.setCenterOfWork(movableComponent.getPos());
			}

			ShortPoint2D closeWorkablePos = getCloseWorkablePos(c);

			if (closeWorkablePos != null && steeringComponent.setTarget(closeWorkablePos)) {
				gameFieldComponent.movableGrid.setMarked(closeWorkablePos, true);
				return NodeStatus.SUCCESS;
			}
			specialistComponent.setCenterOfWork(null);

			ShortPoint2D pos = movableComponent.getPos();
			Path path = steeringComponent.preSearchPath(true, pos.x, pos.y, (short) 30, ESearchType.RESOURCE_SIGNABLE);
			if (path != null) {
				steeringComponent.setPath(path);
				return NodeStatus.SUCCESS;
			}

			return NodeStatus.FAILURE;
		}

		private static ShortPoint2D getCloseWorkablePos(Context c) {
			MovableComponent movableComponent = c.entity.movableComponent();
			GameFieldComponent gameFieldComponent = c.entity.gameFieldComponent();
			SpecialistComponent specialistComponent = c.entity.specialistComponent();

			MutablePoint2D bestNeighbourPos = new MutablePoint2D(-1, -1);
			MutableDouble bestNeighbourDistance = new MutableDouble(Double.MAX_VALUE); // distance from start point

			HexGridArea.streamBorder(movableComponent.getPos(), 2).filter((x, y) ->
				gameFieldComponent.movableGrid.isValidPosition(movableComponent, x, y)
					&& gameFieldComponent.movableGrid.fitsSearchType(movableComponent, x, y, ESearchType.RESOURCE_SIGNABLE)
			).forEach((x, y) -> {
				double distance = ShortPoint2D.getOnGridDist(x - specialistComponent.getCenterOfWork().x, y - specialistComponent.getCenterOfWork().y);
				if (distance < bestNeighbourDistance.value) {
					bestNeighbourDistance.value = distance;
					bestNeighbourPos.x = x;
					bestNeighbourPos.y = y;
				}
			});

			if (bestNeighbourDistance.value != Double.MAX_VALUE) {
				return bestNeighbourPos.createShortPoint2D();
			} else {
				return null;
			}
		}
	}
}
