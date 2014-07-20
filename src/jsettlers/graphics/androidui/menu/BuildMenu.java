package jsettlers.graphics.androidui.menu;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.graphics.action.BuildAction;
import android.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

public class BuildMenu extends AndroidMenu {

	private static final int ITEMS_PER_ROW = 5;

	public BuildMenu(AndroidMenuPutable androidMenuPutable) {
		super(androidMenuPutable);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.build, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		TableLayout list =
				(TableLayout) ((LinearLayout) view)
						.findViewById(R.id.build_list);

		TableRow currentRow = null;
		for (EBuildingType type : EBuildingType.values) {
			if (currentRow == null
					|| currentRow.getChildCount() >= ITEMS_PER_ROW) {
				currentRow = new TableRow(view.getContext());
				list.addView(currentRow);
			}

			int resourceId = Graphics.BUILDING_IMAGE_MAP[type.ordinal()];
			if (resourceId != -1) {
				ImageButton b = new ImageButton(view.getContext());
				b.setImageResource(resourceId);
				b.setOnClickListener(generateActionListener(new BuildAction(
						type), true));
				currentRow.addView(b);
			}
		}

	}
}
