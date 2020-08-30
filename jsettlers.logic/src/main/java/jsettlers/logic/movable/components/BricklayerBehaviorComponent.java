package jsettlers.logic.movable.components;

import jsettlers.common.movable.EMovableAction;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.movable.Context;
import jsettlers.logic.movable.Requires;
import jsettlers.logic.movable.simplebehaviortree.Node;
import jsettlers.logic.movable.simplebehaviortree.NodeStatus;
import jsettlers.logic.movable.simplebehaviortree.nodes.Repeat;

import static jsettlers.logic.movable.BehaviorTreeHelper.action;
import static jsettlers.logic.movable.BehaviorTreeHelper.alwaysFail;
import static jsettlers.logic.movable.BehaviorTreeHelper.condition;
import static jsettlers.logic.movable.BehaviorTreeHelper.debug;
import static jsettlers.logic.movable.BehaviorTreeHelper.guard;
import static jsettlers.logic.movable.BehaviorTreeHelper.memSequence;
import static jsettlers.logic.movable.BehaviorTreeHelper.repeat;
import static jsettlers.logic.movable.BehaviorTreeHelper.selector;
import static jsettlers.logic.movable.BehaviorTreeHelper.sequence;
import static jsettlers.logic.movable.BehaviorTreeHelper.setIdleBehaviorActiveWhile;
import static jsettlers.logic.movable.BehaviorTreeHelper.sleep;
import static jsettlers.logic.movable.BehaviorTreeHelper.startAndWaitForAnimation;
import static jsettlers.logic.movable.BehaviorTreeHelper.triggerGuard;
import static jsettlers.logic.movable.BehaviorTreeHelper.waitForTargetReachedAndFailIfNotReachable;

/**
 * @author homoroselaps
 */

@Requires({
	MaterialComponent.class,
	SteeringComponent.class,
	GameFieldComponent.class,
	AnimationComponent.class,
	MovableComponent.class
})
public final class BricklayerBehaviorComponent extends BehaviorComponent {
	private static final long serialVersionUID = -4581601951753172458L;

	@Override
	protected Node<Context> createBehaviorTree() {
		return setIdleBehaviorActiveWhile(false,
			selector(
				triggerGuard(BricklayerComponent.BricklayerJob.class,
					action("accepting Bricklayer job", context -> {
						context.component.forFirstNotificationOfTypeC(BricklayerComponent.BricklayerJob.class, job -> {
							context.entity.bricklayerComponent().setBricklayerJob(job);
						}, true);
					})
				),
				guard("has active bricklayer job",c -> c.entity.bricklayerComponent().hasJob() && c.entity.bricklayerComponent().isBricklayerRequestActive(),
					selector(
						memSequence(
							action(c->{c.entity.steeringComponent().setTarget(c.entity.bricklayerComponent().getBricklayerTargetPos());}),
							selector(
								waitForTargetReachedAndFailIfNotReachable(),
								sequence(
									abortJob(),
									alwaysFail()
								)
							),
							action("look in direction", c -> { c.entity.movableComponent().setViewDirection(c.entity.bricklayerComponent().getLookDirection());}),
							repeat("try to build", Repeat.Policy.NONPREEMPTIVE,
								condition(c -> c.entity.bricklayerComponent().isBricklayerRequestActive()),
								memSequence(
									action("try take material", c -> { return NodeStatus.of(c.entity.bricklayerComponent().tryTakeMaterialFromConstructionSite()); } ),
									startAndWaitForAnimation(EMovableAction.ACTION1, Constants.BRICKLAYER_ACTION_DURATION)
								)
							)
						),
						jobFinished()
					)
				),
				debug("idle behavior",
					setIdleBehaviorActiveWhile(true,
						sleep(1000)
					)
				)
			)
		);
	}

	private static Node<Context> jobFinished() {
		return debug("job finished",action(c->{c.entity.bricklayerComponent().jobFinished();}));
	}

	private static Node<Context> abortJob() {
		return debug("abort job",action(c->{c.entity.bricklayerComponent().abortJob();}));
	}
}
