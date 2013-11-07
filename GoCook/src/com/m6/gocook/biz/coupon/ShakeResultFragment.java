package com.m6.gocook.biz.coupon;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.m6.gocook.R;
import com.m6.gocook.base.entity.Sale;
import com.m6.gocook.base.fragment.BaseFragment;
import com.m6.gocook.base.view.ActionBar;

public class ShakeResultFragment extends BaseFragment {

	public static final String PARAM_SALE = "param_sale";
	
	private Sale mSale;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		if (bundle != null) {
			mSale = (Sale) bundle.getSerializable(PARAM_SALE);
		}
	}
	
	@Override
	public View onCreateFragmentView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_coupon_shake_result, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		ActionBar actionBar = getActionBar();
		actionBar.setTitle(R.string.biz_coupon_shake_title);
		
		bindData(mSale);
		
		view.findViewById(R.id.positive).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		
		view.findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getActivity().finish();
			}
		});
	}
	
	private void bindData(Sale sale) {
		if (sale == null) {
			return;
		}
		
		View view = getView();
		if (view == null) {
			return;
		}
		
		if (sale.isSuccess()) {
			((TextView) view.findViewById(R.id.time)).setText(getString(R.string.biz_coupon_shake_result_time, sale.getTime()));
			((TextView) view.findViewById(R.id.sale_fee)).setText(getString(R.string.biz_coupon_shake_result_sale_fee, sale.getSaleFee()));
			((TextView) view.findViewById(R.id.sale_count)).setText(getString(R.string.biz_coupon_shake_result_sale_count, sale.getSaleCount()));
			((TextView) view.findViewById(R.id.remark)).setText(getString(R.string.biz_coupon_shake_result_remark, sale.getRemark()));
		} else {
			
		}
	}
}
