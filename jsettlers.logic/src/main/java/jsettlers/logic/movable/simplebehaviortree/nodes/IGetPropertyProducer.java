package jsettlers.logic.movable.simplebehaviortree.nodes;

/**
 * Created by homoroselaps
 */

@FunctionalInterface
public interface IGetPropertyProducer<T, PropertyType> {
    public PropertyType apply(T entity);
}
