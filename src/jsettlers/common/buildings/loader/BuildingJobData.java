package jsettlers.common.buildings.loader;

import jsettlers.common.buildings.jobs.EBuildingJobType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.ESearchType;
import jsettlers.common.movable.EDirection;

public interface BuildingJobData {
	/**
	 * Gets the type of this job.
	 * 
	 * @return The type.
	 */
	EBuildingJobType getType();

	/**
	 * Gets the next job that should be executed if this job succeeds.
	 * 
	 * @return The job name.
	 */
	String getNextSucessJob();

	/**
	 * Gets the next job to be executed if this job fails.
	 * 
	 * @return The job name.
	 */
	String getNextFailJob();

	/**
	 * Gets the time this job should last.
	 * 
	 * @see EBuildingJobType
	 * @return the time in ms.
	 */
	float getTime();

	/**
	 * Gets the x distance associate for this job in map units. See {@link EBuildingJobType} fo the interpretation of this value.
	 * 
	 * @return The x distance.
	 */
	short getDx();

	/**
	 * Gets the y distance associate for this job in map units. See {@link EBuildingJobType} fo the interpretation of this value.
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
	 * Gets the type to search for
	 * 
	 * @return The search type.
	 */
	ESearchType getSearchType();

}
