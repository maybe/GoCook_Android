package com.m6.gocook.biz.recipe;

import com.m6.gocook.R;
import com.m6.gocook.base.entity.RecipeEntity;
import com.m6.gocook.biz.purchase.PurchaseListModel;
import com.m6.gocook.util.cache.util.ImageCache;
import com.m6.gocook.util.cache.util.ImageFetcher;
import com.m6.gocook.util.log.Logger;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class RecipeActivity extends FragmentActivity {

	final private String TAG = RecipeActivity.class.getCanonicalName();
	
	final static public String INTENT_KEY_RECIPE_ID = "intent_key_recipe_id";

	private final String FINISHEN_DISH_TAG_STRING = "<i>%s</i><font color='#3b272d'> %s</font><br/><i>%s</i><font color='#3b272d'> %s</font>";
	private static final String IMAGE_CACHE_DIR = "images";
	
	// DataSet
	private String mRecipeId;
	private RecipeEntity mRecipeEntity;
	private AchieveRecipeTask mAchieveRecipeTask;

	// UI reference
	private View mStatusView;
	private TextView mStatusMessageView;
	
	// Network Image Catchable Fetcher
	private ImageFetcher mImageFetcher;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recipe);
		
		Intent intent = getIntent();
		mRecipeId = intent.getStringExtra(RecipeActivity.INTENT_KEY_RECIPE_ID);
				
		initView();
		
		ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(
				this, IMAGE_CACHE_DIR);
		cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of App memory

		// The ImageFetcher takes care of loading images into our ImageView
		// children asynchronously
		mImageFetcher = new ImageFetcher(this, getResources()
				.getDimensionPixelSize(R.dimen.biz_recipe_cover_image_width),
				getResources().getDimensionPixelSize(
						R.dimen.biz_recipe_cover_image_height));
		mImageFetcher.addImageCache(this.getSupportFragmentManager(),
				cacheParams);
		mImageFetcher.setImageFadeIn(false);

		mAchieveRecipeTask = new AchieveRecipeTask();
		mAchieveRecipeTask.execute((Void) null);

	}
	
	@Override
    public void onResume() {
        super.onResume();
        mImageFetcher.setExitTasksEarly(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        mImageFetcher.setPauseWork(false);
        mImageFetcher.setExitTasksEarly(true);
        mImageFetcher.flushCache();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mImageFetcher.closeCache();
    }

	private void setTitle(String title) {
		((TextView) findViewById(R.id.actionbar_title)).setText(title);
	}

	private void applyData() {

		if (mRecipeEntity == null) {
			Logger.e(TAG, "RecipeEntity is null");
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
		mImageFetcher.loadImage(mRecipeEntity.getCoverImgURL(), coverImage);

		GridView grid = (GridView) findViewById(R.id.material_gridview);
		grid.setAdapter(new RecipeMaterialAdapter(this, mRecipeEntity
				.getMaterials()));

		ListView list = (ListView) findViewById(R.id.procedure_listview);
		list.setAdapter(new RecipeProcedureAdapter(this, mRecipeEntity
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
				// TODO Auto-generated method stub

			}
		});

		// Related Recipes
		GridView gridRelatedGridView = (GridView) findViewById(R.id.related_recipe_gridview);
		gridRelatedGridView.setAdapter(new RecipeRelatedRecipesAdapter(this));

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
		
		TextView tabBarBuyTextView = ((TextView) this.findViewById(R.id.tabbar_textview_buy));
		if(mRecipeEntity != null && PurchaseListModel.isRecipeSavedToProcedureList(getApplicationContext(), String.valueOf(mRecipeEntity.getId()))) {
			tabBarBuyTextView.setCompoundDrawablesWithIntrinsicBounds(
					null,
					getResources().getDrawable(
							R.drawable.recipe_tabbar_bought), null,
					null);
		}
	}

	private void initView() {

		// Progress Bar
		mStatusView = this.findViewById(R.id.progress_status);
		mStatusMessageView = (TextView) this.findViewById(R.id.status_message);

		// Tabbar Event Listener
		TextView tabBarBuyTextView = ((TextView) this.findViewById(R.id.tabbar_textview_buy));
		tabBarBuyTextView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(mRecipeEntity == null) return;
						TextView tabBarBuyTextView = (TextView)v;
						if(PurchaseListModel.isRecipeSavedToProcedureList(getApplicationContext(), String.valueOf(mRecipeEntity.getId()))) {
							PurchaseListModel.removeRecipeFromPurchaseList(getApplicationContext(), String.valueOf(mRecipeEntity.getId()));
							tabBarBuyTextView.setCompoundDrawablesWithIntrinsicBounds(
									null,
									getResources().getDrawable(
											R.drawable.recipe_tabbar_buy), null,
									null);
						} else {
							PurchaseListModel.saveRecipeToProcedureList(getApplicationContext(), mRecipeEntity);
							tabBarBuyTextView.setCompoundDrawablesWithIntrinsicBounds(
									null,
									getResources().getDrawable(
											R.drawable.recipe_tabbar_bought), null,
									null);
						}
					}
				});

		((TextView) this.findViewById(R.id.tabbar_textview_like))
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

		((TextView) this.findViewById(R.id.tabbar_textview_upload))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

					}
				});
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mStatusView.setVisibility(View.VISIBLE);
			mStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
		}
	}

	private class AchieveRecipeTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			showProgress(true);
		}

		@Override
		protected Void doInBackground(Void... params) {

			mRecipeEntity = RecipeModel.getRecipe(getApplicationContext(), mRecipeId);
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
