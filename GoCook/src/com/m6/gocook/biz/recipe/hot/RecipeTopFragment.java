package com.m6.gocook.biz.recipe.hot;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.m6.gocook.R;
import com.m6.gocook.base.entity.RecipeHot;
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
		Bundle args = getArguments();
		if(args != null) {
			String param = args.getString(RecipeTopActivity.PARAM_FROM);
			if(RecipeTopActivity.PARAM_FROM_HOT.equalsIgnoreCase(param)) {
				url = Protocol.URL_RECIPE_HOT;
			} else {
				url = Protocol.URL_RECIPE_NEW;
			}
		} else {
			url = Protocol.URL_RECIPE_NEW;
		}
		
		ActionBar actionBar = getAction();
		actionBar.setTitle(R.string.biz_popular_tophot);
		
		new HotRecipeTask(getActivity(), mImageFetcher, url).execute((Void) null);
	}

	private static class HotRecipeTask extends AsyncTask<Void, Void, RecipeHot> {

		private FragmentActivity mContext;
		private ImageFetcher mImageFetcher;
		private String mUrl;

		public HotRecipeTask(FragmentActivity context, ImageFetcher imageFetcher, String url) {
			mContext = context;
			mImageFetcher = imageFetcher;
			mUrl = url;
		}

		@Override
		protected RecipeHot doInBackground(Void... params) {
			return RecipeModel.getRecipeTop(mUrl, 1);
		}

		@Override
		protected void onPostExecute(RecipeHot result) {
			if (result != null && mContext != null) {
				RecipeTopAdapter adapter = new RecipeTopAdapter(mContext,
						mImageFetcher, result);
				ListView list = (ListView) mContext.findViewById(R.id.list);
				list.setAdapter(adapter);
			}

		}
	}
}
