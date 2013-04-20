package com.m6.gocook.biz.recipe;

import javax.security.auth.PrivateCredentialPermission;

import org.json.JSONException;
import org.json.JSONObject;

import com.m6.gocook.R;
import com.m6.gocook.base.constant.Constants;
import com.m6.gocook.base.entity.RecipeEntity;
import com.m6.gocook.biz.profile.RecipeAdapter;
import com.m6.gocook.biz.purchase.PurchaseListModel;
import com.m6.gocook.util.log.Logger;
import com.m6.gocook.util.net.NetUtils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

public class RecipeActivity extends Activity {

	final private String TAG = RecipeActivity.class.getCanonicalName();

	private final String FINISHEN_DISH_TAG_STRING = "<i>%s</i><font color='#3b272d'> %s</font><br/><i>%s</i><font color='#3b272d'> %s</font>";

	// DataSet
	private int mRecipeId;
	private RecipeEntity mRecipeEntity;
	private AchieveRecipeTask mAchieveRecipeTask;

	// UI reference
	private View mStatusView;
	private TextView mStatusMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recipe);
		
		initView();
		
		showProgress(true);
		mAchieveRecipeTask = new AchieveRecipeTask();
		mAchieveRecipeTask.execute((Void) null);

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
		recipeIntructionTextView.setText(mRecipeEntity.getDesc());

		TextView recipeAuthorTextView = (TextView) findViewById(R.id.recipe_author_textview);
		recipeAuthorTextView.setText(mRecipeEntity.getAuthor());

		TextView recipeAboutTextView = (TextView) findViewById(R.id.recipe_about_textview);
		recipeAboutTextView.setText(String.format(
				getResources().getString(R.string.biz_recipe_about),
				mRecipeEntity.getDishCount(), mRecipeEntity.getCollectCount()));

		GridView grid = (GridView) findViewById(R.id.material_gridview);
		grid.setAdapter(new RecipeMaterialAdapter(this, mRecipeEntity
				.getMaterials()));

		ListView list = (ListView) findViewById(R.id.procedure_listview);
		list.setAdapter(new RecipeProcedureAdapter(this, mRecipeEntity
				.getProcedures()));

		TextView recipeTipsTextView = (TextView) findViewById(R.id.recipe_tips_textview);
		recipeTipsTextView.setText(mRecipeEntity.getTips());

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
		protected Void doInBackground(Void... params) {

			mRecipeEntity = RecipeModel.getRecipe(getApplicationContext(), 0);
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
