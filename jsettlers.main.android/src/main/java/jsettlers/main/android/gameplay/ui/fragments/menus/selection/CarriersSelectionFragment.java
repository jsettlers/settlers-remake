package jsettlers.main.android.gameplay.ui.fragments.menus.selection;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import jsettlers.common.movable.EMovableType;
import jsettlers.graphics.action.ConvertAction;
import jsettlers.graphics.androidui.utils.OriginalImageProvider;
import jsettlers.main.android.R;
import jsettlers.main.android.controls.ActionClickListener;
import jsettlers.main.android.controls.ActionControls;
import jsettlers.main.android.controls.ControlsResolver;
import jsettlers.main.android.gameplay.ImageLinkFactory;

/**
 * Created by tompr on 13/01/2017.
 */

public class CarriersSelectionFragment extends SelectionFragment {
    private ActionControls actionControls;

    public static CarriersSelectionFragment newInstance() {
        return new CarriersSelectionFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_selection_carriers, container, false);

        ImageView pioneerImageView = (ImageView) view.findViewById(R.id.image_view_pioneer);
        ImageView geologistImageView = (ImageView) view.findViewById(R.id.image_view_geologist);
        ImageView thiefImageView = (ImageView) view.findViewById(R.id.image_view_thief);

        OriginalImageProvider.get(ImageLinkFactory.get(EMovableType.PIONEER)).setAsImage(pioneerImageView);
        OriginalImageProvider.get(ImageLinkFactory.get(EMovableType.GEOLOGIST)).setAsImage(geologistImageView);
        OriginalImageProvider.get(ImageLinkFactory.get(EMovableType.THIEF)).setAsImage(thiefImageView);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        actionControls = ControlsResolver.getActionControls(getActivity());

        View convertOnePioneerButton = getView().findViewById(R.id.button_convert_one_pioneer);
        View convertAllPioneerButton = getView().findViewById(R.id.button_convert_all_pioneer);
        View convertOneGeologistButton = getView().findViewById(R.id.button_convert_one_geologist);
        View convertAllGeologistButton = getView().findViewById(R.id.button_convert_all_geologist);
        View convertOneThiefButton = getView().findViewById(R.id.button_convert_one_thief);
        View convertAllThiefButton = getView().findViewById(R.id.button_convert_all_thief);

        convertOnePioneerButton.setOnClickListener(new ActionClickListener(actionControls, new ConvertAction(EMovableType.PIONEER, (short) 1)));
        convertAllPioneerButton.setOnClickListener(new ActionClickListener(actionControls, new ConvertAction(EMovableType.PIONEER, Short.MAX_VALUE)));
        convertOneGeologistButton.setOnClickListener(new ActionClickListener(actionControls, new ConvertAction(EMovableType.GEOLOGIST, (short) 1)));
        convertAllGeologistButton.setOnClickListener(new ActionClickListener(actionControls, new ConvertAction(EMovableType.GEOLOGIST, Short.MAX_VALUE)));
        convertOneThiefButton.setOnClickListener(new ActionClickListener(actionControls, new ConvertAction(EMovableType.THIEF, (short) 1)));
        convertAllThiefButton.setOnClickListener(new ActionClickListener(actionControls, new ConvertAction(EMovableType.THIEF, Short.MAX_VALUE)));
    }
}
