package com.m6.gocook.biz.recipe.recipe;

import com.m6.gocook.R;
import com.m6.gocook.R.string;
import com.m6.gocook.base.activity.BaseActivity;
import com.m6.gocook.base.entity.RecipeCommentList;
import com.m6.gocook.base.entity.RecipeEntity;
import com.m6.gocook.base.entity.RecipeCommentList.RecipeCommentItem;
import com.m6.gocook.base.fragment.BaseFragment;
import com.m6.gocook.base.fragment.FragmentHelper;
import com.m6.gocook.biz.account.AccountModel;
import com.m6.gocook.biz.account.LoginFragment;
import com.m6.gocook.biz.purchase.PurchaseListModel;
import com.m6.gocook.biz.recipe.RecipeModel;
import com.m6.gocook.biz.recipe.comment.RecipeCommentAdapter;
import com.m6.gocook.biz.recipe.comment.RecipeCommentFragment;
import com.m6.gocook.biz.recipe.recipe.RecipeEditFragment.Mode;
import com.m6.gocook.util.log.Logger;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class RecipeFragment extends BaseFragment {

	private final String TAG = RecipeFragment.class.getCanonicalName();
	
	public static final String ARGUMENT_KEY_RECIPE_ID = "intent_key_recipe_id";
	public static final String ARGUMENT_KEY_RECIPE_NAME = "intent_key_recipe_name";

	private final String FINISHEN_DISH_TAG_STRING = "<i>%s</i><font color='#3b272d'> %s</font>";
	private static final String IMAGE_CACHE_DIR = "images";
	
	private Context mContext = null;
	private View mRootView = null;
	
	// DataSet
	private String mRecipeId;
	private RecipeEntity mRecipeEntity;
	
	private boolean mCollected = false;
	
	private boolean mRefreshComments = false;
	
	private AchieveRecipeTask mAchieveRecipeTask;
	private RecipeCollectTask mRecipeCollectTask;
	private AchieveCommentsTask mAchieveCommentsTask;
	
	public static void startInActivity(Context context, String recipeId, String recipeName) {
		Bundle argument = new Bundle();
		argument.putString(RecipeFragment.ARGUMENT_KEY_RECIPE_ID, recipeId);
		argument.putString(RecipeFragment.ARGUMENT_KEY_RECIPE_NAME, recipeName);
        Intent intent = FragmentHelper.getIntent(context, BaseActivity.class, 
        		RecipeFragment.class.getName(), 
        		RecipeFragment.class.getName(), argument);
        context.startActivity(intent);
	}
	
	@Override
	public View onCreateFragmentView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_recipe, null, false);
		return mRootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		if(mContext == null) {
			mContext = getActivity();
			doCreate();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(mRefreshComments) {
			mRefreshComments = false;
			achieveComments();
		}
	}
	
	private View findViewById(int id) {
		if(mRootView != null) {
			return mRootView.findViewById(id);
		} else {
			return null;
		}
	}
	
	private void doCreate() {

		Bundle argument = getArguments();
		mRecipeId = argument.getString(RecipeFragment.ARGUMENT_KEY_RECIPE_ID);
		
		setTitle(argument.getString(RecipeFragment.ARGUMENT_KEY_RECIPE_NAME));
		
		initView();

		mAchieveRecipeTask = new AchieveRecipeTask();
		mAchieveRecipeTask.execute((Void) null);
		
	}


    @Override
    public void onDestroy() {
        super.onDestroy();
        mContext = null;
        mRootView = null;
    }

	private void setTitle(String title) {
		getActionBar().setTitle(title);
	}

	private void applyData() {

		if (mRecipeEntity == null) {
			Logger.e(TAG, "RecipeEntity is null");
			getActivity().finish();
			return;
		}

		achieveComments();
		
		// Set Recipe Properties Values

		setTitle(mRecipeEntity.getName());

		TextView recipeIntructionTextView = (TextView) findViewById(R.id.recipe_intruction_textview);
		if(TextUtils.isEmpty(mRecipeEntity.getDesc())) {
			recipeIntructionTextView.setVisibility(View.GONE);
		} else {
			recipeIntructionTextView.setText(mRecipeEntity.getDesc());
			recipeIntructionTextView.setVisibility(View.VISIBLE);
		}
		recipeIntructionTextView.setText(mRecipeEntity.getDesc());

		TextView recipeAuthorTextView = (TextView) findViewById(R.id.recipe_author_textview);
		recipeAuthorTextView.setText(mRecipeEntity.getAuthor());

		TextView recipeAboutTextView = (TextView) findViewById(R.id.recipe_about_textview);
		recipeAboutTextView.setText(String.format(
				getResources().getString(R.string.biz_recipe_about),
				mRecipeEntity.getDishCount(), mRecipeEntity.getCollectCount()));
		
		ImageView coverImage = (ImageView) findViewById(R.id.cover_image);
		mImageFetcher.setImageSize(
				getResources().getDimensionPixelSize(
						R.dimen.biz_recipe_cover_image_width),
				getResources().getDimensionPixelSize(
						R.dimen.biz_recipe_cover_image_height));
		mImageFetcher.loadImage(mRecipeEntity.getCoverImgURL(), coverImage);

		GridView grid = (GridView) findViewById(R.id.material_gridview);
		grid.setAdapter(new RecipeMaterialAdapter(mContext, mRecipeEntity
				.getMaterials()));

		ListView list = (ListView) findViewById(R.id.procedure_listview);
		list.setAdapter(new RecipeProcedureAdapter(mContext, mRecipeEntity
				.getProcedures(), mImageFetcher));

		LinearLayout tipsLinearLayout = (LinearLayout) findViewById(R.id.tips_layout);
		if(TextUtils.isEmpty(mRecipeEntity.getTips())) {
			tipsLinearLayout.setVisibility(View.GONE);
		} else {
			TextView recipeTipsTextView = (TextView) findViewById(R.id.recipe_tips_textview);
			recipeTipsTextView.setText(mRecipeEntity.getTips());
			tipsLinearLayout.setVisibility(View.VISIBLE);
		}

//		// Related Recipes
//		GridView gridRelatedGridView = (GridView) findViewById(R.id.related_recipe_gridview);
//		gridRelatedGridView.setAdapter(new RecipeRelatedRecipesAdapter(mContext));
//
//		TextView commentNum = (TextView) findViewById(R.id.comment_num);
//		commentNum
//				.setText(String.format(
//						getResources().getString(
//								R.string.biz_recipe_comment_num), 234));
//
//		TextView finishedView = (TextView) findViewById(R.id.finished_dish_textview);
//		finishedView.setText(String.format(
//				getResources().getString(R.string.biz_recipe_finished_dish),
//				"葱油饼"));
//		finishedView.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//
//			}
//		});
		
		TextView tabBarBuyTextView = ((TextView) findViewById(R.id.tabbar_textview_buy));
		if(mRecipeEntity != null && PurchaseListModel.isRecipeSavedToProcedureList(getActivity(), String.valueOf(mRecipeEntity.getId()))) {
			tabBarBuyTextView.setCompoundDrawablesWithIntrinsicBounds(
					null,
					getResources().getDrawable(
							R.drawable.recipe_tabbar_bought), null,
					null);
			tabBarBuyTextView.setText(getResources().getText(R.string.biz_recipe_tabbar_menu_removeshoppinglist));
		}
		
		TextView tabBarCollectTextView = ((TextView) findViewById(R.id.tabbar_textview_like));
		setCollected(mRecipeEntity.isCollected(), tabBarCollectTextView);
	}

	private void achieveComments() {
		// start achieve comments
		if (mAchieveCommentsTask == null) {
			mAchieveCommentsTask = new AchieveCommentsTask();
			mAchieveCommentsTask.execute();
		}
	}

	private void initView() {
		
		getActionBar().setRightButton("修改", R.drawable.bar_delete_normal);

		// Tabbar Event Listener
		TextView tabBarBuyTextView = ((TextView) findViewById(R.id.tabbar_textview_buy));
		tabBarBuyTextView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(mRecipeEntity == null) return;
						TextView tabBarBuyTextView = (TextView)v;
						if(PurchaseListModel.isRecipeSavedToProcedureList(getActivity(), String.valueOf(mRecipeEntity.getId()))) {
							PurchaseListModel.removeRecipeFromPurchaseList(getActivity(), String.valueOf(mRecipeEntity.getId()));
							tabBarBuyTextView.setCompoundDrawablesWithIntrinsicBounds(
									null,
									getResources().getDrawable(
											R.drawable.recipe_tabbar_buy), null,
									null);
							tabBarBuyTextView.setText(getResources().getText(R.string.biz_recipe_tabbar_menu_addshoppinglist));
						} else {
							PurchaseListModel.saveRecipeToProcedureList(getActivity(), mRecipeEntity);
							tabBarBuyTextView.setCompoundDrawablesWithIntrinsicBounds(
									null,
									getResources().getDrawable(
											R.drawable.recipe_tabbar_bought), null,
									null);
							tabBarBuyTextView.setText(getResources().getText(R.string.biz_recipe_tabbar_menu_removeshoppinglist));
						}
					}
				});

		((TextView) findViewById(R.id.tabbar_textview_like))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						
						if(AccountModel.isLogon(mContext)) {
							if(mRecipeCollectTask == null) {
								mRecipeCollectTask = new RecipeCollectTask(v);
								mRecipeCollectTask.execute();
							}
							
						} else {
							Bundle bundle = new Bundle();
							bundle.putBoolean(LoginFragment.PARAM_JUMP_LOGIN, true);
							Intent intent = FragmentHelper.getIntent(mContext, BaseActivity.class,
									LoginFragment.class.getName(), 
									LoginFragment.class.getName()
									,bundle);
							mContext.startActivity(intent);
						}
						
					}
				});

