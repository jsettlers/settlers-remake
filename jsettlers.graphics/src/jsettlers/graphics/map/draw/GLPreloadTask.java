package jsettlers.graphics.map.draw;

import go.graphics.GLDrawContext;

/**
 * @see ImageProvider#addPreloadTask(GLPreloadTask)
 * @author michael
 *
 */
public interface GLPreloadTask {

	void run(GLDrawContext context);

}
