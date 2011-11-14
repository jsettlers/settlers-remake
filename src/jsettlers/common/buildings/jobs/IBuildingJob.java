package jsettlers.common.buildings.jobs;

import java.io.Serializable;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.ESearchType;
import jsettlers.common.movable.EDirection;

/**
 * This is a building job, a job that can be done by a worker.
 * <p>
 * Jobs are organized as an cyclic graph, and every job can give yu the next job that is to be executed if it succeeds or fails.
 * 
 * @author michael
 */
public interface IBuildingJob extends Serializable {
	/**
	 * Gets the type of this job.
	 * 
	 * @return The type.
	 */
	EBuildingJobType getType();

	/**
	 * Gets the next job that should be executed if this job succeeds.
	 * 
	 * @return The job.
	 */
	IBuildingJob getNextSucessJob();

	/**
	 * Gets the next job to be executed if this job fails.
	 * 
	 * @return The job.
	 */
	IBuildingJob getNextFailJob();

	/**
	 * Gets the time this job should last. (in seconds)
	 * 
	 * @see EBuildingJobType
	 * @return the time in seconds.
	 */
	float getTime();

	/**
	 * Gets the x distance associate for this job in map units. See {@link EBuildingJobType} for the interpretation of this value.
	 * 
	 * @return The x distance.
	 */
	short getDx();

	/**
	 * Gets the y distance associate for this job in map units. See {@link EBuildingJobType} for the interpretation of this value.
	 * 
	 * @return The y distance.
	 */
	short getDy();

	/**
	 * Gets the direction associated with this job.
	 * 
	 * @see EBuildingJobType
	 * @return The direction or null if it is not provided by this type.
	 */
	EDirection getDirection();

	/**
	 * Gets the material for this job.
	 * 
	 * @see EBuildingJobType
	 * @return The direction or null if it is not provided by this type.
	 */
	EMaterialType getMaterial();

	/**
	 * gets the type this job is searching for.
	 * 
	 * @see EBuildingJobType
	 * @see EBuildingJobType#SEARCH
	 * @return The thing the settler should search.
	 */
	ESearchType getSearchType();
}
