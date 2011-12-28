package jsettlers.graphics.startscreen;

import go.graphics.GLDrawContext;
import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;
import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.ImageLink;
import jsettlers.common.position.FloatRectangle;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.image.SingleImage;
import jsettlers.graphics.map.draw.ImageProvider;

public class GenericListItem implements UIListItem {

	private FloatRectangle position;
	private boolean highlight;
	private final String title;
	private final String description;

	private static final ImageLink SELECT_MARKER = new ImageLink(
	        EImageLinkType.GUI, 3, 330, 0);

	public GenericListItem(String title, String description) {
		this.title = title;
		this.description = description;

	}

	@Override
	public void setPosition(FloatRectangle position) {
		this.position = position;
	}

	@Override
	public void drawAt(GLDrawContext gl) {
		float minX = position.getMinX();
		if (highlight) {
			SingleImage image = ImageProvider.getInstance().getImage(SELECT_MARKER);
			image.drawImageAtRect(gl, minX, position.getMinY(),
			        position.getMaxX(), position.getMaxY());
		}
		TextDrawer textdrawer = gl.getTextDrawer(EFontSize.NORMAL);

		float height = position.getHeight();
		textdrawer.drawString(.1f, minX + .6f * height, title);
		textdrawer.drawString(.1f, minX + .1f * height, description);
	}

	@Override
	public Action getAction(float relativex, float relativey) {
		return null;
	}

	@Override
	public String getDescription(float relativex, float relativey) {
		return description;
	}

	@Override
	public void setHighlighted(boolean highlight) {
		this.highlight = highlight;
	}

}
