/*******************************************************************************
 * Copyright (c) 2015
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
package jsettlers.graphics.map.controls.original.panel.content;

import go.graphics.GLDrawContext;

import java.util.Arrays;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.OriginalImageLink;
import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.map.partition.IPartitionSettings;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.FloatRectangle;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ExecutableAction;
import jsettlers.graphics.action.SetMaterialPrioritiesAction;
import jsettlers.graphics.image.Image;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.utils.Button;
import jsettlers.graphics.utils.UIPanel;

/**
 * This panel lets the user select the priorities in which the materials should be transported by settlers.
 *
 * @author michael
 */
public class MaterialPriorityPanel extends UIPanel implements IContentProvider {

	private class SelectMaterialAction extends ExecutableAction {
		private final EMaterialType eMaterialType;

		public SelectMaterialAction(EMaterialType eMaterialType) {
			this.eMaterialType = eMaterialType;
		}

		@Override
		public void execute() {
			selectMaterial(eMaterialType);
		}

	}

	private class ReorderButton extends Button {

		private final int add;

		public ReorderButton(int add, OriginalImageLink image,
				OriginalImageLink active, String description) {
			super(null, image, active, description);
			this.add = add;
		}

		@Override
		public Action getAction() {
			return createChangeorderAction(add);
		}
	}

	private static final int COLUMNS = 6;
	private static final float RELATIVE_BUTTONHEIGHT = .06f;

	private static final float RELATIVE_BUTTONWIDTH = 1f / (COLUMNS + 2);

	private static final OriginalImageLink ALL_UP_IMAGE =
			new OriginalImageLink(EImageLinkType.GUI, 3, 219, 0);
	private static final OriginalImageLink ALL_DOWN_IMAGE =
			new OriginalImageLink(EImageLinkType.GUI, 3, 222, 0);
	private static final OriginalImageLink UP_IMAGE = new OriginalImageLink(
			EImageLinkType.GUI, 3, 225, 0);
	private static final OriginalImageLink DOWN_IMAGE = new OriginalImageLink(
			EImageLinkType.GUI, 3, 228, 0);

	private static final float ROWHEIGHT = .1f;

	/**
	 * Points on which materials can be placed.
	 * <p>
	 * They are indexed by slot.
	 */
	private final float[] xpoints =
			new float[EMaterialType.NUMBER_OF_MATERIALS];
	private final float[] ypoints =
			new float[EMaterialType.NUMBER_OF_MATERIALS];

	private EMaterialType[] order = new EMaterialType[0];

	/**
	 * This is a mapping: {@link EMaterialType} -> label position
	 */
	private final AnimateablePosition[] positions =
			new AnimateablePosition[EMaterialType.NUMBER_OF_MATERIALS];

	private EMaterialType selected = null;
	private ShortPoint2D currentPos;
	private IGraphicsGrid currentGrid;

	public MaterialPriorityPanel() {
		addRowPositions(0, true);
		addRowPositions(1, false);
		addRowPositions(2, true);
		addRowPositions(3, false);
		addRowPositions(4, true);

		updateDisplayedData();

		addChild(new ReorderButton(-1000, ALL_UP_IMAGE,
				ALL_UP_IMAGE, "all up"), .75f, .1f, .9f, .18f);
		addChild(new ReorderButton((-1), UP_IMAGE, UP_IMAGE,
				"one up"), .6f, .1f, .75f, .18f);
		addChild(new ReorderButton((1), DOWN_IMAGE, DOWN_IMAGE,
				"one down"), .45f, .1f, .6f, .18f);
		addChild(new ReorderButton((1000), ALL_DOWN_IMAGE,
				ALL_DOWN_IMAGE, "all down"), .3f, .1f, .45f, .18f);
	}

	private void addRowPositions(int rowIndex, boolean descent) {
		for (int column = 0; column < COLUMNS; column++) {
			int index = rowIndex * COLUMNS + column;
			if (index < xpoints.length) {
				xpoints[index] =
						(float) (descent ? (column + 1) : (COLUMNS - column))
								/ (COLUMNS + 2);
				ypoints[index] =
						.8f - rowIndex * ROWHEIGHT - (float) column
								/ (COLUMNS - 1)
								* (ROWHEIGHT - RELATIVE_BUTTONHEIGHT);
			}
		}
	}

	@Override
	public synchronized void showMapPosition(ShortPoint2D pos, IGraphicsGrid grid) {
		currentPos = pos;
		this.currentGrid = grid;
		IPartitionSettings data = grid.getPartitionSettings(pos.x, pos.y);
		if (data != null) {
			order =
					new EMaterialType[EMaterialType.NUMBER_OF_DROPPABLE_MATERIALS];
			for (int i = 0; i < EMaterialType.NUMBER_OF_DROPPABLE_MATERIALS; i++) {
				order[i] = data.getMaterialTypeForPrio(i);
			}
			System.out.println("Using map pos: " + pos + " => " + Arrays.deepToString(order));
		} else {
			order = new EMaterialType[0];
		}
		// TODO: Only call if we left/entered partition.
		updateDisplayedData();
	}

