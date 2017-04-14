package jsettlers.logic.movable;

import jsettlers.common.material.EMaterialType;
import jsettlers.logic.movable.components.AttackableComponent;
import jsettlers.logic.movable.simplebehaviortree.NodeStatus;
import jsettlers.logic.movable.simplebehaviortree.Root;
import jsettlers.logic.movable.simplebehaviortree.nodes.Action;
import jsettlers.logic.movable.simplebehaviortree.nodes.Condition;
import jsettlers.logic.movable.simplebehaviortree.nodes.Guard;
import jsettlers.logic.movable.simplebehaviortree.nodes.MemSequence;
import jsettlers.logic.movable.simplebehaviortree.nodes.Repeat;
import jsettlers.logic.movable.simplebehaviortree.nodes.Selector;
import jsettlers.logic.movable.simplebehaviortree.nodes.Sequence;
import jsettlers.logic.movable.simplebehaviortree.nodes.Succeeder;
import jsettlers.logic.movable.strategies.trading.IDonkeyMarket;

/**
 * @author homoroselaps
 */

class DonkeyBehaviorTreeFactory extends BehaviorTreeFactory{

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
            if (material == EMaterialType.NO_MATERIAL) return NodeStatus.Failure;
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

    public static Root<Entity> create() {
        return new Root<>(new Selector<>(
            TriggerGuard(AttackableComponent.RecievedHit.class,
                new Sequence<>(
                    new Action<Entity>(e->{e.donkeyC().setMarket(null);}),
                    new Repeat<Entity>(e->!e.mmatC().isEmpty(),
                        Optional(tryDropMaterial())
                    )
                )
            ),
            new Guard<Entity>(e->e.donkeyC().getMarket() != null, true,
                new Selector<>(
                    new MemSequence<>(
                        // go to market
                        new Action<>(e -> { e.steerC().goToPos(e.donkeyC().getMarket().getDoor()); }),
                        WaitForTargetReached_FailIfNot(),
                        new Condition<>(e->e.donkeyC().getMarket().needsDonkey()),
                        tryTakeMaterialFromMarket(),
                        // Optionally take a second
                        new Selector<>(
                            tryTakeMaterialFromMarket(),
                            new Succeeder<>()
                        ),
                        new Action<Entity>(e->{e.attC().setAttackable(true);}),
                        new Repeat<>(e->e.donkeyC().hasNextWaypoint(),
                            new MemSequence<>(
                                new Action<>(e -> { e.steerC().goToPos(e.donkeyC().getNextWaypoint()); }),
                                WaitForTargetReached_FailIfNot()
                            )
                        ),
                        new Action<Entity>(e->{e.attC().setAttackable(false);}),
                        // drop materials
                        new Repeat<Entity>(e->!e.mmatC().isEmpty(),
                            Optional(tryDropMaterial())
                        ),
                        tryFindNewMarket()
                    ),
                    new Sequence<>(
                        // drop materials
                        new Action<Entity>(e->{e.donkeyC().setMarket(null);}),
                        new Repeat<Entity>(e->!e.mmatC().isEmpty(),
                            Optional(tryDropMaterial())
                        )
                    )
                )
            ),
            // if no market in need then wait for second
            new Guard<Entity>(e->e.donkeyC().getMarket() == null, true,
                new Selector<>(
                    tryFindNewMarket(),
                    new Wait(1000)
                )
            )
        ));
    }
}
