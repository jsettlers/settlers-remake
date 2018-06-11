package jsettlers.main.android.mainmenu.home;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import jsettlers.main.android.databinding.VhMultiPlayerBinding;
import jsettlers.main.android.databinding.VhSinglePlayerBinding;
import jsettlers.main.android.mainmenu.navigation.MainMenuNavigator;

public class MultiPlayerViewHolder extends RecyclerView.ViewHolder {

    private final MainMenuNavigator mainMenuNavigator;

    public MultiPlayerViewHolder(View itemView, Fragment parent, MainMenuNavigator mainMenuNavigator) {
        super(itemView);
        this.mainMenuNavigator = mainMenuNavigator;

        MainMenuViewModel viewModel = ViewModelProviders.of(parent).get(MainMenuViewModel.class);
        VhMultiPlayerBinding binding = VhMultiPlayerBinding.bind(itemView);
        binding.setLifecycleOwner(parent);
        binding.setViewmodel(viewModel);

		viewModel.getShowMultiplayerPlayer().observe(parent, z -> mainMenuNavigator.showNewMultiPlayerPicker());
		viewModel.getShowJoinMultiplayerPlayer().observe(parent, z -> mainMenuNavigator.showJoinMultiPlayerPicker());
    }
}
