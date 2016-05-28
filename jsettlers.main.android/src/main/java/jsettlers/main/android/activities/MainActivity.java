package jsettlers.main.android.activities;

import jsettlers.common.menu.IMapDefinition;
import jsettlers.common.utils.collections.ChangingList;
import jsettlers.main.StartScreenConnector;
import jsettlers.main.android.MainApplication;
import jsettlers.main.android.R;
import jsettlers.main.android.fragmentsnew.MainMenuFragment;
import jsettlers.main.android.fragmentsnew.MapPickerFragment;
import jsettlers.main.android.fragmentsnew.NewSinglePlayerFragment;
import jsettlers.main.android.navigation.MainMenuNavigator;
import jsettlers.main.android.providers.GameStarter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements MainMenuNavigator, GameStarter {
	private static final String TAG_MAP_PICKER = "map_picker";
	private static final int REQUEST_CODE_GAME = 10;

	private StartScreenConnector startScreenConnector;

	private MainApplication mainApplication;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mainApplication = (MainApplication) getApplication();

		if (savedInstanceState != null)
			return;

		getSupportFragmentManager().beginTransaction()
				.add(R.id.frame_layout, MainMenuFragment.newInstance())
				.commit();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_CODE_GAME:
			getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
			break;
		}
	}

	/**
	 * GameStarter
	 */
	@Override
	public ChangingList<? extends IMapDefinition> getSinglePlayerMaps() {
		return getStartScreenConnector().getSingleplayerMaps();
	}

	@Override
	public IMapDefinition getSelectedMap() {
		MapPickerFragment mapPickerFragment = (MapPickerFragment) getSupportFragmentManager().findFragmentByTag(TAG_MAP_PICKER);
		return mapPickerFragment.getSelectedMap();
	}

	@Override
	public void startGame(IMapDefinition map) {
		mainApplication.startSinglePlayerGame(map);
	}

	/**
	 * MainMenu Navigation
	 */
	@Override
	public void showNewSinglePlayerMapPicker() {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.frame_layout, MapPickerFragment.newInstance(), TAG_MAP_PICKER)
				.addToBackStack(null)
				.commit();
	}

	@Override
	public void showNewSinglePlayerSetup() {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.frame_layout, NewSinglePlayerFragment.newInstance())
				.addToBackStack(null)
				.commit();
	}

	@Override
	public void showGame() {
		Intent intent = new Intent(this, GameActivity.class);
		startActivityForResult(intent, REQUEST_CODE_GAME);
	}

	private StartScreenConnector getStartScreenConnector() {
		if (startScreenConnector == null) {
			startScreenConnector = new StartScreenConnector();
		}
		return startScreenConnector;
	}
}
