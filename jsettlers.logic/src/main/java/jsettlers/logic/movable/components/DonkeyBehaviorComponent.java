package jsettlers.logic.movable.components;

import jsettlers.common.material.EMaterialType;
import jsettlers.logic.movable.BehaviorTreeFactory;
import jsettlers.logic.movable.Entity;
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

    public DonkeyBehaviorComponent() {
        super(DonkeyBehaviorTreeFactory.create());
    }
}

abstract class DonkeyBehaviorTreeFactory extends BehaviorTreeFactory {
    private static final long serialVersionUID = -6930178935920899644L;

    private static Action<Entity> TryDropMaterial() {
        return new Action<>(e->{
            EMaterialType material = e.mmatC().removeMaterial();
            if (material == EMaterialType.NO_MATERIAL) return NodeStatus.Failure;
            e.gameC().getMovableGrid().dropMaterial(e.movC().getPos(), material, true, true);
            return NodeStatus.Success;
        });
    }

    private static Action<Entity> TryTakeMaterialFromMarket() {
        return new Action<>(e->{
            EMaterialType material = e.donkeyC().getMarket().tryToTakeDonkeyMaterial();
            if (material == null || material == EMaterialType.NO_MATERIAL) return NodeStatus.Failure;
            e.mmatC().addMaterial(material);
            return NodeStatus.Success;
        });
    }

    private static Action<Entity> TryFindNewMarket() {
        return new Action<>(e->{
            IDonkeyMarket market = e.donkeyC().findNextMarketNeedingDonkey();
            if (market == null) return NodeStatus.Failure;
            e.donkeyC().setMarket(market);
            return NodeStatus.Success;
        });
    }

    private static boolean hasValidMarket(Entity entity) {
        return entity.donkeyC().getMarket() != null;
    }

    public static Root<Entity> create() {
        return new Root<>($("==<Root>==",
            SetAttackableWhile(false,
                Selector(
                    TriggerGuard(AttackableComponent.ReceivedHit.class,
                        $("received hit", Sequence(
                            $("unassign market", Action(e->{e.donkeyC().resetMarket();})),
                            $("stop going to market", Action(e->{e.steerC().resetTarget();})),
                            $("drop all materials", Repeat(Condition(e->!e.mmatC().isEmpty()),Optional(TryDropMaterial())))
                        ))
                    ),
                    Guard(DonkeyBehaviorTreeFactory::hasValidMarket, true,
                        Selector(
                            $("fulfill request", MemSequence(
                                $("go to market", Action(e->{ e.steerC().setTarget(e.donkeyC().getMarket().getDoor()); })),
                                $("wait for target reached", WaitForTargetReached_FailIfNotReachable()),
                                $("check for pending transport jobs", Condition(e->e.donkeyC().getMarket().needsDonkey())),
                                $("take material", TryTakeMaterialFromMarket()),
                                $("optionally take a second material", Optional(TryTakeMaterialFromMarket())),
                                SetAttackableWhile(true,
                                    $("follow waypoints", Repeat(Repeat.Policy.NONPREEMPTIVE,
                                        Condition(e->e.donkeyC().hasNextWaypoint()),
                                        MemSequence(
                                            $("go to next waypoint", Action(e->{ e.steerC().setTarget(e.donkeyC().peekNextWaypoint()); })),
                                            $("wait", WaitForTargetReached_FailIfNotReachable()),
                                            Action(e->{e.donkeyC().getNextWaypoint();})
                                        )
                                    ))
                                ),
                                $("drop all materials", Repeat(Condition(e->!e.mmatC().isEmpty()),Optional(TryDropMaterial()))),
                                Selector(
                                    $("try find new market", TryFindNewMarket()),
                                    $("go back to market", MemSequence(
                                        Action(e->{ e.steerC().setTarget(e.donkeyC().getMarket().getDoor()); }),
                                        Optional($("wait", WaitForTargetReached_FailIfNotReachable())),
                                        Action(e->{e.donkeyC().resetMarket();})
                                    ))
                                )
                            )),
                            $("resolve failures", Sequence(
                                $("invalidate market", Action(e->{e.donkeyC().resetMarket();})),
                                $("drop materials", Repeat(Condition(e->!e.mmatC().isEmpty()), Optional(TryDropMaterial())))
                            ))
                        )
                    ),
                    // if no market in need then wait for second
                    Guard(DonkeyBehaviorTreeFactory::hasValidMarket, false,
                        $("search for valid market", new MemSelector<>(
                            TryFindNewMarket(),
                            Sleep(1000)
                        ))
                    )
                ))
            )
        );
    }
}
