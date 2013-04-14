package com.m6.gocook.biz.recipe;

import org.json.JSONException;
import org.json.JSONObject;

import com.m6.gocook.R;
import com.m6.gocook.base.constant.Constants;
import com.m6.gocook.base.entity.RecipeEntity;
import com.m6.gocook.biz.profile.RecipeAdapter;
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

		mStatusView = this.findViewById(R.id.progress_status);
		mStatusMessageView = (TextView) this.findViewById(R.id.status_message);

		showProgress(true);
		
		mAchieveRecipeTask = new AchieveRecipeTask();
		mAchieveRecipeTask.execute((Void) null);
		
		
		setTitle("葱油饼");
		
		initView();
		
		
	}
	
	private void setTitle(String title) {
		((TextView) findViewById(R.id.actionbar_title)).setText(title);
	}
	
	private void initView() {

		// Init Recipe Values

		TextView recipeIntructionTextView = (TextView) findViewById(R.id.recipe_intruction_textview);
		recipeIntructionTextView.setText("葱油饼我是宝宝我是宝宝我是，宝宝我是宝宝我是、宝宝我、是宝宝、我是宝宝我是宝宝我是宝宝我是宝宝我是宝宝我是我是宝宝\r\n\r\n我是宝宝宝宝我是宝宝我是宝宝我是宝宝我是宝宝我是宝宝我是宝宝我是宝宝");
		
		TextView recipeAuthorTextView = (TextView) findViewById(R.id.recipe_author_textview);
		recipeAuthorTextView.setText("longrenle");
		
		TextView recipeAboutTextView = (TextView) findViewById(R.id.recipe_about_textview);
		recipeAboutTextView.setText(String.format(getResources().getString(R.string.biz_recipe_about), 233, 454));
		
		GridView grid = (GridView) findViewById(R.id.material_gridview);
		grid.setAdapter(new RecipeMaterialAdapter(this));
		
		ListView list = (ListView) findViewById(R.id.procedure_listview);
		list.setAdapter(new RecipeProcedureAdapter(this));
		
		GridView gridRelatedGridView = (GridView) findViewById(R.id.related_recipe_gridview);
		gridRelatedGridView.setAdapter(new RecipeRelatedRecipesAdapter(this));
		
		TextView commentNum = (TextView) findViewById(R.id.comment_num);
		commentNum.setText(String.format(getResources().getString(R.string.biz_recipe_comment_num), 234));
		
		TextView recipeTipsTextView = (TextView) findViewById(R.id.recipe_tips_textview);
		recipeTipsTextView.setText("小贴士小贴士小贴士小贴士小贴士小贴士小贴士小贴士小贴士小贴士小贴士小贴士小贴士小贴士小贴士小贴士小贴士小贴士小贴士小贴士小贴士");
		
		
		TextView commentItem = (TextView) findViewById(R.id.comment_item);
		commentItem.setText(Html.fromHtml(String.format(FINISHEN_DISH_TAG_STRING,
				"小笨蛋", "真的很好吃啊",
				"longrenle", "不错不错")));
		
		View commentsLinkView = findViewById(R.id.comments_link);
		commentsLinkView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		TextView finishedView = (TextView) findViewById(R.id.finished_dish_textview);
		finishedView.setText(String.format(getResources().getString(R.string.biz_recipe_finished_dish), "葱油饼"));
		finishedView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		// Tabbar Event Listener
		
		((TextView)this.findViewById(R.id.tabbar_textview_buy)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((TextView)v).setCompoundDrawablesWithIntrinsicBounds(null,
						getResources().getDrawable(R.drawable.recipe_tabbar_bought),
						null, null);
			}
		});
		
		((TextView)this.findViewById(R.id.tabbar_textview_like)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((TextView)v).setCompoundDrawablesWithIntrinsicBounds(null,
						getResources().getDrawable(R.drawable.recipe_tabbar_likehl),
						null, null);
			}
		});

		((TextView)this.findViewById(R.id.tabbar_textview_upload)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
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
			
			mRecipeEntity = RecipeModel.getRecipe(RecipeActivity.this, 0);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			
			showProgress(false);
			super.onPostExecute(result);
		}

	}

}
