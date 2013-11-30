package com.m6.gocook.biz.common;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.m6.gocook.R;
import com.m6.gocook.base.fragment.OnActivityAction;
import com.m6.gocook.biz.main.MainActivityHelper;


public class PhotoPickDialogFragment extends DialogFragment implements OnActivityAction {

	private final static int REQ_CAMERA = 0;
	private final static int REQ_PHOTO = 1;
	
	private OnPhotoPickCallback mAvatarCallback;
	
	public static PhotoPickDialogFragment newInstance() {
		return new PhotoPickDialogFragment();
	}
	
	public static boolean startForResult(FragmentManager fragmentManager, OnPhotoPickCallback callback) {
		if(fragmentManager == null) {
			return false;
		}
		FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment prev = fragmentManager.findFragmentByTag(PhotoPickDialogFragment.class.getName());
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
		PhotoPickDialogFragment dialog = PhotoPickDialogFragment.newInstance();
		dialog.setPhotoPickCallback(callback);
		dialog.show(ft, PhotoPickDialogFragment.class.getName());
		return true;
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
	public void onCustomActivityResult(int requestCode, int resultCode,
			Intent data) {
		if(requestCode == REQ_CAMERA) {
			if (resultCode != Activity.RESULT_OK) {
				return;
			}
			Bundle bundle = data == null ? null : data.getExtras();
			Object o = bundle == null ? null : bundle.get("data");
			Bitmap bitmap = (o != null && o instanceof Bitmap) ? (Bitmap)o : null;
			if(mAvatarCallback != null) {
				mAvatarCallback.onPhotoPickResult(null, bitmap);
			}
		} else if (requestCode == REQ_PHOTO) {
			if (resultCode != Activity.RESULT_OK) {
				return;
			}
			if(mAvatarCallback != null) {
				mAvatarCallback.onPhotoPickResult(data != null ? data.getData() : null, null);
			}
		}
	}
	
	public void setPhotoPickCallback(OnPhotoPickCallback avatarCallback) {
		mAvatarCallback = avatarCallback;
	}
	
	public interface OnPhotoPickCallback {
		public void onPhotoPickResult(Uri uri, Bitmap bitmap);
	}

}
