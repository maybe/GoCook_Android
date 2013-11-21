package com.m6.gocook.biz.profile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
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
import com.m6.gocook.base.entity.RecipeList;
import com.m6.gocook.base.entity.RecipeList.RecipeItem;
import com.m6.gocook.base.fragment.BaseFragment;
import com.m6.gocook.base.fragment.FragmentHelper;
import com.m6.gocook.base.fragment.OnActivityAction;
import com.m6.gocook.base.protocol.Protocol;
import com.m6.gocook.base.protocol.ProtocolUtils;
import com.m6.gocook.base.view.ActionBar;
import com.m6.gocook.biz.account.AccountModel;
import com.m6.gocook.biz.account.WebLoginFragment;
import com.m6.gocook.biz.main.MainActivityHelper;
import com.m6.gocook.biz.recipe.RecipeModel;
import com.m6.gocook.biz.recipe.my.MyRecipesFragment;
import com.m6.gocook.biz.recipe.my.OtherRecipesFragment;
import com.m6.gocook.biz.recipe.recipe.RecipeFragment;
import com.m6.gocook.util.model.ModelUtils;

public class ProfileFragment extends BaseFragment implements OnActivityAction {
	
	public static final String PROFILE_FOLLOW_ID = "profile_follow_id";
	public static final String PROFILE_TYPE = "profile_type";
	
	public static final int PROFILE_MYSELF = 0;
	public static final int PROFILE_OTHERS = 1;
	
	private static final int FOLLOWED = 0; // 已关注
	private static final int FOLLOW = 1; // 未关注
	private static final int FOLLOW_DEFAULT = -1;
	
	private int mProfileType = 0;
	private String mUserId;
	private int mFollowStatus = -1;
	
	private String mUsername;
	private String mFollowId;
	
	/**
	 * 启动个人信息页面
	 * 
	 * @param context
	 * @param profileType
	 * @param followId
	 */
	public static void startProfileFragment(Context context, int profileType, String followId) {
		startProfileFragment(context, profileType, followId, false);
	}
	/**
	 * 启动个人信息页面
	 * 
	 * @param context
	 * @param profileType PROFILE_MYSELF代表进入我的页面； PROFILE_OTHERS进入别人的页面
	 * @param followId
	 */
	public static void startProfileFragment(Context context, int profileType, String followId, boolean startForResult) {
		Bundle bundle = new Bundle();
		bundle.putInt(PROFILE_TYPE, profileType);
		bundle.putString(PROFILE_FOLLOW_ID, followId);
		Intent intent = FragmentHelper.getIntent(context, BaseActivity.class, 
				ProfileFragment.class.getName(), ProfileFragment.class.getName(), bundle);
		if (startForResult) {
			((FragmentActivity) context).startActivityForResult(intent, MainActivityHelper.REQUEST_CODE_FOLLOW);
		} else {
			context.startActivity(intent);
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MainActivityHelper.registerOnActivityActionListener(this);
		
		Bundle args = getArguments();
		if(args != null) {
			mProfileType = args.getInt(PROFILE_TYPE);
			mUserId = args.getString(PROFILE_FOLLOW_ID);
			mFollowId = args.getString(PROFILE_FOLLOW_ID);
		}
	}
	
	@Override
	public void onDestroy() {
		MainActivityHelper.unRegisterOnActivityActionListener(this);
		super.onDestroy();
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
				if(TextUtils.isEmpty(intro.getText().toString()) || intro.getLineCount() < 5) {
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
				if (mProfileType == PROFILE_MYSELF) {
					Bundle bundle = new Bundle();
					bundle.putBoolean(MyRecipesFragment.PARAM_FROM_PROFILE, 
							mProfileType == PROFILE_MYSELF ? true : false);
					bundle.putString(MyRecipesFragment.PARAM_USERNAME, mUsername);
					Intent intent = FragmentHelper.getIntent(getActivity(), BaseActivity.class, 
							MyRecipesFragment.class.getName(), MyRecipesFragment.class.getName(), bundle);
					startActivity(intent);
				} else {
					FragmentHelper.startActivity(getActivity(), OtherRecipesFragment.newInstance(mUserId, mUsername));
				}
			}
		});
		
