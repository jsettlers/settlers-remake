package jsettlers.network.client;

import jsettlers.network.client.interfaces.IGameClock;
import jsettlers.network.client.task.ISyncTasksPacketScheduler;
import jsettlers.network.client.time.ISynchronizableClock;

/**
 * This interface combines the three interfaces {@link IGameClock}, {@link ISynchronizableClock}, {@link ISyncTasksPacketScheduler}.
 * 
 * @author Andreas Eberle
 * 
 */
public interface INetworkClientClock extends IGameClock, ISynchronizableClock, ISyncTasksPacketScheduler {

}
