package jsettlers.main.android.mainmenu.presenters.setup.playeritem;

/**
 * Created by tompr on 24/02/2017.
 */

public class PlayerCount {
    private final int numberOfPlayers;

    public PlayerCount(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PlayerCount && ((PlayerCount)obj).getNumberOfPlayers() == numberOfPlayers;
    }

    @Override
    public String toString() {
        return numberOfPlayers + "";
    }
}
