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

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;

import jsettlers.main.android.R;
import jsettlers.main.android.core.ui.dialogs.EditTextDialog;
import jsettlers.main.android.mainmenu.factories.PresenterFactory;
import jsettlers.main.android.mainmenu.presenters.SettingsPresenter;
import jsettlers.main.android.mainmenu.views.SettingsView;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

@EActivity(R.layout.activity_settings)
public class SettingsActivity extends AppCompatActivity implements SettingsView, EditTextDialog.Listener {
	private static final int REQUEST_CODE_PLAYER_NAME = 10;

	@ViewById(R.id.text_view_player_name)
	TextView textViewPlayerName;

	@ViewById(R.id.toolbar)
	Toolbar toolbar;

	private SettingsPresenter presenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		presenter = PresenterFactory.createSettingsPresenter(this, this);
	}

	@AfterViews
	void configureToolbar() {
		setSupportActionBar(toolbar);
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
	}

	@AfterViews
	void bindPresenter() {
		presenter.bindView();
	}

	@Click(R.id.layout_player_name)
	void onClickPlayerNameLayout() {
		EditTextDialog.create(REQUEST_CODE_PLAYER_NAME, R.string.settings_player_name, R.string.settings_name, textViewPlayerName.getText()).show(getSupportFragmentManager(), null);
	}

	@OptionsItem(android.R.id.home)
	void homeSelected() {
		finish();
	}

	@Override
	public void setPlayerName(String playerName) {
		textViewPlayerName.setText(playerName);
	}

	@Override
	public void saveEditTextDialog(int requestCode, String text) {
		switch (requestCode) {
		case REQUEST_CODE_PLAYER_NAME:
			presenter.playerNameEdited(text);
			break;
		}
	}
}
