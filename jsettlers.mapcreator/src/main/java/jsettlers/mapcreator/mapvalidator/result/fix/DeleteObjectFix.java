/*******************************************************************************
 * Copyright (c) 2015 - 2016
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
package jsettlers.mapcreator.mapvalidator.result.fix;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import jsettlers.common.position.ShortPoint2D;
import jsettlers.mapcreator.data.MapData;
import jsettlers.mapcreator.localization.EditorLabels;

/**
 * Delete invalid objects
 * 
 * @author Andreas Butti
 *
 */
public class DeleteObjectFix extends AbstractFix {

	/**
	 * List with invalid building positions
	 */
	private List<ShortPoint2D> list = new ArrayList<>();

	/**
	 * Constructor
	 */
	public DeleteObjectFix() {
	}

	@Override
	public boolean isFixAvailable() {
		return list.size() > 0;
	}

	@Override
	public JPopupMenu getPopupMenu() {
		JPopupMenu menu = new JPopupMenu();
		JMenuItem menuFix = new JMenuItem(EditorLabels.getLabel("fix.delete-invalid-objects"));
		menuFix.addActionListener(e -> autoFix());
		menu.add(menuFix);
		return menu;
	}

	@Override
	public void autoFix() {
		MapData map = data.getMap();
		for (ShortPoint2D p : list) {
			map.deleteObject(p.x, p.y);
		}

		data.getUndoRedo().endUseStep();
		data.getValidator().reValidate();
	}

	/**
	 * Add an invalid building position
	 * 
	 * @param point
	 *            Position
	 */
	public void addInvalidObject(ShortPoint2D point) {
		list.add(point);
	}

}
