package com.m6.gocook.biz.recipe.recipe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.m6.gocook.R;
import com.m6.gocook.base.activity.BaseActivity;
import com.m6.gocook.base.entity.RecipeCommentList;
import com.m6.gocook.base.entity.RecipeCommentList.RecipeCommentItem;
import com.m6.gocook.base.entity.RecipeEntity;
import com.m6.gocook.base.fragment.BaseFragment;
import com.m6.gocook.base.fragment.FragmentHelper;
import com.m6.gocook.base.fragment.OnActivityAction;
import com.m6.gocook.biz.account.AccountModel;
import com.m6.gocook.biz.account.LoginFragment;
import com.m6.gocook.biz.main.MainActivityHelper;
import com.m6.gocook.biz.profile.ProfileFragment;
import com.m6.gocook.biz.purchase.PurchaseListModel;
import com.m6.gocook.biz.recipe.RecipeModel;
import com.m6.gocook.biz.recipe.comment.RecipeCommentFragment;
import com.m6.gocook.biz.recipe.recipe.RecipeEditFragment.Mode;
import com.m6.gocook.util.log.Logger;

public class RecipeFragment extends BaseFragment implements OnActivityAction{

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
	private boolean mRefreshRecipe = false;
	
	private ProgressDialog mProgressDialog;
	
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
	
	public static void startInActivityForResult(Activity context, String recipeId, String recipeName) {
		Bundle argument = new Bundle();
		argument.putString(RecipeFragment.ARGUMENT_KEY_RECIPE_ID, recipeId);
		argument.putString(RecipeFragment.ARGUMENT_KEY_RECIPE_NAME, recipeName);
        Intent intent = FragmentHelper.getIntent(context, BaseActivity.class, 
        		RecipeFragment.class.getName(), 
        		RecipeFragment.class.getName(), argument);
        context.startActivityForResult(intent, MainActivityHelper.REQUEST_CODE_RECIPE);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		MainActivityHelper.registerOnActivityActionListener(this);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
        mContext = null;
        mRootView = null;
		MainActivityHelper.unRegisterOnActivityActionListener(this);
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
		Fragment f = getChildFragmentManager().findFragmentByTag(ActionSelectDialogFragment.class.getName());
		if (f != null) {
			DialogFragment df = (DialogFragment) f;
			df.dismiss();
			getFragmentManager().beginTransaction().remove(f).commit();
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
		mAchieveRecipeTask.execute();
		
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
		
		if(AccountModel.isLogon(mContext) 
				&& TextUtils.equals(AccountModel.getUserId(mContext), mRecipeEntity.getAuthorId())) {
			getActionBar().setRightButton(R.string.biz_recipe_edit_actionbar_opt, R.drawable.btn_selector);
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
		recipeAuthorTextView.setText(mRecipeEntity.getAuthorName());
		recipeAuthorTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String nickName = mRecipeEntity.getAuthorName();
				if (!TextUtils.isEmpty(nickName)) {
					if (nickName.equals(AccountModel.getUsername(getActivity()))) {
						ProfileFragment.startProfileFragment(mContext, 
								ProfileFragment.PROFILE_MYSELF,
								mRecipeEntity.getAuthorId());
					} else {
						ProfileFragment.startProfileFragment(mContext, 
								ProfileFragment.PROFILE_OTHERS,
								mRecipeEntity.getAuthorId());
					}
				}
				
			}
		});

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
							R.drawable.tab_buy), null,
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
											R.drawable.tab_buy_alpha), null,
									null);
							tabBarBuyTextView.setText(getResources().getText(R.string.biz_recipe_tabbar_menu_addshoppinglist));
						} else {
							PurchaseListModel.saveRecipeToProcedureList(getActivity(), mRecipeEntity);
							tabBarBuyTextView.setCompoundDrawablesWithIntrinsicBounds(
									null,
									getResources().getDrawable(
											R.drawable.tab_buy), null,
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
							LoginFragment.JumpToLoginFragment(mContext);
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
	
	private void showProgressBar(boolean show) {
		showProgressBar(show, null);
	}
	private void showProgressBar(boolean show, String message) {
		
		if(mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(getActivity());
			mProgressDialog.setCanceledOnTouchOutside(false);
			mProgressDialog.setCancelable(false);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		}
		
		if(message != null) {
			mProgressDialog.setMessage(message);
		}
			
		if(show) {
			mProgressDialog.show();
		} else {
			mProgressDialog.hide();
		}
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
	
	public class DeleteRecipeTast extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			return RecipeModel.deleteRecipe(mContext, mRecipeId);
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if(result) {
				Toast.makeText(mContext, R.string.biz_recipe_edit_delete_ok, Toast.LENGTH_SHORT).show();
				getActivity().setResult(MainActivityHelper.RESULT_CODE_RECIPE_DELETED);
				getActivity().finish();
			} else {
				Toast.makeText(mContext, R.string.biz_recipe_edit_delete_failed, Toast.LENGTH_SHORT).show();
			}
			showProgressBar(false);
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgressBar(true, getActivity().getResources().getString(R.string.biz_recipe_edit_deleteing));
		}
		
	}

	@Override
	public void onActionBarRightButtonClick(View v) {
		
		final CharSequence[] items = {getResources().getString(R.string.biz_recipe_edit_actionbar_edit),
				getResources().getString(R.string.biz_recipe_edit_actionbar_delete)}; 
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setItems(items, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		        switch (item) {
				case 0:
					RecipeEditFragment.startInActivityForResult(getActivity(), Mode.RECIPE_EDIT, mRecipeId);
					break;
				case 1:
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					builder.setMessage(getResources().getString(R.string.biz_recipe_edit_title_isdelete))
					       .setCancelable(false)
					       .setPositiveButton(R.string.biz_recipe_edit_title_deleteok, new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface dialog, int id) {
					                new DeleteRecipeTast().execute();
					           }
					       })
					       .setNegativeButton(R.string.biz_recipe_edit_title_deleteno, new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface dialog, int id) {
					                dialog.cancel();
					           }
					       });
					AlertDialog alert = builder.create();
					alert.show();
					break;
				default:
					break;
				}
		    }
		});
		AlertDialog alert = builder.create();
		alert.show();

	}
	
	@Override
	public void onCustomActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("RecipeFragment", String.format("onActivityResult: requesCode:%d, resultCode:%d", requestCode, resultCode));
		
		if(requestCode == MainActivityHelper.REQUEST_CODE_RECIPE_EDIT) {
			if(resultCode == MainActivityHelper.RESULT_CODE_RECIPE_EDIT_UPDATED) {
				mAchieveRecipeTask = new AchieveRecipeTask();
				mAchieveRecipeTask.execute();
				
				getActivity().setResult(MainActivityHelper.RESULT_CODE_RECIPE_UPDATED);
			}
		}
	}

}
