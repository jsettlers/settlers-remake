package jsettlers.graphics.androidui.menu;

import go.graphics.UIPoint;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.androidui.R;
import android.view.View;
import android.widget.Button;

public class PauseMenu extends AndroidMobileMenu {

	public PauseMenu(AndroidMenuPutable androidMenuPutable) {
		super(androidMenuPutable, R.layout.pause);
	}

	@Override
	protected void fillLayout(View menu) {
		Button quit = (Button) menu.findViewById(R.id.pause_quit);
		quit.setOnClickListener(generateActionListener(new Action(
		        EActionType.EXIT), true));

		Button resume = (Button) menu.findViewById(R.id.pause_resume);
		resume.setOnClickListener(generateActionListener(new Action(
		        EActionType.SPEED_UNSET_PAUSE), true));
	}

	@Override
	public Action getActionFor(UIPoint position) {
		return new Action(EActionType.SPEED_UNSET_PAUSE);
	}

}
