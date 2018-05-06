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
package jsettlers.graphics.map.controls.original.panel.button.stock;

import java8.util.function.Supplier;
import jsettlers.common.images.ImageLink;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.action.Action;
import jsettlers.common.action.SetAcceptedStockMaterialAction;
import jsettlers.graphics.map.controls.original.panel.content.material.priorities.MaterialPriorityContent;
import jsettlers.graphics.ui.Button;

/**
 * This is a button that changes the storage accepting state for that material.
 *
 * @author Michael Zangl
 * @author Andreas Eberle
 *
 */
public class StockChangeAcceptButton extends Button {
	private boolean accept;
	private boolean local;
	private Supplier<EMaterialType> selectedProvider;
	private Supplier<ShortPoint2D> mapPositionProvider;

	/**
	 * Creates a new {@link MaterialPriorityContent.ChangeAcceptButton}.
	 *
	 * @param image
	 *            The image to display
	 * @param description
	 *            The description to use.
	 */
	public StockChangeAcceptButton(ImageLink image, String description) {
		super(null, image, image, description);
	}

	@Override
	public Action getAction() {
		if (selectedProvider == null || mapPositionProvider == null) {
			return null;
		}
		EMaterialType selected = selectedProvider.get();
		ShortPoint2D mapPosition = mapPositionProvider.get();

		if (selected == null || mapPosition == null) {
			return null;
		}
		return new SetAcceptedStockMaterialAction(mapPosition, selected, accept, local);
	}

	public void configure(Supplier<EMaterialType> selectedProvider, Supplier<ShortPoint2D> mapPositionProvider, boolean accept, boolean local) {
		this.selectedProvider = selectedProvider;
		this.mapPositionProvider = mapPositionProvider;
		this.accept = accept;
		this.local = local;
	}
}