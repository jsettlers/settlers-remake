package networklib.client.interfaces;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import networklib.synchronic.timer.INetworkTimerable;
import networklib.synchronic.timer.ITaskExecutor;

/**
 * This interface defines a clock supported by the networklib to the user of the library.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IGameClock extends IPausingSupplier {

	int getTime();

	void setTime(int newTime);

	void setTaskExecutor(ITaskExecutor taskExecutor);

	void multiplyGameSpeed(float factor);

	void setGameSpeed(float speedFactor);

	void invertPausing();

	void setPausing(boolean b);

	void fastForward();

	void remove(INetworkTimerable timerable);

	void schedule(INetworkTimerable timerable, short delay);

	void startExecution();

	void stopExecution();

	/**
	 * Sets the stream to be used to log the actions of the users.
	 * 
	 * @param replayFileStream
	 */
	void setReplayLogStream(DataOutputStream replayFileStream);

	/**
	 * Saves the remaining tasks to the given stream.
	 * 
	 * @param dos
	 */
	void saveRemainingTasks(DataOutputStream dos) throws IOException;

	void loadReplayLogFromStream(DataInputStream dataInputStream);

	/**
	 * Plays the game with maximum speed to the given game time and then pauses the game.
	 * 
	 * @param targetGameTime
	 *            The desired game time in milliseconds.
	 */
	void fastForwardTo(int targetGameTime);

}
