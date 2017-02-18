package jsettlers.main.android.mainmenu.presenters;

import jsettlers.main.android.mainmenu.views.PlayerItemView;

/**
 * Created by tompr on 18/02/2017.
 */

public class PlayerItemPresenter {

    private PlayerItemView view;

    public PlayerItemPresenter() {
    }

    public void bindView(PlayerItemView view) {
        this.view = view;
        view.setName("Random");
    }
}
