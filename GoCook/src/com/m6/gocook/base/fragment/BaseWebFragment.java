package com.m6.gocook.base.fragment;

import com.m6.gocook.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;


public class BaseWebFragment extends BaseFragment {
	
	private static final String PARAM_URL = "param_url";
	
	private String mUrl;
	
	public static BaseWebFragment newInstance(String url) {
		BaseWebFragment fragment = new BaseWebFragment();
		Bundle bundle = new Bundle();
		bundle.putString(PARAM_URL, url);
		fragment.setArguments(bundle);
		return fragment;
	}
	
	@Override
	public View onCreateFragmentView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.base_web_fragment, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		Bundle bundle = getArguments();
		if (bundle != null) {
			mUrl = bundle.getString(PARAM_URL);
			WebView webView = (WebView) getView().findViewById(R.id.webview);
			webView.loadUrl(mUrl);
		}
	}

}
