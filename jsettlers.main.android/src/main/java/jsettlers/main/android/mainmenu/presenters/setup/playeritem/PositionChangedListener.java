package jsettlers.main.android.mainmenu.presenters.setup.playeritem;

/**
 * Created by tompr on 24/02/2017.
 */

public interface PositionChangedListener {
    void positionChanged(PlayerSlotPresenter updatedPlayerSlotPresenter, StartPosition oldPosition, StartPosition newPosition);
}
