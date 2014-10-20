package jsettlers.common.buildings.loader;


/**
 * This interface specifies a method do access a name d building job which has a
 * named next and fail-job.
 * <p>
 * It is sort of an abstraction framework used by the job creator to generate
 * static list of jobs for a building.
 * 
 * @author michael
 */
public interface BuildingJobDataProvider {
	/**
	 * Gets the data for a given job.
	 * 
	 * @param name
	 *            The name of the job.
	 * @return The data or <code>null</code>
	 */
	BuildingJobData getJobData(String name);
}
