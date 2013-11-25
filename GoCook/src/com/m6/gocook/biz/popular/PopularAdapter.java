package com.m6.gocook.biz.popular;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.m6.gocook.R;
import com.m6.gocook.base.activity.BaseActivity;
import com.m6.gocook.base.entity.Popular;
import com.m6.gocook.base.fragment.FragmentHelper;
import com.m6.gocook.base.image.HeaderImageFetcher;
import com.m6.gocook.base.protocol.ProtocolUtils;
import com.m6.gocook.biz.recipe.top.RecipeTopFragment;
import com.m6.gocook.util.cache.util.ImageFetcher;

public class PopularAdapter extends BaseAdapter {
	
	private static final int VIEW_TYPE_HEADER = 0;
	
	private static final int VIEW_TYPE_NORMAL = 1;
	
	private static final int VIEW_TYPE_AD = 2;

	private LayoutInflater mInflater;
	
	private FragmentActivity mActivity;
	
	private ImageFetcher mImageFetcher;
	
	private ImageFetcher mHeaderImageFetcher;

	private Popular mPopular;
	
	public PopularAdapter(FragmentActivity activity, ImageFetcher imageFetcher, Popular popular) {
		mActivity = activity;
		mInflater = LayoutInflater.from(activity);
		mImageFetcher = imageFetcher;
		mPopular = popular;
		mHeaderImageFetcher = HeaderImageFetcher.getInstance(activity);
	}

	public void setPopularData(Popular popular) {
		mPopular = popular;
	}
	
	@Override
	public int getCount() {
		return mPopular == null ? 0 : mPopular.getRecommendItems().size() + 2;
	}
	
	@Override
	public int getItemViewType(int position) {
		if(position == 0) {
			return VIEW_TYPE_HEADER;
		} else if (position == 1){
			return VIEW_TYPE_AD;
		} else {
			return VIEW_TYPE_NORMAL;
		}
	}
	

	public int getViewTypeCount() {
		return 3;
	}

	@Override
	public Object getItem(int position) {
		if(position == 0 || position == 1) {
			return null;
		} else {
			return mPopular.getRecommendItems().get(position - 2);
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		HeaderViewHolder headerHolder = null;
		NormalViewHolder normalHolder = null;
		ADViewHolder adViewHolder = null;
		int type = getItemViewType(position);
		if(convertView == null) {
			if(type == VIEW_TYPE_HEADER) {
				convertView = mInflater.inflate(R.layout.adapter_popular_header_item, null);
				headerHolder = new HeaderViewHolder();
				headerHolder.image1 = (ImageView) convertView.findViewById(R.id.image1);
				headerHolder.image2 = (ImageView) convertView.findViewById(R.id.image2);
				headerHolder.text1 = (TextView) convertView.findViewById(R.id.text1);
				headerHolder.text2 = (TextView) convertView.findViewById(R.id.text2);
				convertView.setTag(headerHolder);
			} else if(type == VIEW_TYPE_NORMAL) {
				convertView = mInflater.inflate(R.layout.adapter_popular_normal_item, null);
				normalHolder = new NormalViewHolder();
				normalHolder.title = (TextView) convertView.findViewById(R.id.title);
				normalHolder.image1 = (ImageView) convertView.findViewById(R.id.image1);
				normalHolder.image2 = (ImageView) convertView.findViewById(R.id.image2);
				normalHolder.image3 = (ImageView) convertView.findViewById(R.id.image3);
				normalHolder.image4 = (ImageView) convertView.findViewById(R.id.image4);
				convertView.setTag(normalHolder);
			} else if (type == VIEW_TYPE_AD) {
				convertView = mInflater.inflate(R.layout.adapter_popular_ad_item, null);
				adViewHolder = new ADViewHolder();
				adViewHolder.ad1 = (ImageView) convertView.findViewById(R.id.ad1);
				adViewHolder.ad2 = (ImageView) convertView.findViewById(R.id.ad2);
				adViewHolder.ad3 = (ImageView) convertView.findViewById(R.id.ad3);
				convertView.setTag(adViewHolder);
			}
		} else {
			if (type == VIEW_TYPE_HEADER) {
				headerHolder = (HeaderViewHolder) convertView.getTag();
			} else if (type == VIEW_TYPE_NORMAL) {
				normalHolder = (NormalViewHolder) convertView.getTag();
			} else if (type == VIEW_TYPE_AD) {
				adViewHolder = (ADViewHolder) convertView.getTag();
			}
		}

		if (type == VIEW_TYPE_HEADER) {
			mHeaderImageFetcher.loadImage(ProtocolUtils.getURL(mPopular.getTopHotImg()), headerHolder.image1);
			mHeaderImageFetcher.loadImage(ProtocolUtils.getURL(mPopular.getTopNewImg()), headerHolder.image2);
			
			headerHolder.image1.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Bundle args = new Bundle();
					args.putString(RecipeTopFragment.PARAM_TYPE, RecipeTopFragment.PARAM_TYPE_HOT);
					Intent intent = FragmentHelper.getIntent(mActivity, BaseActivity.class, RecipeTopFragment.class.getName(), RecipeTopFragment.class.getName(), args);
					mActivity.startActivity(intent);
				}
			});
			
