package jsettlers.main.android.activities;

import jsettlers.common.menu.IMapDefinition;
import jsettlers.common.utils.collections.ChangingList;
import jsettlers.main.StartScreenConnector;
import jsettlers.main.android.R;
import jsettlers.main.android.fragmentsnew.MainMenuFragment;
import jsettlers.main.android.fragmentsnew.MapPickerFragment;
import jsettlers.main.android.navigation.MainMenuNavigator;
import jsettlers.main.android.providers.GameStarter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements MainMenuNavigator, GameStarter {
	private StartScreenConnector startScreenConnector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState != null)
			return;

		getSupportFragmentManager().beginTransaction()
				.add(R.id.frame_layout, MainMenuFragment.newInstance())
				.commit();
	}

	@Override
	public void showNewSinglePlayer() {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.frame_layout, MapPickerFragment.newInstance())
				.addToBackStack(null)
				.commit();
	}

	@Override
	public ChangingList<? extends IMapDefinition> getSinglePlayerMaps() {
		return getStartScreenConnector().getSingleplayerMaps();
	}

	private StartScreenConnector getStartScreenConnector() {
		if (startScreenConnector == null) {
			startScreenConnector = new StartScreenConnector();
		}
		return startScreenConnector;
	}
}
