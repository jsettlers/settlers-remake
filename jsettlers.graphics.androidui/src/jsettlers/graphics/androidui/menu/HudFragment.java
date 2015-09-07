package jsettlers.graphics.androidui.menu;

import jsettlers.graphics.androidui.NavigationView;
import jsettlers.graphics.androidui.R;
import jsettlers.graphics.map.ScreenPosition;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class HudFragment extends AndroidMenu {
	private final AndroidMenu buildMenu;

	public HudFragment(AndroidMenuPutable putable) {
		super(putable);
		buildMenu = new BuildMenu(putable);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.hud, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		view.findViewById(R.id.button_build).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getPutable().showMenuFragment(buildMenu);
			}
		});

		ScreenPosition screen = getPutable().getMapContext().getScreen();
		((NavigationView) view.findViewById(R.id.navigate)).setScreenPositionToInfluence(screen);

		super.onViewCreated(view, savedInstanceState);
	}
}
