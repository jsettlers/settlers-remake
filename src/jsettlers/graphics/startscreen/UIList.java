package jsettlers.graphics.startscreen;

import go.graphics.GLDrawContext;

import java.util.List;

import jsettlers.common.position.FloatRectangle;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.utils.UIElement;

public class UIList<T extends UIListItem> implements UIElement {
	private static final float RIGHTBORDER = .9f;
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
			listoffset = Math.max(0, totallistheigt);
		}

		float height = position.getHeight();

		int startindex = (int) (listoffset / itemheight);

		int itemsperpage = (int) (height / itemheight);

		for (int i = startindex; i < startindex + itemsperpage && i < items.size(); i++) {
			T item = items.get(i);
			float itemtop = 1 - i * itemheight + listoffset;
			float itembottom = itemtop - itemheight;

			gl.color(1, 0, 0, 1);
			gl.fillQuad(0, itemtop * height, position.getMaxX(), itembottom
			        * height);

			item.setHighlighted(activeItem == item);
			item.setPosition(new FloatRectangle(0, itembottom * height,
			        RIGHTBORDER * position.getWidth(), itemtop * height));
			item.drawAt(gl);
		}
	}

	@Override
	public Action getAction(float relativex, float relativey) {
		if (relativex < RIGHTBORDER) {
			float listy = relativey + listoffset;
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
