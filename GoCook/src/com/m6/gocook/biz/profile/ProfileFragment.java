package com.m6.gocook.biz.profile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import android.widget.Toast;

import com.m6.gocook.R;
import com.m6.gocook.base.activity.BaseActivity;
import com.m6.gocook.base.constant.PrefKeys;
import com.m6.gocook.base.entity.RecipeList;
import com.m6.gocook.base.entity.RecipeList.RecipeItem;
import com.m6.gocook.base.fragment.BaseFragment;
import com.m6.gocook.base.fragment.FragmentHelper;
import com.m6.gocook.base.protocol.Protocol;
import com.m6.gocook.base.protocol.ProtocolUtils;
import com.m6.gocook.base.view.ActionBar;
import com.m6.gocook.biz.account.AccountModel;
import com.m6.gocook.biz.recipe.RecipeModel;
import com.m6.gocook.biz.recipe.my.MyRecipesFragment;
import com.m6.gocook.biz.recipe.recipe.RecipeFragment;
import com.m6.gocook.util.model.ModelUtils;
import com.m6.gocook.util.preference.PrefHelper;

public class ProfileFragment extends BaseFragment {
	
	public static final String PROFILE_FOLLOW_ID = "profile_follow_id";
	public static final String PROFILE_TYPE = "profile_type";
	
	public static final int PROFILE_MYSELF = 0;
	public static final int PROFILE_OTHERS = 1;
	
	private static final int FOLLOWED = 0; // 已关注
	private static final int FOLLOW = 1; // 未关注
	
	private int mProfileType = 0;
	private String mUserId;
	private int mFollowStatus = -1;
	
	/**
	 * 启动个人信息页面
	 * 
	 * @param context
	 * @param profileType PROFILE_MYSELF代表进入我的页面； PROFILE_OTHERS进入别人的页面
	 * @param followId
	 */
	public static void startProfileFragment(Context context, int profileType, String followId) {
		Bundle bundle = new Bundle();
		bundle.putInt(PROFILE_TYPE, ProfileFragment.PROFILE_OTHERS);
		bundle.putString(PROFILE_FOLLOW_ID, followId);
		Intent intent = FragmentHelper.getIntent(context, BaseActivity.class, 
				ProfileFragment.class.getName(), ProfileFragment.class.getName(), bundle);
		context.startActivity(intent);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle args = getArguments();
		if(args != null) {
			mProfileType = args.getInt(PROFILE_TYPE);
			mUserId = args.getString(PROFILE_FOLLOW_ID);
		}
	}
	
