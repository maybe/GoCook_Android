package com.m6.gocook.biz.recipe.list;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.m6.gocook.R;
import com.m6.gocook.base.entity.RecipeListItem;
import com.m6.gocook.base.entity.RecipeListItem.RecipeItem;
import com.m6.gocook.base.fragment.BaseFragment;
import com.m6.gocook.biz.recipe.RecipeModel;
import com.m6.gocook.biz.recipe.recipe.RecipeFragment;

public abstract class RecipeListFragment extends BaseFragment {
	
	protected int mPage = 1;

	@Override
	public View onCreateFragmentView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_recipe_list, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		new RecipeListTask(getActivity(), getURL()).execute((Void) null);
		showProgress(true);
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
	
	private class RecipeListTask extends AsyncTask<Void, Void, RecipeListItem> {

    	private FragmentActivity mActivity;
    	private String mUrl;
    	
    	public RecipeListTask(FragmentActivity activity, String url) {
    		mActivity = activity;
    		mUrl = url;
    	}
    	
		@Override
		protected RecipeListItem doInBackground(Void... params) {
			return RecipeModel.getRecipeData(mUrl);
		}
    	
		@Override
		protected void onPostExecute(RecipeListItem result) {
			showProgress(false);
			if (result != null && mActivity != null) {
				BaseAdapter adapter = getAdapter();
				if(adapter == null) {
					adapter = new RecipeListAdapter(mActivity, mImageFetcher, result, getAdapterLayout());
				}
				final BaseAdapter shandowAdapter = adapter;
				ListView list = (ListView) mActivity.findViewById(R.id.list);
				list.setAdapter(adapter);
				list.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Object item = (RecipeItem) shandowAdapter.getItem(position);
						if(item != null) {
							String recipeId = ((RecipeItem) item).getId();
							RecipeFragment.startInActivity(getActivity(), recipeId);
						}
					}
				});
			}
		}
		
		@Override
		protected void onCancelled() {
			super.onCancelled();
			showProgress(false);
		}
    }
}
