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
package jsettlers.mapcreator.mapvalidator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

import jsettlers.logic.map.loading.newmap.MapFileHeader;
import jsettlers.mapcreator.data.MapData;

/**
 * Validate the map for errors
 * 
 * @author Andreas Butti
 */
public class MapValidator {

	/**
	 * Header of the current open map
	 */
	private MapFileHeader header;

	/**
	 * Map to check
	 */
	protected MapData data;

	/**
	 * Listener for validation results
	 */
	private final List<ValidationResultListener> listener = new ArrayList<>();

	/**
	 * Broadcast listener
	 */
	private final ValidationResultListener resultListener = list -> SwingUtilities.invokeLater(() -> {
		for (ValidationResultListener l : listener) {
			l.validationFinished(list);
		}
	});

	/**
	 * Executor service used to check the errors in another thread
	 */
	private final ExecutorService threadpool = Executors.newSingleThreadExecutor(runnable -> new Thread(runnable, "MapValidator"));

	/**
	 * Constructor
	 */
	public MapValidator() {
	}

	/**
	 * @param listener
	 *            Listener to get informed about notifications
	 */
	public void addListener(ValidationResultListener listener) {
		this.listener.add(listener);
	}

	/**
	 * @param data
	 *            Map to check
	 */
	public void setData(MapData data) {
		this.data = data;
	}

	/**
	 * @param header
	 *            Header of the current open map
	 */
	public void setHeader(MapFileHeader header) {
		this.header = header;
	}

	/**
	 * Validate again
	 */
	public void reValidate() {
		threadpool.execute(new ValidatorRunnable(resultListener, data, header));
	}

	/**
	 * Stop validating, shut down threadpool
	 */
	public void dispose() {
		threadpool.shutdownNow();
		try {
			if (!threadpool.awaitTermination(1, TimeUnit.SECONDS)) {
				throw new IllegalStateException("Could not stop DataTester!");
			}
		} catch (InterruptedException e) {
			System.err.println("InterruptedException on stopping DataTester, shoulden't be a problem");
			e.printStackTrace();
		}
	}
}
