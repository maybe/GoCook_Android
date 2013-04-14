package com.m6.gocook.base.entity;

import java.util.ArrayList;

import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class RecipeEntity implements IParseable<JSONObject> {

	public class Material {
		private String name;
		private String remark;

		public Material(String name, String remark) {
			this.name = name;
			this.remark = remark;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getRemark() {
			return remark;
		}

		public void setRemark(String remark) {
			this.remark = remark;
		}
	}

	public class Procedure {
		private String desc;
		private String imageURL;

		public Procedure(String desc, String imageURL) {
			this.desc = desc;
			this.imageURL = imageURL;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		public String getImageURL() {
			return imageURL;
		}

		public void setImageURL(String imageURL) {
			this.imageURL = imageURL;
		}
	}

	private int id;

	private String name;

	private String coverImgURL;

	private String desc;

	private String author;

	private int dishCount;

	private int collectCount;

	private ArrayList<Material> materials;

	private ArrayList<Procedure> procedures;

	private String tips;

	private int commentCount;

	private String commentStrs;

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

	public String getCoverImgURL() {
		return coverImgURL;
	}

	public void setCoverImgURL(String coverImgURL) {
		this.coverImgURL = coverImgURL;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public int getDishCount() {
		return dishCount;
	}

	public void setDishCount(int dishCount) {
		this.dishCount = dishCount;
	}

	public int getCollectCount() {
		return collectCount;
	}

	public void setCollectCount(int collectCount) {
		this.collectCount = collectCount;
	}

	public ArrayList<Material> getMaterials() {
		return materials;
	}

	public void setMaterials(ArrayList<Material> materials) {
		this.materials = materials;
	}

	public ArrayList<Procedure> getProcedures() {
		return procedures;
	}

	public void setProcedures(ArrayList<Procedure> procedures) {
		this.procedures = procedures;
	}

	public String getTips() {
		return tips;
	}

	public void setTips(String tips) {
		this.tips = tips;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}

	public String getCommentStrs() {
		return commentStrs;
	}

	public void setCommentStrs(String commentStrs) {
		this.commentStrs = commentStrs;
	}

	@Override
	public boolean parse(JSONObject object) {

		// Temporary Fake Data
		this.name = "鱼香肉丝";
		this.author = "居然";
		this.desc = "辣中带酸，酸中带甜，甜中带咸，咸中又带鲜……味道丰富而不杂腻！恰似女儿心，捉摸不透，又飘忽不定，似近又远，偶尔火辣又偶尔羞涩……";
		this.dishCount = 582;
		this.collectCount = 35587;
		
		this.materials = new ArrayList<RecipeEntity.Material>();
		this.materials.add(new Material("里脊肉", "250克"));
		this.materials.add(new Material("青红辣椒", "各一个"));
		this.materials.add(new Material("胡萝卜", "1/3根"));
		this.materials.add(new Material("干木耳", "30克"));
		this.materials.add(new Material("郫县豆瓣酱", "2汤勺"));
		this.materials.add(new Material("蒜瓣", "两粒"));
		this.materials.add(new Material("料酒", "少量"));
		this.materials.add(new Material("油", "适量"));

		this.procedures = new ArrayList<RecipeEntity.Procedure>();
		this.procedures
				.add(new Procedure(
						"木耳泡发，里脊肉切丝，用少许盐，糖抓均匀，一小勺淀粉上浆后用一小勺油拌匀封备用，木耳，辣椒，红萝卜也切丝备用，用糖、香醋，料酒，酱油，清水调成酱汁，比例约为：1：1：0.3：2：3",
						""));
		this.procedures.add(new Procedure("锅烧热，倒入少许油，倒入瘦肉滑油", ""));
		this.procedures.add(new Procedure("锅里留油，放少许蒜末，爆香后放入木耳快炒几秒", ""));
		this.procedures.add(new Procedure("加豆瓣酱", ""));
		this.procedures.add(new Procedure("炒熟吃了", ""));

		this.tips = "1、步骤中提到的比例为大约，具体请根据个人口味稍作调整，要以自己喜欢的口感为准。\r\n"
				+ "2、豆瓣酱一定要炒出红油再倒入肉丝，要不油色会相差比较远，亮泽度也会较差\r\n"
				+ "3、酱汁提前兑好，在肉丝下锅炒均匀后立即可以倒入锅中，避免临时找酱汁引起遗漏\r\n"
				+ "4、整个菜只需要很少的盐，只有在腌制肉丝的时候放一点点，因为酱油比较咸，豆瓣酱也咸，泡椒、香醋都有盐分\r\n"
				+ "5、勾芡别太厚重，吃起来会腻，因为有酸，甜咸鲜味，搭配很均衡，所以，这道菜虽然看着红油很亮，其实并不太辣。";

		return true;
	}

}
