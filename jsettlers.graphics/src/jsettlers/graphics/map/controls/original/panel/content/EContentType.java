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

import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ActionFireable;
import jsettlers.graphics.ui.UIPanel;

/**
 * This are the main content types
 * 
 * @author michael
 */
public enum EContentType implements IContentProvider {
	EMPTY(ESecondaryTabType.NONE, null),

	BUILD_NORMAL(ESecondaryTabType.BUILD, BuildingBuildContent.getNormal()),
	BUILD_SOCIAL(ESecondaryTabType.BUILD, BuildingBuildContent.getSocial()),
	BUILD_MILITARY(ESecondaryTabType.BUILD, BuildingBuildContent.getMilitary()),
	BUILD_FOOD(ESecondaryTabType.BUILD, BuildingBuildContent.getFood()),

	STOCK(ESecondaryTabType.GOODS, null),
	TOOLS(ESecondaryTabType.GOODS, null),
	GOODS_SPREAD(ESecondaryTabType.GOODS, null),
	GOODS_TRANSPORT(ESecondaryTabType.GOODS, new MaterialPriorityContent()),

	SETTLERSTATISTIC(ESecondaryTabType.SETTLERS, null),
	PROFESSION(ESecondaryTabType.SETTLERS, null),
	WARRIORS(ESecondaryTabType.SETTLERS, null),
	PRODUCTION(ESecondaryTabType.SETTLERS, null);

	private final ESecondaryTabType tabs;
	private final IContentFactory factory;

	private EContentType(ESecondaryTabType tabs, IContentFactory factory) {
		this.tabs = tabs;
		this.factory = factory;

	}

	@Override
	public UIPanel getPanel() {
		if (factory == null) {
			return new UIPanel();
		} else {
			return factory.getPanel();
		}
	}

	@Override
	public ESecondaryTabType getTabs() {
		return tabs;
	}

	@Override
	public void showMapPosition(ShortPoint2D pos, IGraphicsGrid grid) {
		if (factory != null) {
			factory.showMapPosition(pos, grid);
		}
	}

	@Override
	public Action catchAction(Action action) {
		if (factory != null) {
			return factory.catchAction(action);
		}
		return action;
	}

	@Override
	public void contentHiding(ActionFireable actionFireable) {
		if (factory != null) {
			factory.contentHiding(actionFireable);
		}
	}

	@Override
	public void contentShowing(ActionFireable actionFireable) {
		if (factory != null) {
			factory.contentShowing(actionFireable);
		}
	}
}
