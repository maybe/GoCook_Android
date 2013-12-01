package com.m6.gocook.base.protocol;

import org.json.JSONObject;

import com.m6.gocook.R;
import com.m6.gocook.biz.account.AccountModel;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

public class ErrorCode {

	/* 
	 * error code

error == 1 : authecation error

通用错误码

101: 非移动设备

102: 未授权用户

103: 不是post上传

104: 上传post不合法

host/recipe 相关的错误码

401: 此菜谱不存在

402: 此菜谱不属于当前用户

403: 协议上报菜谱id为空

404: 新建菜谱名字不能为空

405: 食材必须为双数

406: 新建菜谱食材不能为空

407: steps不能为空

408: 找不到所示的封面图片（既不是刚上传的，也不是已经有的）

409: 新建菜谱的封面不能为空

host/user 注册登陆相关的错误码

201: 电话号码重复

202: 昵称重复

203: 甲方服务器错误

204: 甲方服务器错误(逻辑错误，go_cook校验服务器返回结果错误)

205: 注册失败(甲方服务器未响应或错误)

206: 账号已存在
	 * 
	 * 
	 * 
	 * */
	
	
	
	public static final int ILLEGAL = -1;
	
	/** 未授权用户 */
	public static final int UNAUTHORIZED = 102;
	/** 已经赞过该菜谱 */
	public static final int RECIPE_PRAISED = 407;
	/** 该菜谱本人未赞过 */
	public static final int RECIPE_UNPRAISED = 408;
	
	
	public static int getErrorCode(JSONObject json) {
		return json.optInt(Protocol.KEY_ERROR_CODE, ErrorCode.ILLEGAL);
	}
	
	public static void toast(Context context, int errorCode) {
		String msg = null;
		switch (errorCode) {
		case ILLEGAL:
			return;
		case UNAUTHORIZED:
			msg = context.getString(R.string.error_code_unauthorized);
			break;
		case RECIPE_PRAISED:
			msg = context.getString(R.string.error_code_recipe_praised);
		case RECIPE_UNPRAISED:
			msg = context.getString(R.string.error_code_recipe_unpraised);
			break;
		}
		
		if (!TextUtils.isEmpty(msg)) {
			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
		}
	}
	
	public static void handleError(Context context, int errorCode) {
		toast(context, errorCode);
		switch (errorCode) {
		case ILLEGAL:
			return;
		case UNAUTHORIZED:
			AccountModel.logout(context);
			break;
		}
	}
}
