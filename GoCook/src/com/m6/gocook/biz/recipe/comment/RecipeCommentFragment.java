package com.m6.gocook.biz.recipe.comment;

import java.sql.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.m6.gocook.R;
import com.m6.gocook.base.activity.BaseActivity;
import com.m6.gocook.base.entity.RecipeCommentList;
import com.m6.gocook.base.entity.RecipeCommentList.RecipeCommentItem;
import com.m6.gocook.base.fragment.BaseFragment;
import com.m6.gocook.base.fragment.FragmentHelper;
import com.m6.gocook.base.protocol.Protocol;
import com.m6.gocook.biz.account.AccountModel;
import com.m6.gocook.biz.recipe.RecipeModel;
import com.m6.gocook.biz.recipe.recipe.RecipeFragment;
import com.m6.gocook.util.cache.util.AsyncTask;

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

	private Context mContext = null;
	private View mRootView = null;
	private RecipeCommentAdapter mAdapter = null;
	
	private ReadCommentsTask mReadCommentsTask = null;
	private PostCommentTask mPostTask = null;
	
	// DataSet
	private String mRecipeId;
	
	public static void startInActivity(Context context, String recipeId) {
		Bundle argument = new Bundle();
		argument.putString(RecipeCommentFragment.ARGUMENT_KEY_RECIPE_ID, recipeId);
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
		mRecipeId = argument.getString(RecipeFragment.ARGUMENT_KEY_RECIPE_ID);
		
		mReadCommentsTask = new ReadCommentsTask();
		mReadCommentsTask.execute();
		
		Button postButton = (Button) findViewById(R.id.post_button);
		postButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditText editText = (EditText) findViewById(R.id.input_edittext);
				String content = editText.getText().toString().trim();
				if(TextUtils.isEmpty(content)) {
					Toast.makeText(mContext, "empty", Toast.LENGTH_SHORT).show();
				} else {
					if(mPostTask == null) {
						mPostTask = new PostCommentTask();
						mPostTask.execute(content);
					}
				}
			}
		});
	}
	
	public class ReadCommentsTask extends AsyncTask<Void, Void, RecipeCommentList> {

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
				mAdapter = new RecipeCommentAdapter(mContext, result);
				list.setAdapter(mAdapter);
			}
		}
		
	}
	
	public class PostCommentTask extends AsyncTask<String, Void, Boolean> {
		
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
				RecipeCommentItem item = new RecipeCommentItem();
				item.setName(AccountModel.getUsername(mContext));
				item.setCreateTime(Time.getCurrentTimezone());
				item.setPortrait(AccountModel.getAvatarPath(mContext));
				mAdapter.addItem(item);
				
				if(mAdapter != null) {
					mAdapter.notifyDataSetChanged();
				}
			} else {
				Toast.makeText(mContext, R.string.biz_recipe_comment_addfailed, Toast.LENGTH_SHORT).show();	
			}
		}
		
		
	}

}
