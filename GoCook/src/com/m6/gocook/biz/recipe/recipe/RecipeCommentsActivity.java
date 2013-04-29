package com.m6.gocook.biz.recipe.recipe;

import com.m6.gocook.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

public class RecipeCommentsActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.activity_recipe_comments);
		
		setTitle("葱油饼");
		
		ListView list = (ListView) findViewById(R.id.comments_listview);
		list.setAdapter(new RecipeCommentAdapter(this));
		
		super.onCreate(savedInstanceState);
	}
	
	private void setTitle(String title) {
		((TextView) findViewById(R.id.actionbar_title)).setText(title);
	}

}
