package com.m6.gocook.biz.coupon;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.google.zxing.client.android.encode.EncodeUtil;
import com.m6.gocook.R;
import com.m6.gocook.base.activity.BaseActivity;
import com.m6.gocook.base.entity.Coupon;
import com.m6.gocook.base.fragment.BaseFragment;
import com.m6.gocook.base.fragment.FragmentHelper;
import com.m6.gocook.base.view.ActionBar;
import com.m6.gocook.biz.main.MainActivityHelper;

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
		
		ActionBar actionBar = getActionBar();
		actionBar.setTitle(R.string.biz_coupon_list_title);
		
		if (mCoupon != null) {
			initViews();
		} else {
			setEmptyMessage(R.string.biz_coupon_details_empty);
			showEmpty(true);
		}
	}

	/**
	 * 查看web页面详情
	 */
	private void goToWebDetails() {
		getView().findViewById(R.id.web_detail).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				openBrower(mCoupon.getUrl());
			}
		});
	}
	
	private void initViews() {
		final boolean isInvalid = mCoupon.getStatus() == Coupon.STATUS_INVALID;
		final boolean isDelay = mCoupon.isDelay();
		final boolean isCoupon = mCoupon.getKtype() == Coupon.KTYPE_COUPON;
		final boolean isAd = mCoupon.getKtype() == Coupon.KTYPE_AD;
		final boolean isSellerCoupon = mCoupon.getKtype() == mCoupon.KTYPE_SELLER;
		final boolean isUsed = mCoupon.isused();
		
		TextView content = (TextView) getView().findViewById(R.id.content);
		ImageView couponImage = (ImageView) getView().findViewById(R.id.image);
		View contentZone = getView().findViewById(R.id.content_zone);
		
		if (!isDelay && isCoupon) { // 优惠券
			content.setText(getString(R.string.biz_coupon_list_content_normal, 
					mCoupon.getcTime(), mCoupon.getSupplier(), mCoupon.getVal(), mCoupon.getName(), 
					mCoupon.getCoupon(), mCoupon.getEffDay(), mCoupon.getExpDay(), 
					mCoupon.getStores(), mCoupon.getCouponRemark()));
			contentZone.setBackgroundColor(getResources().getColor(R.color.biz_coupon_normal));
			
			// 条形码
			int barcodeWidth = getResources().getDimensionPixelSize(R.dimen.biz_coupon_list_barcode_width);
			int barcodeHeight = getResources().getDimensionPixelSize(R.dimen.biz_coupon_list_barcode_height);
			try {
				Bitmap barcodeBitmap = EncodeUtil.createBarCode(mCoupon.getCoupon(), barcodeWidth, barcodeHeight);
				ImageView barcode = (ImageView) getView().findViewById(R.id.barcode);
				barcode.setImageBitmap(barcodeBitmap);
				
				barcode.setVisibility(View.VISIBLE);
			} catch (WriterException e) {
				e.printStackTrace();
			}
			mImageFetcher.loadImage(mCoupon.getImg(), couponImage);
			getView().findViewById(R.id.top_title).setVisibility(View.GONE);
			goToWebDetails();
		} else if (!isDelay && isAd) { // 广告
			content.setText(getString(R.string.biz_coupon_list_content_ad, 
					mCoupon.getName(), mCoupon.getSupplier(), mCoupon.getcTime(), mCoupon.getCouponRemark()));
			contentZone.setBackgroundColor(getResources().getColor(R.color.biz_coupon_ad));
			TextView shakeView = (TextView) getView().findViewById(R.id.top_title);
			shakeView.setVisibility(View.VISIBLE);
			shakeView.setText(R.string.biz_coupon_details_ad);
			mImageFetcher.loadImage(mCoupon.getImg(), couponImage);
			getView().findViewById(R.id.barcode).setVisibility(View.GONE);
			goToWebDetails();
		} else if (!isDelay && isSellerCoupon) { // 网络商家券
			content.setText(getString(R.string.biz_coupon_list_content_normal, 
					mCoupon.getcTime(), mCoupon.getSupplier(), mCoupon.getVal(), mCoupon.getName(), 
					mCoupon.getCoupon(), mCoupon.getEffDay(), mCoupon.getExpDay(), 
					mCoupon.getStores(), mCoupon.getCouponRemark()));
			contentZone.setBackgroundColor(getResources().getColor(R.color.biz_coupon_seller));
			
			// 条形码
			int barcodeWidth = getResources().getDimensionPixelSize(R.dimen.biz_coupon_list_barcode_width);
			int barcodeHeight = getResources().getDimensionPixelSize(R.dimen.biz_coupon_list_barcode_height);
			try {
				Bitmap barcodeBitmap = EncodeUtil.createBarCode(mCoupon.getCoupon(), barcodeWidth, barcodeHeight);
				ImageView barcode = (ImageView) getView().findViewById(R.id.barcode);
				barcode.setImageBitmap(barcodeBitmap);
				
				barcode.setVisibility(View.VISIBLE);
			} catch (WriterException e) {
				e.printStackTrace();
			}
			mImageFetcher.loadImage(mCoupon.getImg(), couponImage);
			getView().findViewById(R.id.top_title).setVisibility(View.GONE);
			goToWebDetails();
		} else if (isUsed) { // 已使用
			content.setText(getString(R.string.biz_coupon_list_content_normal, 
					mCoupon.getcTime(), mCoupon.getSupplier(), mCoupon.getVal(), mCoupon.getName(), 
					mCoupon.getCoupon(), mCoupon.getEffDay(), mCoupon.getExpDay(), 
					mCoupon.getStores(), mCoupon.getCouponRemark()));
			
			contentZone.setBackgroundColor(getResources().getColor(R.color.biz_coupon_used));
			TextView shakeView = (TextView) getView().findViewById(R.id.top_title);
			shakeView.setVisibility(View.VISIBLE);
			shakeView.setText(R.string.biz_coupon_details_invalid);
			shakeView.setCompoundDrawablesWithIntrinsicBounds(
					R.drawable.coupon_invalid, 0, 0, 0);
			mImageFetcher.loadImage(mCoupon.getImg(), couponImage);
			getView().findViewById(R.id.barcode).setVisibility(View.GONE);
		} else { // 延期记录
			content.setText(getString(R.string.biz_coupon_list_content_delay,
					mCoupon.getEffDay(), mCoupon.getExpDay()));
			contentZone.setBackgroundColor(getResources().getColor(R.color.biz_coupon_delay));
			
			TextView shakeView = (TextView) getView().findViewById(R.id.top_title);
			shakeView.setVisibility(View.VISIBLE);
			shakeView.setText(R.string.biz_coupon_list_shake);
			shakeView.setCompoundDrawablesWithIntrinsicBounds(
					R.drawable.coupon_list_shake_selector, 0, R.drawable.arrow_coupon, 0);
			
			getView().findViewById(R.id.barcode).setVisibility(View.GONE);
			getView().findViewById(R.id.web_detail).setVisibility(View.GONE);
			
			getView().findViewById(R.id.top).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Bundle bundle = new Bundle();
			    	bundle.putString(ShakeFragment.PARAM_COUPON_ID, mCoupon.getCouponId());
					Intent intent = FragmentHelper.getIntent(getActivity(), BaseActivity.class, 
							ShakeFragment.class.getName(), ShakeFragment.class.getName(), bundle);
					startActivityForResult(intent, MainActivityHelper.REQUEST_CODE_COUPON);
				}
			});
		}
	}
	
	private void openBrower(String url) {
		Intent intent= new Intent();        
	    intent.setAction(Intent.ACTION_VIEW);    
	    intent.setData(Uri.parse(url));
	    getActivity().startActivity(intent);
	}
}
