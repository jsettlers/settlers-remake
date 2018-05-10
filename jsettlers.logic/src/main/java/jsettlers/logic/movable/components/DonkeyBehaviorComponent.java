package jsettlers.logic.movable.components;

import jsettlers.common.material.EMaterialType;
import static jsettlers.logic.movable.BehaviorTreeHelper.*;
import jsettlers.logic.movable.Context;
import jsettlers.logic.movable.Requires;
import jsettlers.logic.movable.simplebehaviortree.NodeStatus;
import jsettlers.logic.movable.simplebehaviortree.Root;
import jsettlers.logic.movable.simplebehaviortree.nodes.Action;
import jsettlers.logic.movable.simplebehaviortree.nodes.MemSelector;
import jsettlers.logic.movable.simplebehaviortree.nodes.Repeat;
import jsettlers.logic.movable.strategies.trading.IDonkeyMarket;

/**
 * @author homoroselaps
 */

@Requires({
    MultiMaterialComponent.class,
    DonkeyComponent.class,
    SteeringComponent.class,
    AttackableComponent.class,
    GameFieldComponent.class,
    AnimationComponent.class,
    MovableComponent.class
})
public final class DonkeyBehaviorComponent extends BehaviorComponent {
    private static final long serialVersionUID = -9105595769767841134L;

    @Override
    protected Root<Context> CreateBehaviorTree() {
        return new Root<>(debug("==<root>==",
            setAttackableWhile(false,
            setIdleBehaviorActiveWhile(false,
                                       selector(
                        triggerGuard(AttackableComponent.ReceivedHit.class,
                        debug("received hit", sequence(
                            debug("unassign market", action(c->{c.entity.donkeyC().resetMarket();})),
                            debug("stop going to market", action(c->{c.entity.steerC().resetTarget();})),
                            debug("drop all materials", repeat(condition(c->!c.entity.mmatC().isEmpty()), alwaysSucceed(TryDropMaterial())))
                                                  ))
                    ),
                        guard(DonkeyBehaviorComponent::hasValidMarket, true,
                              selector(
                            debug("fulfill request", memSequence(
                                    debug("go to market", action(c->{ c.entity.steerC().setTarget(c.entity.donkeyC().getMarket().getDoor()); })),
                                    debug("wait for target reached", waitForTargetReachedAndFailIfNotReachable()),
                                    debug("check for pending transport jobs", condition(c->c.entity.donkeyC().getMarket().needsDonkey())),
                                    debug("take material", TryTakeMaterialFromMarket()),
                                    debug("optionally take a second material", alwaysSucceed(TryTakeMaterialFromMarket())),
                                    setAttackableWhile(true,
                                    debug("follow waypoints", repeat(Repeat.Policy.NONPREEMPTIVE,
                                                                 condition(c->c.entity.donkeyC().hasNextWaypoint()),
                                                                 memSequence(
												debug("go to next waypoint", action(c->{ c.entity.steerC().setTarget(c.entity.donkeyC().peekNextWaypoint()); })),
												debug("wait", waitForTargetReachedAndFailIfNotReachable()),
												action(c->{c.entity.donkeyC().getNextWaypoint();})
                                                                            )
                                                                ))
                                ),
                                    debug("drop all materials", repeat(condition(c->!c.entity.mmatC().isEmpty()), alwaysSucceed(TryDropMaterial()))),
                                    selector(
                                    debug("try find new market", TryFindNewMarket()),
                                    debug("go back to market", memSequence(
											action(c->{ c.entity.steerC().setTarget(c.entity.donkeyC().getMarket().getDoor()); }),
											alwaysSucceed(debug("wait", waitForTargetReachedAndFailIfNotReachable())),
											action(c->{c.entity.donkeyC().resetMarket();})
                                                                      ))
                                            )
                                                            )),
                            debug("resolve failures", sequence(
                                debug("invalidate market", action(c->{c.entity.donkeyC().resetMarket();})),
                                debug("drop materials", repeat(condition(c->!c.entity.mmatC().isEmpty()), alwaysSucceed(TryDropMaterial())))
                                                          ))
                                      )
                             ),
                        // if no market in need then wait for second
                        guard(DonkeyBehaviorComponent::hasValidMarket, false,
                              setIdleBehaviorActiveWhile(true,
                            debug("search for valid market", new MemSelector<>(
                                TryFindNewMarket(),
                                sleep(1000)
                            ))
                        )
                             )
                                               ))
            ))
        );
    }

    private static Action<Context> TryDropMaterial() {
        return new Action<>(c->{
            EMaterialType material = c.entity.mmatC().removeMaterial();
            if (material == EMaterialType.NO_MATERIAL) return NodeStatus.Failure;
            c.entity.gameC().getMovableGrid().dropMaterial(c.entity.movC().getPos(), material, true, true);
            return NodeStatus.Success;
        });
    }

    private static Action<Context> TryTakeMaterialFromMarket() {
        return new Action<>(c->{
            EMaterialType material = c.entity.donkeyC().getMarket().tryToTakeDonkeyMaterial();
            if (material == null || material == EMaterialType.NO_MATERIAL) return NodeStatus.Failure;
            c.entity.mmatC().addMaterial(material);
            return NodeStatus.Success;
        });
    }

    private static Action<Context> TryFindNewMarket() {
        return new Action<>(c->{
            IDonkeyMarket market = c.entity.donkeyC().findNextMarketNeedingDonkey();
            if (market == null) return NodeStatus.Failure;
            c.entity.donkeyC().setMarket(market);
            return NodeStatus.Success;
        });
    }

    private static boolean hasValidMarket(Context c) {
        return c.entity.donkeyC().getMarket() != null;
    }
}
