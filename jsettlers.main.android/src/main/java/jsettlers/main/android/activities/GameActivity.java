package jsettlers.main.android.activities;

import jsettlers.common.menu.EGameError;
import jsettlers.common.menu.EProgressState;
import jsettlers.common.menu.IMapInterfaceConnector;
import jsettlers.common.menu.IStartedGame;
import jsettlers.common.menu.IStartingGameListener;
import jsettlers.graphics.androidui.menu.IFragmentHandler;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.map.MapContent;
import jsettlers.main.android.GameService;
import jsettlers.main.android.menus.GameMenu;
import jsettlers.main.android.R;
import jsettlers.main.android.fragmentsnew.LoadingFragment;
import jsettlers.main.android.fragmentsnew.MapFragment;
import jsettlers.main.android.navigation.Actions;
import jsettlers.main.android.navigation.BackPressedListener;
import jsettlers.main.android.navigation.QuitListener;
import jsettlers.main.android.providers.GameMenuProvider;
import jsettlers.main.android.providers.MapContentProvider;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.List;

public class GameActivity extends AppCompatActivity implements IStartingGameListener, IFragmentHandler, GameMenuProvider, MapContentProvider, QuitListener {//}, GameNavigator {
    private static final String TAG_FRAGMENT_MAP = "map_fragment";
    private static final String TAG_FRAGMENT_LOADING = "loading_fragment";

    private GameService gameService;

    private boolean bound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        bindService(new Intent(this, GameService.class), serviceConnection, Context.BIND_AUTO_CREATE);

        if (savedInstanceState != null)
            return;

        if (getIntent().getAction() == Actions.RESUME_GAME) {
            showMapFragment();
        } else {
            showLoadingFragment();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bound) {
            gameService.setQuitListener(null);
            unbindService(serviceConnection);
        }
    }

    @Override
    public void onBackPressed() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        boolean handled = false;

        for (Fragment fragment : fragments) {
            if (fragment instanceof BackPressedListener) {
                BackPressedListener backPressedListener = (BackPressedListener)fragment;
                if (backPressedListener.onBackPressed()) {
                    handled = true;
                }
            }
        }

        if (!handled) {
            super.onBackPressed();
        }
    }

    /**
     * GameMenuProvider implementation
     */
    @Override
    public GameMenu getGameMenu() {
        return gameService.getGameMenu();
    }

    /**
     * MapContentProvider implementation
     */
    @Override
    public MapContent getMapContent() {
        return gameService.getMapContent();
    }

    /**
     * IStartingGameListener implementation
     */
    @Override
    public void startProgressChanged(EProgressState state, final float progress) {
        final String status = Labels.getProgress(state);
        final LoadingFragment loadingFragment = (LoadingFragment) getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_LOADING);

        if (loadingFragment == null)
            return;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingFragment.progressChanged(status, (int) (progress * 100));
            }
        });
    }

    @Override
    public IMapInterfaceConnector preLoadFinished(IStartedGame game) {
        IMapInterfaceConnector mapInterfaceConnector = gameService.gameStarted(game, this);
        return mapInterfaceConnector;
    }

    @Override
    public void startFailed(final EGameError errorType, Exception exception) {
        gameService.getStartingGame().setListener(null);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), errorType.toString(), Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    @Override
    public void startFinished() {
        gameService.getStartingGame().setListener(null);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showMapFragment();
                attachMapFragmentToGameIfLoaded();
            }
        });
    }

    /**
     * QuitListener implementation
     */
    @Override
    public void onQuit() {
        finish();
    }



    // Service has bound asynchronously, now we can use the service and tell fragments to do stuff that requires service access
    private void serviceReady() {
        gameService.setQuitListener(this);

        if (!gameService.getStartingGame().isStartupFinished()) {
            gameService.getStartingGame().setListener(GameActivity.this);
        }

        attachMapFragmentToGameIfLoaded();
    }

    private void attachMapFragmentToGameIfLoaded() {
        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_MAP);
        if (mapFragment != null) {
            mapFragment.attachToGame();
        }
    }

    private void showLoadingFragment() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frame_layout, LoadingFragment.newInstance(), TAG_FRAGMENT_LOADING)
                .commitNow();
    }

    private void showMapFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, MapFragment.newInstance(), TAG_FRAGMENT_MAP)
                .commitNow();
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            GameService.GameBinder binder = (GameService.GameBinder) service;
            gameService = binder.getService();

            serviceReady();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }
    };



    @Override
    public void hideMenu() {

    }
}
