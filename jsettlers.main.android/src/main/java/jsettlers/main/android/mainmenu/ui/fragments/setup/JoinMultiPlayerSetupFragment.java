package jsettlers.main.android.mainmenu.ui.fragments.setup;

import jsettlers.common.menu.IMapDefinition;
import jsettlers.main.android.R;
import jsettlers.main.android.mainmenu.factories.PresenterFactory;
import jsettlers.main.android.mainmenu.presenters.setup.JoinMultiPlayerSetupPresenter;
import jsettlers.main.android.core.ui.FragmentUtil;
import jsettlers.main.android.mainmenu.presenters.setup.MapSetupPresenter;
import jsettlers.main.android.mainmenu.views.JoinMultiPlayerSetupView;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

/**
 * Created by tompr on 22/01/2017.
 */

public class JoinMultiPlayerSetupFragment extends MapSetupFragment implements JoinMultiPlayerSetupView {
    private static final String ARG_MAP_ID = "mapid";

    private JoinMultiPlayerSetupPresenter presenter;

    public static Fragment create(IMapDefinition mapDefinition) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_MAP_ID, mapDefinition.getMapId());

        Fragment fragment = new JoinMultiPlayerSetupFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected MapSetupPresenter getPresenter() {
        presenter = PresenterFactory.createJoinMultiPlayerSetupPresenter(getActivity(), this, getArguments().getString(ARG_MAP_ID));
        return presenter;
    }

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_join_multi_player_setup, container, false);
//        FragmentUtil.setActionBar(this, view);
//
//        CheckBox checkBox = (CheckBox) view.findViewById(R.id.check_box);
//        checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
//            presenter.setReady(b);
//        });
//
//        return view;
//    }
//
//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        isSaving = true;
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        presenter.dispose();
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        if (isRemoving() && !isSaving) {
//            presenter.viewFinished();
//        }
//    }
}
