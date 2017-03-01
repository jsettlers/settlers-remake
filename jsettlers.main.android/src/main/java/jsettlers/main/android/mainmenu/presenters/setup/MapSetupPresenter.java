package jsettlers.main.android.mainmenu.presenters.setup;

/**
 * Created by tompr on 03/02/2017.
 */
public interface MapSetupPresenter {
    void initView();

    void updateViewTitle();

    void viewFinished();

    void dispose();

    void startGame();

    void playerCountSelected(PlayerCount item);

    void startResourcesSelected(StartResources item);
}
