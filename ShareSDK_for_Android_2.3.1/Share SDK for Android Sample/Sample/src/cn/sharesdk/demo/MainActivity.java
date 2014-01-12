/*
 * 官网地站:http://www.ShareSDK.cn
 * 技术支持QQ: 4006852216
 * 官方微信:ShareSDK   （如果发布新版本的话，我们将会第一时间通过微信将版本更新内容推送给您。如果使用过程中有任何问题，也可以通过微信与我们取得联系，我们将会在24小时内给予回复）
 *
 * Copyright (c) 2013年 ShareSDK.cn. All rights reserved.
 */

package cn.sharesdk.demo;

import java.io.File;
import java.io.FileOutputStream;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.framework.utils.UIHandler;
import m.framework.ui.widget.slidingmenu.SlidingMenu;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.Handler.Callback;
import android.view.KeyEvent;

/**
 * 项目的入口类，是侧栏控件的外壳
 * <p>
 * 侧栏的UI或者逻辑控制基本上都在{@link MainAdapter}中进行
 */
public class MainActivity extends Activity implements Callback {
	private static final String FILE_NAME = "/pic.jpg";
	public static String TEST_IMAGE;
	private SlidingMenu menu;
	private int orientation;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		orientation = getResources().getConfiguration().orientation;

		menu = new SlidingMenu(this);
		menu.setMenuItemBackground(R.color.sliding_menu_item_down, R.color.sliding_menu_item_release);
		menu.setMenuBackground(R.color.sliding_menu_background);
		menu.setTtleHeight(cn.sharesdk.framework.utils.R.dipToPx(this, 44));
		menu.setBodyBackground(R.color.sliding_menu_body_background);
		menu.setShadowRes(R.drawable.sliding_menu_right_shadow);
		menu.setMenuDivider(R.drawable.sliding_menu_sep);
		menu.setAdapter(new MainAdapter(menu));
		setContentView(menu);

		ShareSDK.initSDK(this);
		new Thread() {
			public void run() {
				initImagePath();
				UIHandler.sendEmptyMessageDelayed(1, 100, MainActivity.this);
			}
		}.start();
	}

	private void initImagePath() {
		try {
			if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
					&& Environment.getExternalStorageDirectory().exists()) {
				TEST_IMAGE = Environment.getExternalStorageDirectory().getAbsolutePath() + FILE_NAME;
			}
			else {
				TEST_IMAGE = getApplication().getFilesDir().getAbsolutePath() + FILE_NAME;
			}
			File file = new File(TEST_IMAGE);
			if (!file.exists()) {
				file.createNewFile();
				Bitmap pic = BitmapFactory.decodeResource(getResources(), R.drawable.pic);
				FileOutputStream fos = new FileOutputStream(file);
				pic.compress(CompressFormat.JPEG, 100, fos);
				fos.flush();
				fos.close();
			}
		} catch(Throwable t) {
			t.printStackTrace();
			TEST_IMAGE = null;
		}
	}

	public boolean handleMessage(Message msg) {
		menu.triggerItem(MainAdapter.GROUP_DEMO, MainAdapter.ITEM_DEMO);
		return false;
	}

	/** 屏幕旋转后，此方法会被调用，以刷新侧栏的布局 */
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (orientation != newConfig.orientation) {
			orientation = newConfig.orientation;
			menu.refresh();
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN
				&& !menu.isMenuShown()) {
			menu.showMenu();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	protected void onDestroy() {
		ShareSDK.stopSDK(this);
		super.onDestroy();
	}

	/** 将action转换为String */
	public static String actionToString(int action) {
		switch (action) {
			case Platform.ACTION_AUTHORIZING: return "ACTION_AUTHORIZING";
			case Platform.ACTION_GETTING_FRIEND_LIST: return "ACTION_GETTING_FRIEND_LIST";
			case Platform.ACTION_FOLLOWING_USER: return "ACTION_FOLLOWING_USER";
			case Platform.ACTION_SENDING_DIRECT_MESSAGE: return "ACTION_SENDING_DIRECT_MESSAGE";
			case Platform.ACTION_TIMELINE: return "ACTION_TIMELINE";
			case Platform.ACTION_USER_INFOR: return "ACTION_USER_INFOR";
			case Platform.ACTION_SHARE: return "ACTION_SHARE";
			default: {
				return "UNKNOWN";
			}
		}
	}

}
