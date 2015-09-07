/*******************************************************************************
 * Copyright (c) 2015
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
 *******************************************************************************/
package jsettlers.main.android;

import go.graphics.android.AndroidSoundPlayer;
import go.graphics.android.GOSurfaceView;
import go.graphics.android.IContextDestroyedListener;
import go.graphics.area.Area;
import go.graphics.region.Region;

import java.io.File;

import jsettlers.graphics.androidui.MobileControls;
import jsettlers.graphics.map.MapContent;
import jsettlers.graphics.map.MapInterfaceConnector;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.startscreen.interfaces.FakeMapGame;
import jsettlers.graphics.startscreen.interfaces.IGameExitListener;
import jsettlers.graphics.startscreen.interfaces.IMultiplayerConnector;
import jsettlers.graphics.startscreen.interfaces.IStartedGame;
import jsettlers.graphics.startscreen.interfaces.Player;
import jsettlers.main.StartScreenConnector;
import jsettlers.main.android.bg.BgControls;
import jsettlers.main.android.bg.BgMap;
import jsettlers.main.android.fragments.GameCommandFragment;
import jsettlers.main.android.fragments.JsettlersFragment;
import jsettlers.main.android.fragments.StartScreenFragment;
import jsettlers.main.android.resources.scanner.FileChoserFragment;
import jsettlers.main.android.resources.scanner.ResourceLocationScanner;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class JsettlersActivity extends Activity implements IGameExitListener {

	private static final int SOUND_THREADS = 6;
	private GOSurfaceView goView;
	private Region goRegion;
	private AndroidSoundPlayer soundPlayer;
	private MapContent activeBgMapContent;
	private StartScreenConnector connector;
	private AndroidPreferences prefs;
	private boolean goViewResumeMissing;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		prefs = new AndroidPreferences(getSharedPreferences("prefs", 0));
		keepScreenOn();
		setContentView(R.layout.base);
		System.setProperty("org.xml.sax.driver", "org.xmlpull.v1.sax2.Driver");
		imageLookupFixed();
	}

	public void imageLookupFixed() {
		if (loadImageLookups()) {
			start();
		} else {
			// Display search dialog.
			showFragment(FileChoserFragment.forDirectory(new File("/")));
		}
	}

	private void start() {
		goRegion = new Region(Region.POSITION_CENTER);
		Area goArea = new Area();
		goArea.add(goRegion);
		goView = new GOSurfaceView(this, goArea);
		((FrameLayout) findViewById(R.id.base_gl)).addView(goView);
		soundPlayer = new AndroidSoundPlayer(SOUND_THREADS);

		showStartScreen();

		showBgMap();
		if (goViewResumeMissing) {
			goViewResume();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (goView != null) {
			goViewResume();
		} else {
			goViewResumeMissing = true;
		}
	}

	private void goViewResume() {
		goView.onResume();
		soundPlayer.setPaused(false);
		goViewResumeMissing = false;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (goView != null) {
			goView.onPause();
			soundPlayer.setPaused(true);

			goView.setContextDestroyedListener(new IContextDestroyedListener() {
				@Override
				public void glContextDestroyed() {
					ImageProvider.getInstance().invalidateAll();
				}
			});
		}
		goViewResumeMissing = false;
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// TODO: Destroy current game.

		goRegion = null;
		goView = null;
		((FrameLayout) findViewById(R.id.base_gl)).removeAllViews();
		soundPlayer = null;
	}

	private void keepScreenOn() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		super.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	private boolean loadImageLookups() {
		return new ResourceLocationScanner(this).scanForResources();
	}

	/* - - - - - - - Fragment stuff - - - - - - */

	public void showStartScreen() {
		if (connector == null) {
			connector = new StartScreenConnector();
		}
		showFragment(new StartScreenFragment());
	}

	public void showFragment(JsettlersFragment fragment) {
		FragmentManager manager = getFragmentManager();
		if (!fragment.shouldAddToBackStack()) {
			manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		}
		FragmentTransaction transaction = manager.beginTransaction();
		if (fragment.shouldAddToBackStack()) {
			transaction.addToBackStack(fragment.getName());
		}
		transaction.replace(R.id.base_menu, fragment);
		transaction.commit();
	}

	@Override
	public void onBackPressed() {
		FragmentManager manager = getFragmentManager();
		Fragment last = manager.findFragmentById(R.id.base_menu);
		if (last instanceof JsettlersFragment) {
			if (((JsettlersFragment) last).onBackButtonPressed()) {
				return;
			}
		}
		super.onBackPressed();
	}

	public StartScreenConnector getStartConnector() {
		return connector;
	}

	public MapInterfaceConnector showGameMap(IStartedGame game) {
		stopBgMapThreads();
		GameCommandFragment p = new GameCommandFragment();
		System.out.println("GameCommandFragment created");

		MapContent content = new MapContent(game, soundPlayer,
				new MobileControls(p.getPutable(this)));
		System.out.println("MapContent created");

		goRegion.setContent(content);
		System.out.println("content set");
		showMapFragment(p);
		System.out.println("GameCommandFragment set");
		game.setGameExitListener(this);
		return content.getInterfaceConnector();
	}

	public void showBgMap() {
		stopBgMapThreads();
		IStartedGame game = new FakeMapGame(new BgMap());
		activeBgMapContent = new MapContent(game, soundPlayer, new BgControls());
		goRegion.setContent(activeBgMapContent);
	}

	private void stopBgMapThreads() {
		if (activeBgMapContent != null) {
			activeBgMapContent.stop();
			activeBgMapContent = null;
		}
	}

	private void showMapFragment(final GameCommandFragment cFragment) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				showFragment(cFragment);
			}
		});
	}

	public void fireKey(String string) {
		goView.fireKey(string);
	}

	public IMultiplayerConnector generateMultiplayerConnector() {
		return getStartConnector().getMultiplayerConnector(
				prefs.getServer(),
				new Player(prefs.getPlayerId(), prefs.getPlayerName()));
	}

	public AndroidPreferences getPrefs() {
		return prefs;
	}

	@Override
	public void gameExited(IStartedGame game) {
		showStartScreen();
	}
}