package networklib.client.time;

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
}
