package jsettlers.main.android.ui.activities;

import static jsettlers.main.android.menus.game.GameMenu.ACTION_QUIT_CONFIRM;

import java.util.List;

import jsettlers.main.android.R;
import jsettlers.main.android.controls.ControlsAdapter;
import jsettlers.main.android.controls.ControlsProvider;
import jsettlers.main.android.providers.GameManager;
import jsettlers.main.android.ui.fragments.game.LoadingFragment;
import jsettlers.main.android.ui.fragments.game.MapFragment;
import jsettlers.main.android.ui.navigation.Actions;
import jsettlers.main.android.ui.navigation.BackPressedListener;
import jsettlers.main.android.ui.navigation.GameNavigator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity implements GameNavigator, ControlsProvider {
    private static final String TAG_FRAGMENT_MAP = "map_fragment";
    private static final String TAG_FRAGMENT_LOADING = "loading_fragment";

    private GameManager gameManager;

    private final LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameManager = (GameManager) getApplication();

        setContentView(R.layout.activity_game);

        IntentFilter intentFilter = new IntentFilter(ACTION_QUIT_CONFIRM);
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);

        if (savedInstanceState != null)
            return;

        if (Actions.RESUME_GAME.equals(getIntent().getAction())) {
            showMap();
        } else {
            showLoading();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onBackPressed() {
        if (!gameManager.getStartingGame().isStartupFinished()) {
            return; // Don't let the user back out of the loading screen
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
        return gameManager.getControlsAdapter();
    }


    /**
     * GameNavigator implementation
     */
    @Override
    public void showMap() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, MapFragment.newInstance(), TAG_FRAGMENT_MAP)
                .commitNow();
    }

    private void showLoading() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frame_layout, LoadingFragment.newInstance(), TAG_FRAGMENT_LOADING)
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
}
