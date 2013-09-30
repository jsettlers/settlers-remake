package jsettlers.input.tasks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.position.ShortPoint2D;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class GeneralGuiTask extends SimpleGuiTask {

	private ShortPoint2D position;
	private EBuildingType type;

	public GeneralGuiTask() {
	}

	public GeneralGuiTask(EGuiAction guiAction, byte playerId, ShortPoint2D pos, EBuildingType type) {
		super(guiAction, playerId);
		this.position = pos;
		this.type = type;
	}

	public ShortPoint2D getPosition() {
		return position;
	}

	public EBuildingType getType() {
		return type;
	}

	@Override
	protected void serializeTask(DataOutputStream dos) throws IOException {
		super.serializeTask(dos);
		SimpleGuiTask.serializePosition(dos, position);
		dos.writeInt(type.ordinal);
	}

	@Override
	protected void deserializeTask(DataInputStream dis) throws IOException {
		super.deserializeTask(dis);
		position = SimpleGuiTask.deserializePosition(dis);
		type = EBuildingType.values[dis.readInt()];
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((position == null) ? 0 : position.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		GeneralGuiTask other = (GeneralGuiTask) obj;
		if (position == null) {
			if (other.position != null)
				return false;
		} else if (!position.equals(other.position))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
}
