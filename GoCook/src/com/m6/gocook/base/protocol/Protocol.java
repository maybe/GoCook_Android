package com.m6.gocook.base.protocol;

public class Protocol {

	/* URL */
	
	/* Account */
//	public static final String URL_ROOT = "http://verypanda.com";
	public static final String URL_ROOT = "http://o.m6fresh.com:8081";
	
	public static final String URL_LOGIN = URL_ROOT + "/user/login";
	
	public static final String URL_REGISTER = URL_ROOT + "/user/register";
	
	public static final String URL_LOGIN_EX = URL_ROOT + "/user/login_ex";
	
	public static final String URL_LOGIN_WEB = "http://o.m6fresh.com/ws/mobile_reg.aspx?sid=%s";
	
	/* Recipe URL  */
	public static final String URL_RECIPE = URL_ROOT + "/recipe?id=%s";

	public static final String URL_RECIPE_CREATE = URL_ROOT + "/recipe/create";
	
	public static final String URL_RECIPE_EDIT = URL_ROOT + "/recipe/edit";

	public static final String URL_RECIPE_DELETE = URL_ROOT + "/recipe/delete?recipe_id=%s";

	public static final String URL_RECIPE_COMMENT = URL_ROOT + "/recipe/comments?recipe_id=%s";
	
	public static final String URL_RECIPE_COMMENT_POST = URL_ROOT + "/recipe/comment";
	
	public static final String URL_RECIPE_COLLECT_ADD = URL_ROOT + "/cook/addmycoll?collid=%s";
	
	public static final String URL_RECIPE_COLLECT_DELETE = URL_ROOT + "/cook/delmycoll?collid=%s";
	
	public static final String URL_RECIPE_UPLOAD_COVERIMAGE_STRING = URL_ROOT + "/recipe/uploadCoverPhoto";

	public static final String URL_RECIPE_UPLOAD_STEPIMAGE_STRING = URL_ROOT + "/recipe/uploadStepPhoto";
	
	/* Profile URL */
	public static final String URL_PROFILE_UPDATE = URL_ROOT + "/user/changebasicinfo";
	
	public static final String URL_PROFILE_UPDATE_AVATAR = URL_ROOT + "/user/changeavatar";
	
	public static final String URL_PROFILE_BASIC_INFO = URL_ROOT + "/user/basicinfo";
	
	public static final String URL_PROFILE_MY_RECIPE = URL_ROOT + "/cook/myrecipes";
	
	public static final String URL_PROFILE_MY_COLLECTION = URL_ROOT + "/cook/mycoll?page=";
	
	public static final String URL_PROFILE_FOLLOW = URL_ROOT + "/cook/watch?watchid=";
	
	public static final String URL_PROFILE_UNFOLLOW = URL_ROOT + "/cook/unwatch?watchid=";
	
	public static final String URL_PROFILE_OTHER = URL_ROOT + "/cook/kitchen?userid=";
	
	public static final String URL_PROFILE_OTHER_RECIPES = URL_ROOT + "/cook/usersrecipes?userid=%s&page=";
	
	public static final String URL_PROFILE_MY_FOLLOWS = URL_ROOT + "/cook/mywatch?page=";
	
	public static final String URL_PROFILE_MY_FANS = URL_ROOT + "/cook/myfans?page=";
	
	/** Popular URL */
	public static final String URL_POPULAR = URL_ROOT + "/index/ios_main";
	
	public static final String URL_RECIPE_NEW = URL_ROOT + "/recipe/topnew?page=";
	
	public static final String URL_RECIPE_HOT = URL_ROOT + "/recipe/tophot?page=";
	
	public static final String URL_RECIPE_SEARCH = URL_ROOT + "/index/search?keyword=%s&page=";
	
	/* Buy */
	public static final String URL_BUY = "http://o.m6fresh.com/ws/app.ashx";
	
	public static final String URL_BUY_SEARCH = URL_ROOT + "/cook/search_wares?keyword=%s&page=%s";
	
	public static final String URL_BUY_ORDER = URL_ROOT + "/cook/order";
	
	public static final String URL_BUY_ORDER_QUERY = URL_ROOT + "/cook/his_orders";
	
	/* coupon */
	public static final String URL_COUPON_SALE = URL_ROOT + "/cook/day_sales?test_id=2";
	
	public static final String URL_COUPON_COUPONS = URL_ROOT + "/cook/my_coupons?page=%s&test_id=2";
	
	public static final String URL_COUPON_COUPON = URL_ROOT + "/cook/get_coupon?coupon_id=%s&test_id=2";
	
