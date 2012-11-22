package jsettlers.common.utils.collections;

/**
 * Defines an interface for a predicate of an object.
 * 
 * @see IteratorFilter
 * 
 * @author Andreas Eberle
 * 
 * @param <T>
 *            Generic type of the predicate.
 */
public interface IPredicate<T> {
	/**
	 * Evaluates the given object to a boolean value.
	 * 
	 * @param object
	 *            Object to be evaluated.
	 * @return The result of the evaluation.
	 */
	boolean evaluate(T object);
}
