package jsettlers.main.android.utils;

import android.support.v7.widget.DefaultItemAnimator;

/**
 * Created by Tom on 20/01/2016.
 */
public class NoChangeItemAnimator extends DefaultItemAnimator {
    public NoChangeItemAnimator() {
        setSupportsChangeAnimations(false);
    }
}
