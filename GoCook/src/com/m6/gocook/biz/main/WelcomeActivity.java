package com.m6.gocook.biz.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.m6.gocook.R;

public class WelcomeActivity extends FragmentActivity {
	
	private static Handler mHandler = new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_welcome);
		
//		final ImageView splashView1 = (ImageView) findViewById(R.id.splash_1);
		
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
//				Animation animation = AnimationUtils.loadAnimation(WelcomeActivity.this, R.anim.fade_out_slow);
//				animation.setFillAfter(true);
//				splashView1.startAnimation(animation);
				
				startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
				WelcomeActivity.this.finish();
			}
		}, 2000);
		
//		mHandler.postDelayed(new Runnable() {
//			
//			@Override
//			public void run() {
//				startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
//				WelcomeActivity.this.finish();
//			}
//		}, 4000);
	}
	

}
