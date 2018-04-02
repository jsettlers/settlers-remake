/*******************************************************************************
 * Copyright (c) 2017
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
 *******************************************************************************/
package jsettlers.graphics.map.controls.original.panel.button;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.action.Action;

/**
 * This is a material button for the trading GUI.
 *
 * @author Michael Zangl
 * @author Andreas Eberle
 */
public class SelectionManagedMaterialButton extends MaterialButton {
	private SelectionManager selectionManager;

	/**
	 * Creates a new {@link SelectionManagedMaterialButton}.
	 *
	 * @param material
	 *            The material this button is for.
	 */
	public SelectionManagedMaterialButton(EMaterialType material) {
		super(null, material);
	}

	@Override
	public Action getAction() {
		if (selectionManager != null) {
			return selectionManager.getSelectAction(getMaterial());
		} else {
			return null;
		}
	}

	/**
	 * Binds this button to a selection manager.
	 *
	 * @param selectionManager
	 *            The manager to use.
	 */
	public void setSelectionManager(SelectionManager selectionManager) {
		this.selectionManager = selectionManager;
	}
}