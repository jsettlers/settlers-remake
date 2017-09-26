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

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import jsettlers.main.android.R;
import jsettlers.main.android.gameplay.viewmodels.ControlsViewModelFactory;
import jsettlers.main.android.gameplay.viewmodels.ProductionViewModel;
import jsettlers.main.android.gameplay.viewstates.ProductionState;
import jsettlers.main.android.utils.OriginalImageProvider;

/**
 * Created by tompr on 24/11/2016.
 */
@EFragment(R.layout.menu_goods_production)
public class GoodsProductionFragment extends Fragment {
    public static GoodsProductionFragment newInstance() {
        return new GoodsProductionFragment_();
    }

    private ProductionViewModel viewModel;

    private ProductionAdapter productionAdapter;

    @ViewById(R.id.recyclerView)
    RecyclerView recyclerView;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(this, new ControlsViewModelFactory(getActivity())).get(ProductionViewModel.class);

        productionAdapter = new ProductionAdapter(getActivity());

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(productionAdapter);


        viewModel.getProductionStates().observe(this, productionAdapter::updateProductionStates);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    class ProductionAdapter extends RecyclerView.Adapter<ProductionItemViewHolder> {

        private final LayoutInflater layoutInflater;

        private ProductionState[] productionStates;

        public ProductionAdapter(Activity activity) {
            this.layoutInflater = LayoutInflater.from(activity);
        }

        @Override
        public ProductionItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = layoutInflater.inflate(R.layout.vh_production, parent, false);
            return new ProductionItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ProductionItemViewHolder holder, int position) {
            ProductionState productionState = productionStates[position];
            holder.bind(productionState);
        }

        @Override
        public int getItemCount() {
            if (productionStates == null) {
                return 0;
            } else {
                return productionStates.length;
            }
        }

        public void updateProductionStates(ProductionState[] productionStates) {
            this.productionStates = productionStates;
            notifyDataSetChanged();
        }
    }

    class ProductionItemViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;
        private final SeekBar seekBar;
        private final TextView quantityTextView;
        private final TextView incrementTextView;
        private final TextView decrementTextView;

        public ProductionItemViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView_material);
            seekBar = itemView.findViewById(R.id.seekBar);
            quantityTextView = itemView.findViewById(R.id.textView_quantity);
            incrementTextView = itemView.findViewById(R.id.textView_increment);
            decrementTextView = itemView.findViewById(R.id.textView_decrement);

            incrementTextView.setOnClickListener(view -> {

            });

            decrementTextView.setOnClickListener(view -> {

            });
        }

        public void bind(ProductionState productionState) {
            OriginalImageProvider.get(productionState.getMaterialType()).setAsImage(imageView);
            quantityTextView.setText(productionState.getQuantity() + "");
        }
    }
}
