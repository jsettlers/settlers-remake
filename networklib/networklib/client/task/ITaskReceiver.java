package networklib.client.task;

import networklib.client.NetworkClient;

/**
 * This interface is used by the {@link TaskPacketListener} to give the received {@link TaskPacket}s to the implementor of this interface.
 * <p />
 * In order to be able to receive the packets, the implementor must define public methods called "receiveTask" with an argument of the
 * {@link TaskPacket} subclass this method want's to receive.
 * <p />
 * e.g.<br>
 * <code>public void receiveTask(FooTask task) {...}</code><br>
 * will be called for every FooTask packet received by the {@link NetworkClient}.
 * 
 * @author Andreas Eberle
 * 
 */
public interface ITaskReceiver {

}