	public static final String URL_COUPON_DELAY_COUPON = URL_ROOT + "/cook/delay_coupon?test_id=2";
	
	/* Version */
	public static final String URL_VERSION = URL_ROOT + "/index/android_update";
	
	/* Base JSON Protocol */
	public static final String KEY_RESULT = "result";
	public static final int VALUE_RESULT_OK = 0;
	public static final int VALUE_RESULT_ERROR = 1;
	
	
	/* Recipe JSON Protocol */
	
	public static final String KEY_RECIPE = "result_recipe";
	public static final String KEY_RECIPE_ID = "recipe_id";
	public static final String KEY_RECIPE_AUTHOR_ID = "author_id";
	public static final String KEY_RECIPE_AUTHOR_NAME = "author_name";
	public static final String KEY_RECIPE_NAME = "recipe_name";
	public static final String KEY_RECIPE_INTRO = "intro";
	public static final String KEY_RECIPE_COLLECTED_COUNT = "collected_count";
	public static final String KEY_RECIPE_ISCOLLECTED = "collect";
	public static final String KEY_RECIPE_DISH_COUNT = "dish_count";
	public static final String KEY_RECIPE_COMMENT_COUNT = "comment_count";
	public static final String KEY_RECIPE_COVER_IMAGE = "cover_image";
	public static final String KEY_RECIPE_MATERIALS = "materials";
	public static final String VALUE_RECIPE_MATERIALS_FLAG = "\\|";
	public static final String VALUE_RECIPE_MATERIALS_FLAG2 = "|";
	public static final String KEY_RECIPE_STEPS = "steps";
	public static final String KEY_RECIPE_STEPS_NO = "no";
	public static final String KEY_RECIPE_STEPS_CONTENT = "content";
	public static final String KEY_RECIPE_STEPS_IMG = "img";
	public static final String KEY_RECIPE_TIPS = "tips";
	
	/* Post Recipe JSON Protocol */
	public static final String KEY_RECIPE_POST_ID = "recipe_id";
	public static final String KEY_RECIPE_POST_NAME = "name";
	public static final String KEY_RECIPE_POST_COVER_IMG = "cover_img";
	public static final String KEY_RECIPE_POST_DESC = "desc";
	public static final String KEY_RECIPE_POST_CATEGORY = "category";
	public static final String KEY_RECIPE_POST_MATERIALS = "materials";
	public static final String KEY_RECIPE_POST_STEPS = "steps";
	public static final String KEY_RECIPE_POST_TIPS = "tips";
	
	/* Recipe Comments JSON Protocol */
	public static final String KEY_RECIPE_COMMENT = "result_recipe_comments";
	public static final String KEY_RECIPE_COMMENT_USER_ID = "user_id";
	public static final String KEY_RECIPE_COMMENT_NAME = "name";
	public static final String KEY_RECIPE_COMMENT_PORTRAIT = "portrait";
	public static final String KEY_RECIPE_COMMENT_CONTENT = "content";
	public static final String KEY_RECIPE_COMMENT_CREATE_TIME = "create_time";
	public static final String KEY_RECIPE_COMMENT_CREATE_TIME_DATE = "date";

	public static final String KEY_POST_RECIPE_COMMENT_RECIPE_ID = "recipe_id";
	public static final String KEY_POST_RECIPE_COMMENT_CONTENT = "content";
	
	/* Upload Image File Protocol */
	public static final String KEY_RECIPE_UPLOAD_COVERIMAGE = "cover";
	public static final String KEY_RECIPE_UPLOAD_STEPIMAGE = "step";
	public static final String KEY_RECIPE_UPLOAD_AVATAR = "avatar";
		
	/* Popular JSON Protocol */
	public static final String KEY_POPULAR_TOPNEW_IMG = "topnew_img";
	public static final String KEY_POPULAR_TOPHOT_IMG = "tophot_img";
	public static final String KEY_POPULAR_RECOMMEND_ITEMS = "recommend_items";
	public static final String KEY_POPULAR_RECOMMEND_ITEM_NAME = "name";
	public static final String KEY_POPULAR_RECOMMEND_ITEM_IMG = "images";
	
	/* Recipe List JSON Protocol */
	public static final String KEY_RECIPE_LIST_RECIPES = "result_recipes";
	public static final String KEY_RECIPE_LIST_RECIPE_ID = "recipe_id";
	public static final String KEY_RECIPE_LIST_NAME = "name";
	public static final String KEY_RECIPE_LIST_IMAGE = "image";
	public static final String KEY_RECIPE_LIST_COLLECTION = "dish_count";
	public static final String KEY_RECIPE_LIST_MATERIALS = "materials";
	
}
