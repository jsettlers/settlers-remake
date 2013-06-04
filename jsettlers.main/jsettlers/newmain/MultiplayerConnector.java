package jsettlers.newmain;

import jsettlers.graphics.startscreen.interfaces.IChangingList;
import jsettlers.graphics.startscreen.interfaces.IJoinableGame;
import jsettlers.graphics.startscreen.interfaces.IJoiningGame;
import jsettlers.graphics.startscreen.interfaces.IMultiplayerConnector;
import jsettlers.graphics.startscreen.interfaces.IOpenMultiplayerGameInfo;

public class MultiplayerConnector implements IMultiplayerConnector {

	@Override
	public IChangingList<? extends IJoinableGame> getJoinableMultiplayerGames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IJoiningGame joinMultiplayerGame(IJoinableGame game) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IJoiningGame openNewMultiplayerGame(IOpenMultiplayerGameInfo gameInfo) {
		// TODO Auto-generated method stub
		return null;
	}

}
