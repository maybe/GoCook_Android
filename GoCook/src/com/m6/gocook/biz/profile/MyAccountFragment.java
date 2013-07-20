package com.m6.gocook.biz.profile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
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

import com.m6.gocook.R;
import com.m6.gocook.base.activity.BaseActivity;
import com.m6.gocook.base.constant.Constants;
import com.m6.gocook.base.fragment.FragmentHelper;
import com.m6.gocook.base.protocol.Protocol;
import com.m6.gocook.base.protocol.ProtocolUtils;
import com.m6.gocook.biz.account.AccountModel;
import com.m6.gocook.biz.recipe.my.MyCollectionsFragment;
import com.m6.gocook.biz.recipe.my.MyRecipesFragment;
import com.m6.gocook.biz.recipe.recipe.RecipeEditFragment;
import com.m6.gocook.util.cache.util.ImageCache;
import com.m6.gocook.util.cache.util.ImageFetcher;

public class MyAccountFragment extends Fragment {
	
	public static final String TAG = "MyAccountFragment";
	
	
	private ImageFetcher mImageFetcher;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		int imageThumbSize = getResources().getDimensionPixelSize(R.dimen.biz_profile_avatar);

        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(getActivity(), Constants.IMAGE_CACHE_DIR);
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
		
		view.findViewById(R.id.follow).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putInt(PeopleFragment.TYPE, PeopleFragment.FOLLOWS);
				Intent intent = FragmentHelper.getIntent(activity, BaseActivity.class, 
						PeopleFragment.class.getName(), PeopleFragment.class.getName(), bundle);
				startActivity(intent);
				
			}
		});
		
		view.findViewById(R.id.fans).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putInt(PeopleFragment.TYPE, PeopleFragment.FANS);
				Intent intent = FragmentHelper.getIntent(activity, BaseActivity.class, 
						PeopleFragment.class.getName(), PeopleFragment.class.getName(), bundle);
				startActivity(intent);
				
			}
		});
		
		view.findViewById(R.id.myrecipe).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putBoolean(MyRecipesFragment.PARAM_FROM_PROFILE, false);
				Intent intent = FragmentHelper.getIntent(getActivity(), BaseActivity.class, 
						MyRecipesFragment.class.getName(), MyRecipesFragment.class.getName(), bundle);
				startActivity(intent);
			}
		});
		
		view.findViewById(R.id.mycollection).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = FragmentHelper.getIntent(getActivity(), BaseActivity.class, 
						MyCollectionsFragment.class.getName(), MyCollectionsFragment.class.getName(), null);
				startActivity(intent);
			}
		});
		
		view.findViewById(R.id.profile).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bundle args = new Bundle();
				args.putInt(ProfileFragment.PROFILE_TYPE, ProfileFragment.PROFILE_MYSELF);
				Intent intent = FragmentHelper.getIntent(activity, BaseActivity.class, 
						ProfileFragment.class.getName(), ProfileFragment.class.getName(), args);
				startActivity(intent);
			}
		});
		
		
		ImageView avatar = (ImageView) activity.findViewById(R.id.avatar);
		// 取本地数据
//		String avatarPath = AccountModel.getAvatarPath(activity);
//		if(!TextUtils.isEmpty(avatarPath)) {
//			avatar.setImageBitmap(BitmapFactory.decodeFile(avatarPath));
//		}
//		((TextView) view.findViewById(R.id.name)).setText(AccountModel.getUsername(activity));
		
		
		// 从网络取数据
		Bundle args = getArguments();
		if(args != null) {
			String url = args.getString(AccountModel.RETURN_ICON);
			if(!TextUtils.isEmpty(url)) {
				mImageFetcher.loadImage(ProtocolUtils.getURL(url), avatar);
			}
			String userName = args.getString(AccountModel.RETURN_USERNAME);
			((TextView) activity.findViewById(R.id.name)).setText(userName);
		}
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
