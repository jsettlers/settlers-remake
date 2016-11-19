package jsettlers.main.android.activities;

import jsettlers.main.android.R;
import jsettlers.main.android.fragmentsnew.LoadingFragment;
import jsettlers.main.android.fragmentsnew.MapFragment;
import jsettlers.main.android.navigation.Actions;
import jsettlers.main.android.navigation.BackPressedListener;
import jsettlers.main.android.navigation.GameNavigator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

public class GameActivity extends AppCompatActivity implements GameNavigator {
    private static final String TAG_FRAGMENT_LOADING = "loading_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        if (savedInstanceState != null)
            return;

        if (getIntent().getAction() == Actions.RESUME_GAME) {
            showMap();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_layout, LoadingFragment.newInstance(), TAG_FRAGMENT_LOADING)
                    .commit();
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
     * GameNavigator
     * @return
     */
    @Override
    public void showMap() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, MapFragment.newInstance())
                .commit();
    }
}
