package jsettlers.logic.movable.simplebehaviortree.nodes;

import jsettlers.logic.movable.simplebehaviortree.Decorator;
import jsettlers.logic.movable.simplebehaviortree.Node;
import jsettlers.logic.movable.simplebehaviortree.NodeStatus;
import jsettlers.logic.movable.simplebehaviortree.Tick;

public class Property<T, PropertyType> extends Decorator<T>
{
    private static final long serialVersionUID = 6714370606784586530L;
    private PropertyType oldValue;
    private final PropertyType newValue;
    private final ISetPropertyConsumer<T, PropertyType> setter;
    private final IGetPropertyProducer<T, PropertyType> getter;

    public Property(ISetPropertyConsumer<T, PropertyType> setter, IGetPropertyProducer<T, PropertyType> getter, PropertyType value, Node<T> child) {
        super(child);
        this.newValue = value;
        this.setter = setter;
        this.getter = getter;
    }

    @Override
    protected void onEnter(Tick<T> tick) {
        oldValue = getter.apply(tick.Target);
        setter.accept(tick.Target, newValue);
    }

    @Override
    protected void onClose(Tick<T> tick) {
        setter.accept(tick.Target, oldValue);
    }

    @Override
    protected NodeStatus onTick(Tick<T> tick) {
        return child.execute(tick);
    }
}
