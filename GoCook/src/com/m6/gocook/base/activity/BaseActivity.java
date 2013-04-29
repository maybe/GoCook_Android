package com.m6.gocook.base.activity;

import com.m6.gocook.R;
import com.m6.gocook.base.view.ActionBar;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class BaseActivity extends FragmentActivity {

	private ActionBar mAction;
	
	@Override
	protected final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		LayoutInflater inflater = LayoutInflater.from(this);
		RelativeLayout root = (RelativeLayout) inflater.inflate(R.layout.base_fragment, null);
		View actionBarView = createDefaultActionBarView(inflater);
		
		mAction = new ActionBar(actionBarView);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        root.addView(actionBarView, lp);
		
		View content = onCreateActivityView(inflater);
		if(content != null) {
			lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
            lp.addRule(RelativeLayout.BELOW, actionBarView.getId());
            root.addView(content, lp);
		}
		setContentView(root);
		onCreateAction();
	}
	
	public void onCreateAction() {
		
	}
	
	public ActionBar getAction() {
		return mAction;
	}
	
	private View createDefaultActionBarView(LayoutInflater inflater) {
		return inflater.inflate(R.layout.base_actionbar, null);
	}
	
	public View onCreateActivityView(LayoutInflater inflater) {
		return null;
	}
	
}
