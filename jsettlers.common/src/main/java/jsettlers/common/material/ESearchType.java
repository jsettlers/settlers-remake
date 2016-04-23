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
package jsettlers.common.material;

/**
 * This enum defines things that can be searched on the grid.
 * 
 * @author Andreas Eberle
 * 
 */
public enum ESearchType {
	/**
	 * Searches for foreign ground that is not protected by a tower. This search type is used by pioneers.
	 */
	UNENFORCED_FOREIGN_GROUND,

	/**
	 * Searches for own ground (for movables that need to walk back to their ground).
	 */
	VALID_FREE_POSITION,

	/**
	 * Searches for a tree that can be cut by a forester.
	 */
	CUTTABLE_TREE,

	CUTTABLE_STONE,

	PLANTABLE_TREE,

	PLANTABLE_CORN,

	CUTTABLE_CORN,

	PLANTABLE_WINE,
	HARVESTABLE_WINE,

	FISHABLE,

	/**
	 * A river to get water from
	 */
	RIVER,

	ENEMY,

	NON_BLOCKED_OR_PROTECTED,

	SOLDIER_BOWMAN,
	SOLDIER_SWORDSMAN,
	SOLDIER_PIKEMAN,

	RESOURCE_SIGNABLE,

	/**
	 * Search type for thiefs to find a material on enemy ground.
	 */
	FOREIGN_MATERIAL,

}