package jsettlers.main.android.mainmenu.presenters.setup.playeritem;

import jsettlers.common.ai.EPlayerType;
import jsettlers.common.player.ECivilisation;
import jsettlers.main.android.mainmenu.views.PlayerSlotView;

/**
 * Created by tompr on 18/02/2017.
 */

public class PlayerSlotPresenter {

    private PlayerSlotView view;

    private ECivilisation[] possibleCivilisations;
    private ECivilisation civilisation;

    private EPlayerType[] possiblePlayerTypes;
    private EPlayerType playerType;

    private Integer[] possibleSlots;
    private Integer slot;

    private Integer[] possibleTeams;
    private Integer team;

    public PlayerSlotPresenter() {
    }

    public void bindView(PlayerSlotView view) {
        this.view = view;
        view.setName("Random");

        view.setPossibleCivilisations(possibleCivilisations);
        view.setCivilisation(civilisation);

        view.setPossibleSlots(possibleSlots);
        view.setSlot(slot);

        view.setPossibleTeams(possibleTeams);
        view.setTeam(team);

        view.setPossiblePlayerTypes(possiblePlayerTypes);
        view.setPlayerType(playerType);
    }

    public void setPossibleCivilisations(ECivilisation[] possibleCivilisations) {
        this.possibleCivilisations = possibleCivilisations;
    }

    public void setCivilisation(ECivilisation civilisation) {
        this.civilisation = civilisation;
    }

    public void setPossiblePlayerTypes(EPlayerType[] ePlayerTypes) {
        this.possiblePlayerTypes = ePlayerTypes;
    }

    public void setPlayerType(EPlayerType playerType) {
        this.playerType = playerType;
    }

    public void setPossibleSlots(int numberOfPlayers) {
        possibleSlots = new Integer[numberOfPlayers];
        for (int i = 0; i < numberOfPlayers; i++) {
            possibleSlots[i] = i + 1;
        }
    }

    public void setSlot(byte slot) {
        this.slot = (int) slot + 1;
    }

    public void setPossibleTeams(int numberOfPlayers) {
        possibleTeams = new Integer[numberOfPlayers];
        for (int i = 0; i < numberOfPlayers; i++) {
            possibleTeams[i] = i + 1;
        }
    }

    public void setTeam(byte team) {
        this.team = (int) team + 1;
    }
}
