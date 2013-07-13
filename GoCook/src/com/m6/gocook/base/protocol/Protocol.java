package com.m6.gocook.base.protocol;

public class Protocol {

	/* URL */
<<<<<<< HEAD
//	public static final String URL_ROOT = "http://192.168.1.103";
	public static final String URL_ROOT = "http://verypanda.com";
=======
//	public static final String URL_ROOT = "http://verypanda.com";
	public static final String URL_ROOT = "http://192.168.1.103";
>>>>>>> c8e19d555791f63b05681627387a6c0c488f489f
	
	public static final String URL_LOGIN =  URL_ROOT + "/user/login";
	
	public static final String URL_REGISTER = URL_ROOT + "/user/register";
	
	public static final String URL_RECIPE = URL_ROOT + "/recipe?id=%s";
	
	public static final String URL_RECIPE_COMMENT = URL_ROOT + "/recipe/comments?recipe_id=%s";
	
	public static final String URL_RECIPE_COMMENT_POST = URL_ROOT + "/recipe/comment";
	
	public static final String URL_RECIPE_COLLECT_ADD = URL_ROOT + "/cook/addmycoll?collid=%s";
	
	public static final String URL_RECIPE_COLLECT_DELETE = URL_ROOT + "/cook/delmycoll?collid=%s";
	
	/* Profile URL */
	public static final String URL_PROFILE_UPDATE = URL_ROOT + "/user/changebasicinfo";
	
	public static final String URL_PROFILE_BASIC_INFO = URL_ROOT + "/user/basicinfo";
	
	public static final String URL_PROFILE_MY_RECIPE = URL_ROOT + "/cook/myrecipes";
	
	/** Popular URL */
	public static final String URL_POPULAR = URL_ROOT + "/index/ios_main";
	
	public static final String URL_RECIPE_NEW = URL_ROOT + "/recipe/topnew?page=";
	
	public static final String URL_RECIPE_HOT = URL_ROOT + "/recipe/tophot?page=";
	
	public static final String URL_RECIPE_SEARCH = URL_ROOT + "/index/search?keyword=%s&page=";
	
	
	
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
	public static final String KEY_RECIPE_STEPS = "steps";
	public static final String KEY_RECIPE_STEPS_NO = "no";
	public static final String KEY_RECIPE_STEPS_CONTENT = "content";
	public static final String KEY_RECIPE_STEPS_IMG = "img";
	public static final String KEY_RECIPE_TIPS = "tips";
	
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
