package jsettlers.logic.management.bearer;

import jsettlers.common.player.IPlayerable;
import jsettlers.common.position.ILocatable;
import jsettlers.logic.management.bearer.job.BearerCarryJob;
import jsettlers.logic.management.bearer.job.BearerToWorkerJob;

public interface IBearerJobable extends ILocatable, IPlayerable {

	void setToWorkerJob(BearerToWorkerJob job);

	void setCarryJob(BearerCarryJob job);

}
