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
package jsettlers.graphics.ui;

import go.graphics.GLDrawContext;

import java.util.List;

import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.OriginalImageLink;
import jsettlers.common.position.FloatRectangle;
import jsettlers.common.action.Action;
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

	public UIList(List<? extends T> items, ListItemGenerator<T> generator, float itemheight) {
		setItems(items);
		this.generator = generator;
		this.itemheight = itemheight;
	}

	public void setItems(List<? extends T> list) {
		synchronized (itemsMutex) {
			this.items = list;

			if (!items.contains(activeItem)) {
				if (items.size() > 0) {
					activeItem = items.get(0);
				} else {
					activeItem = null;
				}
			}
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
			provider.getImage(SCROLLBAR_TOP).drawImageAtRect(gl,
					minX + width * RIGHTBORDER, sliderMaxY - edgeHeight,
					 width, sliderMaxY);
			provider.getImage(SCROLLBAR_MIDDLE).drawImageAtRect(gl,
					minX + width * RIGHTBORDER, edgeHeight,
					 width, sliderMaxY - edgeHeight);
			provider.getImage(SCROLLBAR_BOTTOM).drawImageAtRect(gl,
					minX + width * RIGHTBORDER, sliderMinY, width,
					 edgeHeight);
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
				final float halfNumberOfDisplayedItems = (int) (0.5f / itemheight);
				final int destinationOffset = (int) ((1 - relativey) * items.size()
						- halfNumberOfDisplayedItems); // subtract this to get the center of the scrollbar where the player klicked.

				return new ExecutableAction() {
					@Override
					public void execute() {
						scrollBy(destinationOffset);
					}
				};
			}
		}
	}

	protected void scrollBy(int offset) {
		listoffset = itemheight * offset;
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
		synchronized (itemsMutex) {
			if (items.contains(activeItem)) {
				this.activeItem = activeItem;
			}
		}
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
