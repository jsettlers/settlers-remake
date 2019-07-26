/*******************************************************************************
 * Copyright (c) 2017 - 2018
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
package jsettlers.graphics.map.controls.original.panel.selection;

import jsettlers.common.action.Action;
import jsettlers.common.action.EActionType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.selectable.ISelectionSet;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.ui.LabeledButton;
import jsettlers.graphics.ui.UIPanel;

public class ShipSelectionContent extends AbstractSelectionContent {

	private static final EMovableType[] shiptypes = new EMovableType[] {
			EMovableType.FERRY,
			EMovableType.CARGO_SHIP,
	};

	/**
	 * Rows of selectables
	 */
	public static int ROWS = 10;

	private final UIPanel panel;

	public ShipSelectionContent(ISelectionSet selection) {
		panel = new UIPanel();

		addRowsToPanel(panel, selection, shiptypes);

		UIPanel kill = new LabeledButton(Labels.getString("kill"), new Action(EActionType.DESTROY));
		UIPanel unload = new LabeledButton(Labels.getString("unload"), new Action(EActionType.UNLOAD_FERRIES));

		panel.addChild(kill, .1f, .3f, .9f, .4f);
		panel.addChild(unload, .1f, .1f, .9f, .2f);
	}

	public static void addRowsToPanel(UIPanel panel, ISelectionSet selection, EMovableType[] types) {
		float rowHeight = 1f / ROWS;

		int rowi = ROWS - 1; // from bottom
		for (int i = 0; i < types.length; i++) {
			EMovableType type = types[i];
			int count = selection.getMovableCount(type);

			if (count > 0) {
				SelectionRow row = new SelectionRow(type, count);
				panel.addChild(row, 0.1f, rowHeight * (rowi - 1), .9f,
						rowHeight * (rowi));
				rowi--;
			}
		}

	}

	@Override
	public UIPanel getPanel() {
		return panel;
	}

}
