package com.m6.gocook.biz.recipe.recipe;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.m6.gocook.R;

public class UploadImageLayout extends FrameLayout {

	
	private ImageView mImageView;
	private ProgressBar mProgressBar;
	
	private String mServerPath;

	public UploadImageLayout(Context context) {
		super(context);
		init(context);
	}
	
	public UploadImageLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	public UploadImageLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	private void init(Context context) {
		LayoutInflater.from(context).inflate(R.layout.layout_recipe_upload_image, this, true);
		mImageView = (ImageView) findViewById(R.id.image);
		mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
		
		this.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
			}
		});
		
	}

	public static class UploadAsyncTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			
			return null;
		}
		
	}
}
