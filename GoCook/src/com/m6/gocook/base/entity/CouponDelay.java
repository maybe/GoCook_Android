package com.m6.gocook.base.entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

import com.m6.gocook.base.entity.Sale.Condition;

public class CouponDelay {
	
	/** ”yyyy-MM-dd HH:mm:ss”格式的服务器时间 */
	private String time;
	/** ”yyyy-MM-dd HH:mm:ss”格式的优惠券生效时间,如果是 延期获取记录,则为延期有效时间 */
	private String effDay;
	/** "yyyy-MM-dd HH:mm:ss”格式的优惠券失效时间,如果 是延期获取记录,则为延期失效时间" */
	private String expDay;
	/** 优惠券号,如果是延期获取记录,则为空 */
	private String couponId;
	/** 是否符合获取优惠券条件 1 符合费用 0 不符合费 用 2 没有可用促销活动 3 广告 */
	private Condition condition;
	/** 是否符合条件说明 */
	private String remark;
	/** 返回的网络数据是否正确 */
	private boolean success = false;
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getEffDay() {
		return effDay;
	}
	public void setEffDay(String effDay) {
		this.effDay = effDay;
	}
	public String getExpDay() {
		return expDay;
	}
	public void setExpDay(String expDay) {
		this.expDay = expDay;
	}
	public String getCouponId() {
		return couponId;
	}
	public void setCouponId(String couponId) {
		this.couponId = couponId;
	}
	public Condition getCondition() {
		return condition;
	}
	public void setCondition(Condition condition) {
		this.condition = condition;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	public CouponDelay() {
	}
	
	public CouponDelay(JSONObject value) {
		parse(value);
	}
	
	/**
	 * json {result, errorcode, id, time, eff_day, exp_day, condition, remark}
	 * @param value
	 */
	private void parse(JSONObject value) {
		if (value == null) {
			return;
		}
		
		try {
			SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat dfsSimple = new SimpleDateFormat("yyyy-MM-dd");
			Date date;

			time = value.optString("time");
			effDay = value.optString("eff_day");
			date = dfs.parse(value.optString("exp_day"));
			expDay = dfsSimple.format(date);
			couponId = value.optString("id");
			condition = Condition.value(value.optInt("condition", 0));
			remark = value.optString("remark");
			success = true;
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
