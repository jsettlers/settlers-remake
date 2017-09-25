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

package jsettlers.main.android.gameplay.ui.fragments.menus.goods;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import jsettlers.main.android.R;
import jsettlers.main.android.core.controls.ActionControls;
import jsettlers.main.android.core.controls.ControlsResolver;
import jsettlers.main.android.core.controls.DrawControls;
import jsettlers.main.android.core.controls.PositionControls;
import jsettlers.main.android.gameplay.viewmodels.ControlsViewModelFactory;
import jsettlers.main.android.gameplay.viewmodels.ProductionViewModel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import static java8.util.J8Arrays.stream;

/**
 * Created by tompr on 24/11/2016.
 */
@EFragment(R.layout.menu_goods_production)
public class GoodsProductionFragment extends Fragment {
    public static GoodsProductionFragment newInstance() {
        return new GoodsProductionFragment_();
    }

    private ProductionAdapter productionAdapter;

    @ViewById(R.id.recyclerView)
    RecyclerView recyclerView;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ViewModelProviders.of(this, new ControlsViewModelFactory(getActivity())).get(ProductionViewModel.class);


        productionAdapter = new ProductionAdapter();

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(productionAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    class ProductionAdapter extends RecyclerView.Adapter<ProductionItemViewHolder> {

        @Override
        public ProductionItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(ProductionItemViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }

    class ProductionItemViewHolder extends RecyclerView.ViewHolder {

        public ProductionItemViewHolder(View itemView) {
            super(itemView);
        }
    }
}
