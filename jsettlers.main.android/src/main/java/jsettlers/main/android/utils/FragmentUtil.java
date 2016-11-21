package jsettlers.main.android.utils;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import jsettlers.main.android.R;

/**
 * Created by tingl on 27/05/2016.
 */
public class FragmentUtil {
    public static void setActionBar(Fragment fragment, View view) {
        AppCompatActivity activity = (AppCompatActivity) fragment.getActivity();
        activity.setSupportActionBar((Toolbar) view.findViewById(R.id.toolbar));
    }
}
