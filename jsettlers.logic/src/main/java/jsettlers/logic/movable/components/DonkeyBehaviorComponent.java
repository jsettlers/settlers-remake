package jsettlers.logic.movable.components;

import jsettlers.common.material.EMaterialType;
import jsettlers.logic.movable.BehaviorTreeFactory;
import jsettlers.logic.movable.Entity;
import jsettlers.logic.movable.Requires;
import jsettlers.logic.movable.simplebehaviortree.NodeStatus;
import jsettlers.logic.movable.simplebehaviortree.Root;
import jsettlers.logic.movable.simplebehaviortree.nodes.Action;
import jsettlers.logic.movable.simplebehaviortree.nodes.Condition;
import jsettlers.logic.movable.simplebehaviortree.nodes.Guard;
import jsettlers.logic.movable.simplebehaviortree.nodes.MemSelector;
import jsettlers.logic.movable.simplebehaviortree.nodes.MemSequence;
import jsettlers.logic.movable.simplebehaviortree.nodes.Repeat;
import jsettlers.logic.movable.simplebehaviortree.nodes.Selector;
import jsettlers.logic.movable.simplebehaviortree.nodes.Sequence;
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

    private static Action<Entity> tryDropMaterial() {
        return new Action<Entity>(e->{
            EMaterialType material = e.mmatC().removeMaterial();
            if (material == EMaterialType.NO_MATERIAL) return NodeStatus.Failure;
            e.gameC().getMovableGrid().dropMaterial(e.movC().getPos(), material, true, true);
            return NodeStatus.Success;
        });
    }

    private static Action<Entity> tryTakeMaterialFromMarket() {
        return new Action<Entity>(e->{
            EMaterialType material = e.donkeyC().getMarket().tryToTakeDonkeyMaterial();
            if (material == null || material == EMaterialType.NO_MATERIAL) return NodeStatus.Failure;
            e.mmatC().addMaterial(material);
            return NodeStatus.Success;
        });
    }

    private static Action<Entity> tryFindNewMarket() {
        return new Action<Entity>(e->{
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
        return new Root<>($("==<Root>==", new Selector<>(
            TriggerGuard(AttackableComponent.RecievedHit.class,
                $("recieved hit", new Sequence<>(
                    new Action<>(e->{e.donkeyC().resetMarket();}),
                    new Repeat<>(e->!e.mmatC().isEmpty(),
                        Optional(tryDropMaterial())
                    )
                ))
            ),
            new Guard<>(DonkeyBehaviorTreeFactory::hasValidMarket, true,
                new Selector<>(
                    $("fulfill request", new MemSequence<>(
                        $("go to market", new Action<>(e -> { e.steerC().goToPos(e.donkeyC().getMarket().getDoor()); })),
                        $("wait", WaitForTargetReached_FailIfNot()),
                        new Condition<>(e->e.donkeyC().getMarket().needsDonkey()),
                        $("take material", tryTakeMaterialFromMarket()),
                        Optional($("take a second material",tryTakeMaterialFromMarket())),
                        new Action<>(e->{e.attC().setAttackable(true);}),
                        $("follow waypoints", new Repeat<>(e->e.donkeyC().hasNextWaypoint(),
                            new MemSequence<>(
                                $("go to next waypoint", new Action<>(e -> { e.steerC().goToPos(e.donkeyC().peekNextWaypoint()); })),
                                $("wait", WaitForTargetReached_FailIfNot()),
                                new Action<>(e->{e.donkeyC().getNextWaypoint();})
                            )
                        )),
                        new Action<Entity>(e->{e.attC().setAttackable(false);}),
                        $("drop materials",
                            new Repeat<Entity>(e->!e.mmatC().isEmpty(),
                                Optional(tryDropMaterial())
                            )),
                        new Selector<Entity>(
                            tryFindNewMarket(),
                            $("go back to market", new MemSequence<Entity>(
                                new Action<>(e -> { e.steerC().goToPos(e.donkeyC().getMarket().getDoor()); }),
                                Optional($("wait", WaitForTargetReached_FailIfNot())),
                                new Action<Entity>(e->{e.donkeyC().resetMarket();})
                            ))
                        )
                    )),
                    new Sequence<>(
                        new Action<Entity>(e->{e.donkeyC().resetMarket();}),
                        $("drop materials",
                            new Repeat<Entity>(e->!e.mmatC().isEmpty(),
                                Optional(tryDropMaterial())
                            ))
                    )
                )
            ),
            // if no market in need then wait for second
            new Guard<Entity>(DonkeyBehaviorTreeFactory::hasValidMarket, false,
                $("search for valid market", new MemSelector<>(
                    tryFindNewMarket(),
                    new Wait(1000)
                ))
            )
        )));
    }
}
