package com.m6.gocook.biz.coupon;

import java.io.IOException;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.m6.gocook.R;
import com.m6.gocook.base.activity.BaseActivity;
import com.m6.gocook.base.entity.Sale;
import com.m6.gocook.base.fragment.BaseFragment;
import com.m6.gocook.base.fragment.FragmentHelper;
import com.m6.gocook.base.fragment.OnActivityAction;
import com.m6.gocook.base.view.ActionBar;
import com.m6.gocook.biz.main.MainActivityHelper;

public class ShakeFragment extends BaseFragment implements SensorEventListener, OnActivityAction {

	//定义sensor管理器
    private SensorManager mSensorManager;
    //震动
    private Vibrator vibrator;
    
    private SaleTask mSaleTask;
    
    public static final String PARAM_COUPON_ID = "param_coupon_id";
    
    private String mCouponId;
    
    private boolean mSensorTriggered = false;
    
    private MediaPlayer mMediaPlayer;
    
    private Handler mHandler = new Handler();
    
    private Sale mSaleResult;
    
    public static ShakeFragment newInstance(String couponId) {
    	ShakeFragment fragment = new ShakeFragment();
    	Bundle bundle = new Bundle();
    	bundle.putString(PARAM_COUPON_ID, couponId);
    	fragment.setArguments(bundle);
    	return fragment;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	MainActivityHelper.registerOnActivityActionListener(this);
    	
    	Bundle bundle = getArguments();
    	if (bundle != null) {
    		mCouponId = bundle.getString(PARAM_COUPON_ID);
    	}
    	initMediaPlayer();
    }
    
    /**
     * 初始化播放器
     */
    private void initMediaPlayer() {
    	mMediaPlayer = new MediaPlayer();
    	mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    	AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.shake);
    	try {
			mMediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
			file.close();
			mMediaPlayer.prepare();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				if (!TextUtils.isEmpty(mCouponId)) { // 延期记录获取优惠券
					Bundle bundle = new Bundle();
					bundle.putString(ShakeResultFragment.PARAM_COUPON_ID, mCouponId);
					Intent intent = FragmentHelper.getIntent(getActivity(), BaseActivity.class, 
							ShakeResultFragment.class.getName(), ShakeResultFragment.class.getName(), bundle);
					getActivity().startActivityForResult(intent, MainActivityHelper.REQUEST_CODE_COUPON);
				} else {
					if (mSaleTask == null && mSaleResult != null) {
						// 任务完成跳转到结果页
						goToResultAfterShake(mSaleResult);
					}
				}
			}
		});
    }
    
    @Override
	public void onDestroy() {
		super.onDestroy();
		MainActivityHelper.unRegisterOnActivityActionListener(this);
		if (mMediaPlayer != null) {
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.stop();
			}
			mMediaPlayer.release();
		}
	}
    
    @Override
    public View onCreateFragmentView(LayoutInflater inflater,
    		ViewGroup container, Bundle savedInstanceState) {
    	return inflater.inflate(R.layout.fragment_coupon_shake, container, false);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	getActivity();
		//获取传感器管理服务
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        //震动
        vibrator = (Vibrator) getActivity().getSystemService(Service.VIBRATOR_SERVICE);
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    	super.onViewCreated(view, savedInstanceState);
    	
    	ActionBar actionBar = getActionBar();
		actionBar.setTitle(R.string.biz_coupon_shake_title);
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	//加速度传感器
    	mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }
    
    @Override
	public void onStop(){
      mSensorManager.unregisterListener(this);
      super.onStop();
    }
    
    @Override
	public void onPause(){
      mSensorManager.unregisterListener(this);
      super.onPause();
    }
    
	@Override
	public void onSensorChanged(SensorEvent event) {
		int sensorType = event.sensor.getType();
		// values[0]:X轴，values[1]：Y轴，values[2]：Z轴
		float[] values = event.values;
		if (sensorType == Sensor.TYPE_ACCELEROMETER) {
			/*
			 * 因为一般正常情况下，任意轴数值最大就在9.8~10之间，只有在你突然摇动手机的时候，瞬时加速度才会突然增大或减少。
			 * 所以，经过实际测试，只需监听任一轴的加速度大于14的时候，改变你需要的设置就OK了~~~
			 */
			if ((Math.abs(values[0]) > 14 || Math.abs(values[1]) > 14 || Math
					.abs(values[2]) > 14)) {
				
				if (!mSensorTriggered) {
					mSensorTriggered = true;
					// 播放声音+振动
					if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
						mMediaPlayer.start();
					}
					vibrator.vibrate(500);
					
					if (TextUtils.isEmpty(mCouponId)) { // 摇出销售额
						showProgress(true);
						if (mSaleTask == null) {
							mSaleTask = new SaleTask(getActivity());
							mSaleTask.execute((Void) null);
						}
					}
				}
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}
	
	private void goToResultAfterShake(Sale result) {
		Bundle bundle = new Bundle();
		bundle.putSerializable(ShakeResultFragment.PARAM_SALE, result);
		Intent intent = FragmentHelper.getIntent(getActivity(), BaseActivity.class, 
				ShakeResultFragment.class.getName(), ShakeResultFragment.class.getName(), bundle);
		getActivity().startActivityForResult(intent, MainActivityHelper.REQUEST_CODE_COUPON);
		getActivity().finish();
	}

	public class SaleTask extends AsyncTask<Void, Void, Sale> {

		private Context mContext;
		
		public SaleTask(Context context) {
			mContext = context.getApplicationContext();
		}
		
		@Override
		protected Sale doInBackground(Void... params) {
			return CouponModel.getSale(mContext);
		}
		
		@Override
		protected void onPostExecute(Sale result) {
			mSaleTask = null;
			if (isAdded()) {
				if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
					// 如果音乐播放完成就跳转到结果页面，否则就等到音乐播放完毕后自动触发跳转
					showProgress(false);
					goToResultAfterShake(result);
				} else {
					// 音乐尚未播放完，先记录结果，等音乐播放完再自动处理
					mSaleResult = result;
				}
			}
		}
		
		@Override
		protected void onCancelled() {
			mSaleTask = null;
		}
	}

	@Override
	public void onCustomActivityResult(int requestCode, int resultCode,
			Intent data) {
		if (resultCode == MainActivityHelper.RESULT_CODE_COUPON) {
			getActivity().setResult(MainActivityHelper.RESULT_CODE_COUPON);
			getActivity().finish();
		}
		
	}
	
}
