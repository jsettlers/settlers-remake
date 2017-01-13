package jsettlers.main.android.ui.fragments.game.menus.selection;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.ImageLink;
import jsettlers.common.images.OriginalImageLink;
import jsettlers.common.movable.EMovableType;
import jsettlers.graphics.androidui.utils.OriginalImageProvider;
import jsettlers.main.android.R;

/**
 * Created by tompr on 13/01/2017.
 */

public class SpecialistsSelectionFragment extends SelectionFragment {
    private final OriginalImageLink imagePioneer = new OriginalImageLink(EImageLinkType.GUI, 14, 204, 0);
    private final OriginalImageLink imageGeologist = new OriginalImageLink(EImageLinkType.GUI, 14, 186, 0);
    private final OriginalImageLink imageThief = new OriginalImageLink(EImageLinkType.GUI, 14, 183, 0);

    private static final EMovableType[] specialistTypes = new EMovableType[] {
            EMovableType.PIONEER,
            EMovableType.THIEF,
            EMovableType.GEOLOGIST,
    };

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
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        LinearLayout specialistsLayout = (LinearLayout) getView().findViewById(R.id.layout_specialists);

        for (EMovableType movableType : specialistTypes) {
            int count = getSelection().getMovableCount(movableType);

            if (count > 0) {
                View view = layoutInflater.inflate(R.layout.view_specialist, specialistsLayout, false);
                ImageView imageView = (ImageView) view.findViewById(R.id.image_view_specialist);
                TextView textView = (TextView) view.findViewById(R.id.text_view_specialist_count);

                OriginalImageProvider.get(getImageLink(movableType)).setAsImage(imageView);
                textView.setText(count + "");

                specialistsLayout.addView(view);
            }
        }
    }

    private ImageLink getImageLink(EMovableType movableType) {
        switch (movableType) {
            case PIONEER:
                return imagePioneer;
            case GEOLOGIST:
                return imageGeologist;
            case THIEF:
                return imageThief;
            default:
                throw new RuntimeException("SpecialistsSelectionFragment cant find image for: " + movableType.name());
        }
    }
}
