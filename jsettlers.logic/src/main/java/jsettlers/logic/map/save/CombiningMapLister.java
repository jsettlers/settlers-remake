/*******************************************************************************
 * Copyright (c) 2016
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
package jsettlers.logic.map.save;

import java.io.IOException;
import java.io.OutputStream;

/**
 * This class combines two {@link IMapLister}s. Maps are always written to the first list.
 * 
 * @author Michael Zangl
 */
public class CombiningMapLister implements IMapLister {
	private final IMapLister l1;
	private final IMapLister l2;

	public CombiningMapLister(IMapLister l1, IMapLister l2) {
		super();
		this.l1 = l1;
		this.l2 = l2;
	}

	@Override
	public void listMaps(IMapListerCallable callable) {
		l1.listMaps(callable);
		l2.listMaps(callable);
	}

	@Override
	public OutputStream getOutputStream(MapFileHeader header) throws IOException {
		return l1.getOutputStream(header);
	}
}
