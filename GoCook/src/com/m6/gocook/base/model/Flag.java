package com.m6.gocook.base.model;

public class Flag {
	
	public static final int SUCCESS = 1;
	
	public static String getFlagString(int flag) {
		String text = null;
		switch (flag) {
		case -1:
			text = "失败";
			break;
		case -2:
			text = "注册失败，帐号已经存在";
			break;
		case -3:
			text = "认证失败，帐号不存在或无效";
			break;
		case -4:
			text = "认证失败，密码错误";
			break;
		case -5:
			text = "订购失败，客户不存在或无效";
			break;
		case -6:
			text = "查询失败，客户不存在或无效";
			break;
		case -7:
			text = "配送开箱失败，配送单号不存在或无效";
			break;
		case -8:
			text = "配送开箱失败，配送单状态错误";
			break;
		case -9:
			text = "超时开箱失败，柜号不存在或无效";
			break;
		case -10:
			text = "Md5验证失败";
			break;
		case -11:
			text = "参数数据转换失败";
			break;
		default:
			break;
		}
		return text;
	}
}
