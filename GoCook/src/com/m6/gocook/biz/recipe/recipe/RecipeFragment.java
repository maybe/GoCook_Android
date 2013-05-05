package com.m6.gocook.biz.recipe.recipe;

import com.m6.gocook.R;
import com.m6.gocook.base.activity.BaseActivity;
import com.m6.gocook.base.entity.RecipeEntity;
import com.m6.gocook.base.fragment.BaseFragment;
import com.m6.gocook.base.fragment.FragmentHelper;
import com.m6.gocook.biz.purchase.PurchaseListModel;
import com.m6.gocook.biz.recipe.RecipeModel;
import com.m6.gocook.biz.recipe.comment.RecipeCommentFragment;
import com.m6.gocook.util.log.Logger;

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

public class RecipeFragment extends BaseFragment {

	private final String TAG = RecipeFragment.class.getCanonicalName();
	
	public static final String ARGUMENT_KEY_RECIPE_ID = "intent_key_recipe_id";

	private final String FINISHEN_DISH_TAG_STRING = "<i>%s</i><font color='#3b272d'> %s</font><br/><i>%s</i><font color='#3b272d'> %s</font>";
	private static final String IMAGE_CACHE_DIR = "images";
	
	private Context mContext = null;
	private View mRootView = null;
	
	// DataSet
	private String mRecipeId;
	private RecipeEntity mRecipeEntity;
	private AchieveRecipeTask mAchieveRecipeTask;
	
	public static void startInActivity(Context context, String recipeId) {
		Bundle argument = new Bundle();
		argument.putString(RecipeFragment.ARGUMENT_KEY_RECIPE_ID, recipeId);
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
		getAction().setTitle(title);
	}

	private void applyData() {

		if (mRecipeEntity == null) {
			Logger.e(TAG, "RecipeEntity is null");
			getActivity().finish();
			return;
		}

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
		
		// Recipe Comments
		TextView commentItem = (TextView) findViewById(R.id.comment_item);
		commentItem
				.setText(Html.fromHtml(String.format(FINISHEN_DISH_TAG_STRING,
						"小笨蛋", "真的很好吃啊", "longrenle", "不错不错")));

		View commentsLinkView = findViewById(R.id.comments_link);
		commentsLinkView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				RecipeCommentFragment.startInActivity(mContext, mRecipeId);
			}
		});

		// Related Recipes
		GridView gridRelatedGridView = (GridView) findViewById(R.id.related_recipe_gridview);
		gridRelatedGridView.setAdapter(new RecipeRelatedRecipesAdapter(mContext));

		TextView commentNum = (TextView) findViewById(R.id.comment_num);
		commentNum
				.setText(String.format(
						getResources().getString(
								R.string.biz_recipe_comment_num), 234));

		TextView finishedView = (TextView) findViewById(R.id.finished_dish_textview);
		finishedView.setText(String.format(
				getResources().getString(R.string.biz_recipe_finished_dish),
				"葱油饼"));
		finishedView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});
		
		TextView tabBarBuyTextView = ((TextView) findViewById(R.id.tabbar_textview_buy));
		if(mRecipeEntity != null && PurchaseListModel.isRecipeSavedToProcedureList(getActivity(), String.valueOf(mRecipeEntity.getId()))) {
			tabBarBuyTextView.setCompoundDrawablesWithIntrinsicBounds(
					null,
					getResources().getDrawable(
							R.drawable.recipe_tabbar_bought), null,
					null);
			tabBarBuyTextView.setText(getResources().getText(R.string.biz_recipe_tabbar_menu_removeshoppinglist));
		}
	}

	private void initView() {

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
						((TextView) v).setCompoundDrawablesWithIntrinsicBounds(
								null,
								getResources().getDrawable(
										R.drawable.recipe_tabbar_likehl), null,
								null);
					}
				});

		((TextView) findViewById(R.id.tabbar_textview_upload))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

					}
				});
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

			showProgress(false);
			applyData();
			super.onPostExecute(result);
		}

	}

}
