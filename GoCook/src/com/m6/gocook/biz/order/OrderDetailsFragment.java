package com.m6.gocook.biz.order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.m6.gocook.R;
import com.m6.gocook.base.activity.BaseActivity;
import com.m6.gocook.base.entity.response.COrderQueryResult.COrderItem;
import com.m6.gocook.base.fragment.BaseFragment;
import com.m6.gocook.base.fragment.FragmentHelper;
import com.m6.gocook.base.view.ActionBar;

public class OrderDetailsFragment extends BaseFragment {
	
	private OrderDetailsAdapter mAdapter;
	private COrderItem mData;
	
	private static final String PARAM_DATA = "param_data";
	
	
	public static void goToOrderDetails(Context context, COrderItem orderItem) {
		Bundle args = new Bundle();
		args.putSerializable(OrderDetailsFragment.PARAM_DATA, orderItem);
		Intent intent = FragmentHelper.getIntent(context, BaseActivity.class, 
				OrderDetailsFragment.class.getName(), OrderDetailsFragment.class.getName(), args);
		context.startActivity(intent);
	}
	
	@Override
	public View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_buy_order_details, container, false);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle args = getArguments();
		if (args != null) {
			mData = (COrderItem) args.getSerializable(PARAM_DATA);
		}
		if (mData != null) {
			mAdapter = new OrderDetailsAdapter(getActivity(), mImageFetcher, mData.getOrderWares());
		}
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		ActionBar actionBar = getActionBar();
		actionBar.setTitle(R.string.biz_buy_order_title);
		
		if (mData == null) {
			return;
		}
		
		((TextView) view.findViewById(R.id.create_time)).setText(getString(R.string.biz_buy_order_details_createtime, mData.getCreateTime()));
		((TextView) view.findViewById(R.id.id)).setText(getString(R.string.biz_buy_order_details_orderid, mData.getCode()));
		((TextView) view.findViewById(R.id.name)).setText(getString(R.string.biz_buy_order_details_name, mData.getCustName()));
		((TextView) view.findViewById(R.id.phone)).setText(getString(R.string.biz_buy_order_details_phone, mData.getRecvMobile()));
		((TextView) view.findViewById(R.id.delivery_time)).setText(getString(R.string.biz_buy_order_details_deliverytime, mData.getDeliveryTimeType()));
		((TextView) view.findViewById(R.id.delivery_type)).setText(getString(R.string.biz_buy_order_details_deliverytype, mData.getDeliveryType()));
		((TextView) view.findViewById(R.id.cost)).setText(getString(R.string.biz_buy_order_details_cost, mData.getCost()));
		
		ListView listView = (ListView) view.findViewById(R.id.list);
		listView.setAdapter(mAdapter);
	}
}
