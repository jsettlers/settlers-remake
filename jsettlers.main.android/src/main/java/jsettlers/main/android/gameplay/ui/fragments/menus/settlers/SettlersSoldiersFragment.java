/*
 * Copyright (c) 2017
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
 */

package jsettlers.main.android.gameplay.ui.fragments.menus.settlers;

import jsettlers.common.images.ImageLink;
import jsettlers.main.android.R;
import jsettlers.main.android.gameplay.presenters.MenuFactory;
import jsettlers.main.android.gameplay.presenters.SettlersSoldiersMenu;
import jsettlers.main.android.gameplay.ui.views.SettlersSoldiersView;
import jsettlers.main.android.utils.OriginalImageProvider;

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
public class SettlersSoldiersFragment extends Fragment implements SettlersSoldiersView {
	private SettlersSoldiersMenu settlersSoldiersMenu;

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
		settlersSoldiersMenu = new MenuFactory(getActivity()).settlersSoldiersMenu(this);
		settlersSoldiersMenu.start();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		settlersSoldiersMenu.finish();
	}

	/**
	 * SettlersSoldiersView implementation
	 */
	@Override
	public void setStrengthText(String strengthText) {
		soldierStrengthTextView.setText(strengthText);
	}

	@Override
	public void setPromotionText(String promotionText) {
		soldierPromotionTextView.setText(promotionText);
	}

	@Override
	public void setSwordsmenPromotionEnabled(boolean enabled) {
		swordsmenPromotionImageView.setEnabled(enabled);
	}

	@Override
	public void setBowmenPromotionEnabled(boolean enabled) {
		bowmenPromotionImageView.setEnabled(enabled);
	}

	@Override
	public void setPikemenPromotionEnabled(boolean enabled) {
		pikemenPromotionImageView.setEnabled(enabled);
	}

	@Override
	public void setSwordsmenImage(ImageLink imageLink) {
		setPromotionButtonImage(swordsmenPromotionImageView, imageLink);
	}

	@Override
	public void setBowmenImage(ImageLink imageLink) {
		setPromotionButtonImage(bowmenPromotionImageView, imageLink);
	}

	@Override
	public void setPikemenImage(ImageLink imageLink) {
		setPromotionButtonImage(pikemenPromotionImageView, imageLink);
	}

	private void setPromotionButtonImage(ImageView imageView, ImageLink imageLink) {
		if (imageLink == null) {
			imageView.setImageDrawable(null);
		} else {
			OriginalImageProvider.get(imageLink).setAsImage(imageView);
		}
	}
}
