package com.m6.gocook.biz.profile;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.m6.gocook.R;
import com.m6.gocook.base.fragment.BaseFragment;
import com.m6.gocook.base.view.ActionBar;
import com.m6.gocook.biz.account.AccountModel;

public class ProfileIntroFragment extends BaseFragment {
	
	public static final String PARAM_INTRO = "param_intro";

	@Override
	public View onCreateFragmentView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_profile_intro, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		Bundle args = getArguments();
		if(args != null) {
			String intro = args.getString(PARAM_INTRO);
			if(!TextUtils.isEmpty(intro)) {
				TextView introView = (TextView) getView().findViewById(R.id.intro);
				introView.setText(intro);
			}
		}
		
		String title = String.format(getString(R.string.biz_profile_introduction_title), AccountModel.getUsername(getActivity()));
		ActionBar action = getActionBar();
		action.setTitle(title);
	}
}
