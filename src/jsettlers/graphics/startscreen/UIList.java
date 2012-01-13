package jsettlers.graphics.startscreen;

import go.graphics.GLDrawContext;

import java.util.List;

import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.ImageLink;
import jsettlers.common.position.FloatRectangle;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.utils.UIElement;

public class UIList<T extends UIListItem> implements UIElement {
	private static final ImageLink SCROLLBAR_TOP = new ImageLink(
	        EImageLinkType.GUI, 2, 2, 0);
	private static final ImageLink SCROLLBAR_MIDDLE = new ImageLink(
	        EImageLinkType.GUI, 2, 3, 0);
	private static final ImageLink SCROLLBAR_BOTTOM = new ImageLink(
	        EImageLinkType.GUI, 2, 4, 0);

	private static final float RIGHTBORDER = .97f;
	private static final float EDGEPART = .02f;
	private final float itemheight;
	private FloatRectangle position;

	private float listoffset = 0; // relative
	private final List<T> items;
	private T activeItem;

	public UIList(List<T> items, float itemheight) {
		this.items = items;
		this.itemheight = itemheight;
		if (items.size() > 0) {
			activeItem = items.get(0);
		}
	}

	@Override
	public void setPosition(FloatRectangle position) {
		this.position = position;
	}

	@Override
	public void drawAt(GLDrawContext gl) {
		float totallistheigt = itemheight * items.size(); // relative
		if (listoffset < 0) {
			listoffset = 0;
		} else if (listoffset + 1 > totallistheigt) {
			listoffset = Math.min(0, totallistheigt);
		}

		float minY = position.getMinY();
		float minX = position.getMinX();

		float height = position.getHeight();
		float width = position.getWidth();

		int startindex = (int) (listoffset / itemheight);

		int itemsperpage = (int) (1 / itemheight);

		for (int i = startindex; i < startindex + itemsperpage
		        && i < items.size(); i++) {
			T item = items.get(i);
			// relative
			float itemtop = 1 - i * itemheight + listoffset;
			float itembottom = itemtop - itemheight;

			item.setHighlighted(activeItem == item);
			item.setPosition(new FloatRectangle(minX, minY + itembottom
			        * height, minX + RIGHTBORDER * width, minY + itemtop
			        * height));
			item.drawAt(gl);
		}

		// side
		ImageProvider provider = ImageProvider.getInstance();
		gl.color(1, 1, 1, 1);
		provider.getImage(SCROLLBAR_TOP).drawImageAtRect(gl,
		        minX + width * RIGHTBORDER, minY + height * (1 - EDGEPART),
		        minX + width, minY + height);
		provider.getImage(SCROLLBAR_MIDDLE).drawImageAtRect(gl,
		        minX + width * RIGHTBORDER, minY + height * EDGEPART,
		        minX + width, minY + height * (1 - EDGEPART));
		provider.getImage(SCROLLBAR_BOTTOM).drawImageAtRect(gl,
		        minX + width * RIGHTBORDER, minY,
		        minX + width, minY + EDGEPART * height);
	}

	@Override
	public Action getAction(float relativex, float relativey) {
		if (relativex < RIGHTBORDER) {
			float listy = 1 - relativey + listoffset;
			int itemIndex = (int) (listy / itemheight);
			if (itemIndex >= 0 && itemIndex < items.size()) {
				return new SelectAction(items.get(itemIndex));
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	private class SelectAction extends ExecutableAction {
		private final T item;

		public SelectAction(T item) {
			this.item = item;
		}

		@Override
		public void execute() {
			setActiveItem(item);
		}
	}

	@Override
	public String getDescription(float relativex, float relativey) {
		return null;
	}

	public void setActiveItem(T activeItem) {
		this.activeItem = activeItem;
	}

	public T getActiveItem() {
		return activeItem;
	}
}
