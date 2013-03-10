
package com.m6.gocook.base.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

/**
 * 使用本方法可以方便对header 和 footer view进行显示不显示控制</br> 如果使用系统的，在使用时需要控制时机，否则可能报错</br>
 * 目前只支持一个header和一个footer
 * 
 */
public class HeaderFooterAdapter extends MergeAdapter {

    static final int TYPE_HEADER = 1001;

    static final int TYPE_FOOTER = 1002;

    private InnerAdapter mHeaderAdapter;

    private InnerAdapter mFooterAdapter;

    private ListAdapter mOrigAdapter;
    
    private HeaderFooterViewBinder mViewBinder;

    public HeaderFooterAdapter(ListAdapter origAdapter, View header, View footer) {
//        if (header != null) {
            mHeaderAdapter = new InnerAdapter(header, true);
            addAdapter(mHeaderAdapter);
//        }

        mOrigAdapter = origAdapter;
        if (origAdapter != null) {
        	addAdapter(origAdapter);
        }

//        if (footer != null) {
            mFooterAdapter = new InnerAdapter(footer, false);
            addAdapter(mFooterAdapter);
//        }
    }

    @Override
    public boolean isEmpty() {
    	return mOrigAdapter == null || mOrigAdapter.isEmpty();
    }
    
    /**
     * 返回原始Adapter
     * 
     * @return
     */
    public ListAdapter getOrigAdapter() {
        return mOrigAdapter;
    }

    /**
     * 显示header View
     * 
     * @param show
     */
    public void showHeader(boolean show) {
        if (mHeaderAdapter != null) {
            mHeaderAdapter.show(show);
        }
    }

    /**
     * 显示footer View
     * 
     * @param show
     */
    public void showFooter(boolean show) {
        if (mFooterAdapter != null) {
            mFooterAdapter.show(show);
        }
    }

    /**
     * 更新header View
     * 
     * @param view
     */
    public void updateHeader(View view) {
        if (mHeaderAdapter != null) {
            mHeaderAdapter.updateView(view);
        }
    }

    /**
     * 更新footer View
     * 
     * @param view
     */
    public void updateFooter(View view) {
        if (mFooterAdapter != null) {
            mFooterAdapter.updateView(view);
        }
    }

    /**
     * header view 是否显示
     * 
     * @return
     */
    public boolean isHeaderShown() {
        if (mHeaderAdapter != null) {
            return mHeaderAdapter.isShown();
        }
        return false;
    }

    /**
     * footer view 是否显示
     * 
     * @return
     */
    public boolean isFooterShown() {
        if (mFooterAdapter != null) {
            return mFooterAdapter.isShown();
        }
        return false;
    }

    /**
     * 设置View Binder
     * 
     * @param binder
     */
    public void setViewBinder(HeaderFooterViewBinder binder) {
        mViewBinder = binder;
        if (mHeaderAdapter != null) {
            mHeaderAdapter.setViewBinder(binder);
        }
        if (mFooterAdapter != null) {
            mFooterAdapter.setViewBinder(binder);
        }
    }
    
    public View getHeaderView() {
        if (mHeaderAdapter != null) {
            mHeaderAdapter.getView();
        }
        return null;
    }
    
    public View getFooterView() {
        if (mFooterAdapter != null) {
            mFooterAdapter.getView();
        }
        return null;
    }
    
    /**
     * 返回View Binder
     * 
     * @return
     */
    public HeaderFooterViewBinder getViewBinder() {
        return mViewBinder;
    }

    private static class InnerAdapter extends BaseAdapter {

        private HeaderFooterViewBinder mViewBinder;

        private boolean mIsHeader;

        private View mView;

        private boolean mShown = false;

        InnerAdapter(View view, boolean isHeader) {
            mView = view;
            mIsHeader = isHeader;
        }

        @Override
        public int getCount() {
            return isShown() ? 1 : 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (mViewBinder != null) {
                mViewBinder.bindHeaderFooterView(mView, mIsHeader);
            }
            return mView;
        }
        

        @Override
        public boolean isEnabled(int position) {
            return !mIsHeader;
        }

        private boolean isShown() {
            return mShown && mView != null;
        }

        void show(boolean show) {
            mShown = mView == null ? false : show;
            notifyDataSetChanged();
        }

        void updateView(View view) {
            mView = view;
            notifyDataSetChanged();
        }

        void setViewBinder(HeaderFooterViewBinder binder) {
            mViewBinder = binder;
        }
        
        View getView() {
            return mView;
        }
    }

    /**
     * header footer view 数据绑定回调接口
     * 
     * @author wjying
     */
    public static interface HeaderFooterViewBinder {
        /**
         * header footer view 数据绑定回调
         * 
         * @param view header 或者 footer view
         * @param isHeader true为header view
         */
        public void bindHeaderFooterView(View view, boolean isHeader);
    }
}
