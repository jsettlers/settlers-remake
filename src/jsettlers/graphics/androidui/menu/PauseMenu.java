package jsettlers.graphics.androidui.menu;

import go.graphics.UIPoint;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.androidui.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class PauseMenu extends AndroidMenu {

	public PauseMenu(AndroidMenuPutable androidMenuPutable) {
		super(androidMenuPutable);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
		return inflater.inflate(R.layout.pause, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		Button quit = (Button) view.findViewById(R.id.pause_quit);
		quit.setOnClickListener(generateActionListener(new Action(
		        EActionType.EXIT), true));

		Button resume = (Button) view.findViewById(R.id.pause_resume);
		resume.setOnClickListener(generateActionListener(new Action(
		        EActionType.SPEED_UNSET_PAUSE), true));
	}

	 @Override
	 public Action getActionFor(UIPoint position) {
	 return new Action(EActionType.SPEED_UNSET_PAUSE);
	 }

}
