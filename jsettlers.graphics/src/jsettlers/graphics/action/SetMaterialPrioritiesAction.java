package jsettlers.graphics.action;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ShortPoint2D;

/**
 * This {@link Action} is used to set the priority order of
 * {@link EMaterialType}s.
 * 
 * @author Andreas Eberle
 */
public class SetMaterialPrioritiesAction extends Action {

	private final ShortPoint2D managerPosition;
	private final EMaterialType[] materialTypeForPriority;

	/**
	 * Creates a new {@link SetMaterialPrioritiesAction} to change the
	 * priorities of {@link EMaterialType}s.
	 * 
	 * @param managerPosition
	 *            The position of the manager whose settings shall be changed.
	 * @param materialTypeForPriority
	 *            An array of all droppable {@link EMaterialType}s. The first
	 *            element has the highest priority, the last one hast the
	 *            lowest.
	 */
	public SetMaterialPrioritiesAction(ShortPoint2D managerPosition,
	        EMaterialType[] materialTypeForPriority) {
		super(EActionType.SET_MATERIAL_PRIORITIES);

		assert materialTypeForPriority.length == EMaterialType.NUMBER_OF_DROPPABLE_MATERIALS : "The given material types for priorities may only contain droppable materials";

		this.managerPosition = managerPosition;
		this.materialTypeForPriority = materialTypeForPriority;
	}

	/**
	 * @return Returns the position of the manager whose settings will be
	 *         changed.
	 */
	public ShortPoint2D getManagerPosition() {
		return managerPosition;
	}

	/**
	 * @return Returns an array of droppable {@link EMaterialType}s where the
	 *         first element has the highest priority.
	 */
	public EMaterialType[] getMaterialTypeForPriority() {
		return materialTypeForPriority;
	}

}
