package jsettlers.main.android.ui.activities;

import static jsettlers.main.android.GameService.ACTION_QUIT_CONFIRM;

import java.util.List;

import jsettlers.common.menu.EGameError;
import jsettlers.common.menu.EProgressState;
import jsettlers.common.menu.IMapInterfaceConnector;
import jsettlers.common.menu.IStartedGame;
import jsettlers.common.menu.IStartingGameListener;
import jsettlers.graphics.androidui.menu.IFragmentHandler;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.map.MapContent;
import jsettlers.main.android.GameService;
import jsettlers.main.android.R;
import jsettlers.main.android.controls.ControlsAdapter;
import jsettlers.main.android.ui.fragments.game.LoadingFragment;
import jsettlers.main.android.ui.fragments.game.MapFragment;
import jsettlers.main.android.ui.navigation.Actions;
import jsettlers.main.android.ui.navigation.BackPressedListener;
import jsettlers.main.android.providers.ControlsProvider;
import jsettlers.main.android.providers.MapContentProvider;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class GameActivity extends AppCompatActivity implements IStartingGameListener, ControlsProvider, MapContentProvider {
    private static final String TAG_FRAGMENT_SERVICE_BINDER = "service_binder_fragment";
    private static final String TAG_FRAGMENT_MAP = "map_fragment";
    private static final String TAG_FRAGMENT_LOADING = "loading_fragment";

    private GameService gameService;

    private LocalBroadcastManager localBroadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter(ACTION_QUIT_CONFIRM);
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(new ServiceBinderFragment(), TAG_FRAGMENT_SERVICE_BINDER)
                    .commit();
        } else {
            ServiceBinderFragment serviceBinderFragment = (ServiceBinderFragment) getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_SERVICE_BINDER);
            gameService = serviceBinderFragment.getGameService();

            if (!gameService.getStartingGame().isStartupFinished()) {
                gameService.getStartingGame().setListener(GameActivity.this);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
        if (gameService != null && gameService.getStartingGame() != null) {
            gameService.getStartingGame().setListener(null);
        }
    }

    @Override
    public void onBackPressed() {
        if (gameService != null) {
            if (!gameService.getStartingGame().isStartupFinished()) {
                return; // Don't let the user back out of the loading screen
            }
        }

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
     * ControlsProvider implementation
     */
    @Override
    public ControlsAdapter getControlsAdapter() {
        return gameService.getControlsAdapter();
    }

    /**
     * MapContentProvider implementation
     */
    @Override
    public MapContent getMapContent() {
        return getControlsAdapter().getMapContent();
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
        IMapInterfaceConnector mapInterfaceConnector = gameService.gameStarted(game, new IFragmentHandler() {
            @Override
            public void hideMenu() {

            }
        });
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
            }
        });
    }



    // Service has bound asynchronously, now we can use show the fragments which as soon as they are created will require objects from GameService such as MapContent
    private void serviceReady(GameService gameService) {
        this.gameService = gameService;

        if (getIntent().getAction() == Actions.RESUME_GAME) {
            showMapFragment();
        } else {
            showLoadingFragment();
        }

        if (!gameService.getStartingGame().isStartupFinished()) {
            gameService.getStartingGame().setListener(GameActivity.this);
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

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ACTION_QUIT_CONFIRM:
                    finish();
                    break;
            }
        }
    };



    public static class ServiceBinderFragment extends Fragment {
        private GameService gameService;

        private boolean bound = false;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
            // bind using application context because this fragment lives longer than its parent activity, getActivity and getContext are insufficient
            getContext().getApplicationContext().bindService(new Intent(getContext(), GameService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            if (bound) {
                getContext().getApplicationContext().unbindService(serviceConnection);
            }
        }

        public GameService getGameService() {
            return gameService;
        }

        private ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                GameService.GameBinder binder = (GameService.GameBinder) service;
                gameService = binder.getService();

                GameActivity gameActivity = (GameActivity) getActivity();
                gameActivity.serviceReady(gameService);

                bound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                bound = false;
            }
        };
    }
}
