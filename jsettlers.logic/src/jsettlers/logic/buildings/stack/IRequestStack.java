package jsettlers.logic.buildings.stack;

import java.io.Serializable;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.EPriority;
import jsettlers.common.position.ILocatable;

public interface IRequestStack extends ILocatable, IStackSizeSupplier, Serializable {

	boolean hasMaterial();

	boolean isFullfilled();

	boolean pop();

	short getNumberOfPopped();

	EMaterialType getMaterialType();

	void releaseRequests();

	void setPriority(EPriority newPriority);

	void setListener(IRequestStackListener listener);

	short getStillRequired();

}
