package jsettlers.main.android.mainmenu.ui.fragments;

import jsettlers.main.android.R;
import jsettlers.main.android.mainmenu.presenters.JoinMultiPlayerSetupPresenter;
import jsettlers.main.android.core.GameStarter;
import jsettlers.main.android.mainmenu.navigation.MainMenuNavigator;
import jsettlers.main.android.core.ui.FragmentUtil;
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

public class JoinMultiPlayerSetupFragment extends Fragment implements JoinMultiPlayerSetupView {
    private JoinMultiPlayerSetupPresenter presenter;

    private boolean isSaving = false;

    public static Fragment create() {
        return new JoinMultiPlayerSetupFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GameStarter gameStarter = (GameStarter) getActivity().getApplication();
        MainMenuNavigator navigator = (MainMenuNavigator) getActivity();

        presenter = new JoinMultiPlayerSetupPresenter(this, gameStarter, navigator);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_join_multi_player_setup, container, false);
        FragmentUtil.setActionBar(this, view);

        CheckBox checkBox = (CheckBox) view.findViewById(R.id.check_box);
        checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            presenter.setReady(b);
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        isSaving = true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.dispose();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (isRemoving() && !isSaving) {
            presenter.viewFinished();
        }
    }
}