			headerHolder.image2.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Bundle args = new Bundle();
					args.putString(RecipeTopFragment.PARAM_TYPE, RecipeTopFragment.PARAM_TYPE_NEW);
					Intent intent = FragmentHelper.getIntent(mActivity, BaseActivity.class, RecipeTopFragment.class.getName(), RecipeTopFragment.class.getName(), args);
					mActivity.startActivity(intent);
				}
			});
			
		} else if (type == VIEW_TYPE_NORMAL) {
			Pair<String, String[]> data = mPopular.getRecommendItems().get(position - 2);
			normalHolder.title.setText(data.first);
			String[] images = data.second;
			if(images != null) {
				int length = images.length;
				if(length >=4) {
					mImageFetcher.loadImage(ProtocolUtils.getURL(images[0]), normalHolder.image1);
					mImageFetcher.loadImage(ProtocolUtils.getURL(images[1]), normalHolder.image2);
					mImageFetcher.loadImage(ProtocolUtils.getURL(images[2]), normalHolder.image3);
					mImageFetcher.loadImage(ProtocolUtils.getURL(images[3]), normalHolder.image4);
				} else if (length  == 3) {
					mImageFetcher.loadImage(ProtocolUtils.getURL(images[0]), normalHolder.image1);
					mImageFetcher.loadImage(ProtocolUtils.getURL(images[1]), normalHolder.image2);
					mImageFetcher.loadImage(ProtocolUtils.getURL(images[2]), normalHolder.image3);
				} else if (length  == 2) {
					mImageFetcher.loadImage(ProtocolUtils.getURL(images[0]), normalHolder.image1);
					mImageFetcher.loadImage(ProtocolUtils.getURL(images[1]), normalHolder.image2);
				} else if (length  == 1) {
					mImageFetcher.loadImage(ProtocolUtils.getURL(images[0]), normalHolder.image1);
				}
			}
		} else if (type == VIEW_TYPE_AD) {
			adViewHolder.ad1.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					openBrower("c2b.m6fresh.com");
				}
			});
			adViewHolder.ad2.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					openBrower("share.m6fresh.com");
				}
			});
			adViewHolder.ad3.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					openBrower("o2o.m6fresh.com");
				}
			});
		}
		return convertView;
	}
	
	private void openBrower(String url) {
		Intent intent= new Intent();        
	    intent.setAction(Intent.ACTION_VIEW);    
	    intent.setData(Uri.parse(url));
	    intent.setClassName("com.android.browser","com.android.browser.BrowserActivity");
	    mActivity.startActivity(intent);
	}
	
	class HeaderViewHolder {
		private ImageView image1;
		private ImageView image2;
		private TextView text1;
		private TextView text2;
	}

	class NormalViewHolder {
		private TextView title;
		private ImageView image1;
		private ImageView image2;
		private ImageView image3;
		private ImageView image4;
	}

	class ADViewHolder {
		private ImageView ad1;
		private ImageView ad2;
		private ImageView ad3;
	}
}
