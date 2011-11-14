package jsettlers.graphics.map.controls.original.panel.content;

import go.graphics.GLDrawContext;
import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;
import jsettlers.common.Color;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.graphics.image.Image;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.map.draw.settlerimages.SettlerImageMap;
import jsettlers.graphics.utils.UIPanel;

public class SelectionRow extends UIPanel {

	private final EMovableType type;
	private final int count;

	/**
	 * Creates a new row in the selection view
	 * 
	 * @param type
	 *            The type of the movables
	 * @param count
	 *            How many of them are selected.
	 */
	public SelectionRow(EMovableType type, int count) {
		this.type = type;
		this.count = count;
	}

	@Override
	public void drawAt(GLDrawContext gl) {
		float width = getPosition().getWidth();
		Image image =
		        SettlerImageMap.getInstance().getImageForSettler(type,
		                EAction.NO_ACTION, EMaterialType.NO_MATERIAL,
		                EDirection.SOUTH_EAST, 0);

		Color color = getColor();
		float bottomy = getPosition()
		        .getMinY() + getPosition().getHeight() / 4;
		float left = getPosition().getMinX();
		float imagex = left + width / 20;
		image.drawAt(gl, imagex, bottomy, color);
		
		TextDrawer drawer = gl.getTextDrawer(EFontSize.NORMAL);
		
		drawer.drawString(left + width / 5, getPosition().getMinY() + getPosition().getHeight() * .75f, "" + count);
		drawer.drawString(left + width / 5, bottomy, Labels.getName(type));

	}

	private Color getColor() {
		return count != 0 ? Color.RED : Color.BLACK;
	}
}
