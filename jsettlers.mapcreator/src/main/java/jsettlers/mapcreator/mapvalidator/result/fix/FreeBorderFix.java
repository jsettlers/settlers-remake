/*******************************************************************************
 * Copyright (c) 2015 - 2017
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

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.shapes.IMapArea;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.coordinates.CoordinateStream;
import jsettlers.mapcreator.localization.EditorLabels;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Delete invalid resources
 * 
 * @author Andreas Butti
 *
 */
public class FreeBorderFix extends AbstractFix implements IMapArea {
	private static final long serialVersionUID = 1L;

	/**
	 * Points to delete
	 */
	private final List<ShortPoint2D> points = new ArrayList<>();

	/**
	 * Menu to display with possible fixes
	 */
	private final JPopupMenu menu = new JPopupMenu();

	/**
	 * Constructor
	 */
	public FreeBorderFix() {
		JMenuItem menuFix = new JMenuItem(EditorLabels.getLabel("fix.free-borders"));
		menuFix.addActionListener(e -> autoFix());
		menu.add(menuFix);
	}

	@Override
	public void autoFix() {
		data.getMap().fill(ELandscapeType.WATER8, FreeBorderFix.this);
		data.getUndoRedo().endUseStep();
		data.getValidator().reValidate();
	}

	/**
	 * Add a point, which should be freed
	 * 
	 * @param p
	 *            Point
	 */
	public void addPosition(ShortPoint2D p) {
		points.add(p);
	}

	@Override
	public boolean isFixAvailable() {
		return points.size() > 0;
	}

	@Override
	public JPopupMenu getPopupMenu() {
		return menu;
	}

	@Override
	public boolean contains(ShortPoint2D position) {
		return points.contains(position);
	}

	@Override
	public boolean contains(int x, int y) {
		return stream().contains(x, y);
	}

	@Override
	public Iterator<ShortPoint2D> iterator() {
		return points.iterator();
	}

	@Override
	public CoordinateStream stream() {
		return CoordinateStream.fromList(points);
	}
}
