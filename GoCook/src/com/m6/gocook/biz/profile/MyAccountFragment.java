package com.m6.gocook.biz.profile;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.m6.gocook.R;
import com.m6.gocook.base.activity.BaseActivity;
import com.m6.gocook.base.constant.Constants;
import com.m6.gocook.base.entity.response.COrderQueryResult;
import com.m6.gocook.base.fragment.BaseWebFragment;
import com.m6.gocook.base.fragment.FragmentHelper;
import com.m6.gocook.base.protocol.Protocol;
import com.m6.gocook.base.protocol.ProtocolUtils;
import com.m6.gocook.biz.account.AccountModel;
import com.m6.gocook.biz.coupon.CouponListFragment;
import com.m6.gocook.biz.order.OrderListFragment;
import com.m6.gocook.biz.order.OrderModel;
import com.m6.gocook.biz.order.OrderWebFragment;
import com.m6.gocook.biz.recipe.my.MyCollectionsFragment;
import com.m6.gocook.biz.recipe.my.MyRecipesFragment;
import com.m6.gocook.util.cache.util.ImageCache;
import com.m6.gocook.util.cache.util.ImageFetcher;
import com.m6.gocook.util.model.ModelUtils;

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
        mImageFetcher.setLoadingImage(R.drawable.image_avatar_default);
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
		
		view.findViewById(R.id.coupon).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = FragmentHelper.getIntent(activity, BaseActivity.class, 
						CouponListFragment.class.getName(), CouponListFragment.class.getName(), null);
				startActivity(intent);
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
				bundle.putBoolean(MyRecipesFragment.PARAM_FROM_PROFILE, true);
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
		
		view.findViewById(R.id.myorder).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FragmentHelper.startActivity(getActivity(), 
						BaseWebFragment.newInstance(getActivity(), OrderWebFragment.class.getName(), 
								Protocol.URL_BUY_ORDERS, getString(R.string.biz_buy_order_list_title, AccountModel.getUsername(getActivity()))));
			}
		});
		
	}
	
	private void bindView(Map<String, Object> data) {
		if (data != null) {
			View view = getView();
			if (view == null) {
				return;
			}
			
			((TextView) view.findViewById(R.id.follow_count)).setText(
					ModelUtils.getStringValue(data, ProfileModel.FOLLOWING_COUNT));
			((TextView) view.findViewById(R.id.fans_count)).setText(
					ModelUtils.getStringValue(data, ProfileModel.FOLLOWED_COUNT));
			((TextView) view.findViewById(R.id.collect_count)).setText(
					ModelUtils.getStringValue(data, ProfileModel.COLLECT_COUNT));
			((TextView) view.findViewById(R.id.recipe_count)).setText(
					ModelUtils.getStringValue(data, ProfileModel.RECIPES_COUNT));
		}
	}
	
	private void bindOrder(COrderQueryResult data) {
		if (getView() != null && data != null) {
			((TextView) getView().findViewById(R.id.order_count)).setText(String.valueOf(data.getTotalCount()));
		}
	}
	
	@Override
    public void onResume() {
        super.onResume();
        mImageFetcher.setExitTasksEarly(false);
        
        String userName = AccountModel.getUsername(getActivity());
        ((TextView) getView().findViewById(R.id.name)).setText(userName);
        
        ImageView avatar = (ImageView) getActivity().findViewById(R.id.avatar);
		String url = AccountModel.getAvatarPath(getActivity());
		if(!TextUtils.isEmpty(url)) {
			mImageFetcher.loadImage(ProtocolUtils.getURL(url), avatar);
		}
		
		// 我的xxx数量
		String userid = AccountModel.getUserId(getActivity());
		new BasicInfoTask(getActivity(), userid).execute((Void) null);
		// 我的购买数量
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		String endDate = df.format(new Date());
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -6);
		calendar.set(Calendar.DATE, 1);
		String startDate = df.format(calendar.getTime());
		new OrdersTask(getActivity(), startDate, endDate, 1).execute((Void) null);
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
    
    private class BasicInfoTask extends AsyncTask<Void, Void, Map<String, Object>> {

		private Context mContext;
		private String mUserId;
		
		public BasicInfoTask(Context context, String userId) {
			mContext = context.getApplicationContext();
			mUserId = userId;
		}
		
		@Override
		protected Map<String, Object> doInBackground(Void... params) {
			return ProfileModel.getOtherInfo(mContext, mUserId);
		}
		
		@Override
		protected void onPostExecute(Map<String, Object> result) {
			if (isAdded()) {
				bindView(result);
			}
		}
	}
    
    private class OrdersTask extends AsyncTask<Void, Void, COrderQueryResult> {

		private Context mContext;
		private int mPage;
		private String mStartDate;
		private String mEndDate;
		
		public OrdersTask(Context context, String startDate, String endDate, int pageIndex) {
			mContext = context.getApplicationContext();
			mStartDate = startDate;
			mEndDate = endDate;
			mPage = pageIndex;
		}
		
		@Override
		protected COrderQueryResult doInBackground(Void... params) {
			return OrderModel.getOrderQueryResult(mContext, mStartDate, mEndDate, mPage);
		}
		
		@Override
		protected void onPostExecute(COrderQueryResult result) {
			if (isAdded()) {
				bindOrder(result);
			}
		}
	}
	
}
