package com.m6.gocook.biz.recipe.comment;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.m6.gocook.R;
import com.m6.gocook.base.activity.BaseActivity;
import com.m6.gocook.base.constant.Constants;
import com.m6.gocook.base.entity.RecipeCommentList;
import com.m6.gocook.base.entity.RecipeCommentList.RecipeCommentItem;
import com.m6.gocook.base.fragment.BaseFragment;
import com.m6.gocook.base.fragment.FragmentHelper;
import com.m6.gocook.base.protocol.Protocol;
import com.m6.gocook.biz.account.AccountModel;
import com.m6.gocook.biz.recipe.RecipeModel;
import com.m6.gocook.biz.recipe.recipe.RecipeFragment;
import com.m6.gocook.util.cache.util.AsyncTask;
import com.m6.gocook.util.cache.util.ImageFetcher;
import com.m6.gocook.util.cache.util.ImageCache.ImageCacheParams;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class RecipeCommentFragment extends BaseFragment {

	private final String TAG = RecipeCommentFragment.class.getCanonicalName();
	
	public static final String ARGUMENT_KEY_RECIPE_ID = "intent_key_recipe_id";
	public static final String ARGUMENT_KEY_RECIPE_NAME = "intent_key_recipe_name";

	private Context mContext = null;
	private View mRootView = null;
	private EditText mInputText = null;
	
	private RecipeCommentAdapter mAdapter = null;
	
	private AchieveCommentsTask mReadCommentsTask = null;
	private PostCommentTask mPostTask = null;
	
	// DataSet
	private String mRecipeId;
	
	public static void startInActivity(Context context, String recipeId, String recipeName) {
		Bundle argument = new Bundle();
		argument.putString(RecipeCommentFragment.ARGUMENT_KEY_RECIPE_ID, recipeId);
		argument.putString(RecipeCommentFragment.ARGUMENT_KEY_RECIPE_NAME, recipeName);
        Intent intent = FragmentHelper.getIntent(context, BaseActivity.class, 
        		RecipeCommentFragment.class.getName(), 
        		RecipeCommentFragment.class.getName(), argument);
        context.startActivity(intent);
	}
	
	private void setTitle(String title) {
		getActionBar().setTitle(title);
	}
	
	private View findViewById(int id) {
		if(mRootView != null) {
			return mRootView.findViewById(id);
		} else {
			return null;
		}
	}
	
	@Override
	public View onCreateFragmentView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_recipe_comment, null, false);
		return mRootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		doCreate();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// set SoftinputMode for activity
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		
		mContext = getActivity();
	}
	
	private void doCreate() {
		Bundle argument = getArguments();
		mRecipeId = argument.getString(RecipeCommentFragment.ARGUMENT_KEY_RECIPE_ID);
		
		setTitle(argument.getString(RecipeCommentFragment.ARGUMENT_KEY_RECIPE_NAME));
		
		mInputText = (EditText) findViewById(R.id.input_edittext);
		
		mReadCommentsTask = new AchieveCommentsTask();
		mReadCommentsTask.execute();
		
		
		Button postButton = (Button) findViewById(R.id.post_button);
		postButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String content = mInputText.getText().toString().trim();
				if(TextUtils.isEmpty(content)) {
					Toast.makeText(mContext, R.string.biz_recipe_comment_input_null, Toast.LENGTH_SHORT).show();
				} else {
					if(mPostTask == null) {
						mPostTask = new PostCommentTask();
						mPostTask.execute(content);
					}
				}
			}
		});
	}
	
	public class AchieveCommentsTask extends AsyncTask<Void, Void, RecipeCommentList> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgress(true);
		}
		
		@Override
		protected RecipeCommentList doInBackground(Void... params) {
			return RecipeModel.getRecipeComments(mContext, mRecipeId);
		}
		
		@Override
		protected void onPostExecute(RecipeCommentList result) {
			showProgress(false);
			if(result != null) {
				ListView list = (ListView) findViewById(R.id.comments_listview);
				mAdapter = new RecipeCommentAdapter(mContext, result, mImageFetcher);
				list.setAdapter(mAdapter);
			}
		}
		
	}
	
	public class PostCommentTask extends AsyncTask<String, Void, Boolean> {
		
		@Override
		protected void onPreExecute() {
			mInputText.setEnabled(false);
		}
		
		@Override
		protected Boolean doInBackground(String... params) {
			if(params.length > 0 && !TextUtils.isEmpty(params[0])) {
				String result = RecipeModel.postComment(mContext, mRecipeId, params[0]);
				if (!TextUtils.isEmpty(result)) {
					try {
						JSONObject json = new JSONObject(result);
						int responseCode = json.optInt(Protocol.KEY_RESULT);
						if (responseCode == Protocol.VALUE_RESULT_OK) {
							return true;
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
			return false;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			mPostTask = null;
			
			if(result) {
				if(mAdapter != null) {
					RecipeCommentItem item = new RecipeCommentItem();
					item.setName(AccountModel.getUsername(mContext));
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
					String currentDateandTime = sdf.format(new Date());
					item.setCreateTime(currentDateandTime);
					item.setPortrait(AccountModel.getAvatarPath(mContext));
					item.setContent(mInputText.getText().toString().trim());
					mAdapter.addItem(item);
					mAdapter.notifyDataSetChanged();
				}

				mInputText.setText("");
				ListView list = (ListView) findViewById(R.id.comments_listview);
				list.setSelection(0);
				
			} else {
				Toast.makeText(mContext, R.string.biz_recipe_comment_addfailed, Toast.LENGTH_SHORT).show();	
			}
			
			mInputText.setEnabled(true);
		}
		
		
	}

}
