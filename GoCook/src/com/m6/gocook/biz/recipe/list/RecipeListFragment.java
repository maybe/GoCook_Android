package com.m6.gocook.biz.recipe.list;

import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.m6.gocook.R;
import com.m6.gocook.base.entity.RecipeList;
import com.m6.gocook.base.entity.RecipeList.RecipeItem;
import com.m6.gocook.base.fragment.BaseListFragment;
import com.m6.gocook.biz.account.AccountModel;
import com.m6.gocook.biz.recipe.RecipeModel;
import com.m6.gocook.biz.recipe.recipe.RecipeFragment;

public abstract class RecipeListFragment extends BaseListFragment {
	
    private RecipeListTask mRecipeListTask;
    private RecipeListAdapter mAdapter;
    private RecipeList mRecipeList;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		mAdapter = new RecipeListAdapter(getActivity(), mImageFetcher, mRecipeList, getAdapterLayout());
		super.onActivityCreated(savedInstanceState);
		
		ListView listView = getListView();
		if(listView != null) {
			listView.setOnItemClickListener(new OnItemClickListener() {
				
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Object item = (RecipeItem) mAdapter.getItem(position);
					if(item != null) {
						String recipeId = ((RecipeItem) item).getId();
						RecipeFragment.startInActivity(getActivity(), recipeId);
					}
				}
			});
		}
	}
	
	@Override
	protected final void executeTask() {
		if(mRecipeListTask == null) {
			mRecipeListTask = new RecipeListTask(getActivity(), getURLWithPageNum());
			mRecipeListTask.execute((Void) null);
		}
	}
	
	@Override
	protected BaseAdapter getAdapter() {
		return mAdapter;
	}
	
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
	 * 子类重写改方法返回业务需要的数据列表
	 * 
	 * @param url
	 * @return
	 */
	protected RecipeList getListData(String url) {
		return RecipeModel.getRecipeData(url, needCookie() ? AccountModel.getCookie(getActivity()) : null);
	}
	
	/**
	 * 获取数据的请求是否需要带cookie
	 * 
	 * @return
	 */
	protected boolean needCookie() {
		return false;
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
			return getListData(mUrl);
		}
    	
		@Override
		protected void onPostExecute(RecipeList result) {
			if (!isAdded()) {
				return;
			}
			
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
