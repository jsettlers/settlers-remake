package jsettlers.logic.movable.simplebehaviortree.nodes;

import java.io.Serializable;

/**
 * Created by homoroselaps
 */
@FunctionalInterface
public interface IGetPropertyProducer<T, PropertyType> extends Serializable {
    PropertyType apply(T entity);
}
