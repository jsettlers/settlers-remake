package jsettlers.main.android.core.controls;

import jsettlers.common.map.partition.IPartitionData;

/**
 * Created by tompr on 27/05/2017.
 */

public interface PositionControls {
    boolean isInPlayerPartition();
    IPartitionData getCurrentPartitionData();

    void addPositionChangedListener(PositionChangedListener positionChangedListener);

    void removePositionChangedListener(PositionChangedListener positionChangedListener);
}
