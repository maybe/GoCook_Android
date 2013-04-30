package com.m6.gocook.biz.recipe.top;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.m6.gocook.R;
import com.m6.gocook.base.entity.RecipeListItem;
import com.m6.gocook.base.fragment.BaseFragment;
import com.m6.gocook.base.protocol.Protocol;
import com.m6.gocook.base.view.ActionBar;
import com.m6.gocook.biz.recipe.RecipeModel;
import com.m6.gocook.util.cache.util.ImageFetcher;

public class RecipeTopFragment extends BaseFragment {

	@Override
	public View onCreateFragmentView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_recipe_top, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		String url;
		ActionBar actionBar = getAction();
		Bundle args = getArguments();
		if(args != null) {
			String param = args.getString(RecipeTopActivity.PARAM_FROM);
			if(RecipeTopActivity.PARAM_FROM_HOT.equalsIgnoreCase(param)) {
				actionBar.setTitle(R.string.biz_popular_tophot);
				url = Protocol.URL_RECIPE_HOT;
			} else {
				actionBar.setTitle(R.string.biz_popular_topnew);
				url = Protocol.URL_RECIPE_NEW;
			}
		} else {
			actionBar.setTitle(R.string.biz_popular_tophot);
			url = Protocol.URL_RECIPE_HOT;
		}
		
		new HotRecipeTask(this, mImageFetcher, url).execute((Void) null);
		showProgress(true);
	}

	private class HotRecipeTask extends AsyncTask<Void, Void, RecipeListItem> {

		private Fragment mContext;
		private ImageFetcher mImageFetcher;
		private String mUrl;

		public HotRecipeTask(Fragment context, ImageFetcher imageFetcher, String url) {
			mContext = context;
			mImageFetcher = imageFetcher;
			mUrl = url;
		}

		@Override
		protected RecipeListItem doInBackground(Void... params) {
			return RecipeModel.getRecipeTop(mUrl, 1);
		}

		@Override
		protected void onPostExecute(RecipeListItem result) {
			if (result != null && mContext != null) {
				View root = mContext.getView();
				showProgress(false);
				
				RecipeTopAdapter adapter = new RecipeTopAdapter(mContext.getActivity(),
						mImageFetcher, result);
				ListView list = (ListView) root.findViewById(R.id.list);
				list.setAdapter(adapter);
			}
		}
		
		@Override
		protected void onCancelled() {
			super.onCancelled();
			showProgress(false);
		}
	}
}
