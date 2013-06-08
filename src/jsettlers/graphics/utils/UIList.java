package jsettlers.graphics.utils;

import go.graphics.GLDrawContext;

import java.util.List;

import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.OriginalImageLink;
import jsettlers.common.position.FloatRectangle;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ExecutableAction;
import jsettlers.graphics.map.draw.ImageProvider;

public class UIList<T> implements UIElement {
	private static final OriginalImageLink SCROLLBAR_TOP =
	        new OriginalImageLink(EImageLinkType.GUI, 2, 2, 0);
	private static final OriginalImageLink SCROLLBAR_MIDDLE =
	        new OriginalImageLink(EImageLinkType.GUI, 2, 3, 0);
	private static final OriginalImageLink SCROLLBAR_BOTTOM =
	        new OriginalImageLink(EImageLinkType.GUI, 2, 4, 0);

	private static final float RIGHTBORDER = .97f;
	private static final float EDGEPART = .02f;
	private static final float SLIDER_MIN_HEIGHT = 0.1f;
	private final float itemheight;
	private FloatRectangle position;

	private float listoffset = 0; // relative in UI space
	private List<? extends T> items;
	private final Object itemsMutex = new Object();
	private T activeItem;
	private final ListItemGenerator<T> generator;

	public interface ListItemGenerator<T> {
		UIListItem getItem(T item);
	}

	public UIList(List<? extends T> items, ListItemGenerator<T> generator,
	        float itemheight) {
		this.items = items;
		this.generator = generator;
		this.itemheight = itemheight;
		if (items.size() > 0) {
			activeItem = items.get(0);
		}
	}

	public void setItems(List<? extends T> list) {
		synchronized (itemsMutex) {
			this.items = list;
		}
	}

	@Override
	public void setPosition(FloatRectangle position) {
		this.position = position;
	}

	@Override
	public void drawAt(GLDrawContext gl) {
		synchronized (itemsMutex) {
			float totallistheigt = itemheight * items.size(); // relative
			if (listoffset < 0) {
				listoffset = 0;
			} else if (listoffset + 1 > totallistheigt) {
				listoffset = Math.max(0, totallistheigt - 1);
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
				// TODO: Cache
				UIListItem listItem = generator.getItem(item);
				// relative
				float itemtop = 1 - i * itemheight + listoffset;
				float itembottom = itemtop - itemheight;

				listItem.setHighlighted(activeItem == item);
				listItem.setPosition(new FloatRectangle(minX, minY + itembottom
				        * height, minX + RIGHTBORDER * width, minY + itemtop
				        * height));
				listItem.drawAt(gl);
			}

			float sliderHeight =
			        Math.min(1, Math.max(SLIDER_MIN_HEIGHT, 1 / totallistheigt));
			float slide =
			        (1 - sliderHeight) * listoffset / (totallistheigt - 1);
			float sliderMax = 1 - slide;

			float sliderMin = sliderMax - sliderHeight;
			float edgeHeight = height * EDGEPART; // bottom / top edge at side.

			float sliderMinY = sliderMin * height + minY;
			float sliderMaxY = sliderMax * height + minY;

			// side
			ImageProvider provider = ImageProvider.getInstance();
			gl.color(1, 1, 1, 1);
			provider.getImage(SCROLLBAR_TOP).drawImageAtRect(gl,
			        minX + width * RIGHTBORDER, sliderMaxY - edgeHeight,
			        minX + width, sliderMaxY);
			provider.getImage(SCROLLBAR_MIDDLE).drawImageAtRect(gl,
			        minX + width * RIGHTBORDER, sliderMinY + edgeHeight,
			        minX + width, sliderMaxY - edgeHeight);
			provider.getImage(SCROLLBAR_BOTTOM).drawImageAtRect(gl,
			        minX + width * RIGHTBORDER, sliderMinY, minX + width,
			        sliderMinY + edgeHeight);
		}
	}

	@Override
	public Action getAction(float relativex, float relativey) {
		synchronized (itemsMutex) {
			if (relativex < RIGHTBORDER) {
				float listy = 1 - relativey + listoffset;
				int itemIndex = (int) (listy / itemheight);
				if (itemIndex >= 0 && itemIndex < items.size()) {
					return new SelectAction(items.get(itemIndex));
				} else {
					return null;
				}
			} else {
				// relative to list height
				int destCenterItem = (int) ((1 - relativey) * items.size());
				int currentCenterItem = (int) (listoffset + .5f / itemheight);

				final int toScroll = destCenterItem - currentCenterItem;

				return new ExecutableAction() {
					@Override
					public void execute() {
						scrollBy(toScroll);
					}
				};
			}
		}
	}

	protected void scrollBy(int toScroll) {
		listoffset += itemheight * toScroll;
	}

	private class SelectAction extends ExecutableAction {
		private final T item;

		public SelectAction(T item) {
			this.item = item;
		}

		@Override
		public void execute() {
			// TODO: setActiveUIElement();
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

	@Override
	public void onAttach() {
	}

	@Override
	public void onDetach() {
	}
}
