package jsettlers.main.android.gameplay.ui.fragments.menus.settlers;

import jsettlers.common.images.ImageLink;
import jsettlers.graphics.androidui.utils.OriginalImageProvider;
import jsettlers.main.android.R;
import jsettlers.main.android.core.controls.ControlsResolver;
import jsettlers.main.android.core.controls.DrawControls;
import jsettlers.main.android.core.controls.DrawListener;
import jsettlers.main.android.gameplay.presenters.SettlersSoldiersMenu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by tompr on 13/01/2017.
 */

public class SettlersSoldiersFragment extends Fragment implements DrawListener {
    private SettlersSoldiersMenu settlersSoldiersMenu;
    private DrawControls drawControls;

    private TextView soldierStrengthTextView;
    private TextView soldierPromotionTextView;
    private ImageView swordsmenPromotionImageView;
    private ImageView bowmenPromotionImageView;
    private ImageView pikemenPromotionImageView;

    public static SettlersSoldiersFragment newInstance() {
        return new SettlersSoldiersFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_settlers_soldiers, container, false);

        soldierStrengthTextView = (TextView) view.findViewById(R.id.text_view_soldier_strength);
        soldierPromotionTextView = (TextView) view.findViewById(R.id.text_view_soldier_promotion);
        swordsmenPromotionImageView = (ImageView) view.findViewById(R.id.image_view_promotion_swordsmen);
        bowmenPromotionImageView = (ImageView) view.findViewById(R.id.image_view_promotion_bowmen);
        pikemenPromotionImageView = (ImageView) view.findViewById(R.id.image_view_promotion_pikemen);

        swordsmenPromotionImageView.setOnClickListener(view1 -> settlersSoldiersMenu.swordsmenPromotionClicked());
        bowmenPromotionImageView.setOnClickListener(view1 -> settlersSoldiersMenu.bowmenPromotionClicked());
        pikemenPromotionImageView.setOnClickListener(view1 -> settlersSoldiersMenu.pikemenPromotionClicked());

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        settlersSoldiersMenu = ControlsResolver.getMenuFactory(getActivity()).getSettlersSoldiersMenu();
        drawControls = ControlsResolver.getDrawControls(getActivity());

        drawControls.addDrawListener(this);
        update();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        drawControls.removeDrawListener(this);
    }

    @Override
    public void draw() {
        getActivity().runOnUiThread(() -> update());
    }

    private void update() {
        soldierStrengthTextView.setText(settlersSoldiersMenu.getStrengthText());
        soldierPromotionTextView.setText(settlersSoldiersMenu.getPromotionText());

        swordsmenPromotionImageView.setEnabled(settlersSoldiersMenu.isSwordsmenPromotionPossible());
        bowmenPromotionImageView.setEnabled(settlersSoldiersMenu.isBowmenPromotionPossible());
        pikemenPromotionImageView.setEnabled(settlersSoldiersMenu.isPikemenPromotionPossible());

        setPromotionButtonImage(swordsmenPromotionImageView, settlersSoldiersMenu.getSwordsmenPromotionImageLink());
        setPromotionButtonImage(bowmenPromotionImageView, settlersSoldiersMenu.getBowmenPromotionImageLink());
        setPromotionButtonImage(pikemenPromotionImageView, settlersSoldiersMenu.getPikemenPromotionImageLink());
    }

    public void setPromotionButtonImage(ImageView imageView, ImageLink imageLink) {
        if (imageLink == null) {
            imageView.setImageDrawable(null);
        } else {
            OriginalImageProvider.get(imageLink).setAsImage(imageView);
        }
    }
}