	private void updateDisplayedData() {
		Arrays.fill(positions, null);
		for (int i = 0; i < order.length; i++) {
			EMaterialType material = order[i];
			positions[material.ordinal] =
					new AnimateablePosition(xpoints[i], ypoints[i]);
		}
	}

	protected synchronized Action createChangeorderAction(final int add) {
		EMaterialType[] types = reorderSelected(add);
		if (types.length > 0 && currentPos != null) {
			System.out.println("New order for " + currentPos + ": " + Arrays.deepToString(types));
			return new SetMaterialPrioritiesAction(currentPos, types);
		} else {
			return null;
		}
	}

	protected synchronized EMaterialType[] reorderSelected(int add) {
		for (int i = 0; i < order.length; i++) {
			if (order[i] == selected) {
				return reorder(selected, i + add);
			}
		}
		return order;
	}

	private EMaterialType[] reorder(EMaterialType type, int desiredNewPosition) {
		int oldPos = -1;
		for (int i = 0; i < order.length; i++) {
			if (order[i] == type) {
				oldPos = i;
			}
		}
		if (oldPos < 0) {
			return order;
		}
		EMaterialType[] newOrder = order.clone();
		int newPos =
				Math.max(Math.min(desiredNewPosition, order.length - 1), 0);

		if (newPos > oldPos) {
			for (int i = oldPos; i < newPos; i++) {
				newOrder[i] = newOrder[i + 1];
			}
		} else {
			for (int i = oldPos; i > newPos; i--) {
				newOrder[i] = newOrder[i - 1];
			}
		}
		newOrder[desiredNewPosition] = type;
		return newOrder;
	}

	private void setToPosition(EMaterialType type, int newindex) {
		positions[type.ordinal()]
				.setPosition(xpoints[newindex], ypoints[newindex]);
	}

	@Override
	public synchronized void drawAt(GLDrawContext gl) {
		super.drawAt(gl);
		gl.color(1, 1, 1, 1);

		updatePositions();

		gl.glPushMatrix();
		FloatRectangle position = getPosition();
		gl.glTranslatef(position.getMinX(), position.getMinY(), 0);
		gl.glScalef(position.getWidth(), position.getHeight(), 1);

		for (EMaterialType mat : order) {
			int i = mat.ordinal();
			drawButton(gl, positions[i].getX(), positions[i].getY(), mat);
		}
		gl.glPopMatrix();
	}

	private void updatePositions() {
		if (currentGrid != null) {
			IPartitionSettings settings = currentGrid.getPartitionSettings(currentPos.x, currentPos.y);
			if (settings != null) {
				for (int i = 0; i < order.length; i++) {
					EMaterialType should = settings.getMaterialTypeForPrio(i);
					if (order[i] != should) {
						setToPosition(should, i);
						order[i] = should;
					}
				}
			}
		}
	}

	private void drawButton(GLDrawContext gl, float x, float y,
			EMaterialType materialType) {
		// TODO: add 1 or 2 for bigger gui
		int image = materialType.getGuiIconBase();
		int file = materialType.getGuiFile();
		Image iamgeLink =
				ImageProvider.getInstance().getImage(
						new OriginalImageLink(EImageLinkType.GUI, file, image,
								0));
		iamgeLink.drawImageAtRect(gl, x, y, x + RELATIVE_BUTTONWIDTH, y
				+ RELATIVE_BUTTONHEIGHT);

		if (selected == materialType) {
			ImageProvider
					.getInstance()
					.getImage(new OriginalImageLink(EImageLinkType.GUI, 3, 339, 0))
					.drawImageAtRect(gl, x, y, x + RELATIVE_BUTTONWIDTH,
							y + RELATIVE_BUTTONHEIGHT);
		}
	}

	@Override
	public UIPanel getPanel() {
		return this;
	}

	@Override
	public synchronized Action getAction(float relativex, float relativey) {
		Action action = super.getAction(relativex, relativey);
		if (action == null) {
			for (int i = 0; i < positions.length; i++) {
				AnimateablePosition pos = positions[i];
				if (pos != null) {
					float x = pos.getX();
					float y = pos.getY();
					if (relativex >= x && relativex < x + RELATIVE_BUTTONWIDTH
							&& relativey >= y
							&& relativey < y + RELATIVE_BUTTONWIDTH) {
						return new SelectMaterialAction(EMaterialType.values[i]);
					}
				}
			}
		}
		return action;
	}

	/**
	 * Sets the selected material type.
	 *
	 * @param eMaterialType
	 */
	public synchronized void selectMaterial(EMaterialType eMaterialType) {
		selected = eMaterialType;
	}

	@Override
	public ESecondaryTabType getTabs() {
		return ESecondaryTabType.GOODS;
	}

	@Override
	public void displayBuildingBuild(EBuildingType type) {
	}

}
