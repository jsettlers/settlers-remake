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
 * The purpose of the higher level IWhatToDoAi is to decide WHAT to do. It delegates the decision WHERE it is to do to the lower level KI. For example
 * if the AI player has no stone cutters the WhatToDoAi decides to build with high priority a stone cutter because otherwise the AI player would be
 * unable to build more houses without stones. Then the WhatToDoAi gets a BestConstructionPositionFinder for Stonecutters and asks it to find the best
 * place to put a stone cutter on the map - which means near some stones ;-) - and builds the stone cutter there.
 * 
 * @author codingberlin
 */
interface IWhatToDoAi {

	void applyRules();
}
