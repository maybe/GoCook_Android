package com.m6.gocook.biz.coupon;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

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
						sale.setTime(json.optString("time"));
						sale.setRemark(json.optString("remark"));
						sale.setSaleCount(json.optInt("sale_count", 0));
						sale.setSaleFee(json.optString("sale_fee"));
						sale.setCondition(Condition.value(json.optInt("condition", 0)));
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return sale;
	}

}
