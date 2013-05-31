package jsettlers.input.tasks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import jsettlers.common.movable.EMovableType;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class ConvertGuiTask extends MovableGuiTask {
	private EMovableType targetType;

	public ConvertGuiTask() {
	}

	public ConvertGuiTask(List<Integer> selection, EMovableType targetType) {
		super(EGuiAction.CONVERT, selection);
		this.targetType = targetType;
	}

	public EMovableType getTargetType() {
		return targetType;
	}

	@Override
	protected void serializeTask(DataOutputStream dos) throws IOException {
		super.serializeTask(dos);
		dos.writeInt(targetType.ordinal());
	}

	@Override
	protected void deserializeTask(DataInputStream dis) throws IOException {
		super.deserializeTask(dis);
		targetType = EMovableType.values[dis.readInt()];
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((targetType == null) ? 0 : targetType.hashCode());
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
		ConvertGuiTask other = (ConvertGuiTask) obj;
		if (targetType != other.targetType)
			return false;
		return true;
	}
}