//		((TextView) findViewById(R.id.tabbar_textview_upload))
//				.setOnClickListener(new OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//
//					}
//				});
		
		
		// Comments
		View commentsLinkView = findViewById(R.id.comments_link);
		commentsLinkView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mRefreshComments = true;
				RecipeCommentFragment.startInActivity(mContext, mRecipeId, mRecipeEntity.getName());
			}
		});
	}

	private void setCollected(boolean mCollected, TextView view) {
		this.mCollected = mCollected;
		view.setCompoundDrawablesWithIntrinsicBounds(
				null,
				getResources().getDrawable(
						this.mCollected ? R.drawable.recipe_tabbar_likehl
									: R.drawable.recipe_tabbar_like), null,
				null);
		view.setText(mCollected ? 
				R.string.biz_recipe_tabbar_menu_removecollecting :
				R.string.biz_recipe_tabbar_menu_addcollecting);
	}

	private class AchieveRecipeTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			showProgress(true);
		}

		@Override
		protected Void doInBackground(Void... params) {

			mRecipeEntity = RecipeModel.getRecipe(getActivity(), mRecipeId);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if(isAdded()) {
				showProgress(false);
				if(mRecipeEntity != null) {
					applyData();
				} else {
					getActivity().finish();
				}
			}
			super.onPostExecute(result);
		}

	}
	
	private class RecipeCollectTask extends AsyncTask<Void, Void, Boolean> {

		private TextView view;
		
		public RecipeCollectTask(View view) {
			this.view = (TextView) view;
		}
		
		@Override
		protected void onPreExecute() {
			showProgress(true);
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			if(mCollected) {
				return RecipeModel.removeFromCollectList(mContext,mRecipeId);
			} else {
				return RecipeModel.addToCollectList(mContext, mRecipeId);
			}
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			
			mRecipeCollectTask = null;
			
			if(result && view != null) {
				setCollected(!mCollected, view);
			} else {
				if(isAdded()) {
					Toast.makeText(mContext,
							mCollected ?
							R.string.biz_recipe_tabbar_menu_removecollectfailed :
							R.string.biz_recipe_tabbar_menu_addcollectfailed,
							Toast.LENGTH_SHORT)
							.show();
				}
			}
			showProgress(false);
			
		}
		
	}
	
	public class AchieveCommentsTask extends AsyncTask<Void, Void, RecipeCommentList> {

		
		@Override
		protected RecipeCommentList doInBackground(Void... params) {
			return RecipeModel.getRecipeComments(mContext, mRecipeId);
		}
		
		@Override
		protected void onPostExecute(RecipeCommentList result) {
			
			mAchieveCommentsTask = null;
			
			if(result == null || !isAdded()) {
				return;
			}
			
			int commentsNum = result.getCount();
			
			if(commentsNum == 0 ) {
				return;
			}
			
			StringBuilder topTwo = new StringBuilder();
			
			RecipeCommentItem item = (RecipeCommentItem) result.getItem(0);
			topTwo.append(String.format(FINISHEN_DISH_TAG_STRING,
						item.getName(), item.getContent()));
			if (commentsNum > 1) {
				item = (RecipeCommentItem) result.getItem(1);
				topTwo.append("<br />");
				topTwo.append(String.format(FINISHEN_DISH_TAG_STRING,
						item.getName(), item.getContent()));
			}
			
			TextView commentNum = (TextView) findViewById(R.id.comment_num);
			commentNum.setText(String.format(getResources().getString(R.string.biz_recipe_comment_num)
					, commentsNum));
			
			// Set Recipe Top two Comments
			TextView commentItem = (TextView) findViewById(R.id.comment_item);
			commentItem.setVisibility(View.VISIBLE);
			commentItem.setText(Html.fromHtml(topTwo.toString()));

			
		}
		
	}

	@Override
	public void onActionBarRightButtonClick(View v) {
		RecipeEditFragment.startInActivity(mContext, Mode.RECIPE_EDIT, mRecipeId);
	}

}
