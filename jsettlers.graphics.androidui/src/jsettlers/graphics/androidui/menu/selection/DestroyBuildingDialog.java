package jsettlers.graphics.androidui.menu.selection;

import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.androidui.R;
import jsettlers.graphics.androidui.menu.AndroidMenuPutable;
import jsettlers.graphics.androidui.menu.Dialog;

public class DestroyBuildingDialog extends Dialog {

	public DestroyBuildingDialog(AndroidMenuPutable puttable) {
		super(puttable);
	}

	@Override
	protected int getMessageId() {
		return R.string.building_destroy_text;
	}

	@Override
	protected int getOkId() {
		return R.string.building_destroy_ok;
	}

	@Override
	protected int getAbortId() {
		return R.string.building_destroy_abort;
	}

	@Override
	protected void okClicked() {
		getActionFireable().fireAction(new Action(EActionType.DESTROY));
		getPutable().hideMenu();
	}

}
