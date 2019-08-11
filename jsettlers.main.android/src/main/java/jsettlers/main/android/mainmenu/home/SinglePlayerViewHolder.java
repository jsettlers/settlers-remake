package jsettlers.main.android.mainmenu.home;

import androidx.lifecycle.ViewModelProviders;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import jsettlers.main.android.databinding.VhSinglePlayerBinding;
import jsettlers.main.android.mainmenu.navigation.MainMenuNavigator;

public class SinglePlayerViewHolder extends RecyclerView.ViewHolder {

	private final MainMenuNavigator mainMenuNavigator;

	public SinglePlayerViewHolder(View itemView, Fragment parent, MainMenuNavigator mainMenuNavigator) {
		super(itemView);
		this.mainMenuNavigator = mainMenuNavigator;

		MainMenuViewModel viewModel = ViewModelProviders.of(parent).get(MainMenuViewModel.class);
		VhSinglePlayerBinding binding = VhSinglePlayerBinding.bind(itemView);
		binding.setLifecycleOwner(parent);
		binding.setViewmodel(viewModel);

		viewModel.getShowSinglePlayer().observe(parent, z -> mainMenuNavigator.showNewSinglePlayerPicker());
		viewModel.getShowLoadSinglePlayer().observe(parent, z -> mainMenuNavigator.showLoadSinglePlayerPicker());
	}
}
