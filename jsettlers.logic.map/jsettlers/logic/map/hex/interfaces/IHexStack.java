package jsettlers.logic.map.hex.interfaces;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.IStack;
import jsettlers.common.player.IPlayerable;

/**
 * defines a stack that can be handled by the hex grid.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IHexStack extends IStack, IPlayerable {

	/**
	 * push the given materialType to this stack
	 * 
	 * @param materialType
	 * @return true if the push succeeded
	 */
	boolean push(EMaterialType materialType);

	/**
	 * Is called if this stack needs to be removed from the map.<br>
	 * Releases the resources of this stack
	 */
	void destroy();

	/**
	 * pop the given material from this stack.
	 * 
	 * @param material
	 * @return true if the take succeeded, false if it didn't.
	 */
	boolean pop(EMaterialType material);

	/**
	 * @return true if this stack is full, false otherwise
	 */
	boolean isFull();

	/**
	 * 
	 * @return true if this stack has at least one element
	 */
	boolean isEmpty();

	/**
	 * this stack should stop requesting anything and start offering everything it has
	 */
	void releaseRequests();
}
