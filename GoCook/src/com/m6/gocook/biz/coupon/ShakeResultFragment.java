package com.m6.gocook.biz.coupon;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.m6.gocook.R;
import com.m6.gocook.base.entity.Coupon;
import com.m6.gocook.base.entity.CouponDelay;
import com.m6.gocook.base.entity.Sale;
import com.m6.gocook.base.entity.Sale.Condition;
import com.m6.gocook.base.fragment.BaseFragment;
import com.m6.gocook.base.view.ActionBar;
import com.m6.gocook.biz.main.MainActivityHelper;

public class ShakeResultFragment extends BaseFragment {

	public static final String PARAM_SALE = "param_sale";
	public static final String PARAM_COUPON_ID = "param_coupon_id";
	
	private Sale mSale;
	
	private GetCouponTask mCouponTask;
	private DelayCouponTask mDelayCouponTask;
	
	private ProgressDialog mProgressDialog;
	
	private String mCouponId;
	
	public static ShakeResultFragment newInstance(String couponId) {
		ShakeResultFragment fragment = new ShakeResultFragment();
    	Bundle bundle = new Bundle();
    	bundle.putString(PARAM_COUPON_ID, couponId);
    	fragment.setArguments(bundle);
    	return fragment;
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		if (bundle != null) {
			mSale = (Sale) bundle.getSerializable(PARAM_SALE);
			mCouponId = bundle.getString(PARAM_COUPON_ID);
		}
	}
	
	@Override
	public View onCreateFragmentView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		mProgressDialog = new ProgressDialog(getActivity());
		mProgressDialog.setCanceledOnTouchOutside(false);
		return inflater.inflate(R.layout.fragment_coupon_shake_result, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		ActionBar actionBar = getActionBar();
		actionBar.setTitle(R.string.biz_coupon_shake_title);
		
		if (mSale != null) { // 摇一摇得到销售金额，绑定数据
			bindData(mSale);
		} else if (!TextUtils.isEmpty(mCouponId)) { // 延期记录获取优惠券
			if (mCouponTask == null) {
				mCouponTask = new GetCouponTask(getActivity(), mCouponId);
				mCouponTask.execute((Void) null);
			}
		}
	}
	
	private void bindData(final Sale sale) {
		if (sale == null) {
			return;
		}
		
		View view = getView();
		if (view == null) {
			return;
		}
		
		final TextView message = (TextView) view.findViewById(R.id.message);
		if (sale.isSuccess()) { // 查询正常
			if (sale.getCondition() == Condition.Match) {
				message.setText(getString(R.string.biz_coupon_shake_result_message, 
						sale.getTime(), sale.getSaleCount(), sale.getSaleFee()));
				view.findViewById(R.id.delay).setVisibility(View.VISIBLE);
			} else {
				view.findViewById(R.id.delay).setVisibility(View.GONE);
				message.setText(getString(R.string.biz_coupon_shake_result_mismatch, sale.getRemark()));
			}
		} else { // 查询失败
			showFailureMessage(R.string.biz_coupon_shake_result_failed_tip);
		}
		
		view.findViewById(R.id.positive).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (sale.getCondition() == Condition.Match) {
					if (mCouponTask == null) {
						mCouponTask = new GetCouponTask(getActivity(), "0");
						mCouponTask.execute((Void) null);
						
						mProgressDialog.setMessage(getString(R.string.biz_coupon_shake_result_progress));
						mProgressDialog.show();
					}
				} else {
					getActivity().finish();
				}
			}
		});
		
		view.findViewById(R.id.delay).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mDelayCouponTask == null) {
					mDelayCouponTask = new DelayCouponTask(getActivity());
					mDelayCouponTask.execute((Void) null);
					
					mProgressDialog.setMessage(getString(R.string.biz_coupon_shake_result_delay_progress));
					mProgressDialog.show();
				}
			}
		});
	}
	
	private void showFailureMessage(int message) {
		setEmptyMessage(message);
		showEmpty(true);
		getView().findViewById(R.id.delay).setVisibility(View.GONE);
		getView().findViewById(R.id.positive).setVisibility(View.GONE);
	}
	
	public class GetCouponTask extends AsyncTask<Void, Void, List<Coupon>> {
		
		private String mCouponId;
		private Context mContext;
		
		public GetCouponTask(Context context, String couponId) {
			mCouponId = couponId;
			mContext = context.getApplicationContext();
		}
		
		@Override
		protected void onPreExecute() {
			if (isAdded()) {
				showProgress(true);
			}
		}

		@Override
		protected List<Coupon> doInBackground(Void... params) {
			return CouponModel.getCoupon(mContext, mCouponId);
		}
		
		@Override
		protected void onPostExecute(List<Coupon> result) {
			mCouponTask = null;
			if (isAdded()) {
				showProgress(false);
				if (!result.isEmpty()) {
					getView().findViewById(R.id.message).setVisibility(View.GONE);
					getView().findViewById(R.id.delay).setVisibility(View.GONE);
					ListView listView = (ListView) getView().findViewById(R.id.list);
					listView.setVisibility(View.VISIBLE);
					
					ShakeResultListAdapter adapter = new ShakeResultListAdapter(mContext, result);
					listView.setAdapter(adapter);
					getView().findViewById(R.id.positive).setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							getActivity().setResult(MainActivityHelper.RESULT_CODE_COUPON);
							getActivity().finish();
						}
					});
				} else {
					showFailureMessage(R.string.biz_coupon_shake_result_get_coupon_failed_tip);
				}
			}
		}
		
		@Override
		protected void onCancelled() {
			mCouponTask = null;
		}
	}
	
	private class DelayCouponTask extends AsyncTask<Void, Void, CouponDelay> {

		private Context mContext;
		
		public DelayCouponTask(Context context) {
			mContext = context.getApplicationContext();
		}
		
		@Override
		protected CouponDelay doInBackground(Void... params) {
			return CouponModel.delayCoupon(mContext);
		}
		
		@Override
		protected void onPostExecute(CouponDelay result) {
			mDelayCouponTask = null;
			if (isAdded()) {
				if (result.getDelayRst() == CouponDelay.DELAY_RESULT_SUCCESS) {
					TextView message = (TextView) getView().findViewById(R.id.message);
					if (result.getCondition() == Condition.Match) {
						message.setText(getString(R.string.biz_coupon_shake_result_delay_match, result.getExpDay()));
					} else if (result.getCondition() == Condition.MisMatch) {
						message.setText(getString(R.string.biz_coupon_shake_result_delay_mismatch, result.getRemark()));
					}
					getView().findViewById(R.id.delay).setVisibility(View.GONE);
					getView().findViewById(R.id.positive).setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							getActivity().setResult(MainActivityHelper.RESULT_CODE_COUPON);
							getActivity().finish();
						}
					});
				} else if (result.getDelayRst() == CouponDelay.DELAY_RESULT_ALREADY) {
					showFailureMessage(R.string.biz_coupon_shake_result_delay_coupon_already_tip);
				} else {
					showFailureMessage(R.string.biz_coupon_shake_result_delay_coupon_failed_tip);
				}
				
				if (mProgressDialog != null) {
					mProgressDialog.dismiss();
				}
			}
		}
		
		@Override
		protected void onCancelled() {
			mDelayCouponTask = null;
		}
		
	}
}
