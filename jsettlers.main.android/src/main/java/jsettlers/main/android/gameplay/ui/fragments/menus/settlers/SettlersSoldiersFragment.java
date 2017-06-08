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

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import jsettlers.common.images.ImageLink;
import jsettlers.main.android.R;
import jsettlers.main.android.gameplay.presenters.MenuFactory;
import jsettlers.main.android.gameplay.presenters.SettlersSoldiersMenu;
import jsettlers.main.android.gameplay.ui.views.SettlersSoldiersView;
import jsettlers.main.android.utils.OriginalImageProvider;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by tompr on 13/01/2017.
 */
@EFragment(R.layout.menu_settlers_soldiers)
public class SettlersSoldiersFragment extends Fragment implements SettlersSoldiersView {
	public static SettlersSoldiersFragment newInstance() {
		return new SettlersSoldiersFragment_();
	}

	SettlersSoldiersMenu settlersSoldiersMenu;

	@ViewById(R.id.text_view_soldier_strength)
	TextView soldierStrengthTextView;
	@ViewById(R.id.text_view_soldier_promotion)
	TextView soldierPromotionTextView;
	@ViewById(R.id.image_view_promotion_swordsmen)
	ImageView swordsmenPromotionImageView;
	@ViewById(R.id.image_view_promotion_bowmen)
	ImageView bowmenPromotionImageView;
	@ViewById(R.id.image_view_promotion_pikemen)
	ImageView pikemenPromotionImageView;

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

	@Click(R.id.image_view_promotion_swordsmen)
	void promoteSwordsmenClicked() {
		settlersSoldiersMenu.swordsmenPromotionClicked();
	}

	@Click(R.id.image_view_promotion_pikemen)
	void promotePikemenClicked() {
		settlersSoldiersMenu.pikemenPromotionClicked();
	}

	@Click(R.id.image_view_promotion_bowmen)
	void promoteBowmenClicked() {
		settlersSoldiersMenu.bowmenPromotionClicked();
	}

	/**
	 * SettlersSoldiersView implementation
	 */
	@Override
	public void setStrengthText(String strengthText) {
		if (soldierStrengthTextView != null) {
			soldierStrengthTextView.setText(strengthText);
		}
	}

	@Override
	public void setPromotionText(String promotionText) {
		if (soldierPromotionTextView != null) {
			soldierPromotionTextView.setText(promotionText);
		}
	}

	@Override
	public void setSwordsmenPromotionEnabled(boolean enabled) {
		if (swordsmenPromotionImageView != null) {
			swordsmenPromotionImageView.setEnabled(enabled);
		}
	}

	@Override
	public void setBowmenPromotionEnabled(boolean enabled) {
		if (bowmenPromotionImageView != null) {
			bowmenPromotionImageView.setEnabled(enabled);
		}
	}

	@Override
	public void setPikemenPromotionEnabled(boolean enabled) {
		if (pikemenPromotionImageView != null) {
			pikemenPromotionImageView.setEnabled(enabled);
		}
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
		if (imageView == null) {
			return;
		}

		if (imageLink == null) {
			imageView.setImageDrawable(null);
		} else {
			OriginalImageProvider.get(imageLink).setAsImage(imageView);
		}
	}
}
