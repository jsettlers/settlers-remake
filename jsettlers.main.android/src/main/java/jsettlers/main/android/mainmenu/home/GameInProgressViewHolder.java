package jsettlers.main.android.mainmenu.home;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import jsettlers.main.android.R;
import jsettlers.main.android.databinding.VhGameInProgressBinding;
import jsettlers.main.android.mainmenu.navigation.MainMenuNavigator;

public class GameInProgressViewHolder extends RecyclerView.ViewHolder {

	private final MainMenuNavigator mainMenuNavigator;
	private final TextView quitButton;
	private final TextView pauseButton;

	public GameInProgressViewHolder(View itemView, Fragment parent, MainMenuNavigator mainMenuNavigator) {
		super(itemView);
		this.mainMenuNavigator = mainMenuNavigator;
		pauseButton = itemView.findViewById(R.id.button_pause);
		quitButton = itemView.findViewById(R.id.button_quit);

		MainMenuViewModel viewModel = ViewModelProviders.of(parent).get(MainMenuViewModel.class);
		VhGameInProgressBinding binding = VhGameInProgressBinding.bind(itemView);
		binding.setLifecycleOwner(parent);
		binding.setViewmodel(viewModel);

		viewModel.getResumeState().observe(parent, this::update);

		itemView.setOnClickListener(v -> mainMenuNavigator.showGame());
	}

	private void update(MainMenuViewModel.ResumeViewState resumeViewState) {
		if (resumeViewState != null) {
			pauseButton.setText(resumeViewState.isPaused() ? R.string.game_menu_unpause : R.string.game_menu_pause);
			quitButton.setText(resumeViewState.isConfirmQuit() ? R.string.game_menu_quit_confirm : R.string.game_menu_quit);
		}
	}
}
