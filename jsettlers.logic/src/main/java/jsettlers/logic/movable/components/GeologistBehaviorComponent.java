package jsettlers.logic.movable.components;

import jsettlers.algorithms.path.Path;
import jsettlers.common.map.shapes.HexGridArea;
import jsettlers.common.material.ESearchType;
import jsettlers.common.movable.EMovableAction;
import jsettlers.common.position.MutablePoint2D;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.mutables.MutableDouble;
import jsettlers.logic.movable.BehaviorTreeHelper;
import jsettlers.logic.movable.Context;
import jsettlers.logic.movable.Requires;
import jsettlers.logic.movable.simplebehaviortree.NodeStatus;
import jsettlers.logic.movable.simplebehaviortree.Root;
import jsettlers.logic.movable.simplebehaviortree.nodes.Action;

import static jsettlers.logic.movable.BehaviorTreeHelper.guard;
import static jsettlers.logic.movable.BehaviorTreeHelper.memSequence;
import static jsettlers.logic.movable.BehaviorTreeHelper.selector;
import static jsettlers.logic.movable.BehaviorTreeHelper.sequence;
import static jsettlers.logic.movable.BehaviorTreeHelper.startAnimation;
import static jsettlers.logic.movable.BehaviorTreeHelper.triggerGuard;
import static jsettlers.logic.movable.BehaviorTreeHelper.waitForNotification;
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
	MovableComponent.class
})
public final class GeologistBehaviorComponent extends BehaviorComponent {
	private static final long serialVersionUID = -4157235942699928852L;

	@Override
	protected Root<Context> createBehaviorTree() {
		final short ACTION1_DURATION = 1400;
		final short ACTION2_DURATION = 1500;

		return new Root<>(selector(
			triggerGuard(PlayerCmdComponent.LeftClickCommand.class,
				memSequence(
					BehaviorTreeHelper.action(c -> {
						c.entity.specC().setIsWorking(false);
					}),
					BehaviorTreeHelper.action(GeologistBehaviorComponent::setTargetWorkPos)
				)
			),
			triggerGuard(PlayerCmdComponent.AltLeftClickCommand.class,
				memSequence(
					BehaviorTreeHelper.action(c -> {
						c.entity.specC().setIsWorking(true);
					}),
					BehaviorTreeHelper.action(GeologistBehaviorComponent::setTargetWorkPos)
				)
			),
			triggerGuard(PlayerCmdComponent.StartWorkCommand.class,
				sequence(
					BehaviorTreeHelper.action(c -> {
						c.entity.specC().setIsWorking(true);
					}),
					BehaviorTreeHelper.action(c -> {
						c.entity.specC().resetTargetWorkPos();
					})
				)
			),
			guard(c -> c.entity.specC().getTargetWorkPos() != null, true,
				selector(
					memSequence(
						BehaviorTreeHelper.action(c -> {
							c.entity.steerC().setTarget(c.entity.specC().getTargetWorkPos());
						}),
						waitForTargetReachedAndFailIfNotReachable(),
						BehaviorTreeHelper.action(c -> {
							c.entity.specC().resetTargetWorkPos();
						})
					),
					sequence(
						BehaviorTreeHelper.action(c -> {
							c.entity.specC().resetTargetWorkPos();
						}),
						BehaviorTreeHelper.action(c -> {
							c.entity.specC().setIsWorking(false);
						})
					)
				)
			),
			guard(c -> c.entity.specC().isWorking(), true,
				selector(
					BehaviorTreeHelper.debug("find a place and work there", memSequence(
						Find_GoToWorkablePosition(),
						waitForTargetReachedAndFailIfNotReachable(),
						WorkOnPosIfPossible(),
						startAnimation(EMovableAction.ACTION1, ACTION1_DURATION),
						waitForNotification(AnimationComponent.AnimationFinishedTrigger.class, true),
						startAnimation(EMovableAction.ACTION2, ACTION2_DURATION),
						waitForNotification(AnimationComponent.AnimationFinishedTrigger.class, true)
					)),
					BehaviorTreeHelper.debug("on failure: stop working", BehaviorTreeHelper.action(c -> {
						c.entity.specC().setIsWorking(false);
					}))
				)
			)
		));
	}

	private static Action<Context> WorkOnPosIfPossible() {
		return new Action<>(c -> {
			ShortPoint2D pos = c.entity.movC().getPos();

			if (c.entity.specC().getCenterOfWork() == null) {
				c.entity.specC().setCenterOfWork(pos);
			}

			c.entity.gameC().getMovableGrid().setMarked(pos, false); // unmark the pos for the following check
			boolean canWorkOnPos = c.entity.gameC().getMovableGrid().fitsSearchType(c.entity.movC(), pos.x, pos.y, ESearchType.RESOURCE_SIGNABLE);

			if (canWorkOnPos) {
				c.entity.gameC().getMovableGrid().setMarked(pos, true);
				return NodeStatus.SUCCESS;
			}
			return NodeStatus.FAILURE;
		}
		);
	}

	private static Find_GoToWorkablePosition Find_GoToWorkablePosition() {
		return new Find_GoToWorkablePosition();
	}

	private static class Find_GoToWorkablePosition extends Action<Context> {

		private static final long serialVersionUID = -5393050237159114345L;

		public Find_GoToWorkablePosition() {
			super(Find_GoToWorkablePosition::run);
		}

		private static ShortPoint2D getCloseWorkablePos(Context c) {
			MovableComponent movC = c.entity.movC();
			GameFieldComponent gameC = c.entity.gameC();
			SpecialistComponent specC = c.entity.specC();

			MutablePoint2D bestNeighbourPos = new MutablePoint2D(-1, -1);
			MutableDouble bestNeighbourDistance = new MutableDouble(Double.MAX_VALUE); // distance from start point

			HexGridArea.streamBorder(movC.getPos(), 2).filter((x, y) -> {
					boolean isValidPosition = gameC.getMovableGrid().isValidPosition(movC, x, y);
					boolean canWorkOnPos = gameC.getMovableGrid().fitsSearchType(movC, x, y, ESearchType.RESOURCE_SIGNABLE);
					return isValidPosition && canWorkOnPos;
				}
			).forEach((x, y) -> {
				double distance = ShortPoint2D.getOnGridDist(x - specC.getCenterOfWork().x, y - specC.getCenterOfWork().y);
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

		public static NodeStatus run(Context c) {
			MovableComponent movC = c.entity.movC();
			GameFieldComponent gameC = c.entity.gameC();
			SpecialistComponent specC = c.entity.specC();
			SteeringComponent steerC = c.entity.steerC();

			ShortPoint2D closeWorkablePos = getCloseWorkablePos(c);

			if (closeWorkablePos != null && steerC.setTarget(closeWorkablePos)) {
				gameC.getMovableGrid().setMarked(closeWorkablePos, true);
				return NodeStatus.SUCCESS;
			}
			specC.setCenterOfWork(null);

			ShortPoint2D pos = movC.getPos();
			Path path = steerC.preSearchPath(true, pos.x, pos.y, (short) 30, ESearchType.RESOURCE_SIGNABLE);
			if (path != null) {
				steerC.setPath(path);
				return NodeStatus.SUCCESS;
			}

			return NodeStatus.FAILURE;
		}
	}

	private static void setTargetWorkPos(Context c) {
		PlayerCmdComponent.LeftClickCommand cmd = c.component.getNotificationsIterator(PlayerCmdComponent.LeftClickCommand.class).next();
		c.entity.specC().setTargetWorkPos(cmd.pos);
	}
}
