package com.m6.gocook.biz.account;

import java.util.Random;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.m6.gocook.R;
import com.m6.gocook.base.fragment.BaseFragment;
import com.m6.gocook.base.fragment.BaseWebFragment;
import com.m6.gocook.base.fragment.FragmentHelper;
import com.m6.gocook.base.protocol.Protocol;

public class WebLoginOrRegisterFragment extends BaseFragment {
	
	private int mLoginRND;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Random random = new Random();
		mLoginRND = random.nextInt();
	}
	
	@Override
	public View onCreateFragmentView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_web_loginorregister, container, false);
	}
	
	@Override
	protected View onCreateActionBarView(LayoutInflater inflater,
			ViewGroup container) {
		return null;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		view.findViewById(R.id.login).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FragmentHelper.startActivity(getActivity(), 
						BaseWebFragment.newInstance(String.format(Protocol.URL_LOGIN_WEB, mLoginRND), 
								 getString(R.string.biz_account_tab_login)));
			}
		});
		
		view.findViewById(R.id.register).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
	}
}
