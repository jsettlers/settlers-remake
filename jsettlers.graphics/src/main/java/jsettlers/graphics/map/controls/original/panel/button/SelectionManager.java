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

import static java8.util.J8Arrays.stream;

import java.util.Collection;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.action.Action;
import jsettlers.graphics.action.ExecutableAction;

/**
 * This class manages the selection of the {@link SelectionManagedMaterialButton}s.
 *
 * @author Michael Zangl
 * @author Andreas Eberle
 */
public class SelectionManager {
	private SelectionManagedMaterialButton[] buttons;
	private EMaterialType selected;

	public void setButtons(Collection<? extends SelectionManagedMaterialButton> buttons) {
		setButtons(buttons.toArray(new SelectionManagedMaterialButton[buttons.size()]));
	}

	public void setButtons(SelectionManagedMaterialButton[] buttons) {
		if (this.buttons != null) {
			stream(this.buttons).forEach(button -> button.setSelectionManager(null));
		}
		this.buttons = buttons;
		if (buttons != null) {
			stream(buttons).forEach(button -> button.setSelectionManager(this));
			updateSelected();
		}
	}

	public Action getSelectAction(final EMaterialType material) {
		return new ExecutableAction() {
			@Override
			public void execute() {
				select(material);
			}
		};
	}

	protected void select(EMaterialType material) {
		this.selected = material;
		updateSelected();
	}

	private void updateSelected() {
		stream(buttons).forEach(button -> button.setSelected(selected == button.getMaterial()));
	}

	public EMaterialType getSelected() {
		return selected;
	}
}