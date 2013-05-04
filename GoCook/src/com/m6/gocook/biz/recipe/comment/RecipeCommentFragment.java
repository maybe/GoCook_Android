package com.m6.gocook.biz.recipe.comment;

import com.m6.gocook.R;
import com.m6.gocook.base.activity.BaseActivity;
import com.m6.gocook.base.fragment.BaseFragment;
import com.m6.gocook.base.fragment.FragmentHelper;
import com.m6.gocook.biz.recipe.recipe.RecipeFragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

public class RecipeCommentFragment extends BaseFragment {

	private final String TAG = RecipeCommentFragment.class.getCanonicalName();
	
	public static final String ARGUMENT_KEY_RECIPE_ID = "intent_key_recipe_id";

	private Context mContext = null;
	private View mRootView = null;
	
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
		getAction().setTitle(title);
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
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// set softinputmode for activity
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
	}
	
	private void doCreate() {
		Bundle argument = getArguments();
		mRecipeId = argument.getString(RecipeFragment.ARGUMENT_KEY_RECIPE_ID);
		
		setTitle("葱油饼");
		
		ListView list = (ListView) findViewById(R.id.comments_listview);
		list.setAdapter(new RecipeCommentAdapter(getActivity()));
	}

}
