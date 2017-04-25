package jsettlers.main.android.mainmenu.ui.activities;

import jsettlers.main.android.FullScreenAppCompatActivity;
import jsettlers.main.android.R;
import jsettlers.main.android.core.ui.dialogs.EditTextDialog;
import jsettlers.main.android.mainmenu.factories.PresenterFactory;
import jsettlers.main.android.mainmenu.presenters.SettingsPresenter;
import jsettlers.main.android.mainmenu.views.SettingsView;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

public class SettingsActivity extends FullScreenAppCompatActivity implements SettingsView, EditTextDialog.Listener {
	private static final int REQUEST_CODE_PLAYER_NAME = 10;

	private SettingsPresenter presenter;

	private TextView textViewPlayerName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		textViewPlayerName = (TextView) findViewById(R.id.text_view_player_name);

		presenter = PresenterFactory.createSettingsPresenter(this, this);
		presenter.bindView();

		findViewById(R.id.layout_player_name).setOnClickListener(view -> {
			EditTextDialog
					.create(REQUEST_CODE_PLAYER_NAME, R.string.settings_player_name, R.string.settings_name, textViewPlayerName.getText())
					.show(getSupportFragmentManager(), null);
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void setPlayerName(String playerName) {
		textViewPlayerName.setText(playerName);
	}

	@Override
	public void saveEditTextDialog(int requestCode, CharSequence text) {
		switch (requestCode) {
		case REQUEST_CODE_PLAYER_NAME:
			presenter.playerNameEdited(text.toString());
			break;
		}
	}
}
