package com.m6.gocook.biz.coupon;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.m6.gocook.R;
import com.m6.gocook.base.activity.BaseActivity;
import com.m6.gocook.base.entity.Sale;
import com.m6.gocook.base.fragment.BaseFragment;
import com.m6.gocook.base.fragment.FragmentHelper;
import com.m6.gocook.base.view.ActionBar;

public class ShakeFragment extends BaseFragment implements SensorEventListener {

	//定义sensor管理器
    private SensorManager mSensorManager;
    //震动
    private Vibrator vibrator;
    
    private SaleTask mSaleTask;
    
    private static final String PARAM_COUPON_ID = "param_coupon_id";
    
    private String mCouponId;
    
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
    	
    	Bundle bundle = getArguments();
    	if (bundle != null) {
    		mCouponId = bundle.getString(PARAM_COUPON_ID);
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
				
				if (!TextUtils.isEmpty(mCouponId)) { // 延期记录获取优惠券
					FragmentHelper.startActivity(getActivity(), new ShakeResultFragment());
				} else { // 摇出销售额
					showProgress(true);
					if (mSaleTask == null) {
						mSaleTask = new SaleTask(getActivity());
						mSaleTask.execute((Void) null);
					}
				}
				
				// 摇动手机后，再伴随震动提示
				vibrator.vibrate(500);
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
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
				showProgress(false);
				Bundle bundle = new Bundle();
				bundle.putSerializable(ShakeResultFragment.PARAM_SALE, result);
				Intent intent = FragmentHelper.getIntent(getActivity(), BaseActivity.class, 
						ShakeResultFragment.class.getName(), ShakeResultFragment.class.getName(), bundle);
				startActivity(intent);
				getActivity().finish();
			}
		}
		
		@Override
		protected void onCancelled() {
			mSaleTask = null;
		}
	}
	
}
