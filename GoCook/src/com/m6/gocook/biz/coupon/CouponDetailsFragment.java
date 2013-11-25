package com.m6.gocook.biz.coupon;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.google.zxing.client.android.encode.EncodeUtil;
import com.m6.gocook.R;
import com.m6.gocook.base.entity.Coupon;
import com.m6.gocook.base.fragment.BaseFragment;

public class CouponDetailsFragment extends BaseFragment {

	private static final String PARAM_COUPON = "param_coupon";
	
	private Coupon mCoupon;
	
	public static CouponDetailsFragment newInstance(Coupon coupon) {
		CouponDetailsFragment fragment = new CouponDetailsFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(PARAM_COUPON, coupon);
		fragment.setArguments(bundle);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mCoupon = getArguments() == null ? null : (Coupon) getArguments().getSerializable(PARAM_COUPON);
	}
	
	@Override
	public View onCreateFragmentView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_coupon_details, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		if (mCoupon != null) {
			initViews();
		} else {
			setEmptyMessage(R.string.biz_coupon_details_empty);
			showEmpty(true);
		}
	}
	
	private void initViews() {
		int barcodeWidth = getResources().getDimensionPixelSize(R.dimen.biz_coupon_list_barcode_width);
		int barcodeHeight = getResources().getDimensionPixelSize(R.dimen.biz_coupon_list_barcode_height);
		try {
			Bitmap barcodeBitmap = EncodeUtil.createBarCode(mCoupon.getCoupon(), barcodeWidth, barcodeHeight);
			ImageView barcode = (ImageView) getView().findViewById(R.id.barcode);
			barcode.setImageBitmap(barcodeBitmap);
		} catch (WriterException e) {
			e.printStackTrace();
		}
		
		final boolean isInvalid = mCoupon.getStatus() == Coupon.STATUS_INVALID;
		final boolean isDelay = mCoupon.isDelay();
		final boolean isCoupon = mCoupon.getKtype() == Coupon.KTYPE_COUPON;
		TextView content = (TextView) getView().findViewById(R.id.content);
		
		if (!isDelay && isCoupon) { // 优惠券，包括过期和未过期的优惠券
			if (isInvalid) { // 过期
				content.setText(getString(R.string.biz_coupon_list_content_normal, 
						mCoupon.getcTime(), mCoupon.getSupplier(), mCoupon.getVal(), mCoupon.getName(), 
						mCoupon.getCoupon(), mCoupon.getEffDay(), mCoupon.getExpDay(), 
						mCoupon.getStores(), mCoupon.getCouponRemark()));
				
				content.setBackgroundColor(getResources().getColor(R.color.biz_coupon_invalid));
			} else { // 未过期
				content.setText(getString(R.string.biz_coupon_list_content_normal, 
						mCoupon.getcTime(), mCoupon.getSupplier(), mCoupon.getVal(), mCoupon.getName(), 
						mCoupon.getCoupon(), mCoupon.getEffDay(), mCoupon.getExpDay(), 
						mCoupon.getStores(), mCoupon.getCouponRemark()));
				content.setBackgroundColor(getResources().getColor(R.color.biz_coupon_normal));
			}
		} else { // 延期记录
			content.setText(getString(R.string.biz_coupon_list_content_delay,
					mCoupon.getEffDay(), mCoupon.getExpDay()));
			content.setBackgroundColor(getResources().getColor(R.color.biz_coupon_delay));
		}
	}
}
