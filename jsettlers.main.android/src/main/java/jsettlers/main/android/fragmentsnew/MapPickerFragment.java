package jsettlers.main.android.fragmentsnew;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jsettlers.common.menu.IMapDefinition;
import jsettlers.common.utils.collections.ChangingList;
import jsettlers.common.utils.collections.IChangingListListener;
import jsettlers.main.android.R;
import jsettlers.main.android.utils.FragmentUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapPickerFragment extends Fragment {
    private MapAdapter adapter;

    public static MapPickerFragment newInstance() {
        return new MapPickerFragment();
    }

    public MapPickerFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new MapAdapter(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_picker, container, false);
        FragmentUtil.setActionBar(this, view);

        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter.onDestroy();
    }


    private class MapAdapter extends RecyclerView.Adapter<MapHolder> implements IChangingListListener<IMapDefinition> {
        private ChangingList<IMapDefinition> changingMaps;
        private List<IMapDefinition> maps;

        public MapAdapter(ChangingList<IMapDefinition> changingMaps) {
            this.maps = changingMaps.getItems();
            this.changingMaps = changingMaps;
            this.changingMaps.setListener(this);
        }

        @Override
        public int getItemCount() {
            return maps.size();
        }

        @Override
        public MapHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View itemView = getActivity().getLayoutInflater().inflate(R.layout.item_map, parent, false);
            final MapHolder mapHolder = new MapHolder(itemView);


            return mapHolder;
        }

        @Override
        public void onBindViewHolder(MapHolder holder, int position) {
            IMapDefinition map = maps.get(position);
            holder.getNameTextView().setText(map.getMapName());
        }

        @Override
        public void listChanged(ChangingList<? extends IMapDefinition> list) {
            final List<IMapDefinition> newList = new ArrayList<>(list.getItems());
            Collections.sort(newList, getDefaultComparator());
            maps = newList;
            notifyDataSetChanged();
        }

        public void onDestroy() {
            changingMaps.removeListener(this);
        }
        private Comparator<IMapDefinition> getDefaultComparator() {
            return IMapDefinition.MAP_NAME_COMPARATOR;
        }
    }

    private class MapHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;

        public MapHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView)itemView.findViewById(R.id.text_view_name);
        }

        public TextView getNameTextView() {
            return nameTextView;
        }
    }
}
