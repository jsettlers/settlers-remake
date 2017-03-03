package jsettlers.main.android.mainmenu.presenters.setup.playeritem;

/**
 * Created by tompr on 24/02/2017.
 */

public class Team {
    private final byte teamByte;

    public Team(byte teamByte) {
        this.teamByte = teamByte;
    }

    public byte asByte() {
        return teamByte;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Team && ((Team)obj).asByte() == teamByte;
    }

    @Override
    public String toString() {
        return "Team " + (teamByte + 1);
    }
}
