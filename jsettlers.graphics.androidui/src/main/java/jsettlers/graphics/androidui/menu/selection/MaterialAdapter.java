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
package jsettlers.graphics.androidui.menu.selection;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.map.partition.IMaterialsDistributionSettings;
import jsettlers.common.material.EMaterialType;
import jsettlers.graphics.androidui.R;
import jsettlers.graphics.androidui.utils.OriginalImageProvider;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MaterialAdapter extends BaseAdapter implements ListAdapter {

	public interface DistributionListener {
		void distributionChanged(EMaterialType material, float[] distribution);
	}

	private final class MaterialChanged implements OnSeekBarChangeListener {

		public MaterialChanged() {
		}

		@Override
		public void onStopTrackingTouch(SeekBar arg0) {
		}

		@Override
		public void onStartTrackingTouch(SeekBar arg0) {
		}

		@Override
		public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
			if (arg2) {
				updateMaterialDistribution();
			}
		}
	}

	private final OnSeekBarChangeListener changeListener =
			new MaterialChanged();

	private final Context context;
	private final IMaterialsDistributionSettings distributionSettings;
	private final float scale;

	private final SeekBar[] bars;

	private final DistributionListener listener;

	public MaterialAdapter(Context context,
			IMaterialsDistributionSettings distributionSettings, DistributionListener listener) {
		this.distributionSettings = distributionSettings;
		this.context = context;
		this.listener = listener;

		float max = 0.0001f;
		int count = distributionSettings.getNumberOfBuildingTypes();
		for (int i = 0; i < count; i++) {
			max = Math.max(distributionSettings.getProbablity(i), max);
		}
		scale = 1 / max;

		bars = new SeekBar[count];
	}

	@Override
	public int getCount() {
		return distributionSettings.getNumberOfBuildingTypes();
	}

	@Override
	public EBuildingType getItem(int i) {
		return distributionSettings.getBuildingType(i);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int index, View arg1, ViewGroup arg2) {
		EBuildingType type = getItem(index);
		View view;
		if (arg1 == null) {
			LayoutInflater inflater =
					(LayoutInflater) context
							.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.materialitem, arg2, false);
		} else {
			view = arg1;
		}
		ImageView image =
				((ImageView) view.findViewById(R.id.materialitem_image));
		OriginalImageProvider.get(type).setAsButton(image);

		SeekBar bar = (SeekBar) view.findViewById(R.id.materialitem_priority);
		bar.setEnabled(getCount() > 1);

		int setTo =
				(int) (distributionSettings.getProbablity(index) * scale * 100);
		bar.setProgress(setTo);
		bar.setOnSeekBarChangeListener(changeListener);
		for (int i = 0; i < bars.length; i++) {
			if (bars[i] == bar) {
				bars[i] = null;
			}
		}
		bars[index] = bar;

		return view;
	}

	private void updateMaterialDistribution() {
		float[] distribution = new float[bars.length];
		for (int index = 0; index < bars.length; index++) {
			if (bars[index] == null) {
				distribution[index] =
						distributionSettings.getProbablity(index) * scale;
			} else {
				distribution[index] = bars[index].getProgress() / 100f;
			}
		}
		listener.distributionChanged(distributionSettings.getMaterialType(), distribution);
	}
}