	@Override
	public View onCreateFragmentView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_profile, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
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
					RecipeList.RecipeItem recipeItem = (RecipeList.RecipeItem) adapter.getItem(position);
					if (recipeItem != null) {
						RecipeFragment.startInActivity(getActivity(), recipeItem.getId(), recipeItem.getName());
					}
				}
			}
		});
		
		view.findViewById(R.id.more).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putBoolean(MyRecipesFragment.PARAM_FROM_PROFILE, 
						mProfileType == PROFILE_MYSELF ? true : false);
				Intent intent = FragmentHelper.getIntent(getActivity(), BaseActivity.class, 
						MyRecipesFragment.class.getName(), MyRecipesFragment.class.getName(), bundle);
				startActivity(intent);
			}
		});
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		final FragmentActivity activity = getActivity();
		ImageView avatar = (ImageView) activity.findViewById(R.id.avatar);
		String url = AccountModel.getAvatarPath(getActivity());
		if(!TextUtils.isEmpty(url)) {
			mImageFetcher.loadImage(ProtocolUtils.getURL(url), avatar);
		}
		
		if(mProfileType == PROFILE_MYSELF) {
			new BasicInfoTask(getActivity()).execute((Void) null);
			new RecipeTask(getActivity()).execute((Void) null);
		} else {
			if (!TextUtils.isEmpty(mUserId)) {
				new OtherInfoTask(getActivity(), mUserId).execute((Void) null);
			}
		}
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
		
		Button edit  = (Button) view.findViewById(R.id.edit);
		if (mProfileType == PROFILE_MYSELF) {
			edit.setText(R.string.biz_profile_edit_btn);
		} else {
			mFollowStatus = ModelUtils.getIntValue(info, ProfileModel.FOLLOW, 1);
			edit.setText(mFollowStatus == FOLLOW ? R.string.biz_profile_add_follow : R.string.biz_profile_add_followd);
		}
		edit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mProfileType == PROFILE_MYSELF) {
					Intent intent = FragmentHelper.getIntent(getActivity(), BaseActivity.class, 
							ProfileEditFragment.class.getName(), ProfileEditFragment.class.getName(), null);
					startActivity(intent);
				} else {
					String followId = getArguments() == null ? null : getArguments().getString(PROFILE_FOLLOW_ID);
					if (!TextUtils.isEmpty(followId)) {
						if (mFollowStatus == FOLLOW) {
							new FollowTask(getActivity(), followId).execute((Void) null); 
						} else if(mFollowStatus == FOLLOWED) {
							new UnFollowTask(getActivity(), followId).execute((Void) null); 
						}
					}
				}
			}
		});
		
		String intro = ModelUtils.getStringValue(info, ProfileModel.INTRO);
		((TextView) view.findViewById(R.id.intro)).setText(getString(R.string.biz_profile_introduction, intro));
		
		String recipesCount = ModelUtils.getStringValue(info, ProfileModel.RECIPES_COUNT);
		((Button) view.findViewById(R.id.more)).setText(getString(R.string.biz_profile_more_btn, recipesCount));
		
		List<Map<String, Object>> recipes = ModelUtils.getListMapValue(info, ProfileModel.RECIPES);
		if (recipes != null && mProfileType == PROFILE_OTHERS) {
			int size = recipes.size();
			ArrayList<RecipeItem> recipeItems = new ArrayList<RecipeList.RecipeItem>(); 
			for (int i = 0; i < size; i++) {
				RecipeItem item = new RecipeItem();
				item.setId(ModelUtils.getStringValue(recipes.get(i), Protocol.KEY_RECIPE_ID));
				item.setName(ModelUtils.getStringValue(recipes.get(i), Protocol.KEY_RECIPE_LIST_NAME));
				item.setImage(ModelUtils.getStringValue(recipes.get(i), Protocol.KEY_RECIPE_LIST_IMAGE));
				item.setMaterial(ModelUtils.getStringValue(recipes.get(i), Protocol.KEY_RECIPE_MATERIALS));
				item.setCollectCount(ModelUtils.getIntValue(recipes.get(i), Protocol.KEY_RECIPE_DISH_COUNT, 0));
				recipeItems.add(item);
			}
			RecipeList recipeList = new RecipeList();
			recipeList.setRecipes(recipeItems);
			
			GridView grid = (GridView) view.findViewById(R.id.recipe_grid);
			grid.setAdapter(new ProfileRecipeAdapter(getActivity(), mImageFetcher, recipeList));
			hideRecipesView(false);
		} else if (recipes != null && !recipes.isEmpty() && mProfileType == PROFILE_OTHERS) {
			hideRecipesView(true);
		}
		
		if(mProfileType == PROFILE_MYSELF) {
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
	}
	
	private void updateFollow(boolean followed) {
		Button edit = (Button) getView().findViewById(R.id.edit);
		if (followed) {
			edit.setText(R.string.biz_profile_add_followd);
		} else {
			edit.setText(R.string.biz_profile_add_follow);
		}
	}
	
	private void hideRecipesView(boolean hide) {
		if (hide) {
			getView().findViewById(R.id.recipe_grid).setVisibility(View.GONE);
			getView().findViewById(R.id.more).setVisibility(View.GONE);
		} else {
			getView().findViewById(R.id.recipe_grid).setVisibility(View.VISIBLE);
			getView().findViewById(R.id.more).setVisibility(View.VISIBLE);
		}
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
	
	private class OtherInfoTask extends AsyncTask<Void, Void, Map<String, Object>> {

		private Context mContext;
		private String mUserId;
		
		public OtherInfoTask(Context context, String userId) {
			mContext = context.getApplicationContext();
			mUserId = userId;
		}
		
		@Override
		protected Map<String, Object> doInBackground(Void... params) {
			return ProfileModel.getOtherInfo(mContext, mUserId);
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
			if (!isAdded()) {
				return;
			}
			
			GridView grid = (GridView) mActivity.findViewById(R.id.recipe_grid);
			if (grid != null && result != null && !result.getRecipes().isEmpty()) {
				grid.setAdapter(new ProfileRecipeAdapter(mActivity, mImageFetcher, result));
				hideRecipesView(false);
			} else {
				hideRecipesView(true);
			}
		}
	}
	
	private class FollowTask extends AsyncTask<Void, Void, Map<String, Object>> {
		
		private Context mContext;
		private String mFollowId;
		
		public FollowTask(Context context, String followId) {
			mContext = context.getApplicationContext();
			mFollowId = followId;
		}
		
		@Override
		protected Map<String, Object> doInBackground(Void... params) {
			return ProfileModel.follow(mContext, mFollowId, true);
		}
		
		@Override
		protected void onPostExecute(Map<String, Object> result) {
			if (!isAdded()) {
				return;
			}
			
			if (result != null && ModelUtils.getIntValue(result, Protocol.KEY_RESULT, 1) == Protocol.VALUE_RESULT_OK) {
				updateFollow(true);
			} else {
				Toast.makeText(mContext, R.string.biz_profile_add_follow_fail, Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private class UnFollowTask extends AsyncTask<Void, Void, Map<String, Object>> {
		
		private Context mContext;
		private String mFollowId;
		
		public UnFollowTask(Context context, String followId) {
			mContext = context.getApplicationContext();
			mFollowId = followId;
		}
		
		@Override
		protected Map<String, Object> doInBackground(Void... params) {
			return ProfileModel.follow(mContext, mFollowId, false);
		}
		
		@Override
		protected void onPostExecute(Map<String, Object> result) {
			if (!isAdded()) {
				return;
			}
			
			if (result != null && ModelUtils.getIntValue(result, Protocol.KEY_RESULT, 1) == Protocol.VALUE_RESULT_OK) {
				updateFollow(false);
			} else {
				Toast.makeText(mContext, R.string.biz_profile_unfollow_fail, Toast.LENGTH_SHORT).show();
			}
		}
	}
}
