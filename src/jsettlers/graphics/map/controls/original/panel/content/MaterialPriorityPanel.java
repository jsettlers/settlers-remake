package jsettlers.graphics.map.controls.original.panel.content;

import go.graphics.GLDrawContext;
import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.OriginalImageLink;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.FloatRectangle;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.image.Image;
import jsettlers.graphics.map.controls.original.panel.IContextListener;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.startscreen.ExecutableAction;
import jsettlers.graphics.utils.Button;
import jsettlers.graphics.utils.UIPanel;

/**
 * This panel lets the user select the priorities in which the materials should
 * be transported by settlers.
 * 
 * @author michael
 */
public class MaterialPriorityPanel extends UIPanel implements IContentProvider {

	private static final int COLUMNS = 6;
	private static final float RELATIVE_BUTTONHEIGHT = .06f;

	private static final float RELATIVE_BUTTONWIDTH = 1f / (COLUMNS + 2);


	private static final OriginalImageLink ALL_UP_IMAGE = new OriginalImageLink(
	        EImageLinkType.GUI, 3, 219, 0);
	private static final OriginalImageLink ALL_DOWN_IMAGE = new OriginalImageLink(
	        EImageLinkType.GUI, 3, 222, 0);
	private static final OriginalImageLink UP_IMAGE = new OriginalImageLink(EImageLinkType.GUI,
	        3, 225, 0);
	private static final OriginalImageLink DOWN_IMAGE = new OriginalImageLink(
	        EImageLinkType.GUI, 3, 228, 0);

	private static final float ROWHEIGHT = .1f;

	/**
	 * Points on which materials can be placed.
	 * <p>
	 * They are indexed by slot.
	 */
	private final float[] xpoints;
	private final float[] ypoints;

	/**
	 * This is a mapping: {@link EMaterialType} -> order
	 */
	private final int[] order;

	/**
	 * This is a mapping: {@link EMaterialType} -> label position
	 */
	private final AnimateablePosition[] positions;

	private EMaterialType selected = null;

	@Deprecated
	public MaterialPriorityPanel() {
		this(new IStatistics() {
			@Override
			public int[] getMaterialPermutation() {
				int[] a = new int[EMaterialType.values().length];
				for (int i = 0; i < a.length; i++) {
					a[i] = i;
				}
				return a;
			}
		});
	}

	public MaterialPriorityPanel(IStatistics statistics) {
		int[] permutation = statistics.getMaterialPermutation();
		order = permutation.clone();
		xpoints = new float[order.length];
		ypoints = new float[order.length];
		positions = new AnimateablePosition[order.length];

		addRowPositions(0, true);
		addRowPositions(1, false);
		addRowPositions(2, true);
		addRowPositions(3, false);
		addRowPositions(4, true);

		for (int i = 0; i < order.length; i++) {
			int slotIndex = order[i];
			positions[i] =
			        new AnimateablePosition(xpoints[slotIndex],
			                ypoints[slotIndex]);
		}

		addChild(new Button(createChangeorderAction(-1000), ALL_UP_IMAGE,
		        ALL_UP_IMAGE, "all up"), .75f, .1f, .9f, .18f);
		addChild(new Button(createChangeorderAction(-1), UP_IMAGE, UP_IMAGE,
		        "one up"), .6f, .1f, .75f, .18f);
		addChild(new Button(createChangeorderAction(1), DOWN_IMAGE,
		        DOWN_IMAGE, "one down"), .45f, .1f, .6f, .18f);
		addChild(new Button(createChangeorderAction(1000), ALL_DOWN_IMAGE,
		        ALL_DOWN_IMAGE, "all down"), .3f, .1f, .45f, .18f);
	}

	private void addRowPositions(int rowIndex, boolean descent) {
		for (int column = 0; column < COLUMNS; column++) {
			int index = rowIndex * COLUMNS + column;
			if (index < xpoints.length) {
				xpoints[index] =
				        (float) (descent ? (column + 1) : (COLUMNS - column))
				                / (COLUMNS + 2);
				ypoints[index] = .8f - rowIndex * ROWHEIGHT -
				        (float) column / (COLUMNS - 1)
				                * (ROWHEIGHT - RELATIVE_BUTTONHEIGHT);
			}
		}
	}

