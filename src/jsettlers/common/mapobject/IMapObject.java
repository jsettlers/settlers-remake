package jsettlers.common.mapobject;

/**
 * This interface is used to define an object that can be displayed on the map.
 * 
 * @author michael
 * 
 */
public interface IMapObject {
	public static final float TREE_CUT_1 = 0.03F;
	public static final float TREE_CUT_2 = 0.06F;
	public static final float TREE_CUT_3 = 0.09F;
	public static final float TREE_TAKEN = 0.1F;

	/**
	 * Gets the type of the object.
	 * 
	 * @return The type of the object to display. May not be <code>null</code>.
	 */
	public EMapObjectType getObjectType();

	/**
	 * this value is used for different things:<br>
	 * for trees:<br>
	 * - when the tree is growing, the value increases from 0 to 1 according to it's size.<br>
	 * - when the tree has been cut, the value increases from 0 to 1. The upper constants define which value defines which state.
	 * <p />
	 * for stones:<br>
	 * - the value gives the number of stones that can be picked from this stone.
	 * <p />
	 * for buildings:<br>
	 * - the value gives the construction state
	 * 
	 * @return a positive float, normally from 0..1
	 */
	public float getStateProgress();

	/**
	 * Gets the next map object for that position.
	 * 
	 * @return The next object at the same position
	 */
	public IMapObject getNextObject();

}
