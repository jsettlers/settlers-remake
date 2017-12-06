package jsettlers.logic.movable.components;

import jsettlers.algorithms.path.Path;
import jsettlers.common.map.shapes.HexGridArea;
import jsettlers.common.material.ESearchType;
import jsettlers.common.movable.EMovableAction;
import jsettlers.common.position.MutablePoint2D;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.mutables.MutableDouble;
import jsettlers.logic.movable.BehaviorTreeHelper.*;
import jsettlers.logic.movable.Context;
import jsettlers.logic.movable.Requires;
import jsettlers.logic.movable.simplebehaviortree.Node;
import jsettlers.logic.movable.simplebehaviortree.NodeStatus;
import jsettlers.logic.movable.simplebehaviortree.Root;
import jsettlers.logic.movable.simplebehaviortree.nodes.Action;

import static jsettlers.logic.movable.BehaviorTreeHelper.$;
import static jsettlers.logic.movable.BehaviorTreeHelper.Action;
import static jsettlers.logic.movable.BehaviorTreeHelper.Guard;
import static jsettlers.logic.movable.BehaviorTreeHelper.MemSequence;
import static jsettlers.logic.movable.BehaviorTreeHelper.Selector;
import static jsettlers.logic.movable.BehaviorTreeHelper.Sequence;
import static jsettlers.logic.movable.BehaviorTreeHelper.StartAnimation;
import static jsettlers.logic.movable.BehaviorTreeHelper.TriggerGuard;
import static jsettlers.logic.movable.BehaviorTreeHelper.WaitForNotification;
import static jsettlers.logic.movable.BehaviorTreeHelper.WaitForTargetReached_FailIfNotReachable;

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
    protected Root<Context> CreateBehaviorTree() {
        final short ACTION1_DURATION = 1400;
        final short ACTION2_DURATION = 1500;

        return new Root<>(Selector(
            TriggerGuard(PlayerCmdComponent.LeftClickCommand.class,
                MemSequence(
                    Action(c->{c.entity.specC().setIsWorking(false);}),
                    Action(GeologistBehaviorComponent::setTargetWorkPos)
                )
            ),
            TriggerGuard(PlayerCmdComponent.AltLeftClickCommand.class,
                MemSequence(
                    Action(c->{c.entity.specC().setIsWorking(true);}),
                    Action(GeologistBehaviorComponent::setTargetWorkPos)
                )
            ),
            TriggerGuard(PlayerCmdComponent.StartWorkCommand.class,
                Sequence(
                    Action(c->{c.entity.specC().setIsWorking(true);}),
                    Action(c->{c.entity.specC().resetTargetWorkPos();})
                )
            ),
            Guard(c -> c.entity.specC().getTargetWorkPos() != null, true,
                Selector(
                    MemSequence(
                        Action(c->{c.entity.steerC().setTarget(c.entity.specC().getTargetWorkPos());}),
                        WaitForTargetReached_FailIfNotReachable(),
                        Action(c->{c.entity.specC().resetTargetWorkPos();})
                    ),
                    Sequence(
                        Action(c->{c.entity.specC().resetTargetWorkPos();}),
                        Action(c->{c.entity.specC().setIsWorking(false);})
                    )
                )
            ),
            Guard(c -> c.entity.specC().isWorking(), true,
                Selector(
                    $("find a place and work there", MemSequence(
                        Find_GoToWorkablePosition(),
                        WaitForTargetReached_FailIfNotReachable(),
                        WorkOnPosIfPossible(),
                        StartAnimation(EMovableAction.ACTION1, ACTION1_DURATION),
                        WaitForNotification(AnimationComponent.AnimationFinishedTrigger.class, true),
                        StartAnimation(EMovableAction.ACTION2, ACTION2_DURATION),
                        WaitForNotification(AnimationComponent.AnimationFinishedTrigger.class, true)
                    )),
                    $("on failure: stop working", Action(c -> { c.entity.specC().setIsWorking(false);}))
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
                return NodeStatus.Success;
            }
            return NodeStatus.Failure;
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
                return NodeStatus.Success;
            }
            specC.setCenterOfWork(null);

            ShortPoint2D pos = movC.getPos();
            Path path = steerC.preSearchPath(true, pos.x, pos.y, (short) 30, ESearchType.RESOURCE_SIGNABLE);
            if (path != null) {
                steerC.setPath(path);
                return NodeStatus.Success;
            }

            return NodeStatus.Failure;
        }
    }

    private static void setTargetWorkPos(Context c) {
        PlayerCmdComponent.LeftClickCommand cmd = c.comp.getNotificationsIt(PlayerCmdComponent.LeftClickCommand.class).next();
        c.entity.specC().setTargetWorkPos(cmd.pos);
    }
}
