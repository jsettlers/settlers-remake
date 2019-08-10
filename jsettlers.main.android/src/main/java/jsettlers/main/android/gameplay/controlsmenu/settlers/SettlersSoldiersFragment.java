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

package jsettlers.main.android.gameplay.controlsmenu.settlers;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.ImageView;
import android.widget.TextView;

import jsettlers.common.images.ImageLink;
import jsettlers.main.android.R;
import jsettlers.main.android.core.resources.OriginalImageProvider;

/**
 * Created by tompr on 13/01/2017.
 */
@EFragment(R.layout.menu_settlers_soldiers)
public class SettlersSoldiersFragment extends Fragment {
	public static SettlersSoldiersFragment newInstance() {
		return new SettlersSoldiersFragment_();
	}

	private SoldiersViewModel viewModel;

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
		viewModel = ViewModelProviders.of(this, new SoldiersViewModel.Factory(getActivity())).get(SoldiersViewModel.class);

		viewModel.getStrengthText().observe(this, s -> soldierStrengthTextView.setText(s));
		viewModel.getPromotionText().observe(this, s -> soldierPromotionTextView.setText(s));
		viewModel.getSwordsmenPromotionEnabled().observe(this, enabled -> bowmenPromotionImageView.setEnabled(enabled));
		viewModel.getBowmenPromotionEnabled().observe(this, enabled -> bowmenPromotionImageView.setEnabled(enabled));
		viewModel.getPikemenPromotionEnabled().observe(this, enabled -> pikemenPromotionImageView.setEnabled(enabled));
		viewModel.getSwordsmenImageLink().observe(this, imageLink -> setPromotionButtonImage(swordsmenPromotionImageView, imageLink));
		viewModel.getBowmenImageLink().observe(this, imageLink -> setPromotionButtonImage(bowmenPromotionImageView, imageLink));
		viewModel.getPikemenImageLink().observe(this, imageLink -> setPromotionButtonImage(pikemenPromotionImageView, imageLink));
	}

	@Click(R.id.image_view_promotion_swordsmen)
	void promoteSwordsmenClicked() {
		viewModel.swordsmenPromotionClicked();
	}

	@Click(R.id.image_view_promotion_pikemen)
	void promotePikemenClicked() {
		viewModel.pikemenPromotionClicked();
	}

	@Click(R.id.image_view_promotion_bowmen)
	void promoteBowmenClicked() {
		viewModel.bowmenPromotionClicked();
	}

	private void setPromotionButtonImage(ImageView imageView, ImageLink imageLink) {
		if (imageLink == null) {
			imageView.setImageDrawable(null);
		} else {
			OriginalImageProvider.get(imageLink).setAsImage(imageView);
		}
	}
}
