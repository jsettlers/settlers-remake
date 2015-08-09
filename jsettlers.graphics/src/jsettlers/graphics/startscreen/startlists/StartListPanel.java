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
package jsettlers.graphics.startscreen.startlists;

import java.util.Collections;
import java.util.List;

import jsettlers.common.images.ImageLink;
import jsettlers.common.utils.collections.ChangingList;
import jsettlers.common.utils.collections.IChangingListListener;
import jsettlers.graphics.action.ExecutableAction;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.ui.LabeledButton;
import jsettlers.graphics.ui.UIList;
import jsettlers.graphics.ui.UIList.ListItemGenerator;
import jsettlers.graphics.ui.UIPanel;

/**
 * A side panel of the start screen. TODO: Do not reload the list each time it changes, and only use it if we are in foreground.
 *
 * @author michael
 */
public abstract class StartListPanel<T> extends UIPanel implements
		IChangingListListener<T>, ListItemGenerator<T> {

	// private static final ImageLink LIST_BACKGROUND = new DirectImageLink("startscreen.0");
	private static final ImageLink LIST_BACKGROUND = null;
	private final ChangingList<T> list;
	private final UIList<T> uiList;
	private final LabeledButton startbutton;

	public StartListPanel(ChangingList<T> list) {
		this.list = list;
		uiList = new UIList<T>(Collections.<T> emptyList(), this, .1f);
		UIPanel listBg = new UIPanel();
		listBg.addChild(uiList, 1f / 345, 1f / 406, 1 - 4f / 345, 1 - 5f / 406);
		listBg.setBackground(LIST_BACKGROUND);
		this.addChild(listBg, 0, .15f, 1, 1);

		startbutton =
				new LabeledButton(
						Labels.getString(getSubmitTextId()),
						new ExecutableAction() {
							@Override
							public void execute() {
								onSubmitAction();
							}
						}
				);
		this.addChild(startbutton, .3f, 0, 1, .1f);

		if (list != null) {
			list.setListener(this);
			listChanged(list);
		}
	}

	protected abstract void onSubmitAction();

	protected abstract String getSubmitTextId();

	protected T getActiveListItem() {
		return uiList.getActiveItem();
	}

	@Override
	public void listChanged(ChangingList<T> list) {
		List<? extends T> items = list.getItems();
		startbutton.setEnabled(items.size() > 0);
		uiList.setItems(items);
	}

	protected ChangingList<T> getList() {
		return list;
	}

	@Override
	public void onAttach() {
		super.onAttach();
		ChangingList<T> list = getList();
		list.setListener(this);
		listChanged(list);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		getList().setListener(null);
	}
}
