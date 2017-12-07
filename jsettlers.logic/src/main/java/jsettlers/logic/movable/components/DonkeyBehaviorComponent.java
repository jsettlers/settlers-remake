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
        return new Root<>($("==<Root>==",
            SetAttackableWhile(false,
            SetIdleBehaviorActiveWhile(false,
                Selector(
                    TriggerGuard(AttackableComponent.ReceivedHit.class,
                        $("received hit", Sequence(
                            $("unassign market", Action(c->{c.entity.donkeyC().resetMarket();})),
                            $("stop going to market", Action(c->{c.entity.steerC().resetTarget();})),
                            $("drop all materials", Repeat(Condition(c->!c.entity.mmatC().isEmpty()),Optional(TryDropMaterial())))
                        ))
                    ),
                    Guard(DonkeyBehaviorComponent::hasValidMarket, true,
                        Selector(
                            $("fulfill request", MemSequence(
                                $("go to market", Action(c->{ c.entity.steerC().setTarget(c.entity.donkeyC().getMarket().getDoor()); })),
                                $("wait for target reached", WaitForTargetReached_FailIfNotReachable()),
                                $("check for pending transport jobs", Condition(c->c.entity.donkeyC().getMarket().needsDonkey())),
                                $("take material", TryTakeMaterialFromMarket()),
                                $("optionally take a second material", Optional(TryTakeMaterialFromMarket())),
                                SetAttackableWhile(true,
                                    $("follow waypoints", Repeat(Repeat.Policy.NONPREEMPTIVE,
                                        Condition(c->c.entity.donkeyC().hasNextWaypoint()),
                                        MemSequence(
                                            $("go to next waypoint", Action(c->{ c.entity.steerC().setTarget(c.entity.donkeyC().peekNextWaypoint()); })),
                                            $("wait", WaitForTargetReached_FailIfNotReachable()),
                                            Action(c->{c.entity.donkeyC().getNextWaypoint();})
                                        )
                                    ))
                                ),
                                $("drop all materials", Repeat(Condition(c->!c.entity.mmatC().isEmpty()),Optional(TryDropMaterial()))),
                                Selector(
                                    $("try find new market", TryFindNewMarket()),
                                    $("go back to market", MemSequence(
                                        Action(c->{ c.entity.steerC().setTarget(c.entity.donkeyC().getMarket().getDoor()); }),
                                        Optional($("wait", WaitForTargetReached_FailIfNotReachable())),
                                        Action(c->{c.entity.donkeyC().resetMarket();})
                                    ))
                                )
                            )),
                            $("resolve failures", Sequence(
                                $("invalidate market", Action(c->{c.entity.donkeyC().resetMarket();})),
                                $("drop materials", Repeat(Condition(c->!c.entity.mmatC().isEmpty()), Optional(TryDropMaterial())))
                            ))
                        )
                    ),
                    // if no market in need then wait for second
                    Guard(DonkeyBehaviorComponent::hasValidMarket, false,
                        SetIdleBehaviorActiveWhile(true,
                            $("search for valid market", new MemSelector<>(
                                TryFindNewMarket(),
                                Sleep(1000)
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
