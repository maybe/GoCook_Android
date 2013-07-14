package com.m6.gocook.biz.profile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.R.integer;
import android.content.Context;
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
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.m6.gocook.R;
import com.m6.gocook.base.activity.BaseActivity;
import com.m6.gocook.base.constant.PrefKeys;
import com.m6.gocook.base.entity.RecipeList;
import com.m6.gocook.base.fragment.BaseFragment;
import com.m6.gocook.base.fragment.FragmentHelper;
import com.m6.gocook.base.protocol.Protocol;
import com.m6.gocook.base.view.ActionBar;
import com.m6.gocook.biz.account.AccountModel;
import com.m6.gocook.biz.recipe.RecipeModel;
import com.m6.gocook.biz.recipe.my.MyRecipesFragment;
import com.m6.gocook.biz.recipe.recipe.RecipeFragment;
import com.m6.gocook.util.model.ModelUtils;
import com.m6.gocook.util.preference.PrefHelper;

public class ProfileFragment extends BaseFragment {
	
	public static final String PROFILE_TYPE = "profile_type";
	public static final int PROFILE_MYSELF = 0;
	public static final int PROFILE_OTHERS = 1;
	
	private int mProfileType = 0;
	
	@Override
	public View onCreateFragmentView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_profile, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		Button edit = (Button) view.findViewById(R.id.edit);
		edit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mProfileType == PROFILE_MYSELF) {
					Intent intent = FragmentHelper.getIntent(getActivity(), BaseActivity.class, 
							ProfileEditFragment.class.getName(), ProfileEditFragment.class.getName(), null);
					startActivity(intent);
				} else {
					
				}
				
			}
		});
		
		final TextView intro = (TextView) view.findViewById(R.id.intro);
		intro.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(TextUtils.isEmpty(intro.getText().toString()) || intro.getLineCount() <= 5) {
					return;
				}
				
				FragmentManager fm = getActivity().getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				Bundle args = new Bundle();
				args.putString(ProfileIntroFragment.PARAM_INTRO, ProfileModel.getIntro(getActivity()));
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
		
		final GridView grid = (GridView) view.findViewById(R.id.recipe_grid);
		grid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Adapter adapter = grid.getAdapter();
				if (adapter != null) {
					Map<String, Object> map = (Map<String, Object>) adapter.getItem(position);
					if (map != null) {
						RecipeFragment.startInActivity(getActivity(), ModelUtils.getStringValue(map, Protocol.KEY_RECIPE_ID));
					}
				}
			}
		});
		
		view.findViewById(R.id.more).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putBoolean(MyRecipesFragment.PARAM_FROM_PROFILE, true);
				Intent intent = FragmentHelper.getIntent(getActivity(), BaseActivity.class, 
						MyRecipesFragment.class.getName(), MyRecipesFragment.class.getName(), bundle);
				startActivity(intent);
			}
		});
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		Bundle args = getArguments();
		if(args != null) {
			mProfileType = args.getInt(PROFILE_TYPE);
		}
		
		final FragmentActivity activity = getActivity();
		View view = getView();
		
		ImageView avatar = (ImageView) activity.findViewById(R.id.avatar);
		// 取本地数据
		String avatarPath = PrefHelper.getString(activity, PrefKeys.ACCOUNT_AVATAR, "");
		if(!TextUtils.isEmpty(avatarPath)) {
			avatar.setImageBitmap(BitmapFactory.decodeFile(avatarPath));
		}
		
		new BasicInfoTask(getActivity()).execute((Void) null);
		new RecipeTask(getActivity()).execute((Void) null);
		showProgress(true);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		View view = getView();
		ActionBar action = getActionBar();
		String username = AccountModel.getUsername(getActivity());
		action.setTitle(username);
		((TextView) view.findViewById(R.id.name)).setText(username);
		((TextView) view.findViewById(R.id.title)).setText(String.format(getString(R.string.biz_profile_myrecipe_title), username));
		((TextView) view.findViewById(R.id.intro)).setText(getString(R.string.biz_profile_introduction,
				ProfileModel.getIntro(getActivity())));
	}
	
	private void updateInfo(Map<String, Object> info) {
		View view = getView();
		if (view == null || info == null) {
			showEmpty(true);
			return;
		}
		
		ActionBar action = getActionBar();
		String username = ModelUtils.getStringValue(info, ProfileModel.NICKNAME);
		action.setTitle(username);
		((TextView) view.findViewById(R.id.name)).setText(username);
		((TextView) view.findViewById(R.id.title)).setText(String.format(getString(R.string.biz_profile_myrecipe_title), username));
		
		String intro = ModelUtils.getStringValue(info, ProfileModel.INTRO);
		((TextView) view.findViewById(R.id.intro)).setText(getString(R.string.biz_profile_introduction, intro));
		
		AccountModel.saveUsername(getActivity(), ModelUtils.getStringValue(info, ProfileModel.NICKNAME));
		ProfileModel.saveAge(getActivity(), ModelUtils.getStringValue(info, ProfileModel.AGE));
		int sexType = ModelUtils.getIntValue(info, ProfileModel.SEX, 0);
		String sex;
		if (sexType == 0) {
			sex = "男";
		} else if (sexType == 1) {
			sex = "女";
		} else {
			sex = "2";
		}
		ProfileModel.saveSex(getActivity(), sex);
		ProfileModel.saveCity(getActivity(), ModelUtils.getStringValue(info, ProfileModel.CITY));
		ProfileModel.saveCareer(getActivity(), ModelUtils.getStringValue(info, ProfileModel.CAREER));
		ProfileModel.saveIntro(getActivity(), intro);
	}
	
	
	private class BasicInfoTask extends AsyncTask<Void, Void, Map<String, Object>> {

		private Context mContext;
		
		public BasicInfoTask(Context context) {
			mContext = context.getApplicationContext();
		}
		
		@Override
		protected Map<String, Object> doInBackground(Void... params) {
			return ProfileModel.getBasicInfo(mContext);
		}
		
		@Override
		protected void onPostExecute(Map<String, Object> result) {
			if (isAdded()) {
				showProgress(false);
				if (result != null) {
					updateInfo(result);
				}
			}
		}
	}
	private class RecipeTask extends AsyncTask<Void, Void, RecipeList> {

		private FragmentActivity mActivity;
		
		public RecipeTask(FragmentActivity activity) {
			mActivity = activity;
		}
		
		@Override
		protected RecipeList doInBackground(Void... params) {
			return RecipeModel.getMyRecipes(mActivity);
		}
		
		@Override
		protected void onPostExecute(RecipeList result) {
			GridView grid = (GridView) mActivity.findViewById(R.id.recipe_grid);
			if (grid != null) {
				grid.setAdapter(new ProfileRecipeAdapter(mActivity, mImageFetcher, result));
			}
		}
		
	}
}
