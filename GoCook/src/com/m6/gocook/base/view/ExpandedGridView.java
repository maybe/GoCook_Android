package com.m6.gocook.base.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.GridView;

import com.m6.gocook.R;

public class ExpandedGridView extends GridView
{

    boolean mDiasbleScroll = false;

    public boolean setScrollEnablement() {
		return mDiasbleScroll;
	}

	public ExpandedGridView(Context context)
    {
        super(context);
    }

    public ExpandedGridView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        
    	TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.Attrubites); 
    	mDiasbleScroll = array.getBoolean(R.styleable.Attrubites_layout_disbleScroll, false);
    }

    public ExpandedGridView(Context context, AttributeSet attrs,
            int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        // HACK! TAKE THAT ANDROID!
        if (mDiasbleScroll)
        {
            // Calculate entire height by providing a very large height hint.
            // But do not use the highest 2 bits of this integer; those are
            // reserved for the MeasureSpec mode.
            int expandSpec = MeasureSpec.makeMeasureSpec(
                    Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);

            ViewGroup.LayoutParams params = getLayoutParams();
            params.height = getMeasuredHeight();
        }
        else
        {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
    
}