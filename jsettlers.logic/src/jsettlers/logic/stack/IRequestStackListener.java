package jsettlers.logic.stack;

/**
 * This interface defines a listener for a {@link RequestStack}. The listener can be registered on a {@link RequestStack} to receive events when a
 * material is delivered.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IRequestStackListener {
	void materialDelivered(RequestStack stack);
}
