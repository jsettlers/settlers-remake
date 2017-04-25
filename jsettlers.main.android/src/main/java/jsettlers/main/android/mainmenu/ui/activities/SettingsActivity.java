/*
 * Copyright (c) 2017
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

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
