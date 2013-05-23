package networklib.client;

import networklib.client.task.ISyncTasksPacketScheduler;
import networklib.client.time.IGameClock;
import networklib.client.time.ISynchronizableClock;

/**
 * This interface combines the three interfaces {@link IGameClock}, {@link ISynchronizableClock}, {@link ISyncTasksPacketScheduler}.
 * 
 * @author Andreas Eberle
 * 
 */
public interface INetworkClientClock extends IGameClock, ISynchronizableClock, ISyncTasksPacketScheduler {

}
