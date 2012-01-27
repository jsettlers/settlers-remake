package jsettlers.main.network;

/**
 * A task to connect to a network match.
 * <p />
 * This is either open a new match or join an opened match.
 * 
 * @author michael
 * 
 */
public interface INetworkConnectTask {
	public void cancel();
}
