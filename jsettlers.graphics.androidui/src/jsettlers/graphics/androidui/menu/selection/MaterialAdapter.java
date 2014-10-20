package jsettlers.graphics.androidui.menu.selection;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.map.partition.IMaterialsDistributionSettings;
import jsettlers.common.material.EMaterialType;
import jsettlers.graphics.androidui.Graphics;
import jsettlers.graphics.androidui.R;
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
		int count = distributionSettings.getNumberOfBuildings();
		for (int i = 0; i < count; i++) {
			max = Math.max(distributionSettings.getProbablity(i), max);
		}
		scale = 1 / max;

		bars = new SeekBar[count];
	}

	@Override
	public int getCount() {
		return distributionSettings.getNumberOfBuildings();
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
		image.setImageResource(Graphics.BUILDING_IMAGE_MAP[type.ordinal()]);

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
