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
package jsettlers.graphics.androidui.menu;

import java.util.BitSet;

import jsettlers.common.Color;
import jsettlers.graphics.androidui.R;
import jsettlers.graphics.map.MapDrawContext;
import jsettlers.graphics.map.minimap.AbstractLineLoader;
import jsettlers.graphics.map.minimap.IMinimapData;
import jsettlers.graphics.map.minimap.MinimapMode;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

/**
 * This is the menu that pops up when the user wants to see the minimap.
 * <p>
 * This class manages the minimap loader thread.
 * 
 * @author Michael Zangl
 */
public class MinimapMenu extends AndroidMenu {

	private final AndroidLineLoader loader = new AndroidLineLoader(new MinimapData(), new MinimapMode());
	private MinimapView map;
	private Handler updateHandler;
	private boolean updateRunnerWanted;

	private Runnable updateRunner = new Runnable() {
		@Override
		public void run() {
			// post() may be delayed, check if still necessary.
			if (updateRunnerWanted) {
				updateBitmap();
			}
		}
	};

	public MinimapMenu(AndroidMenuPutable putable) {
		super(putable);
		// TODO: State handling
		new Thread(loader, "Minimap loader").start();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.minimap, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		map = (MinimapView) view.findViewById(R.id.minimap_display);
		map.setMapContext(getPutable().getMapContext());
		map.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getPutable().goBackInMenu();
			}
		});
		updateBitmap();
	}

	@Override
	public void onResume() {
		super.onResume();
		synchronized (updateRunner) {
			if (updateHandler == null) {
				updateHandler = new Handler();
			}
			updateRunnerWanted = true;
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		synchronized (updateRunner) {
			updateRunnerWanted = false;
		}
	}

	protected void updateBitmap() {
		map.updateBitmap(loader.updateBitmap());
	}

	public void gameStop() {
		loader.stop();
	}

	public class AndroidLineLoader extends AbstractLineLoader {
		private int stride;
		private int yMax;
		private int[] buffer;
		private BitSet updatedLines = new BitSet();
		private Bitmap bitmap;

		public AndroidLineLoader(MinimapData minimapData, MinimapMode modeSettings) {
			super(minimapData, modeSettings);
			bitmap = Bitmap.createBitmap(minimapData.getWidth(), minimapData.getHeight(), Bitmap.Config.ARGB_8888);
		}

		@Override
		protected synchronized void markLineUpdate(int line) {
			updatedLines.set(line);
		}

		@Override
		protected void resizeBuffer(int width, int height) {
			stride = width;
			yMax = height - 1;
			buffer = new int[width * height];
		}

		@Override
		protected void setBuffer(int y, int x, short color) {
			buffer[y * stride + x] = Color.convertTo32Bit(color);
		}

		protected Bitmap updateBitmap() {
			while (true) {
				int nextLine;
				synchronized (this) {
					nextLine = updatedLines.nextSetBit(0);
					if (nextLine < 0) {
						break;
					}
					updatedLines.clear(nextLine);
				}
				int bufferOffset = (yMax - nextLine) * minimapData.getWidth();

				bitmap.setPixels(buffer, bufferOffset, minimapData.getWidth(), 0, nextLine, minimapData.getWidth(), 1);
			}
			return bitmap;
		}
	}

	private class MinimapData implements IMinimapData {

		private static final int MINIMAP_WIDTH = 300;
		private static final int MINIMAP_HEIGHT = 300;
		private int loop;

		@Override
		public int getWidth() {
			return MINIMAP_WIDTH;
		}

		@Override
		public int getHeight() {
			return MINIMAP_HEIGHT;
		}

		@Override
		public MapDrawContext getContext() {
			return getPutable().getMapContext();
		}

		@Override
		public void blockUntilUpdateAllowedOrStopped() {
			if (loop > 10) {
				loop = 0;
				synchronized (updateRunner) {
					if (updateRunnerWanted) {
						updateHandler.post(updateRunner);
					}
				}
			} else {
				loop++;
			}

			// Nothing more for now...
			// TODO: Clean up.
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
	}
}
