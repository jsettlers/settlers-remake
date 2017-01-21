package jsettlers.main.android.ui.activities;

import jsettlers.common.menu.IMapDefinition;
import jsettlers.main.StartScreenConnector;
import jsettlers.main.android.R;
import jsettlers.main.android.ui.fragments.mainmenu.JoinMultiPlayerPickerFragment;
import jsettlers.main.android.ui.fragments.mainmenu.LoadSinglePlayerPickerFragment;
import jsettlers.main.android.ui.fragments.mainmenu.MainMenuFragment;
import jsettlers.main.android.ui.fragments.mainmenu.MapSetupFragment;
import jsettlers.main.android.ui.fragments.mainmenu.NewMultiPlayerPickerFragment;
import jsettlers.main.android.ui.fragments.mainmenu.NewSinglePlayerPickerFragment;
import jsettlers.main.android.ui.navigation.Actions;
import jsettlers.main.android.ui.navigation.MainMenuNavigator;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements MainMenuNavigator {//}, GameStarter {
	private static final int REQUEST_CODE_GAME = 10;

	//private StartScreenConnector startScreenConnector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getSupportFragmentManager().addOnBackStackChangedListener(this::setUpButton);

		if (savedInstanceState != null)
			return;

		getSupportFragmentManager().beginTransaction()
				.add(R.id.frame_layout, MainMenuFragment.newInstance())
				.commit();
	}

	@Override
	protected void onPostCreate(@Nullable Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		setUpButton();
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

//	/**
//	 * GameStarter implementation
//	 */
//	@Override
//	public StartScreenConnector getStartScreenConnector() {
//		if (startScreenConnector == null) {
//			startScreenConnector = new StartScreenConnector();
//		}
//		return startScreenConnector;
//	}
//
//	@Override
//	public void startSinglePlayerGame(IMapDefinition mapDefinition) {
//		startService(new Intent(this, GameService.class));
//		bindService(new Intent(this, GameService.class), new StartGameConnection() {
//			@Override
//			protected void startGame(GameService gameService) {
//				gameService.startSinglePlayerGame(mapDefinition);
//			}
//		}, Context.BIND_AUTO_CREATE);
//	}
//
//	@Override
//	public void loadSinglePlayerGame(IMapDefinition mapDefinition) {
//		startService(new Intent(this, GameService.class));
//		bindService(new Intent(this, GameService.class), new StartGameConnection() {
//			@Override
//			protected void startGame(GameService gameService) {
//				gameService.loadSinglePlayerGame(mapDefinition);
//			}
//		}, Context.BIND_AUTO_CREATE);
//	}

	/**
	 * MainMenu Navigation
	 */
	@Override
	public void showNewSinglePlayerPicker() {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.frame_layout, NewSinglePlayerPickerFragment.newInstance())
				.addToBackStack(null)
				.commit();
	}

	@Override
	public void showLoadSinglePlayerPicker() {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.frame_layout, LoadSinglePlayerPickerFragment.newInstance())
				.addToBackStack(null)
				.commit();
	}

	@Override
	public void showNewSinglePlayerSetup(IMapDefinition mapDefinition) {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.frame_layout, MapSetupFragment.createNewSinglePlayerSetupFragment(mapDefinition))
				.addToBackStack(null)
				.commit();
	}

	@Override
	public void showNewMultiPlayerPicker() {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.frame_layout, NewMultiPlayerPickerFragment.newInstance())
				.addToBackStack(null)
				.commit();
	}

	@Override
	public void showJoinMultiPlayerPicker() {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.frame_layout, JoinMultiPlayerPickerFragment.create())
				.addToBackStack(null)
				.commit();
	}

	@Override
	public void showNewMultiPlayerSetup(IMapDefinition mapDefinition) {
	}

	@Override
	public void resumeGame() {
		Intent intent = new Intent(this, GameActivity.class);
		intent.setAction(Actions.RESUME_GAME);
		startActivityForResult(intent, REQUEST_CODE_GAME);
	}

	@Override
	public void showGame() {
		Intent intent = new Intent(this, GameActivity.class);
		startActivityForResult(intent, REQUEST_CODE_GAME);
	}




	
	private void setUpButton() {
		boolean isAtRootOfBackStack = getSupportFragmentManager().getBackStackEntryCount() == 0;
		getSupportActionBar().setDisplayHomeAsUpEnabled(!isAtRootOfBackStack);
	}

//	private abstract class StartGameConnection implements ServiceConnection {
//		protected abstract void startGame(GameService gameService);
//
//		@Override
//		public void onServiceConnected(ComponentName className, IBinder binder) {
//			GameService.GameBinder gameBinder = (GameService.GameBinder) binder;
//			GameService gameService = gameBinder.getService();
//
//			startGame(gameService);
//			showGame();
//
//			unbindService(this);
//		}
//
//		@Override
//		public void onServiceDisconnected(ComponentName arg0) {
//		}
//	}
}
