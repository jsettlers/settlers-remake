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
package jsettlers.buildingcreator.job;

import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EDirection;

public class SimpleBuildingJob implements BuildingJob {

	private final int dx;

	private final int dy;

	private final int time;

	private final EMaterialType material;

	private final EDirection direction;

	private final EBuildingJobType type;

	private BuildingJob successJob;
	private BuildingJob failJob;

	private SimpleBuildingJob(BuildingJobData data) {
		dx = data.getDx();
		dy = data.getDy();
		time = data.getTime();
		material = data.getMaterial();
		direction = data.getDirection();
		type = data.getType();
	}

	private SimpleBuildingJob(EBuildingJobType type, int dx, int dy, int time,
			EMaterialType material, EDirection direction) {
		this.dx = dx;
		this.dy = dy;
		this.time = time;
		this.material = material;
		this.direction = direction;
		this.type = type;
	}

	@Override
	public EDirection getDirection() {
		return direction;
	}

	@Override
	public int getDx() {
		return dx;
	}

	@Override
	public int getDy() {
		return dy;
	}

	@Override
	public EMaterialType getMaterial() {
		return material;
	}

	@Override
	public BuildingJob getNextFailJob() {
		return failJob;
	}

	@Override
	public BuildingJob getNextSucessJob() {
		return successJob;
	}

	@Override
	public int getTime() {
		return time;
	}

	/**
	 * This method creates a new graph of linked jobs, starting with a start job.
	 * <p>
	 * The data of the provider is not checked for the type of the job.
	 * 
	 * @param provider
	 *            The provider that provides the job definitions.
	 * @param startJob
	 *            The first job.
	 * @return The start job as object and a perfectly linked list.
	 * @throws IllegalArgumentException
	 *             if the provider does provide wrog links.
	 */
	public static BuildingJob createLinkedJobs(
			BuildingJobDataProvider provider, String startJob) {
		if (startJob == null) {
			throw new IllegalArgumentException("Start job is null.");
		}
		Hashtable<String, SimpleBuildingJob> converted =
				new Hashtable<>();

		fillHashtableWithUnlinked(provider, startJob, converted);

		linkJobs(provider, converted);

		return converted.get(startJob);
	}

	private static void linkJobs(BuildingJobDataProvider provider,
			Hashtable<String, SimpleBuildingJob> converted) {
		Set<Entry<String, SimpleBuildingJob>> items = converted.entrySet();
		for (Entry<String, SimpleBuildingJob> item : items) {
			String name = item.getKey();
			SimpleBuildingJob job = item.getValue();
			BuildingJobData definition = provider.getJobData(name);

			job.failJob = converted.get(definition.getNextFailJob());
			job.successJob = converted.get(definition.getNextSucessJob());
			if (job.failJob == null || job.successJob == null) {
				throw new IllegalArgumentException("Next jobs were not found.");
			}
		}
	}

	private static void fillHashtableWithUnlinked(
			BuildingJobDataProvider provider, String startJob,
			Hashtable<String, SimpleBuildingJob> converted) {
		ConcurrentLinkedQueue<String> toBuild =
				new ConcurrentLinkedQueue<>();
		toBuild.offer(startJob);
		while (!toBuild.isEmpty()) {
			String currentName = toBuild.poll();
			if (!converted.containsKey(currentName)) {
				SimpleBuildingJob job =
						createUnlinkedJob(provider, toBuild, currentName);

				converted.put(currentName, job);
			}
		}
	}

	private static SimpleBuildingJob createUnlinkedJob(
			BuildingJobDataProvider provider,
			ConcurrentLinkedQueue<String> toBuild, String currentName) {
		BuildingJobData data = provider.getJobData(currentName);
		if (data == null) {
			throw new IllegalArgumentException(
					"Error on building job list conversion: job " + currentName
							+ " not found.");
		}
		SimpleBuildingJob job = new SimpleBuildingJob(data);

		toBuild.add(data.getNextFailJob());
		toBuild.add(data.getNextSucessJob());
		return job;
	}

	@Override
	public EBuildingJobType getType() {
		return type;
	}

	/**
	 * Creates a primitive fallback job list.
	 * 
	 * @return
	 */
	public static BuildingJob createFallback() {
		SimpleBuildingJob wait =
				new SimpleBuildingJob(EBuildingJobType.WAIT, 0, 0, 1000, null,
						null);
		SimpleBuildingJob hide =
				new SimpleBuildingJob(EBuildingJobType.HIDE, 0, 0, 0, null,
						null);
		wait.successJob = wait;
		wait.failJob = wait;
		hide.successJob = wait;
		hide.failJob = wait;
		return hide;
	}
}
