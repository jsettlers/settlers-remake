/*******************************************************************************
 * Copyright (c) 2015 - 2017
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
package jsettlers.ai.highlevel;

/**
 * @author codingberlin
 */
class AiBuildingConstants {
	static final float COAL_MINE_TO_IRON_MINE_RATIO = 2F / 1F;
	static final float WEAPON_SMITH_TO_BARRACKS_RATIO = 3F / 1F;
	static final float WEAPON_SMITH_TO_FISHER_HUT_RATIO = 2F / 3F;
	static final float WEAPON_SMITH_TO_FARM_RATIO = 2F / 1F;
	static final float WEAPON_SMITH_TO_IRONMELT_RATIO = 1F / 1F;
	static final float IRONMELT_TO_WEAPON_SMITH_RATIO = 1F / WEAPON_SMITH_TO_IRONMELT_RATIO;
	static final float FARM_TO_BAKER_RATIO = 1F / 1F;
	static final float FARM_TO_MILL_RATIO = 3F / 1F;
	static final float FARM_TO_WATERWORKS_RATIO = 3F / 1F;
	static final float FARM_TO_PIG_FARM_RATIO = 3F / 1F;
	static final float FARM_TO_SLAUGHTER_RATIO = 6F / 1F;
	static final float WEAPON_SMITH_TO_LUMBERJACK_RATIO = 20F / 8F;
	static final float LUMBERJACK_TO_SAWMILL_RATIO = 2F / 1F;
	static final float LUMBERJACK_TO_FORESTER_RATIO = 2F / 1F;
	static final float LUMBERJACK_TO_STONE_CUTTER_RATIO = 8F / 5F;
	static final float COAL_MINE_TO_SMITH_RATIO = 1F / 1.8F;
	static final float IRON_MINE_TO_IRONMELT_RATIO = COAL_MINE_TO_SMITH_RATIO / COAL_MINE_TO_IRON_MINE_RATIO;
	static final float WINEGROWER_TO_TEMPLE_RATIO = 1F / 1F;
}
