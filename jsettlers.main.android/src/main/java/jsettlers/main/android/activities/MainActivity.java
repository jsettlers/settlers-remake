package jsettlers.main.android.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import jsettlers.main.android.R;
import jsettlers.main.android.fragmentsnew.MainMenuFragment;
import jsettlers.main.android.resources.scanner.ResourceLocationScanner;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        if (savedInstanceState != null)
            return;

        getSupportFragmentManager().beginTransaction()
                .add(R.id.frame_layout, MainMenuFragment.newInstance())
                .commit();
	}
}
