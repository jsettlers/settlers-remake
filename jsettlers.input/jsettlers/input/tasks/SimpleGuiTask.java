package jsettlers.input.tasks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import jsettlers.common.position.ShortPoint2D;
import networklib.client.task.packets.TaskPacket;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class SimpleGuiTask extends TaskPacket {
	private EGuiAction guiAction;

	public SimpleGuiTask() {
	}

	public SimpleGuiTask(EGuiAction guiAction) {
		this.guiAction = guiAction;
	}

	public EGuiAction getGuiAction() {
		return guiAction;
	}

	@Override
	protected void serializeTask(DataOutputStream dos) throws IOException {
		dos.writeInt(guiAction.ordinal());
	}

	@Override
	protected void deserializeTask(DataInputStream dis) throws IOException {
		guiAction = EGuiAction.values[dis.readInt()];
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((guiAction == null) ? 0 : guiAction.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimpleGuiTask other = (SimpleGuiTask) obj;
		if (guiAction != other.guiAction)
			return false;
		return true;
	}

	public static void serializePosition(DataOutputStream dos, ShortPoint2D position) throws IOException {
		dos.writeShort(position.x);
		dos.writeShort(position.y);
	}

	public static ShortPoint2D deserializePosition(DataInputStream dis) throws IOException {
		return new ShortPoint2D(dis.readShort(), dis.readShort());
	}
}
