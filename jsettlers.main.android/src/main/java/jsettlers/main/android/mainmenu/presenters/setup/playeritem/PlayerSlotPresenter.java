package jsettlers.main.android.mainmenu.presenters.setup.playeritem;

import jsettlers.common.ai.EPlayerType;
import jsettlers.common.player.ECivilisation;
import jsettlers.logic.player.PlayerSetting;
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

    private Integer[] possiblePositions;
    private Integer position;

    private Integer[] possibleTeams;
    private Integer team;

    public PlayerSlotPresenter() {
    }

    public void bindView(PlayerSlotView view) {
        this.view = view;
        view.setName("Random");

        view.setPossibleCivilisations(possibleCivilisations);
        view.setCivilisation(civilisation);

        view.setPossibleSlots(possiblePositions);
        view.setSlot(position);

        view.setPossibleTeams(possibleTeams);
        view.setTeam(team);

        view.setPossiblePlayerTypes(possiblePlayerTypes);
        view.setPlayerType(playerType);
    }

    public PlayerSetting getPlayerSettings() {
        return new PlayerSetting(playerType, civilisation, (byte) (team - 1));
    }

    // Civilisation
    public void setPossibleCivilisations(ECivilisation[] possibleCivilisations) {
        this.possibleCivilisations = possibleCivilisations;
    }

    public void setCivilisation(ECivilisation civilisation) {
        this.civilisation = civilisation;
    }

    // Player stpe
    public void setPossiblePlayerTypes(EPlayerType[] ePlayerTypes) {
        this.possiblePlayerTypes = ePlayerTypes;
    }

    public void setPlayerType(EPlayerType playerType) {
        this.playerType = playerType;
    }

    // Position
    public void setPossiblePositions(int numberOfPlayers) {
        possiblePositions = new Integer[numberOfPlayers];
        for (int i = 0; i < numberOfPlayers; i++) {
            possiblePositions[i] = i + 1;
        }
    }

    public void setPosition(byte position) {
        this.position = (int) position + 1;
    }

    public void positionSelected(Integer position) {
        this.position = position;
    }

    public Integer getPosition() {
        // this should be wrapped in something better than Integer, same for team
        return position;
    }

    public byte getPlayerId() {
        // this should be wrapped in something better than Integer, same for team
        return (byte) (position - 1);
    }


    // Team
    public void setPossibleTeams(int numberOfPlayers) {
        possibleTeams = new Integer[numberOfPlayers];
        for (int i = 0; i < numberOfPlayers; i++) {
            possibleTeams[i] = i + 1;
        }
    }

    public void setTeam(byte team) {
        this.team = (int) team + 1;
    }

    public void teamSelected(Integer team) {
        this.team = team;
    }
}
