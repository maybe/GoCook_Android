
package com.m6.gocook.base.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Scroller;

public class PullRefreshListView extends FrameLayout implements ListViewDelegator {
    public final static int INVALID_VALUE = -1;

    public final static int REFRESH_STATE_IDLE = 0;

    public final static int REFRESH_STATE_SHOWING = REFRESH_STATE_IDLE + 1;

    public final static int REFRESH_STATE_PREPARE_REFRESHING = REFRESH_STATE_SHOWING + 1;

    public final static int REFRESH_STATE_REFRESHING = REFRESH_STATE_PREPARE_REFRESHING + 1;

    private final static int REFRESH_STATE_PREPARE_REFRESH_DONE = 9999;


    private View mRefreshView;

    private PullListView mListView;

    private int mRefreshState = REFRESH_STATE_IDLE;

    private OnRefreshListener mOnRefreshListener;

    private String mTag;

    private int mLastMotionY;

    private float mDragFactor = 2.f;

    private int mAnimationDuration = 500;

    private FlingRunnable mFlingRunnable = new FlingRunnable();

    private boolean mForbidPull = false;

    private Runnable mInLayoutRunnable;

    private RefreshTask mRefreshTask = new RefreshTask();

    private RefreshDoneTask mRefreshDoneTask = new RefreshDoneTask();

    private boolean mAnimateRefresh = false;
    
    private boolean mHasTouchEvent = false;
    
    private int mRefreshViewId = 0;
    
    int mTopPadding = 0;

