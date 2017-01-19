package jsettlers.main.android.ui.activities;

import jsettlers.common.menu.IMapDefinition;
import jsettlers.common.utils.collections.ChangingList;
import jsettlers.main.StartScreenConnector;
import jsettlers.main.android.GameService;
import jsettlers.main.android.R;
import jsettlers.main.android.ui.fragments.mainmenu.MainMenuFragment;
import jsettlers.main.android.ui.fragments.mainmenu.MapPickerFragment;
import jsettlers.main.android.ui.fragments.mainmenu.NewSinglePlayerFragment;
import jsettlers.main.android.ui.fragments.mainmenu.NewSinglePlayerPickerFragment;
import jsettlers.main.android.ui.navigation.Actions;
import jsettlers.main.android.ui.navigation.MainMenuNavigator;
import jsettlers.main.android.providers.GameStarter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements MainMenuNavigator, GameStarter {
	private static final String TAG_MAP_PICKER = "map_picker";
	private static final int REQUEST_CODE_GAME = 10;

	private StartScreenConnector startScreenConnector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getSupportFragmentManager().addOnBackStackChangedListener(this::onBackStackChanged);

		if (savedInstanceState != null)
			return;

		getSupportFragmentManager().beginTransaction()
				.add(R.id.frame_layout, MainMenuFragment.newInstance())
				.commit();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				getSupportFragmentManager().popBackStack();
				return true;
		}
		return super.onOptionsItemSelected(item);
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
	 * GameStarter implementation
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
	public void startGame() {
		startService(new Intent(this, GameService.class));
		bindService(new Intent(this, GameService.class), serviceConnection, Context.BIND_AUTO_CREATE);
	}

	/**
	 * MainMenu Navigation
	 */
	@Override
	public void showNewSinglePlayerMapPicker() {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.frame_layout, NewSinglePlayerPickerFragment.newInstance(), TAG_MAP_PICKER)
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
	public void resumeGame() {
		Intent intent = new Intent(this, GameActivity.class);
		intent.setAction(Actions.RESUME_GAME);
		startActivityForResult(intent, REQUEST_CODE_GAME);
	}


	private void showGame() {
		Intent intent = new Intent(this, GameActivity.class);
		startActivityForResult(intent, REQUEST_CODE_GAME);
	}

	private StartScreenConnector getStartScreenConnector() {
		if (startScreenConnector == null) {
			startScreenConnector = new StartScreenConnector();
		}
		return startScreenConnector;
	}

	private ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder binder) {
			GameService.GameBinder gameBinder = (GameService.GameBinder) binder;
			GameService gameService = gameBinder.getService();

			gameService.startSinglePlayerGame(getSelectedMap());
			showGame();

			unbindService(serviceConnection);
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
		}
	};

	private void onBackStackChanged() {
		boolean isAtRootOfBackStack = getSupportFragmentManager().getBackStackEntryCount() == 0;
		getSupportActionBar().setDisplayHomeAsUpEnabled(!isAtRootOfBackStack);
	}
}
