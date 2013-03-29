package jsettlers.graphics.map;

import go.graphics.GLDrawContext;

/**
 * This is a {@link GLDrawContext} factory.
 * 
 * @author michael
 */
public interface IGLProvider {

	/**
	 * Gets a gl draw context.
	 * 
	 * @return The gl draw context we should use.
	 */
	GLDrawContext getGl();

}
