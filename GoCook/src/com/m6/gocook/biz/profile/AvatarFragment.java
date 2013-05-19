package com.m6.gocook.biz.profile;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.m6.gocook.R;
import com.m6.gocook.base.fragment.OnActivityAction;
import com.m6.gocook.biz.main.MainActivityHelper;


public class AvatarFragment extends DialogFragment implements OnActivityAction {

	private final static int REQ_CAMERA = 0;
	private final static int REQ_PHOTO = 1;
	
	private AvatarCallback mAvatarCallback;
	
	public static AvatarFragment newInstance() {
		return new AvatarFragment();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MainActivityHelper.registerOnActivityActionListener(this);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		MainActivityHelper.unRegisterOnActivityActionListener(this);
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		
		dialog.setCanceledOnTouchOutside(true);
		Window window = dialog.getWindow();

		window.getAttributes().dimAmount = 0.7f;
		window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		window.setGravity(Gravity.CENTER);
		window.setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
		window.setBackgroundDrawableResource(android.R.color.transparent);
		
		return dialog;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_avatar, container, false);
		
		view.findViewById(R.id.camera).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 照相
        		Intent innerIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        		Intent wrapperIntent = Intent.createChooser(innerIntent, null);
        		getActivity().startActivityForResult(wrapperIntent, REQ_CAMERA);
			}
		});
		
		view.findViewById(R.id.photo).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 图片库
        		Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        		innerIntent.setType("image/*");
        		Intent wrapperIntent = Intent.createChooser(innerIntent, null);
        		getActivity().startActivityForResult(wrapperIntent, REQ_PHOTO);
			}
		});
		return view;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode,
			Intent data) {
		if(requestCode == REQ_CAMERA) {
			if (resultCode != Activity.RESULT_OK) {
				return;
			}
			Bundle bundle = data == null ? null : data.getExtras();
			Object o = bundle == null ? null : bundle.get("data");
			Bitmap bitmap = (o != null && o instanceof Bitmap) ? (Bitmap)o : null;
			if(mAvatarCallback != null) {
				mAvatarCallback.onAvatarUpdate(null, bitmap);
			}
		} else if (requestCode == REQ_PHOTO) {
			if (resultCode != Activity.RESULT_OK) {
				return;
			}
			if(mAvatarCallback != null) {
				mAvatarCallback.onAvatarUpdate(data != null ? data.getData() : null, null);
			}
		}
	}
	
	public void setAvatarCallback(AvatarCallback avatarCallback) {
		mAvatarCallback = avatarCallback;
	}
	
	public interface AvatarCallback {
		public void onAvatarUpdate(Uri uri, Bitmap bitmap);
	}

}
