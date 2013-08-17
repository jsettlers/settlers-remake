package jsettlers.graphics.startscreen.startlists;

import java.util.Collections;

import jsettlers.graphics.action.Action;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.map.controls.original.panel.content.UILabeledButton;
import jsettlers.graphics.startscreen.interfaces.IChangingList;
import jsettlers.graphics.startscreen.interfaces.IChangingListListener;
import jsettlers.graphics.utils.UIList;
import jsettlers.graphics.utils.UIList.ListItemGenerator;
import jsettlers.graphics.utils.UIPanel;

/**
 * A side panel of the start screen. TODO: Do not reload the list each time it
 * changes, and only use it if we are in foreground.
 * 
 * @author michael
 */
public abstract class StartListPanel<T> extends
        UIPanel implements IChangingListListener<T>, ListItemGenerator<T> {

	private final IChangingList<T> list;
	private final UIList<T> uiList;

	public StartListPanel(IChangingList<T> list) {
		this.list = list;
		uiList = new UIList<T>(Collections.<T>emptyList(), this, .1f);
		this.addChild(uiList, 0, .15f, 1, 1);

		// start button
		UILabeledButton startbutton =
		        new UILabeledButton(Labels.getString(getSubmitTextId()), getSubmitAction());
		this.addChild(startbutton, .3f, 0, 1, .1f);
	}

	protected abstract Action getSubmitAction();
	
	protected abstract String getSubmitTextId();

	protected T getActiveListItem() {
		return uiList.getActiveItem();
	}

	@Override
	public void listChanged(IChangingList<T> list) {
		uiList.setItems(list.getItems());
	}
	
	protected IChangingList<T> getList() {
		return list;
	}
	
	@Override
	public void onAttach() {
		IChangingList<T> list2 = getList();
		listChanged(list2);
		list2.setListener(this);
	}

	@Override
	public void onDetach() {
		getList().setListener(null);
	};
}
