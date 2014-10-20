package jsettlers.common.mapobject;

import jsettlers.common.material.EMaterialType;

/**
 * Specifies a stack on the map.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IStackMapObject extends IMapObject {

	/**
	 * Gives the {@link EMaterialType} of the elements placed on this stack.
	 * 
	 * @return the {@link EMaterialType} of this stack.
	 */
	EMaterialType getMaterialType();

	/**
	 * Gives the number of elements on this stack
	 * 
	 * @return size of stack.
	 */
	byte getSize();
}
