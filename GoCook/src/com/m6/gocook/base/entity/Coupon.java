package com.m6.gocook.base.entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

import com.m6.gocook.base.entity.Sale.Condition;

public class Coupon {
	
	public static final int STATUS_INVALID = 0;
	public static final int STATUS_VALID = 1;
	
	/** ”yyyy-MM-dd HH:mm:ss”格式的服务器时间 */
	private String time;
	/** ”yyyy-MM-dd HH:mm:ss”格式的优惠券生效时间,如果是 延期获取记录,则为延期有效时间 */
	private String effDay;
	/** "yyyy-MM-dd HH:mm:ss”格式的优惠券失效时间,如果 是延期获取记录,则为延期失效时间" */
	private String expDay;
	/** 优惠券号,如果是延期获取记录,则为空 */
	private String couponId;
	/** 优惠券描述,如果是延期获取记录,则为延期获取 的信息 */
	private String couponRemark;
	/** 使用门店 */
	private String stores;
	/** 是否符合获取优惠券条件 1 符合费用 0 不符合费 用 2 没有可用促销活动 3 广告 */
	private Condition condition;
	/** 是否符合条件说明 */
	private String remark;
	/** 是否延期获取 1 是 0 否 */
	private boolean isDelay;
	/** 提供商 */
	private String supplier;
	/** 0 券 1 广告 */
	private int ktype;
	/** 0 无效 1 有效 */
	private int status;
	/** 券名称 */
	private String name;
	/** 券信息链接 */
	private String url;
	/** 图片链接 */
	private String img;
	/** ”yyyy-MM-dd HH:mm:ss”格式的客户确认金额服务器时间 */
	private String ccTime;
	/** ”yyyy-MM-dd HH:mm:ss”格式的创建时间 */
	private String cTime;
	/** 券价值 */
	private int val;
	/** 对应商品编号 */
	private String wid;
	/** 数据项在列表的展开状态 */
	private boolean expand = false;
	
	public Coupon() {
	}
	
	public Coupon(JSONObject value) {
		parse(value);
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
	public String getCouponRemark() {
		return couponRemark;
	}
	public void setCouponRemark(String couponRemark) {
		this.couponRemark = couponRemark;
	}
	public String getStores() {
		return stores;
	}
	public void setStores(String stores) {
		this.stores = stores;
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
	public boolean isDelay() {
		return isDelay;
	}
	public void setDelay(boolean isDelay) {
		this.isDelay = isDelay;
	}
	public String getSupplier() {
		return supplier;
	}
	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}
	public int getKtype() {
		return ktype;
	}
	public void setKtype(int ktype) {
		this.ktype = ktype;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public String getCcTime() {
		return ccTime;
	}
	public void setCcTime(String ccTime) {
		this.ccTime = ccTime;
	}
	public String getcTime() {
		return cTime;
	}
	public void setcTime(String cTime) {
		this.cTime = cTime;
	}
	public int getVal() {
		return val;
	}
	public void setVal(int val) {
		this.val = val;
	}
	public String getWid() {
		return wid;
	}
	public void setWid(String wid) {
		this.wid = wid;
	}
	public boolean isExpand() {
		return expand;
	}
	public void setExpand(boolean expand) {
		this.expand = expand;
	}
	
	/**
	 * json {result, errorcode, time, eff_day, exp_day, coupon_id, coupon_remark, stores, condition, 
	 * remark, is_delay, supplier, ktype, status, name, url, img, cctime, ctime, val, wid}
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
			couponId = value.optString("coupon_id");
			couponRemark = value.optString("coupon_remark");
			stores = value.optString("stores");
			condition = Condition.value(value.optInt("condition", 0));
			remark = value.optString("remark");
			isDelay = value.optInt("is_delay", 0) == 0 ? false : true;
			supplier = value.optString("supplier");
			ktype = value.optInt("ktype", 0);
			status = value.optInt("status", 0);
			name = value.optString("name");
			url = value.optString("url");
			img = value.optString("img");
			ccTime = value.optString("cctime");
			date = dfs.parse(value.optString("ctime"));
			cTime = dfsSimple.format(date);
			val = value.optInt("val", 0);
			wid = value.optString("wid");
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
