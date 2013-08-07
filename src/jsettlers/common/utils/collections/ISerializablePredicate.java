package jsettlers.common.utils.collections;

import java.io.Serializable;

/**
 * This is an extension of the {@link IPredicate} interface that ensures that the class can be serialized.
 * 
 * @author Andreas Eberle
 * 
 * @param <T>
 */
public interface ISerializablePredicate<T> extends IPredicate<T>, Serializable {
}