package jsettlers.graphics.androidui.menu;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.graphics.action.BuildAction;
import jsettlers.graphics.androidui.Graphics;
import jsettlers.graphics.androidui.R;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

public class BuildMenu extends AndroidMobileMenu {

	private static final int ITEMS_PER_ROW = 5;

	public BuildMenu(AndroidMenuPutable androidMenuPutable) {
		super(androidMenuPutable, R.layout.build);
	}

	protected void fillLayout(View menu) {
		TableLayout list =
		        (TableLayout) ((LinearLayout) menu)
		                .findViewById(R.id.build_list);

		TableRow currentRow = null;
		for (EBuildingType type : EBuildingType.values()) {
			if (currentRow == null
			        || currentRow.getChildCount() >= ITEMS_PER_ROW) {
				currentRow = new TableRow(menu.getContext());
				list.addView(currentRow);
			}
			
			int resourceId = Graphics.BUILDING_IMAGE_MAP[type.ordinal()];
			if (resourceId != -1) {
			ImageButton b = new ImageButton(menu.getContext());
			b.setImageResource(resourceId);
			b.setOnClickListener(generateActionListener(new BuildAction(type),
			        true));
			currentRow.addView(b);
			}
		}

	}
}