		view.findViewById(R.id.edit).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mProfileType == PROFILE_MYSELF) {
					Intent intent = FragmentHelper.getIntent(getActivity(), BaseActivity.class, 
							ProfileEditFragment.class.getName(), ProfileEditFragment.class.getName(), null);
					startActivity(intent);
				} else {
					if (AccountModel.isLogon(getActivity())) {
						if (!TextUtils.isEmpty(mFollowId)) {
							if (mFollowStatus == FOLLOW || mFollowStatus == FOLLOW_DEFAULT) {
								new FollowTask(getActivity(), mFollowId).execute((Void) null); 
							} else if(mFollowStatus == FOLLOWED) {
								new UnFollowTask(getActivity(), mFollowId).execute((Void) null); 
							}
							getActivity().setResult(MainActivityHelper.RESULT_CODE_FOLLOW);
						}
					} else {
						Intent intent = FragmentHelper.getIntent(getActivity(), BaseActivity.class, 
								WebLoginFragment.class.getName(), WebLoginFragment.class.getName(), null);
						((FragmentActivity) getActivity()).startActivityForResult(intent, MainActivityHelper.REQUEST_CODE_JUMP_LOGIN);
					}
				}
			}
		});
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		if(mProfileType == PROFILE_MYSELF) {
			new BasicInfoTask(getActivity()).execute((Void) null);
			new RecipeTask(getActivity()).execute((Void) null);
		} else {
			if (!TextUtils.isEmpty(mUserId)) {
				new OtherInfoTask(getActivity(), mUserId).execute((Void) null);
			}
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		if(mProfileType == PROFILE_MYSELF) {
			View view = getView();
			ActionBar action = getActionBar();
			String username = AccountModel.getUsername(getActivity());
			action.setTitle(username);
			((TextView) view.findViewById(R.id.name)).setText(username);
			((TextView) view.findViewById(R.id.title)).setText(String.format(getString(R.string.biz_profile_myrecipe_title), username));
			((TextView) view.findViewById(R.id.intro)).setText(getString(R.string.biz_profile_introduction,
					ProfileModel.getIntro(getActivity())));
			// 更新头像
			String url = AccountModel.getAvatarPath(getActivity());
			if(!TextUtils.isEmpty(url)) {
				mImageFetcher.loadImage(ProtocolUtils.getURL(url), (ImageView) getView().findViewById(R.id.avatar));
			}
		}
	}
	
	private void updateInfo(Map<String, Object> info) {
		View view = getView();
		if (view == null || info == null) {
			showEmpty(true);
			return;
		}
		
		ActionBar action = getActionBar();
		mUsername = ModelUtils.getStringValue(info, ProfileModel.NICKNAME);
		action.setTitle(mUsername);
		((TextView) view.findViewById(R.id.name)).setText(mUsername);
		((TextView) view.findViewById(R.id.title)).setText(String.format(getString(R.string.biz_profile_myrecipe_title), mUsername));
		
		// 更新头像
		String avatarUrl = ModelUtils.getStringValue(info, ProfileModel.AVATAR);
		if (!TextUtils.isEmpty(avatarUrl)) {
			mImageFetcher.loadImage(ProtocolUtils.getURL(avatarUrl), (ImageView) getView().findViewById(R.id.avatar));
		}
		
		// 关注、取消关注状态
		Button edit  = (Button) view.findViewById(R.id.edit);
		if (mProfileType == PROFILE_MYSELF) {
			edit.setText(R.string.biz_profile_edit_btn);
		} else {
			mFollowStatus = ModelUtils.getIntValue(info, ProfileModel.FOLLOW, 1);
			if (mFollowStatus == FOLLOW || mFollowStatus == FOLLOW_DEFAULT) {
				edit.setText(R.string.biz_profile_add_follow);
			} else if(mFollowStatus == FOLLOWED) {
				edit.setText(R.string.biz_profile_add_unfollow);
			}
		}
		
		// 粉丝、关注数
		String fans = ModelUtils.getStringValue(info, ProfileModel.FOLLOWED_COUNT);
		((TextView) view.findViewById(R.id.fans)).setText(getString(R.string.biz_profile_myaccount_fans_count, fans));
		
		String follows = ModelUtils.getStringValue(info, ProfileModel.FOLLOWING_COUNT);
		((TextView) view.findViewById(R.id.follow)).setText(getString(R.string.biz_profile_myaccount_follows_count, follows));
		
		// 个人简介
		String intro = ModelUtils.getStringValue(info, ProfileModel.INTRO);
		((TextView) view.findViewById(R.id.intro)).setText(getString(R.string.biz_profile_introduction, intro));
		
		// 个人菜谱
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
		} else if (recipes != null && recipes.isEmpty() && mProfileType == PROFILE_OTHERS) {
			hideRecipesView(true);
		}
		
		// 保存个人信息
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
	
	private void updateFollowStatus(boolean followed) {
		Button edit = (Button) getView().findViewById(R.id.edit);
		if (followed) {
			mFollowStatus = FOLLOWED;
			edit.setText(R.string.biz_profile_add_unfollow);
		} else {
			mFollowStatus = FOLLOW;
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
		protected void onPreExecute() {
			super.onPreExecute();
			showProgress(true);
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
		protected void onPreExecute() {
			super.onPreExecute();
			if (isAdded()) {
				showProgress(true);
			}
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

		private Context mContext;
		
		public RecipeTask(FragmentActivity activity) {
			mContext = activity.getApplicationContext();
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgress(true);
		}
		
		@Override
		protected RecipeList doInBackground(Void... params) {
			return RecipeModel.getMyRecipes(mContext);
		}
		
		@Override
		protected void onPostExecute(RecipeList result) {
			if (!isAdded()) {
				return;
			}
			
			GridView grid = (GridView) getView().findViewById(R.id.recipe_grid);
			if (grid != null && result != null && !result.getRecipes().isEmpty()) {
				grid.setAdapter(new ProfileRecipeAdapter(mContext, mImageFetcher, result));
				((Button) getView().findViewById(R.id.more)).setText(getString(R.string.biz_profile_more_btn, result.getRecipes().size()));
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
				updateFollowStatus(true);
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
				updateFollowStatus(false);
			} else {
				Toast.makeText(mContext, R.string.biz_profile_unfollow_fail, Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public void onCustomActivityResult(int requestCode, int resultCode,
			Intent data) {
		if (resultCode == MainActivityHelper.RESULT_CODE_JUMP_LOGIN) {
			if(mProfileType == PROFILE_OTHERS) {
				if (!TextUtils.isEmpty(mUserId)) {
					new OtherInfoTask(getActivity(), mUserId).execute((Void) null);
				}
			}
		}
	}
}
