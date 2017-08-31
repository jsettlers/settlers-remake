/*******************************************************************************
 * Copyright (c) 2015
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.common.buildings.jobs;

import jsettlers.common.buildings.IBuilding;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.ESearchType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ShortPoint2D;

/**
 * This is a building job, a job that can be done by a worker.
 * <p>
 * Jobs are organized as an cyclic graph, and every job can give you the next job that is to be executed if it succeeds or fails.
 * <p>
 * They are defined in XML files for each building 
 * 
 * @author Michael Zangl
 */
public interface IBuildingJob {
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
	 * Calculates the point on the grid from the given building.
	 * 
	 * @param building
	 * @return
	 */
	ShortPoint2D calculatePoint(IBuilding building);

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

	String getName();

	boolean isTakeMaterialFromMap();

	EMaterialType[] getFoodOrder();
}
