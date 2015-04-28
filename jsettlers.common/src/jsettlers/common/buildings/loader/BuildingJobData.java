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

	/**
	 * Gets the name of the job
	 * 
	 * @return The name.
	 */
	String getName();

}
