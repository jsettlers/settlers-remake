package jsettlers.logic.movable.simplebehaviortree.nodes;

import java.io.Serializable;

/**
 * Created by homoroselaps
 */

@FunctionalInterface
public interface ISetPropertyConsumer<T,PropertyType> extends Serializable {
    void accept(T entity, PropertyType value);
}