    public PullRefreshListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public PullRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PullRefreshListView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mListView = (PullListView) findViewById(android.R.id.list);
        mListView.setPullRefreshListView(this);
    }
    
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mInLayoutRunnable != null) {
            removeCallbacks(mInLayoutRunnable);
        }
        removeCallbacks(mFlingRunnable);
        removeCallbacks(mRefreshTask);
        removeCallbacks(mRefreshDoneTask);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mListView.layout(0, Math.min(getMeasuredHeight() - mListView.getMeasuredHeight(), 0),
                getMeasuredWidth(), getMeasuredHeight());
        if (mInLayoutRunnable != null) {
            post(mInLayoutRunnable);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (null!=mRefreshView /*&& mRefreshView.getMeasuredHeight() == 0*/) {
            mListView.measureRefresh(widthMeasureSpec, heightMeasureSpec);
        }

        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mListView.getLayoutParams();
        if (mRefreshState == REFRESH_STATE_REFRESHING && mInLayoutRunnable == null && mFlingRunnable.mScroller.isFinished()) {
            lp.topMargin = 0;
        } else {
            lp.topMargin = -getRefreshHeight();
        }

        // 以下写法在4.0以下系统显示异常
        /*
         * measureChildWithMargins(mListView, widthMeasureSpec, 0,
         * heightMeasureSpec, 0); int measuredWidth =
         * MeasureSpec.getSize(widthMeasureSpec); int measuredHeight =
         * MeasureSpec.getSize(heightMeasureSpec);
         * setMeasuredDimension(measuredWidth, measuredHeight);
         */

        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measuredHeight = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(measuredWidth, measuredHeight);

        int listMeasuredWidth = MeasureSpec.makeMeasureSpec(MeasureSpec.EXACTLY, measuredWidth);
        int listMeasuredHeight = MeasureSpec.makeMeasureSpec(MeasureSpec.EXACTLY, measuredHeight
                - lp.topMargin);
        mListView.measure(listMeasuredWidth, listMeasuredHeight);

    }
    
    boolean needShowRefreshView() {
    	FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mListView.getLayoutParams();
    	return getScrollY() < 0 || lp.topMargin >= 0;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int scrollYOffset = 0;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mHasTouchEvent = false;
                mLastMotionY = (int) ev.getY();
                break;
            }
            case MotionEvent.ACTION_MOVE: {

                final int y = (int) (ev.getY());
                int deltaY = y - mLastMotionY;
                boolean hasListScrolled = mListView.hasTouchScrolled();
                
                if (!mHasTouchEvent) {
                    mHasTouchEvent = !mForbidPull && mFlingRunnable.mScroller.isFinished() && hasListScrolled && willInterpreterTouchEvent(deltaY);
                }
                
                
                if (mHasTouchEvent) {
                    scrollYOffset = deltaY;
                    mLastMotionY = y;
                } else if (hasListScrolled) {
                    if (!mFlingRunnable.mScroller.isFinished()) {
                        scrollYOffset = deltaY;
                    }
                    mLastMotionY = y;
                }
                break;
            }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                if (mHasTouchEvent) {
                    onUp();
                    mHasTouchEvent = false;
                }
                break;
            }
        }

        
        if (scrollYOffset != 0) {
            scrollBy(0, -scrollYOffset);
        }

        boolean ret = super.dispatchTouchEvent(ev);
        
        if (scrollYOffset != 0) {
            scrollBy(0, scrollYOffset);
            
            if (mFlingRunnable.mScroller.isFinished()) {
                mHasTouchEvent = onMove(scrollYOffset);
            }
        }
        return ret;
    }
    
    private boolean onMove(int deltaY) {
        if (deltaY > 0) {
            
            deltaY = (int) (deltaY / mDragFactor);
        }
        trackMotionScroll(-deltaY);
        final int nextState = getNextRefreshState();
        if (nextState == REFRESH_STATE_REFRESHING && isRefreshing()) {

        } else {
            updateRereshState(nextState, false);
        }

        return getScrollY() != 0;
    }

    public void onUp() {
        if (mFlingRunnable.mScroller.isFinished()) {
            if (mRefreshState == REFRESH_STATE_REFRESHING) {
                if (getScrollY() < 0) {
                    mFlingRunnable.startUsingDistance(-getScrollY());
                }
            } else if (mRefreshState == REFRESH_STATE_PREPARE_REFRESHING) {
                updateRereshState(REFRESH_STATE_REFRESHING, true);
            } else if (mRefreshState == REFRESH_STATE_SHOWING) {
                updateRereshState(REFRESH_STATE_IDLE, true);
            }
        }
        mListView.invalidate();
    }

    private void trackMotionScroll(int deltaY) {
        if (deltaY == 0) {
            return;
        }
        deltaY = getLimitedMotionScrollAmount(deltaY);

        scrollBy(0, deltaY);
        mListView.invalidate(mListView.getLeft(), mListView.getTop(), mListView.getRight(), getRefreshHeight() + mTopPadding);
    }

    private int getLimitedMotionScrollAmount(int deltaY) {
        final int scrollY = getScrollY();
        return deltaY < 0 ? (Math.max(-getMeasuredHeight() - scrollY, deltaY)) : Math.min(
                0 - scrollY, deltaY);
    }

    private boolean willInterpreterTouchEvent(int deltaY) {
        final ListView listView = mListView;
//        System.out.println("deltaY > 0="+(deltaY > 0));
//        System.out.println("listView.getAdapter() == null="+(listView.getAdapter() == null));
//        System.out.println("listView.getAdapter().getCount() == 0="+(listView.getAdapter().getCount() == 0));
//        System.out.println("listView.getFirstVisiblePosition() == 0="+(listView.getFirstVisiblePosition() == 0));
//        System.out.println("listView.getChildAt(0) != null="+(listView.getChildAt(0) != null));
//        System.out.println("listView.getChildAt(0).getTop() >= 0="+(listView.getChildAt(0).getTop() >= 0) + ",top="+listView.getChildAt(0).getTop());
        return deltaY > 0
                && (listView.getAdapter() == null || listView.getAdapter().getCount() == 0 || (listView
                        .getFirstVisiblePosition() == 0
                        && listView.getChildAt(0) != null && listView.getChildAt(0).getTop() >= 0));
    }

    public int getRefreshHeight() {
        return null==mRefreshView?0:mRefreshView.getMeasuredHeight() + mListView.getDividerHeight() - mTopPadding;
    }

    private int getNextRefreshState() {
        if (isRefreshing()) {
            return REFRESH_STATE_REFRESHING;
        }

        final int scrollY = getScrollY();

        int state;
        if (scrollY <= -getRefreshHeight()) {
            state = REFRESH_STATE_PREPARE_REFRESHING;
        } else if (scrollY < 0) {
            state = REFRESH_STATE_SHOWING;
        } else {
            state = REFRESH_STATE_IDLE;
        }

        return state;
    }

    private void updateRereshState(int state, boolean animate) {
        if (mRefreshState == state) {
            return;
        }

        if (state == REFRESH_STATE_REFRESHING) {
            // 如果不允许刷新，重新置为idle状态
            if (mOnRefreshListener == null || !mOnRefreshListener.onPrepareRefresh(mTag)) {
                state = REFRESH_STATE_IDLE;
            }
        }

        if (mRefreshState == state) {
            return;
        }

        final int lastState = mRefreshState == REFRESH_STATE_PREPARE_REFRESH_DONE ? REFRESH_STATE_REFRESHING
                : mRefreshState;
        mRefreshState = state;
        final int newState = mRefreshState == REFRESH_STATE_PREPARE_REFRESH_DONE ? REFRESH_STATE_REFRESHING
                : mRefreshState;
        if (mOnRefreshListener != null && newState != lastState) {
            mOnRefreshListener.updateRefreshView(mTag, mRefreshView, lastState, newState);
        }

        switch (state) {
            case REFRESH_STATE_IDLE: {
                if (animate) {
                    mFlingRunnable.startUsingDistance(-getScrollY(), null, false);
                } else {
                    scrollTo(0, 0);
                }
                break;
            }
            case REFRESH_STATE_SHOWING: {
                break;
            }
            case REFRESH_STATE_PREPARE_REFRESHING: {
                break;
            }
            case REFRESH_STATE_REFRESHING: {
                mAnimateRefresh = animate;
                if (animate) {
                    mFlingRunnable.startUsingDistance(-getRefreshHeight() - getScrollY(),
                            mRefreshTask, false);
                } else {
                    scrollTo(0, 0);
                    if (mOnRefreshListener != null) {
                        mOnRefreshListener.doRefresh(mTag);
                    }
                }
                break;
            }
            case REFRESH_STATE_PREPARE_REFRESH_DONE: {
                if (animate && mAnimateRefresh) {
                    // 当显示的第一个view不是第0条时，不使用动画
                    if (mListView.getChildCount() > 0 && mListView.getFirstVisiblePosition() != 0) {
                        scrollTo(0, 0);
                        updateRereshState(REFRESH_STATE_IDLE, false);
                        return;
                    } if (mListView.getFirstVisiblePosition() == 0 && mListView.getChildAt(0) != null) {
                        scrollBy(0, -getRefreshHeight() - mListView.getChildAt(0).getTop());
                    } else {
                        scrollBy(0, -getRefreshHeight());
                    }
                    
                    
                    
                    FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mListView
                            .getLayoutParams();
                    lp.topMargin = -getRefreshHeight();

                    int listMeasuredWidth = MeasureSpec.makeMeasureSpec(MeasureSpec.EXACTLY, getMeasuredWidth());
                    int listMeasuredHeight = MeasureSpec.makeMeasureSpec(MeasureSpec.EXACTLY, getMeasuredHeight()
                            - lp.topMargin);
                    mListView.measure(listMeasuredWidth, listMeasuredHeight);
                    mListView.layout(0, 0 - lp.topMargin, getMeasuredWidth(), getMeasuredHeight());

                    mFlingRunnable.startUsingDistance(-getScrollY(), mRefreshDoneTask,
                            false);

                } else {
                    scrollTo(0, 0);
                    updateRereshState(REFRESH_STATE_IDLE, false);
                }
                mAnimateRefresh = false;
                break;
            }
            default:
                break;
        }
    }

    private void resetInternal() {
        if (mInLayoutRunnable != null) {
            removeCallbacks(mInLayoutRunnable);
            mInLayoutRunnable = null;
        }
        mAnimateRefresh = false;
        mFlingRunnable.stop(true, false);
        scrollTo(0, 0);

    }

    public boolean isRefreshing() {
        return mRefreshState == REFRESH_STATE_REFRESHING
                || mRefreshState == REFRESH_STATE_PREPARE_REFRESH_DONE;
    }

    public void reset() {
        updateRereshState(REFRESH_STATE_IDLE, false);
        resetInternal();
    }

    public void refreshDone() {
        if (mRefreshState != REFRESH_STATE_REFRESHING) {
            return;
        }
        updateRereshState(REFRESH_STATE_PREPARE_REFRESH_DONE, true);
        requestLayout();
    }

    public void refresh() {
    	if (isRefreshing() || mOnRefreshListener == null || !mOnRefreshListener.onPrepareRefresh(mTag)) {
            return;
        }
        if (getHeight() == 0) {
            if (mInLayoutRunnable != null) {
                return;
            }
            mInLayoutRunnable = new Runnable() {
                @Override
                public void run() {
                    mInLayoutRunnable = null;
                    updateRereshState(REFRESH_STATE_REFRESHING, true);
                }
            };
        } else {
            if (isRefreshing()) {
                return;
            } else if (mForbidPull) {
                mListView.setSelection(0);
                updateRereshState(REFRESH_STATE_PREPARE_REFRESHING, false);
            } else {
                mInLayoutRunnable = new Runnable() {
                    @Override
                    public void run() {
                        mInLayoutRunnable = null;
                        updateRereshState(REFRESH_STATE_REFRESHING, true);
                    }
                };
                mListView.setSelection(0);
                requestLayout();
            }
           
        }
    }

    public void refreshDoneNoAnimate() {
        if (mRefreshState != REFRESH_STATE_REFRESHING) {
            return;
        }
        updateRereshState(REFRESH_STATE_PREPARE_REFRESH_DONE, false);
        requestLayout();
    }

    public void refreshNoAnimate() {
        if (isRefreshing() || mOnRefreshListener == null || !mOnRefreshListener.onPrepareRefresh(mTag)) {
            return;
        }
        mListView.setSelection(0);
        updateRereshState(REFRESH_STATE_REFRESHING, false);
    }

    public void forbidPull(boolean b) {
        mForbidPull = b;

        if (b) {
            resetInternal();
            hideRefreshView();
        } else {
            showRefreshView();
        }
    }

    private void hideRefreshView() {
        mRefreshView.setVisibility(View.INVISIBLE);
    }

    public void showRefreshView() {
        mRefreshView.setVisibility(View.VISIBLE);
    }

    public void setRefreshView(int id) {
        if (mRefreshViewId != id) {
            mRefreshViewId = id;
            mRefreshView = inflate(getContext(), id, null);
            mListView.setRefreshView(mRefreshView);
        }
    }
    
    public void setRefreshViewTopPadding(int padding) {
        mTopPadding = padding;
    }
    
    public View getRefreshView() {
        return mRefreshView;
    }

    public void setOnRefreshListener(String tag, OnRefreshListener l) {
        mTag = tag;
        mOnRefreshListener = l;
    }

    public ListView getListView() {
        return mListView;
    }

    private class RefreshDoneTask implements Runnable {
        @Override
        public void run() {
            updateRereshState(REFRESH_STATE_IDLE, false);
        }

    }

    private class RefreshTask implements Runnable {
        @Override
        public void run() {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mListView.getLayoutParams();
            lp.topMargin = 0;
//            mListView.requestLayout();
            int listMeasuredWidth = MeasureSpec.makeMeasureSpec(MeasureSpec.EXACTLY, getMeasuredWidth());
            int listMeasuredHeight = MeasureSpec.makeMeasureSpec(MeasureSpec.EXACTLY, getMeasuredHeight()
                    - lp.topMargin);
            mListView.measure(listMeasuredWidth, listMeasuredHeight);
            mListView.layout(0, 0 - lp.topMargin, getMeasuredWidth(), getMeasuredHeight());
            scrollTo(0, 0);
            post(new Runnable() {
                @Override
                public void run() {
                    if (mOnRefreshListener != null) {
                        mOnRefreshListener.doRefresh(mTag);
                    }

                }
            });
        }

    }

    public interface OnRefreshListener {
        // 更新刷新view
        public void updateRefreshView(final String tag, final View view, final int lastState,
                final int state);

        // 检测是否进行刷新
        public boolean onPrepareRefresh(final String tag);

        // 耗时操作，在单独线程中
        public void doRefresh(final String tag);
    }

    private class FlingRunnable implements Runnable {
        private Scroller mScroller;

        private int mLastFlingY;

        private Runnable mEndRunnable;

        public FlingRunnable() {
            mScroller = new Scroller(getContext());
        }

        private void startCommon() {
            removeCallbacks(this);
        }


        public void startUsingDistance(int distance, Runnable task, boolean postLastTask) {
            endFling(true, postLastTask);

            mEndRunnable = task;
            if (distance == 0) {
                endFling(true, true);
                return;
            }

            startCommon();

            mLastFlingY = 0;
            mScroller.startScroll(0, 0, 0, -distance, mAnimationDuration);
            post(this);
        }

        public void startUsingDistance(int distance) {
            startUsingDistance(distance, null, false);
        }

        public void stop(boolean scrollIntoSlots, boolean postTask) {
            removeCallbacks(this);
            endFling(scrollIntoSlots, postTask);
        }

        private void endFling(boolean scrollIntoSlots, boolean postTask) {
            mScroller.forceFinished(true);

            if (postTask && mEndRunnable != null) {
                post(mEndRunnable);
            }
            mEndRunnable = null;
            mListView.invalidate();
        }

        @Override
        public void run() {
            boolean shouldStopFling = false;

            final Scroller scroller = mScroller;
            boolean more = scroller.computeScrollOffset();
            final int y = scroller.getCurrY();

            int delta = mLastFlingY - y;
            trackMotionScroll(delta);

            final int scrollY = getScrollY();
            int targetscrollY = 0;
            if (scrollY == targetscrollY) {
                shouldStopFling = true;
            }

            if (more && !shouldStopFling) {
                mLastFlingY = y;
                post(this);
            } else {
                endFling(true, true);
            }
        }

    }
}
