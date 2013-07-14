package com.m6.gocook.base.fragment;

import com.m6.gocook.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

public abstract class BaseListFragment extends BaseFragment implements OnScrollListener {

	/** Page Number */
	private int mPage = 1;
	
	/** 每页加载的数据条数 */  
    private static final int COUNT_PER_PAGE = 10;
    
    /** 最后可见条目的索引  */
    private int mLastVisibleIndex;
    
    private ListView mListView;
    protected View mFooterView;
    
    private BaseAdapter mAdapter;
    
    @Override
	public View onCreateFragmentView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.base_fragment_list, container, false);
	}
    
    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		showProgress(true);
		
		executeTask();
		
		View view = getView();
		mListView = (ListView) view.findViewById(R.id.list);
		mListView.setOnScrollListener(this);
		
		mFooterView = LayoutInflater.from(getActivity()).inflate(R.layout.base_more_footer, null);
		TextView loadingMoreFooter = (TextView) mFooterView.findViewById(R.id.text);
		loadingMoreFooter.setText(getString(R.string.base_loading_more, COUNT_PER_PAGE));
		
		mAdapter = getAdapter();
		mListView.addFooterView(mFooterView);
		mListView.setAdapter(mAdapter);
    }
    
    @Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		mLastVisibleIndex = firstVisibleItem + visibleItemCount - 1;
	}
    
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// 滑到底部后自动加载，判断listview已经停止滚动并且最后可视的条目等于adapter的条目  
        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && mLastVisibleIndex == mAdapter.getCount() && doPaging()) {  
        	mPage++;
        	mFooterView.setVisibility(View.VISIBLE);
        	executeTask();
        }
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		
		mListView = null;
		mFooterView = null;
	}
	
	protected ListView getListView() {
		return mListView;
	}
	
	protected String getURLWithPageNum() {
		return getURL() + mPage;
	}
	
	/**
	 * 是否分页 
	 * 
	 * @return
	 */
	protected boolean doPaging() {
		return true;
	}
	
	/**
	 * 子类实现此方法返回业务的URL
	 * 
	 * @return
	 */
	abstract protected String getURL();
	
	/**
	 * 子类实现此方法执行业务所需的任务
	 */
	abstract protected void executeTask();
	
	
	/**
	 * 子类重写此方法返回自己需要的Adapter
	 * 
	 * @return
	 */
	abstract protected BaseAdapter getAdapter();

}