	private Action createChangeorderAction(final int add) {
		return new ChangeOrderAction(add);
	}

	private class ChangeOrderAction extends ExecutableAction {
		private final int add;

		public ChangeOrderAction(int add) {
			this.add = add;
		}

		@Override
		public void execute() {
			if (selected != null) {
				reorder(selected, order[selected.ordinal()] + add);
			}
		}

	}

	protected void reorder(EMaterialType type, int desiredNewPosition) {
		int oldPos = order[type.ordinal()];
		int newPos =
		        Math.max(Math.min(desiredNewPosition, order.length - 1), 0);

		if (newPos == oldPos) {
			return;
		} else if (newPos > oldPos) {
			for (int i = 0; i < order.length; i++) {
				if (order[i] > oldPos && order[i] <= newPos) {
					setToPosition(i, order[i] - 1);
				}
			}
		} else {
			for (int i = 0; i < order.length; i++) {
				if (order[i] < oldPos && order[i] >= newPos) {
					setToPosition(i, order[i] + 1);
				}
			}
		}

		setToPosition(type.ordinal(), newPos);
	}

	private void setToPosition(int typeOrdinal, int newindex) {
		order[typeOrdinal] = newindex;
		positions[typeOrdinal]
		        .setPosition(xpoints[newindex], ypoints[newindex]);
	}

	@Override
	public void drawAt(GLDrawContext gl) {
		super.drawAt(gl);
		gl.color(1, 1, 1, 1);

		EMaterialType[] types = EMaterialType.values();
		gl.glPushMatrix();
		FloatRectangle position = getPosition();
		gl.glTranslatef(position.getMinX(), position.getMinY(), 0);
		gl.glScalef(position.getWidth(), position.getHeight(), 1);

		for (int i = 0; i < positions.length; i++) {
			drawButton(gl, positions[i].getX(), positions[i].getY(), types[i]);
		}
		gl.glPopMatrix();
	}

	private void drawButton(GLDrawContext gl, float x, float y,
	        EMaterialType materialType) {
		// TODO: add 1 or 2 for bigger gui
		int image = materialType.getGuiIconBase();
		int file = materialType.getGuiFile();
		Image iamgeLink =
		        ImageProvider.getInstance().getImage(
		                new OriginalImageLink(EImageLinkType.GUI, file, image, 0));
		iamgeLink.drawImageAtRect(gl, x, y, x + RELATIVE_BUTTONWIDTH, y
		        + RELATIVE_BUTTONHEIGHT);

		if (selected == materialType) {
			ImageProvider
			        .getInstance()
			        .getGuiImage(3, 339)
			        .drawImageAtRect(gl, x, y, x + RELATIVE_BUTTONWIDTH,
			                y + RELATIVE_BUTTONHEIGHT);
		}
	}

	@Override
	public UIPanel getPanel() {
		return this;
	}

	@Override
	public Action getAction(float relativex, float relativey) {
		Action action = super.getAction(relativex, relativey);
		if (action == null) {
			for (int i = 0; i < positions.length; i++) {
				float x = positions[i].getX();
				float y = positions[i].getY();
				if (relativex >= x && relativex < x + RELATIVE_BUTTONWIDTH
				        && relativey >= y
				        && relativey < y + RELATIVE_BUTTONWIDTH) {
					return new SelectMaterialAction(EMaterialType.values()[i]);
				}
			}
		}
		return action;
	}

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

	@Override
	public IContextListener getContextListener() {
		return null;
	}

	/**
	 * Sets the selected material type.
	 * 
	 * @param eMaterialType
	 */
	public void selectMaterial(EMaterialType eMaterialType) {
		selected = eMaterialType;
	}

	@Override
	public ESecondaryTabType getTabs() {
		return null;
	}

}
