package jsettlers.main.android.ui.fragments.game.menus.selection;

import jsettlers.common.menu.action.EActionType;
import jsettlers.common.movable.EMovableType;
import jsettlers.graphics.action.ConvertAction;
import jsettlers.graphics.androidui.utils.OriginalImageProvider;
import jsettlers.main.android.R;
import jsettlers.main.android.controls.ActionClickListener;
import jsettlers.main.android.controls.ActionControls;
import jsettlers.main.android.controls.ControlsResolver;
import jsettlers.main.android.ui.images.ImageLinkFactory;

import android.os.Bundle;
import android.support.annotation.Nullable;
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

public class SpecialistsSelectionFragment extends SelectionFragment {
    private static final EMovableType[] specialistTypes = new EMovableType[] {
            EMovableType.PIONEER,
            EMovableType.THIEF,
            EMovableType.GEOLOGIST,
    };

    private ActionControls actionControls;

    public static Fragment newInstance() {
        return new SpecialistsSelectionFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.menu_selection_specialists, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        actionControls = ControlsResolver.getActionControls(getActivity());

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        LinearLayout specialistsLayout = (LinearLayout) getView().findViewById(R.id.layout_specialists);

        View convertCarriersButton = getView().findViewById(R.id.button_convert_carriers);
        View workHereButton = getView().findViewById(R.id.button_work_here);
        View haltButton = getView().findViewById(R.id.button_halt);

        convertCarriersButton.setOnClickListener(new ActionClickListener(actionControls, new ConvertAction(EMovableType.BEARER, Short.MAX_VALUE)));
        workHereButton.setOnClickListener(new ActionClickListener(actionControls, EActionType.START_WORKING));
        haltButton.setOnClickListener(new ActionClickListener(actionControls, EActionType.STOP_WORKING));

        for (EMovableType movableType : specialistTypes) {
            int count = getSelection().getMovableCount(movableType);

            if (count > 0) {
                View view = layoutInflater.inflate(R.layout.view_specialist, specialistsLayout, false);
                ImageView imageView = (ImageView) view.findViewById(R.id.image_view_specialist);
                TextView textView = (TextView) view.findViewById(R.id.text_view_specialist_count);

                OriginalImageProvider.get(ImageLinkFactory.get(movableType)).setAsImage(imageView);
                textView.setText(count + "");

                specialistsLayout.addView(view);
            }
        }
    }
}
