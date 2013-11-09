package com.m6.gocook.biz.coupon;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.m6.gocook.R;
import com.m6.gocook.base.entity.Coupon;
import com.m6.gocook.base.fragment.BaseWebFragment;
import com.m6.gocook.base.fragment.FragmentHelper;

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
			holder.expand = (ImageView) convertView.findViewById(R.id.expand);
			holder.go = (ImageView) convertView.findViewById(R.id.go);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		final Coupon coupon = mData.get(position);
		
		if (coupon.getStatus() == Coupon.STATUS_VALID) { // 过期优惠券
			holder.content.setText(mContext.getString(R.string.biz_coupon_list_content_normal, 
					coupon.getcTime(), coupon.getVal(), coupon.getName(), coupon.getExpDay(),
					coupon.getCouponId(), coupon.getStores(), coupon.getCouponRemark()));
			
			holder.go.setBackgroundResource(R.drawable.coupon_list_go_selector);
			convertView.setBackgroundColor(mResources.getColor(R.color.biz_coupon_invalid));
		} else if (TextUtils.isEmpty(coupon.getCouponId())) { // 延期获取
			holder.go.setBackgroundResource(R.drawable.coupon_list_shake_selector);
			convertView.setBackgroundColor(mResources.getColor(R.color.biz_coupon_delay));
		} else { // 优惠券
			holder.content.setText(mContext.getString(R.string.biz_coupon_list_content_normal, 
					coupon.getcTime(), coupon.getVal(), coupon.getName(), coupon.getExpDay(),
					coupon.getCouponId(), coupon.getStores(), coupon.getCouponRemark()));
			holder.go.setBackgroundResource(R.drawable.coupon_list_go_selector);
			convertView.setBackgroundColor(mResources.getColor(R.color.biz_coupon_normal));
		}
		
		if (coupon.isExpand()) {
			holder.content.setMaxLines(6);
			holder.expand.setImageResource(R.drawable.coupon_list_fold);
		} else {
			holder.content.setMaxLines(2);
			holder.expand.setImageResource(R.drawable.coupon_list_expand);
		}
		
		holder.expand.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (coupon.isExpand()) {
					coupon.setExpand(false);
				} else {
					coupon.setExpand(true);
				}
				notifyDataSetChanged();
			}
		});
		
		holder.go.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(coupon.getCouponId())) { // 延期优惠券
					FragmentHelper.startActivity(mContext, new ShakeFragment());
				} else {
					FragmentHelper.startActivity(mContext, BaseWebFragment.newInstance(coupon.getUrl(), 
							mContext.getString(R.string.biz_coupon_details_title)));
				}
			}
		});
		
		return convertView;
	}

	class ViewHolder {
		private TextView content;
		private ImageView expand;
		private ImageView go;
	}
}
