package com.m6.gocook.biz.profile;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.m6.gocook.R;
import com.m6.gocook.base.constant.PrefKeys;
import com.m6.gocook.base.fragment.BaseFragment;
import com.m6.gocook.base.view.ActionBar;
import com.m6.gocook.biz.account.AccountModel;
import com.m6.gocook.biz.recipe.recipe.RecipeFragment;
import com.m6.gocook.util.preference.PrefHelper;

public class ProfileFragment extends BaseFragment {
	
	@Override
	public View onCreateFragmentView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_profile, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		FragmentActivity activity = getActivity();
		View view = getView();
		ActionBar action = getAction();
		
		ImageView avatar = (ImageView) activity.findViewById(R.id.avatar);
		// 取本地数据
		String avatarPath = PrefHelper.getString(activity, PrefKeys.ACCOUNT_AVATAR, "");
		if(!TextUtils.isEmpty(avatarPath)) {
			avatar.setImageBitmap(BitmapFactory.decodeFile(avatarPath));
		}
		
		String username = AccountModel.getUsername(activity);
		action.setTitle(username);
		((TextView) view.findViewById(R.id.name)).setText(username);
		((TextView) view.findViewById(R.id.title)).setText(String.format(getString(R.string.biz_profile_myrecipe_title), username));
		
		final TextView intro = (TextView) activity.findViewById(R.id.intro);
		intro.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				intro.setText("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
						"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
						"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
						"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
				if(TextUtils.isEmpty(intro.getText().toString()) || intro.getLineCount() <= 5) {
					return;
				}
				
				FragmentManager fm = getActivity().getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				Bundle args = new Bundle();
				args.putString(ProfileIntroFragment.PARAM_INTRO, "aaaaaaaaaaaaaaaaaaaaa" +
						"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
						"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
						"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
						"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
				Fragment f = fm.findFragmentByTag(ProfileIntroFragment.class.getName());
				if(f == null) {
					f = ProfileIntroFragment.instantiate(getActivity(), ProfileIntroFragment.class.getName(), args);
					ft.add(R.id.container, f);
				} else {
					f.setArguments(args);
					ft.attach(f);
				}
				ft.show(f);
				ft.addToBackStack(null);
				ft.commit();
			}
		});
		
		GridView grid = (GridView) activity.findViewById(R.id.recipe_grid);
		grid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//TODO Replace with the real recipe id
				RecipeFragment.startInActivity(getActivity(), "0");
			}
		});
		
		new RecipeTask(getActivity()).execute((Void) null);
	}
	
	private static class RecipeTask extends AsyncTask<Void, Void, Void> {

		private FragmentActivity mActivity;
		
		public RecipeTask(FragmentActivity activity) {
			mActivity = activity;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			GridView grid = (GridView) mActivity.findViewById(R.id.recipe_grid);
			grid.setAdapter(new RecipeAdapter(mActivity));
		}
		
	}
}
