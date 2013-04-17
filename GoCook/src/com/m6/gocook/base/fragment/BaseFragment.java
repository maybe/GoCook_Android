package com.m6.gocook.base.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.m6.gocook.R;
import com.m6.gocook.base.view.ActionBar;

public class BaseFragment extends Fragment {

	private ActionBar mAction;
	
	@Override
	public final View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		RelativeLayout root = (RelativeLayout) inflater.inflate(R.layout.base_fragment_layout, container, false);
		
		View actionBarView = createDefaultActionBarView(inflater, container);
		mAction = new ActionBar(actionBarView);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        root.addView(actionBarView, lp);
		
		View content = onCreateFragmentView(inflater, container, savedInstanceState);
		if(content != null) {
			lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
            lp.addRule(RelativeLayout.BELOW, actionBarView.getId());
            root.addView(content, lp);
		}
		return root;
	}
	
	/**
	 * Called to have the fragment instantiate its user interface view. This is optional.
	 * 
	 * @return Return the View for the fragment's UI, or null.
	 */
	public View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return null;
	}
	
	private View createDefaultActionBarView(LayoutInflater inflater, ViewGroup container) {
		return inflater.inflate(R.layout.base_actionbar, container, false);
	}
	
	public ActionBar getAction() {
		return mAction;
	}
	
}
