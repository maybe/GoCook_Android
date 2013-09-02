package com.m6.gocook.biz.buy;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.m6.gocook.R;
import com.m6.gocook.base.entity.response.CWareItem;
import com.m6.gocook.base.fragment.BaseFragment;
import com.m6.gocook.base.view.ActionBar;

public class BuyDetailsFragment extends BaseFragment {
	
	public static final String PARAM_RESULT = "param_result";
	
	private CWareItem mWareItem;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle args = getArguments();
		if (args != null) {
			mWareItem = (CWareItem) args.getSerializable(PARAM_RESULT);
		}
	}
	
	@Override
	public View onCreateFragmentView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_buy_details, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		if (mWareItem != null) {
			ActionBar actionBar = getActionBar();
			actionBar.setTitle(mWareItem.getName());
			((TextView) view.findViewById(R.id.title)).setText(mWareItem.getName());
			((TextView) view.findViewById(R.id.price)).setText(getString(R.string.biz_buy_search_adapter_price, String.valueOf(mWareItem.getPrice())));
			((TextView) view.findViewById(R.id.unit)).setText(getString(R.string.biz_buy_search_adapter_unit, mWareItem.getUnit()));
			((TextView) view.findViewById(R.id.norm)).setText(getString(R.string.biz_buy_search_adapter_norm, mWareItem.getNorm()));
			List<String> methods = mWareItem.getDealMethod();
			StringBuilder methodBuilder = new StringBuilder();
			if (methods != null && !methods.isEmpty()) {
				for (int i = 0; i < methods.size(); i++) {
					methodBuilder.append(methods.get(i));
					methodBuilder.append("\n");
				}
			}
			((TextView) view.findViewById(R.id.method)).setText(getString(R.string.biz_buy_search_adapter_method, methodBuilder));
//			((TextView) view.findViewById(R.id.intro)).setText(getString(R.string.biz_buy_search_adapter_intro, mWareItem.get));
			mImageFetcher.loadImage(mWareItem.getImageUrl(), (ImageView) view.findViewById(R.id.image));
			view.findViewById(R.id.buy).setOnClickListener(buyListener);
		}
	}
	
	private OnClickListener buyListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
		}
	};

}
