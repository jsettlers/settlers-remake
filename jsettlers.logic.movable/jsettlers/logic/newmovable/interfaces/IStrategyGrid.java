package jsettlers.logic.newmovable.interfaces;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableBearer;
import jsettlers.logic.newmovable.NewMovableStrategy;

/**
 * Defines methods needed by the {@link NewMovableStrategy}.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IStrategyGrid {

	void addJoblessBearer(IManageableBearer bearer);

	/**
	 * Take a material from the stack at given position of given {@link EMaterialType}.
	 * 
	 * @param pos
	 * @param materialType
	 * @return true if the material was available<br>
	 *         false otherwise.
	 */
	boolean takeMaterial(ShortPoint2D pos, EMaterialType materialType);

}
