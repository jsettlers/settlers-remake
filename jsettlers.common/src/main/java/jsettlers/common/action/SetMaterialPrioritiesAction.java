/*
 * Copyright (c) 2018
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */
package jsettlers.common.action;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ShortPoint2D;

/**
 * This {@link Action} is used to set the priority order of {@link EMaterialType}s.
 * 
 * @author Andreas Eberle
 */
public class SetMaterialPrioritiesAction extends PointAction {

	private final EMaterialType[] materialTypeForPriority;

	/**
	 * Creates a new {@link SetMaterialPrioritiesAction} to change the priorities of {@link EMaterialType}s.
	 * 
	 * @param managerPosition
	 *            The position of the manager whose settings shall be changed.
	 * @param materialTypeForPriority
	 *            An array of all droppable {@link EMaterialType}s. The first element has the highest priority, the last one hast the lowest.
	 */
	public SetMaterialPrioritiesAction(ShortPoint2D managerPosition,
			EMaterialType[] materialTypeForPriority) {
		super(EActionType.SET_MATERIAL_PRIORITIES, managerPosition);

		assert materialTypeForPriority.length == EMaterialType.NUMBER_OF_DROPPABLE_MATERIALS : "The given material types for priorities may only contain droppable materials";

		this.materialTypeForPriority = materialTypeForPriority;
	}

	/**
	 * @return Returns the position of the manager whose settings will be changed.
	 */
	@Override
	public ShortPoint2D getPosition() {
		return super.getPosition();
	}

	/**
	 * @return Returns an array of droppable {@link EMaterialType}s where the first element has the highest priority.
	 */
	public EMaterialType[] getMaterialTypeForPriority() {
		return materialTypeForPriority;
	}

}
