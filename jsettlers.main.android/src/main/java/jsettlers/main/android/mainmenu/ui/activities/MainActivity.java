package jsettlers.main.android.mainmenu.ui.activities;

import jsettlers.main.android.R;
import jsettlers.main.android.gameplay.ui.activities.GameActivity;
import jsettlers.main.android.mainmenu.ui.fragments.MainMenuFragment;
import jsettlers.main.android.mainmenu.ui.fragments.JoinMultiPlayerPickerFragment;
import jsettlers.main.android.mainmenu.ui.fragments.LoadSinglePlayerPickerFragment;
import jsettlers.main.android.mainmenu.ui.fragments.NewMultiPlayerPickerFragment;
import jsettlers.main.android.mainmenu.ui.fragments.NewSinglePlayerPickerFragment;
import jsettlers.main.android.mainmenu.ui.fragments.JoinMultiPlayerSetupFragment;
import jsettlers.main.android.mainmenu.ui.fragments.NewMultiPlayerSetupFragment;
import jsettlers.main.android.mainmenu.ui.fragments.NewSinglePlayerSetupFragment;
import jsettlers.main.android.mainmenu.navigation.Actions;
import jsettlers.main.android.mainmenu.navigation.MainMenuNavigator;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements MainMenuNavigator {

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
		startActivity(intent);
		finish();
	}

	@Override
	public void showGame() {
		Intent intent = new Intent(this, GameActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	public void popToMenuRoot() {
		getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
	}

	private void setUpButton() {
		boolean isAtRootOfBackStack = getSupportFragmentManager().getBackStackEntryCount() == 0;
		getSupportActionBar().setDisplayHomeAsUpEnabled(!isAtRootOfBackStack);
	}
}
