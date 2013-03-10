
package com.m6.gocook.base.view;

import com.m6.gocook.base.adapter.HeaderFooterAdapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

public class PullListView extends ListView implements OnScrollListener{
    private FrameLayout mRefreshViewContainer;

    private int mLastMotionX;

    private int mLastMotionY;

    private int mTouchSlop;
    
    private int mScrollState = OnScrollListener.SCROLL_STATE_IDLE;
    
    private OnScrollListener mOnScrollListener;
    
    private PullRefreshListView mPullRefreshListView;

    public PullListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public PullListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PullListView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {        
        mRefreshViewContainer = new FrameLayout(context);
//        addHeaderView(mRefreshViewContainer, null, false);

        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
        setOnScrollListener(this);
    }

    public void measureRefresh(int widthMeasureSpec, int heightMeasureSpec) {
        if (mRefreshViewContainer.getLayoutParams() == null) {
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(LayoutParams.FILL_PARENT,
                    LayoutParams.WRAP_CONTENT);
            mRefreshViewContainer.setLayoutParams(lp);
        }
        measureChild(mRefreshViewContainer, widthMeasureSpec, heightMeasureSpec);
    }
    
    void setPullRefreshListView(PullRefreshListView pullRefreshListView) {
    	mPullRefreshListView = pullRefreshListView;
    }
    
    @Override
    protected void layoutChildren() {
        super.layoutChildren();
    }
    
    protected void onDrawVerticalScrollBar(Canvas canvas, Drawable scrollBar,
            int l, int t, int r, int b) {
        if (mPullRefreshListView == null) {
            return;
        }
    	final int newT = Math.max(t, Math.abs(mPullRefreshListView.getRefreshHeight() + mPullRefreshListView.mTopPadding) + getPaddingTop());
    	if (t != newT) {
    		final int length = b - t;
    		t = newT;
        	b = Math.min(b, t + length);
    	}
        scrollBar.setBounds(l, t, r, b);
        scrollBar.draw(canvas);
    }
    
	@Override
    public void setAdapter(ListAdapter adapter) {
        if (adapter != null) {
            HeaderFooterAdapter headerFooterAdapter = new HeaderFooterAdapter(adapter, mRefreshViewContainer, null);
            headerFooterAdapter.showHeader(true);
            adapter = headerFooterAdapter;
        }
        super.setAdapter(adapter);
    }

    @Override
    public Object getItemAtPosition(int position) {
        position++;
        if (position >= getCount()) {
            return null;
        }
        return super.getItemAtPosition(position);
    }

//    @Override
//    public long getItemIdAtPosition(int position) {
//        position++;
//        if (position >= getCount()) {
//            return -1;
//        }
//        return super.getItemIdAtPosition(position);
//    }

    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
        if (listener != null) {
            final OnItemClickListener l = listener;
            listener = new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    l.onItemClick(arg0, arg1, arg2 - 1, arg3);
                }
            };
        }
        super.setOnItemClickListener(listener);
    }

    @Override
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        if (listener != null) {
            final OnItemLongClickListener l = listener;
            listener = new OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                    return l.onItemLongClick(arg0, arg1, arg2 - 1, arg3);
                }
            };
        }
        super.setOnItemLongClickListener(listener);
    }

    @Override
    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        if (listener != null) {
            final OnItemSelectedListener l = listener;
            listener = new OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    l.onItemSelected(arg0, arg1, arg2 - 1, arg3);
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    l.onNothingSelected(arg0);
                }
            };
        }
        super.setOnItemSelectedListener(listener);
    }

    @Override
    public void draw(Canvas canvas) {
        if (mPullRefreshListView != null && mPullRefreshListView.getScrollY() < 0) {
            dispatchDraw(canvas);
            return;
        }
        super.draw(canvas);
    }
    
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionX = (int) ev.getX();
                mLastMotionY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = Math.abs((int) ev.getX() - mLastMotionX);
                int deltaY = Math.abs((int) ev.getY() - mLastMotionY);
                if (deltaX > deltaY && deltaX > mTouchSlop) {
                    return false;
                }
                break;
            default:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean ret = super.onTouchEvent(ev);

        if (ev.getAction() == MotionEvent.ACTION_MOVE
                && mScrollState != OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
            int deltaX = Math.abs((int) ev.getX() - mLastMotionX);
            int deltaY = Math.abs((int) ev.getY() - mLastMotionY);
            if (deltaY > mTouchSlop || deltaX > mTouchSlop) {
                mScrollState = OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;
            }
        }
        return ret;
    }

    boolean hasTouchScrolled() {
        return mScrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;
    }


    public void setRefreshView(View refreshView) {
        mRefreshViewContainer.removeAllViews();
        mRefreshViewContainer.addView(refreshView);

    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        if (l == this) {
            super.setOnScrollListener(l);
            return;
        }
        mOnScrollListener = l;
    }
    
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
            int totalItemCount) { 
        if (mOnScrollListener != null) {
            mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        mScrollState = scrollState;   
        if (mOnScrollListener != null) {
            mOnScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

	@Override
	protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
		if (child == mRefreshViewContainer) {
			if (!mPullRefreshListView.needShowRefreshView()) {
				return false;
			}
		}
		
		return super.drawChild(canvas, child, drawingTime);
	}
    
    
}
