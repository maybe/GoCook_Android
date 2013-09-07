package com.m6.gocook.base.entity.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.m6.gocook.base.entity.IParseable;
import com.m6.gocook.base.protocol.Protocol;

public class COrderQueryResult extends BaseResponse implements IParseable<String> {

	private int pageIndex;  //页索引
	private int pageRows;  //每页记录数
	private int totalCount; //总记录数
	private List<COrderItem> rows; //具体记录
	
	public COrderQueryResult(String value) {
		parse(value);
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public int getPageRows() {
		return pageRows;
	}

	public void setPageRows(int pageRows) {
		this.pageRows = pageRows;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public List<COrderItem> getRows() {
		return rows;
	}

	public void setRows(List<COrderItem> rows) {
		this.rows = rows;
	}

	public class COrderItem implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 45035044987020401L;
		
		private int id; //编号
		private int custId; //客户编号
		private String custName; //客户名称
		private String code; //订单号
		private String deliveryType; //配送类型
		private String deliveryTimeType; //配送时间类型
		private String recvMobile; //收货人手机
		private double cost; //订单金额
		private String createTime; //”yyyy-MM-dd HH:mm:ss”格式的创建时间
		private List<COrderWareItem> orderWares; //订单商品明细
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public int getCustId() {
			return custId;
		}
		public void setCustId(int custId) {
			this.custId = custId;
		}
		public String getCustName() {
			return custName;
		}
		public void setCustName(String custName) {
			this.custName = custName;
		}
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		public String getDeliveryType() {
			return deliveryType;
		}
		public void setDeliveryType(String deliveryType) {
			this.deliveryType = deliveryType;
		}
		public String getDeliveryTimeType() {
			return deliveryTimeType;
		}
		public void setDeliveryTimeType(String deliveryTimeType) {
			this.deliveryTimeType = deliveryTimeType;
		}
		public String getRecvMobile() {
			return recvMobile;
		}
		public void setRecvMobile(String recvMobile) {
			this.recvMobile = recvMobile;
		}
		public double getCost() {
			return cost;
		}
		public void setCost(double cost) {
			this.cost = cost;
		}
		public String getCreateTime() {
			return createTime;
		}
		public void setCreateTime(String createTime) {
			this.createTime = createTime;
		}
		public List<COrderWareItem> getOrderWares() {
			return orderWares;
		}
		public void setOrderWares(List<COrderWareItem> orderWares) {
			this.orderWares = orderWares;
		}
	}
	
	public class COrderWareItem {
		private int id; //商品编号
		private String name; //商品名称
		private String code; //商品编码
		private String remark; //说明
		private String norm; //规格
		private String unit; //单位
		private double price; //价格
		private double quantity ; //数量
		private double cost; //金额
		private String imageUrl; //图片
		private String dealMethod; //加工方式
		
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		public String getRemark() {
			return remark;
		}
		public void setRemark(String remark) {
			this.remark = remark;
		}
		public String getNorm() {
			return norm;
		}
		public void setNorm(String norm) {
			this.norm = norm;
		}
		public String getUnit() {
			return unit;
		}
		public void setUnit(String unit) {
			this.unit = unit;
		}
		public double getPrice() {
			return price;
		}
		public void setPrice(double price) {
			this.price = price;
		}
		public double getQuantity() {
			return quantity;
		}
		public void setQuantity(double quantity) {
			this.quantity = quantity;
		}
		public double getCost() {
			return cost;
		}
		public void setCost(double cost) {
			this.cost = cost;
		}
		public String getImageUrl() {
			return imageUrl;
		}
		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}
		public String getDealMethod() {
			return dealMethod;
		}
		public void setDealMethod(String dealMethod) {
			this.dealMethod = dealMethod;
		}
	}

	@Override
	public boolean parse(String value) {
		if (TextUtils.isEmpty(value)) {
			return false;
		}
		
		try {
			JSONObject jsonObject = new JSONObject(value);
			if (jsonObject != null) {
				result = jsonObject.optInt(BaseResponse.RESULT);
				errorCode = jsonObject.optInt(BaseResponse.ERROR);
				if (result == Protocol.VALUE_RESULT_OK) {
					JSONArray orders = jsonObject.optJSONArray("orders");
					rows = new ArrayList<COrderItem>();
					if (orders != null) {
						for (int i = 0; i < orders.length(); i++) {
							JSONObject orderItemJson = orders.optJSONObject(i);
							COrderItem orderItem = new COrderItem();
							orderItem.id = orderItemJson.optInt("id");
							orderItem.custName = orderItemJson.optString("name");
							orderItem.code = orderItemJson.optString("code");
							orderItem.deliveryType = orderItemJson.optString("delivery_type");
							orderItem.deliveryTimeType = orderItemJson.optString("delivery_time_type");
							orderItem.recvMobile = orderItemJson.optString("recv_mobile");
							orderItem.cost = orderItemJson.optDouble("cost");
							orderItem.createTime = orderItemJson.optString("create_time");
							
							JSONArray wares = orderItemJson.optJSONArray("order_wares");
							orderItem.orderWares = new ArrayList<COrderQueryResult.COrderWareItem>();
							if (wares != null) {
								for (int j = 0; j < wares.length(); j++) {
									JSONObject wareItemJson = wares.optJSONObject(j);
									COrderWareItem cOrderWareItem = new COrderWareItem();
									cOrderWareItem.id = wareItemJson.optInt("id");
									cOrderWareItem.name = wareItemJson.optString("name");
									cOrderWareItem.code = wareItemJson.optString("code");
									cOrderWareItem.remark = wareItemJson.optString("remark");
									cOrderWareItem.norm = wareItemJson.optString("norm");
									cOrderWareItem.unit = wareItemJson.optString("unit");
									cOrderWareItem.price = wareItemJson.optDouble("price");
									cOrderWareItem.imageUrl = wareItemJson.optString("image_url");
									cOrderWareItem.dealMethod = wareItemJson.optString("deal_method");
									cOrderWareItem.quantity = wareItemJson.optDouble("quantity");
									cOrderWareItem.cost = wareItemJson.optDouble("cost");
									orderItem.orderWares.add(cOrderWareItem);
								}
							}
							rows.add(orderItem);
						}
					}
				}
				return true;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}
}
