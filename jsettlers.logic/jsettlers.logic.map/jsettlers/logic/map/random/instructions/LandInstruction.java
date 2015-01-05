package jsettlers.logic.map.random.instructions;

import java.util.Random;

import jsettlers.logic.map.random.generation.PlayerStart;
import jsettlers.logic.map.random.landscape.LandscapeMesh;

public abstract class LandInstruction extends GenerationInstruction {

	/**
	 * Executes this instruction
	 * 
	 * @param landscape
	 *            The landscape to work on
	 * @param starts
	 *            TODO: better param for connections, ...
	 * @param rand
	 *            A random generator to use
	 */
	public abstract void execute(LandscapeMesh landscape, PlayerStart[] starts,
			Random rand);
}
