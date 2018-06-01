package cscCCCIX.geocracy.fragments;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by mcp on 15/03/16.
 */
public class ExpandedBottomSheetBehavior<V extends View> extends android.support.design.widget.BottomSheetBehavior<V> {

    public ExpandedBottomSheetBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onLayoutChild(final CoordinatorLayout parent, final V child, final int layoutDirection) {
        SavedState dummySavedState = new SavedState(super.onSaveInstanceState(parent, child), STATE_EXPANDED);
        super.onRestoreInstanceState(parent, child, dummySavedState);
        return super.onLayoutChild(parent, child, layoutDirection);
        /*
            Unfortunately its not good enough to just call setState(STATE_EXPANDED); after super.onLayoutChild
            The reason is that an animation plays after calling setState. This can cause some graphical issues with other layouts
            Instead we need to use setInternalState, however this is a private method.
            The trick is to utilise onRestoreInstance to call setInternalState immediately and indirectly
         */
    }

}