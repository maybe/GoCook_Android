package com.m6.gocook.biz.profile;

import com.m6.gocook.R;
import com.m6.gocook.biz.account.AccountModel;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.GridView;
import android.widget.TextView;

public class ProfileActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		
		Intent intent = getIntent();
		if(intent != null) {
			Bundle args = intent.getExtras();
			if(args != null) {
				setTitle(args.getString(AccountModel.RETURN_USERNAME));
			}
		}
		
		new RecipeTask(this).execute((Void) null);
	}
	
	private void setTitle(String title) {
		((TextView) findViewById(R.id.actionbar_title)).setText(title);
	}
	
	private static class RecipeTask extends AsyncTask<Void, Void, Void> {

		private FragmentActivity mActivity;
		
		public RecipeTask(FragmentActivity activity) {
			mActivity = activity;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			GridView grid = (GridView) mActivity.findViewById(R.id.recipe_grid);
			grid.setAdapter(new RecipeAdapter(mActivity));
		}
		
	}
}
