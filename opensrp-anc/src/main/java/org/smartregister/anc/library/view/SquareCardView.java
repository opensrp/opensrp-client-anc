package org.smartregister.anc.library.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.cardview.widget.CardView;

//import androidx.cardview.widget.CardView;

public class SquareCardView extends CardView {
    public SquareCardView(Context context) {
        super(context);
    }

    public SquareCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}