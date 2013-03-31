package com.m6.gocook.biz.recipe;

import com.m6.gocook.R;
import com.m6.gocook.biz.profile.RecipeAdapter;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

public class RecipeActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recipe);
		
		setTitle("葱油饼");
		
		initView();
		
		GridView grid = (GridView) findViewById(R.id.material_gridview);
		grid.setAdapter(new RecipeMaterialAdapter(this));
		
		ListView list = (ListView) findViewById(R.id.procedure_listview);
		list.setAdapter(new RecipeprocedureAdapter(this));
	}
	
	private void setTitle(String title) {
		((TextView) findViewById(R.id.actionbar_title)).setText(title);
	}
	
	private void initView() {

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
	
}
