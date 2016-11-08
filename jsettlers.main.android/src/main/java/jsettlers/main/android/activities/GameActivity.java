package jsettlers.main.android.activities;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import jsettlers.common.menu.IMapInterfaceConnector;
import jsettlers.common.menu.IStartedGame;
import jsettlers.common.menu.IStartingGame;
import jsettlers.graphics.androidui.menu.IFragmentHandler;
import jsettlers.graphics.map.MapContent;
import jsettlers.main.android.GameService;
import jsettlers.main.android.MainApplication;
import jsettlers.main.android.R;
import jsettlers.main.android.fragmentsnew.MapFragment;
import jsettlers.main.android.fragmentsnew.LoadingFragment;
import jsettlers.main.android.navigation.GameNavigator;
import jsettlers.main.android.providers.GameProvider;

public class GameActivity extends AppCompatActivity implements GameNavigator, GameProvider, IFragmentHandler {
    private static final String TAG_FRAGMENT_LOADING = "loading_fragment";

    private GameService gameService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        bindService(new Intent(this, GameService.class), serviceConnection, Context.BIND_AUTO_CREATE);

        if (savedInstanceState != null)
            return;

        getSupportFragmentManager().beginTransaction()
                .add(R.id.frame_layout, LoadingFragment.newInstance(), TAG_FRAGMENT_LOADING)
                .commit();
    }

    /**
     * GameProvider
     * @return
     */
    @Override
    public IStartingGame getStartingGame() {
        return gameService.getStartingGame();
    }

    @Override
    public IMapInterfaceConnector loadFinished(IStartedGame game) {
        return gameService.gameStarted(game, this);
    }

    @Override
    public MapContent getMapContent() {
        return gameService.getMapContent();
    }

    /**
     * GameNavigator
     * @return
     */
    @Override
    public void showMap() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, MapFragment.newInstance())
                .commit();
    }

    /**
     * IFragmentHandler
     * @return
     */
    @Override
    public void showMenuFragment(Fragment fragment) {

    }

    @Override
    public void hideMenu() {

    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            GameService.GameBinder binder = (GameService.GameBinder) service;
            gameService = (GameService)binder.getService();

            LoadingFragment loadingFragment = (LoadingFragment)getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_LOADING);
            loadingFragment.setGameProvider(GameActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };
}
