package jsettlers.main.android.fragmentsnew;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jsettlers.main.android.R;
import jsettlers.main.android.utils.FragmentUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapPickerFragment extends Fragment {

    public static MapPickerFragment newInstance() {
        return new MapPickerFragment();
    }

    public MapPickerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_picker, container, false);
        FragmentUtil.setActionBar(this, view);
        return view;
    }

}
