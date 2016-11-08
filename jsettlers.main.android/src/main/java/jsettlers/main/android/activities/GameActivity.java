package jsettlers.main.android.activities;

import jsettlers.main.android.R;
import jsettlers.main.android.fragmentsnew.LoadingFragment;
import jsettlers.main.android.fragmentsnew.MapFragment;
import jsettlers.main.android.navigation.GameNavigator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity implements GameNavigator {
    private static final String TAG_FRAGMENT_LOADING = "loading_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        if (savedInstanceState != null)
            return;

        getSupportFragmentManager().beginTransaction()
                .add(R.id.frame_layout, LoadingFragment.newInstance(), TAG_FRAGMENT_LOADING)
                .commit();
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
