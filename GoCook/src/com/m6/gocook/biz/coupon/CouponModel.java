package com.m6.gocook.biz.coupon;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.m6.gocook.base.entity.Coupon;
import com.m6.gocook.base.entity.CouponDelay;
import com.m6.gocook.base.entity.Sale;
import com.m6.gocook.base.entity.Sale.Condition;
import com.m6.gocook.base.protocol.Protocol;
import com.m6.gocook.biz.account.AccountModel;
import com.m6.gocook.util.net.NetUtils;

public class CouponModel {
	
	/**
	 * result, errorcode, time, sale_fee, sale_count, condition, remark
	 * 
	 * @param context
	 * @return
	 */
	public static Sale getSale(Context context) {
		Sale sale = new Sale();
		String result = NetUtils.httpGet(Protocol.URL_COUPON_SALE, AccountModel.getCookie(context));
		if (!TextUtils.isEmpty(result)) {
			try {
				JSONObject json = new JSONObject(result);
				if (json != null) {
					int resultCode = json.optInt("result", 1);
					if (resultCode == Protocol.VALUE_RESULT_OK) {
						SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						SimpleDateFormat dfsSimple = new SimpleDateFormat("yyyy-MM-dd");
						sale.setSuccess(true);
						sale.setTime(dfsSimple.format(dfs.parse(json.optString("time"))));
						sale.setRemark(json.optString("remark"));
						sale.setSaleCount(json.optInt("sale_count", 0));
						sale.setSaleFee(json.optString("sale_fee"));
						sale.setCondition(Condition.value(json.optInt("condition", 0)));
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return sale;
	}
	
	public static List<Coupon> getCoupons(Context context, int page) {
		List<Coupon> list = new ArrayList<Coupon>();
		try {
			String result = NetUtils.httpGet(String.format(Protocol.URL_COUPON_COUPONS, URLEncoder.encode(String.valueOf(page), "UTF-8")), 
					AccountModel.getCookie(context));
			if (!TextUtils.isEmpty(result)) {
				JSONObject json = new JSONObject(result);
				if (json != null) {
					int resultCode = json.optInt("result", 1);
					if (resultCode == Protocol.VALUE_RESULT_OK) {
						JSONArray array = json.optJSONArray("coupons");
						if (array != null) {
							int size = array.length();
							for (int i = 0; i < size; i++) {
								list.add(new Coupon(array.optJSONObject(i)));
							}
						}
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		return list;
	}
	
	public static List<Coupon> getCoupon(Context context, String couponId) {
		String result = NetUtils.httpGet(String.format(Protocol.URL_COUPON_COUPON, couponId), AccountModel.getCookie(context));
		List<Coupon> list = new ArrayList<Coupon>();
		if (!TextUtils.isEmpty(result)) {
			try {
				JSONObject json = new JSONObject(result);
				if (json != null) {
					int resultCode = json.optInt("result", 1);
					if (resultCode == Protocol.VALUE_RESULT_OK) {
						JSONArray array = json.optJSONArray("coupons");
						if (array != null) {
							int size = array.length();
							for (int i = 0; i < size; i++) {
								list.add(new Coupon(array.optJSONObject(i)));
							}
						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	
	public static CouponDelay delayCoupon(Context context) {
		String result = NetUtils.httpGet(String.format(Protocol.URL_COUPON_DELAY_COUPON), AccountModel.getCookie(context));
		if (!TextUtils.isEmpty(result)) {
			try {
				JSONObject json = new JSONObject(result);
				if (json != null) {
					int resultCode = json.optInt("result", 1);
					if (resultCode == Protocol.VALUE_RESULT_OK) {
						return new CouponDelay(json);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return new CouponDelay();
	}
	
}
