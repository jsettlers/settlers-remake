package jsettlers.graphics;

import java.util.List;

public interface INetworkScreenAdapter {

	void setListener(INetworkScreenListener networkScreen);
	
	List<INetworkPlayer> getPlayerList();

	interface INetworkScreenListener {
		void playerListChanged();
	}
	
	interface INetworkPlayer {
		String getPlayerName();
	}

	void setStartAllowed(boolean startAllowed);
}
