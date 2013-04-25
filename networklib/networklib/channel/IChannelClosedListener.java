package networklib.channel;

/**
 * This interface defines a listener that can get informed when a {@link Channel} is closed.,
 * 
 * @author Andreas Eberle
 * 
 */
public interface IChannelClosedListener {

	/**
	 * This method will be called by a Channel, when it's closed.
	 */
	void channelClosed();

}
