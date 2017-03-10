package jsettlers.main.android.gameplay.ui.fragments.menus.selection;

import jsettlers.common.menu.action.EActionType;
import jsettlers.common.movable.EMovableType;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.androidui.utils.OriginalImageProvider;
import jsettlers.main.android.R;
import jsettlers.main.android.core.controls.ActionClickListener;
import jsettlers.main.android.core.controls.ActionControls;
import jsettlers.main.android.core.controls.ControlsResolver;
import jsettlers.main.android.gameplay.ImageLinkFactory;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by tompr on 13/01/2017.
 */

public class SoldiersSelectionFragment extends SelectionFragment {
    private static final EMovableType[] soldierTypes = new EMovableType[] {
            EMovableType.SWORDSMAN_L1,
            EMovableType.SWORDSMAN_L2,
            EMovableType.SWORDSMAN_L3,
            EMovableType.PIKEMAN_L1,
            EMovableType.PIKEMAN_L2,
            EMovableType.PIKEMAN_L3,
            EMovableType.BOWMAN_L1,
            EMovableType.BOWMAN_L2,
            EMovableType.BOWMAN_L3,
    };

    private ActionControls actionControls;

    private LinearLayout soldiers1Layout;
    private LinearLayout soldiers2Layout;
    private LinearLayout soldiers3Layout;

    public static Fragment newInstance() {
        return new SoldiersSelectionFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.menu_selection_soldiers, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        actionControls = new ControlsResolver(getActivity()).getActionControls();

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        soldiers1Layout = (LinearLayout) getView().findViewById(R.id.layout_soldiers_level_1);
        soldiers2Layout = (LinearLayout) getView().findViewById(R.id.layout_soldiers_level_2);
        soldiers3Layout = (LinearLayout) getView().findViewById(R.id.layout_soldiers_level_3);

        View killButton = getView().findViewById(R.id.button_kill);
        View haltButton = getView().findViewById(R.id.button_halt);

        killButton.setOnClickListener(killClickListener);
        haltButton.setOnClickListener(new ActionClickListener(actionControls, EActionType.STOP_WORKING));

        for (EMovableType movableType : soldierTypes) {
            int count = getSelection().getMovableCount(movableType);

            if (count > 0) {
                LinearLayout soldiersLayout = getLevelLayout(movableType);

                View view = layoutInflater.inflate(R.layout.view_specialist, soldiersLayout, false);
                ImageView imageView = (ImageView) view.findViewById(R.id.image_view_specialist);
                TextView textView = (TextView) view.findViewById(R.id.text_view_specialist_count);

                OriginalImageProvider.get(ImageLinkFactory.get(movableType)).setAsImage(imageView);
                textView.setText(count + "");

                soldiersLayout.addView(view);
            }
        }

        goneIfEmpty(soldiers1Layout);
        goneIfEmpty(soldiers2Layout);
        goneIfEmpty(soldiers3Layout);
    }

    private void goneIfEmpty(LinearLayout linearLayout) {
        if(linearLayout.getChildCount() == 0) {
            linearLayout.setVisibility(View.GONE);
        }
    }

    private LinearLayout getLevelLayout(EMovableType movableType) {
        switch (movableType) {
            case SWORDSMAN_L1:
            case BOWMAN_L1:
            case PIKEMAN_L1:
                return soldiers1Layout;
            case SWORDSMAN_L2:
            case BOWMAN_L2:
            case PIKEMAN_L2:
                return soldiers2Layout;
            case SWORDSMAN_L3:
            case BOWMAN_L3:
            case PIKEMAN_L3:
                return soldiers3Layout;
            default:
                throw new RuntimeException("SoldiersSelctionFragment can't display movable: " + movableType.name());
        }
    }

    private final View.OnClickListener killClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Snackbar.make(getView(), R.string.confirm_kill, Snackbar.LENGTH_SHORT)
                    .setAction(R.string.yes, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            actionControls.fireAction(new Action(EActionType.DESTROY));
                        }
                    })
                    .show();
        }
    };
}
