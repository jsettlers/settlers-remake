package jsettlers.logic.movable.simplebehaviortree.nodes;

/**
 * Created by homoroselaps
 */

@FunctionalInterface
public interface ISetPropertyConsumer<T,PropertyType> {
    public void accept(T entity, PropertyType value);
}

