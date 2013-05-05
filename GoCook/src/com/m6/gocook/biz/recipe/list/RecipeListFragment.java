package com.m6.gocook.biz.recipe.list;

import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.m6.gocook.R;
import com.m6.gocook.base.entity.RecipeList;
import com.m6.gocook.base.entity.RecipeList.RecipeItem;
import com.m6.gocook.base.fragment.BaseFragment;
import com.m6.gocook.biz.recipe.RecipeModel;

public abstract class RecipeListFragment extends BaseFragment implements OnScrollListener {
	
	/** Page Number */
	private int mPage = 1;
	/** 每页加载的数据条数 */  
    private static final int COUNT_PER_PAGE = 10;  
    /** 最后可见条目的索引  */
    private int mLastVisibleIndex;
    
    private ListView mListView;
    private View mFooterView;
    
    private RecipeListTask mRecipeListTask;
    private RecipeListAdapter mAdapter;
    private RecipeList mRecipeList;

	@Override
	public View onCreateFragmentView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_recipe_list, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		if(mRecipeListTask == null) {
			mRecipeListTask = new RecipeListTask(getActivity(), getURLWithPageNum());
			mRecipeListTask.execute((Void) null);
		}
		
		showProgress(true);
		
		View view = getView();
		mListView = (ListView) view.findViewById(R.id.list);
		mListView.setOnScrollListener(this);
		
		mFooterView = LayoutInflater.from(getActivity()).inflate(R.layout.base_more_footer, null);
		TextView loadingMoreFooter = (TextView) mFooterView.findViewById(R.id.text);
		loadingMoreFooter.setText(getString(R.string.base_loading_more, COUNT_PER_PAGE));
		
		mAdapter = new RecipeListAdapter(getActivity(), mImageFetcher, mRecipeList, getAdapterLayout());
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
        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && mLastVisibleIndex == mAdapter.getCount()) {  
        	mPage++;
        	mFooterView.setVisibility(view.VISIBLE);
        	if(mRecipeListTask == null) {
    			mRecipeListTask = new RecipeListTask(getActivity(), getURLWithPageNum());
    			mRecipeListTask.execute((Void) null);
    		}
        }
		
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mListView = null;
		mFooterView = null;
	}
	
	private String getURLWithPageNum() {
		return getURL() + mPage;
	}
	
	/**
	 * 子类实现此方法返回业务的URL
	 * 
	 * @return
	 */
	abstract protected String getURL();
	
	/**
	 * 子类重写此方法给出需要的Adapter Item Layout，默认使用adapter_recipe_list_item。</br>
	 * 注意，此方法返回的布局只适用于RecipeListAdapter(默认Adapter)，若使用getAdapter()返回自定义Adapter，则此方法不再有效。
	 * 
	 * @return
	 */
	protected int getAdapterLayout() {
		return R.layout.adapter_recipe_list_item;
	}
	
	/**
	 * 子类重写此方法返回自己需要的Adapter
	 * 
	 * @return
	 */
	protected BaseAdapter getAdapter() {
		return null;
	}
	
	private class RecipeListTask extends AsyncTask<Void, Void, RecipeList> {

    	private FragmentActivity mActivity;
    	private String mUrl;
    	
    	public RecipeListTask(FragmentActivity activity, String url) {
    		mActivity = activity;
    		mUrl = url;
    	}
    	
		@Override
		protected RecipeList doInBackground(Void... params) {
			return RecipeModel.getRecipeData(mUrl);
		}
    	
		@Override
		protected void onPostExecute(RecipeList result) {
			mRecipeListTask = null;
			showProgress(false);
			mFooterView.setVisibility(View.GONE);
			if (result != null && mActivity != null) {
				if(mAdapter != null) {
					if(mRecipeList == null) { // 第一页
						mRecipeList = result;
					} else {
						List<RecipeItem> list = mRecipeList.getRecipes();
						if(list != null) {
							list.addAll(result.getRecipes());
						}
					}
					mAdapter.setRecipeList(mRecipeList);
					mAdapter.notifyDataSetChanged();
				}
			}
		}
		
		@Override
		protected void onCancelled() {
			mRecipeListTask = null;
			showProgress(false);
		}
    }
}
