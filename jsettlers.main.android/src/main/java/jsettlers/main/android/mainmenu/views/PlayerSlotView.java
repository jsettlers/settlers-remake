package jsettlers.main.android.mainmenu.views;

import jsettlers.main.android.mainmenu.presenters.setup.playeritem.Civilisation;
import jsettlers.main.android.mainmenu.presenters.setup.playeritem.PlayerType;
import jsettlers.main.android.mainmenu.presenters.setup.playeritem.StartPosition;
import jsettlers.main.android.mainmenu.presenters.setup.playeritem.Team;

/**
 * Created by tompr on 18/02/2017.
 */

public interface PlayerSlotView {
    void setName(String name);
    void setReady(boolean ready);

    void setPossibleCivilisations(Civilisation[] possibleCivilisations);
    void setCivilisation(Civilisation civilisation);

    void setPossiblePlayerTypes(PlayerType[] ePlayerTypes);
    void setPlayerType(PlayerType playerType);

    void setPossibleStartPositions(StartPosition[] possibleSlots);
    void setStartPosition(StartPosition slot);

    void setPossibleTeams(Team[] possibleTeams);
    void setTeam(Team team);

    void showReadyControl();
    void hideReadyControl();

    void setControlsEnabled();
    void setControlsDisabled();
}
