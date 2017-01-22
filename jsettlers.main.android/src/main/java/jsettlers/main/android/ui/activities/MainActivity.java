package jsettlers.main.android.ui.activities;

import jsettlers.main.android.R;
import jsettlers.main.android.ui.fragments.mainmenu.MainMenuFragment;
import jsettlers.main.android.ui.fragments.mainmenu.mappicker.JoinMultiPlayerPickerFragment;
import jsettlers.main.android.ui.fragments.mainmenu.mappicker.LoadSinglePlayerPickerFragment;
import jsettlers.main.android.ui.fragments.mainmenu.mappicker.NewMultiPlayerPickerFragment;
import jsettlers.main.android.ui.fragments.mainmenu.mappicker.NewSinglePlayerPickerFragment;
import jsettlers.main.android.ui.fragments.mainmenu.mapsetup.JoinMultiPlayerSetupFragment;
import jsettlers.main.android.ui.fragments.mainmenu.mapsetup.NewMultiPlayerSetupFragment;
import jsettlers.main.android.ui.fragments.mainmenu.mapsetup.NewSinglePlayerSetupFragment;
import jsettlers.main.android.ui.navigation.Actions;
import jsettlers.main.android.ui.navigation.MainMenuNavigator;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements MainMenuNavigator {
	private static final int REQUEST_CODE_GAME = 10;

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
	public void showNewSinglePlayerSetup() {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.frame_layout, NewSinglePlayerSetupFragment.create())
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
	public void showJoinMultiPlayerSetup() {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.frame_layout, JoinMultiPlayerSetupFragment.create())
				.addToBackStack(null)
				.commit();
	}

	@Override
	public void showNewMultiPlayerSetup() {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.frame_layout, NewMultiPlayerSetupFragment.create())
				.addToBackStack(null)
				.commit();
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
}
