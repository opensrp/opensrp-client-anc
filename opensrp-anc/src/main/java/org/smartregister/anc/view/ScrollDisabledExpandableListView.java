package org.smartregister.anc.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ExpandableListView;

public class ScrollDisabledExpandableListView extends ExpandableListView {
    private  int position;
    public ScrollDisabledExpandableListView(Context context) {
        super(context);
    }

    public ScrollDisabledExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollDisabledExpandableListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int actionMasked = ev.getActionMasked() & MotionEvent.ACTION_MASK;

        if (actionMasked == MotionEvent.ACTION_DOWN) {
            // Record the position the list the touch landed on
            position = pointToPosition((int) ev.getX(), (int) ev.getY());
            return super.dispatchTouchEvent(ev);
        }

        if (actionMasked == MotionEvent.ACTION_MOVE) {
            // Ignore move events
            return true;
        }

        if (actionMasked == MotionEvent.ACTION_UP) {
            // Check if we are still within the same view
            if (pointToPosition((int) ev.getX(), (int) ev.getY()) == position) {
                super.dispatchTouchEvent(ev);
            } else {
                // Clear pressed state, cancel the action
                setPressed(false);
                invalidate();
                return true;
            }
        }

        return super.dispatchTouchEvent(ev);
    }
}
