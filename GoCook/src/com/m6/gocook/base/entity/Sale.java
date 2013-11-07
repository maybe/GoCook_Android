package com.m6.gocook.base.entity;

import java.io.Serializable;


public class Sale implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public enum Condition {
		MisMatch, Match, NoPromotion, Advertisement;
		
		public static Condition value(int value) {
			switch (value) {
			case 0:
				return MisMatch;
			case 1:
				return Match;
			case 2: 
				return NoPromotion;
			case 3:
				return Advertisement;
			default:
				return MisMatch;
			}
		}
	}
	/** result=0表示成功，1表示失败 */
	private boolean success = false;
	/** "yyyy-MM-dd HH:mm:ss"格式的日期 */
	private String time;
	/** 指定日期的销售额 */
	private String saleFee;
	/** 销售笔数 */
	private int saleCount;
	/** 是否符合获取优惠券条件 1:符合费用; 0:不符合费用; 2:没有可用促销活动; 3:广告 */
	private Condition condition;
	/** 条件说明 */
	private String remark;
	
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
	public String getSaleFee() {
		return saleFee;
	}
	public void setSaleFee(String saleFee) {
		this.saleFee = saleFee;
	}
	public int getSaleCount() {
		return saleCount;
	}
	public void setSaleCount(int saleCount) {
		this.saleCount = saleCount;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Condition getCondition() {
		return condition;
	}
	public void setCondition(Condition condition) {
		this.condition = condition;
	}
	
	
}
