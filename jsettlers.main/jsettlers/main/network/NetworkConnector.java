package jsettlers.main.network;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.UUID;

import jsettlers.common.network.IMatch;
import jsettlers.common.network.IMatchSettings;
import jsettlers.common.network.INetworkableMap;
import jsettlers.graphics.startscreen.INetworkConnector;
import jsettlers.main.IErrorDisplayer;
import networklib.NetworkConstants;
import networklib.client.NetworkClient;
import networklib.client.exceptions.InvalidStateException;
import networklib.client.interfaces.INetworkClient;
import networklib.client.receiver.IPacketReceiver;
import networklib.common.packets.ArrayOfMatchInfosPacket;
import networklib.common.packets.ChatMessagePacket;
import networklib.common.packets.MapInfoPacket;
import networklib.common.packets.MatchInfoPacket;
import networklib.common.packets.MatchInfoUpdatePacket;
import networklib.common.packets.MatchStartPacket;
import networklib.infrastructure.channel.IChannelClosedListener;
import networklib.infrastructure.channel.reject.RejectPacket;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class NetworkConnector implements INetworkConnector {
	private static final String DEFAULT_SERVER_ADDRESS = "localhost";

	private final IErrorDisplayer errorDisplayer;

	private String serverAddress;
	private INetworkClient networkClient;
	private INetworkListener networkListener;

	MatchInfoPacketAdapter[] matches;

	public NetworkConnector(IErrorDisplayer errorDisplayer) {
		this.errorDisplayer = errorDisplayer;
	}

	@Override
	public void setServerAddress(String serverAddress) throws UnknownHostException, IOException {
		if (serverAddress == null) {
			this.serverAddress = DEFAULT_SERVER_ADDRESS;
		} else {
			this.serverAddress = serverAddress;
		}

		disconnect();
		connect(serverAddress);
	}

	private void connect(String serverAddress) throws UnknownHostException, IOException {
		networkClient = new NetworkClient(serverAddress, new IChannelClosedListener() {
			@Override
			public void channelClosed() {
				disconnect();
			}
		});
		networkClient.registerRejectReceiver(generateRejectReceiver());

		try {// TODO @Andreas Eberle: get players name from UI and a good id
			networkClient.logIn(UUID.randomUUID().toString(), "playerName", generateMatchListUpdatesReceiver());
		} catch (InvalidStateException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getServerAddress() {
		return serverAddress;
	}

	@Override
	public void setListener(INetworkListener networkListener) {
		this.networkListener = networkListener;
	}

	@Override
	public IMatch[] getMatches() {
		return matches;
	}

	@Override
	public void disconnect() {
		if (networkClient != null) {
			networkClient.close();
			networkClient = null;
		}
	}

	public void openNewNetworkGame(IMatchSettings matchSettings) {
		try {
			networkClient.openNewMatch(matchSettings.getMatchName(), matchSettings
					.getMaxPlayers(), createMapInfoPacket(matchSettings.getMap()), matchSettings.getRandomSeed(), generateMatchStartReceiver(),
					generateMatchInfoUpdateReceiver(), generateChatMessageReceiver());
		} catch (InvalidStateException e) {
			e.printStackTrace();
		}
	}

	public void joinNetworkGame(IMatch match) {
		try {
			networkClient.joinMatch(((MatchInfoPacketAdapter) match).getMatchInfoPacket(), generateMatchStartReceiver(),
					generateMatchInfoUpdateReceiver(), generateChatMessageReceiver());
		} catch (InvalidStateException e) {
			e.printStackTrace();
		}
	}

	private IPacketReceiver<ChatMessagePacket> generateChatMessageReceiver() {
		return new IPacketReceiver<ChatMessagePacket>() {
			@Override
			public void receivePacket(ChatMessagePacket packet) {
				// TODO Auto-generated method stub

			}
		};
	}

	private IPacketReceiver<MatchInfoUpdatePacket> generateMatchInfoUpdateReceiver() {
		return new IPacketReceiver<MatchInfoUpdatePacket>() {
			@Override
			public void receivePacket(MatchInfoUpdatePacket packet) {
				// TODO Auto-generated method stub

			}
		};
	}

	private IPacketReceiver<MatchStartPacket> generateMatchStartReceiver() {
		return new IPacketReceiver<MatchStartPacket>() {
			@Override
			public void receivePacket(MatchStartPacket packet) {
				// TODO Auto-generated method stub

			}
		};
	}

	private MapInfoPacket createMapInfoPacket(INetworkableMap map) {
		return new MapInfoPacket(map.getUniqueID(), map.getName(), "", "", map.getMaxPlayers());
	}

	private IPacketReceiver<RejectPacket> generateRejectReceiver() {
		return new IPacketReceiver<RejectPacket>() {
			@Override
			public void receivePacket(RejectPacket packet) {
				switch (packet.getRejectedKey()) {
				case NetworkConstants.Keys.IDENTIFY_USER:
					disconnect();
					errorDisplayer.showError("Couldn't log in to the server!");
					break;
				}

			}
		};
	}

	private IPacketReceiver<ArrayOfMatchInfosPacket> generateMatchListUpdatesReceiver() {
		return new IPacketReceiver<ArrayOfMatchInfosPacket>() {
			@Override
			public void receivePacket(ArrayOfMatchInfosPacket packet) {
				MatchInfoPacket[] matchInfoPackets = packet.getMatches();
				MatchInfoPacketAdapter[] matches = new MatchInfoPacketAdapter[matchInfoPackets.length];

				for (int i = 0; i < matchInfoPackets.length; i++) {
					matches[i] = new MatchInfoPacketAdapter(matchInfoPackets[i]);
				}

				NetworkConnector.this.matches = matches;

				if (networkListener != null)
					networkListener.matchListChanged(NetworkConnector.this);
			}
		};
	}

}
