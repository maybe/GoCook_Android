package com.m6.gocook.biz.coupon;

import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.m6.gocook.R;
import com.m6.gocook.base.entity.Coupon;

public class CouponListAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private Resources mResources;
	private List<Coupon> mData;
	
	public CouponListAdapter(Context context, List<Coupon> data) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mResources = context.getResources();
		mData = data;
	}
	
	@Override
	public int getCount() {
		return mData == null ? 0 : mData.size();
	}

	@Override
	public Coupon getItem(int position) {
		return mData == null ? null : mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.adapter_coupon_list, parent, false);
			holder.content = (TextView) convertView.findViewById(R.id.content);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		final Coupon coupon = mData.get(position);
		
		final boolean isDelay = coupon.getKtype() == Coupon.KTYPE_DELAY;
		final boolean isCoupon = coupon.getKtype() == Coupon.KTYPE_COUPON;
		final boolean isAd = coupon.getKtype() == Coupon.KTYPE_AD;
		final boolean isSellerCoupon = coupon.getKtype() == Coupon.KTYPE_SELLER;
		final boolean isUsed = coupon.isused();
		
		if (isDelay) { // 延期记录
			holder.content.setText(mContext.getString(R.string.biz_coupon_list_content_delay,
					coupon.getEffDay(), coupon.getExpDay()));
			convertView.setBackgroundColor(mResources.getColor(R.color.biz_coupon_delay));
		} else if (isUsed) { // 已使用
			holder.content.setText(mContext.getString(R.string.biz_coupon_list_content_normal, 
					coupon.getcTime(), coupon.getSupplier(), coupon.getVal(), coupon.getName(), 
					coupon.getCoupon(), coupon.getEffDay(), coupon.getExpDay(), 
					coupon.getStores(), coupon.getCouponRemark()));
			convertView.setBackgroundColor(mResources.getColor(R.color.biz_coupon_used));
		} else if (isCoupon) { // M6优惠券
			holder.content.setText(mContext.getString(R.string.biz_coupon_list_content_normal, 
					coupon.getcTime(), coupon.getSupplier(), coupon.getVal(), coupon.getName(), 
					coupon.getCoupon(), coupon.getEffDay(), coupon.getExpDay(), 
					coupon.getStores(), coupon.getCouponRemark()));
			convertView.setBackgroundColor(mResources.getColor(R.color.biz_coupon_normal));
		} else if (isAd) { // 广告
			holder.content.setText(mContext.getString(R.string.biz_coupon_list_content_ad, 
					coupon.getName(), coupon.getSupplier(), coupon.getcTime(), coupon.getCouponRemark()));
			convertView.setBackgroundColor(mResources.getColor(R.color.biz_coupon_ad));
		} else if (isSellerCoupon) { // 网络商家券
			holder.content.setText(mContext.getString(R.string.biz_coupon_list_content_normal, 
					coupon.getcTime(), coupon.getSupplier(), coupon.getVal(), coupon.getName(), 
					coupon.getCoupon(), coupon.getEffDay(), coupon.getExpDay(), 
					coupon.getStores(), coupon.getCouponRemark()));
			convertView.setBackgroundColor(mResources.getColor(R.color.biz_coupon_seller));
		}
		
		return convertView;
	}

	class ViewHolder {
		private TextView content;
	}
}
