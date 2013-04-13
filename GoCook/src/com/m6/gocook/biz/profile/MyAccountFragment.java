package com.m6.gocook.biz.profile;

import com.m6.gocook.R;
import com.m6.gocook.base.constant.PrefKeys;
import com.m6.gocook.biz.account.AccountModel;
import com.m6.gocook.util.cache.util.ImageCache;
import com.m6.gocook.util.cache.util.ImageFetcher;
import com.m6.gocook.util.cache.util.ImageWorker;
import com.m6.gocook.util.preference.PrefHelper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class MyAccountFragment extends Fragment {
	
	public static final String TAG = "MyAccountFragment";
	
	private static final String IMAGE_CACHE_DIR = "images";
	private ImageFetcher mImageFetcher;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		int imageThumbSize = getResources().getDimensionPixelSize(R.dimen.biz_profile_avatar);

        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(getActivity(), IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(getActivity(), imageThumbSize);
        mImageFetcher.addImageCache(getActivity().getSupportFragmentManager(), cacheParams);
        mImageFetcher.setImageFadeIn(false);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_myaccount, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		View view = getView();
		final FragmentActivity activity = getActivity();
		
		view.findViewById(R.id.logout).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				builder.setTitle(R.string.biz_account_logout_dialog_title);
				builder.setMessage(R.string.biz_account_logout_dialog_message);
				builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						AccountModel.logout(activity);
					}
				});
				builder.setNegativeButton(android.R.string.cancel, null);
				builder.create().show();
			}
		});
		
		view.findViewById(R.id.profile).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(activity, ProfileActivity.class));
			}
		});
		
		ImageView avatar = (ImageView) activity.findViewById(R.id.avatar);
		// 取本地数据
		String avatarPath = AccountModel.getAvatarPath(activity);
		if(!TextUtils.isEmpty(avatarPath)) {
			avatar.setImageBitmap(BitmapFactory.decodeFile(avatarPath));
		}
		((TextView) view.findViewById(R.id.name)).setText(AccountModel.getUsername(activity));
		
		
		// 从网络取数据
//		Bundle args = getArguments();
//		if(args != null) {
//			String url = args.getString(AccountModel.RETURN_ICON);
//			if(!TextUtils.isEmpty(url)) {
//				mImageFetcher.loadImage(url, avatar);
//			}
//			String userName = args.getString(AccountModel.RETURN_USERNAME);
//			setTitle(userName);
//		}
	}
	
	@Override
    public void onResume() {
        super.onResume();
        mImageFetcher.setExitTasksEarly(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        mImageFetcher.setPauseWork(false);
        mImageFetcher.setExitTasksEarly(true);
        mImageFetcher.flushCache();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mImageFetcher.closeCache();
    }
	
}
