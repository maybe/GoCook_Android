package com.m6.gocook.biz.account;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.m6.gocook.R;
import com.m6.gocook.base.fragment.OnActivityAction;
import com.m6.gocook.biz.account.LoginFragment.UserLoginTask;
import com.m6.gocook.biz.main.MainActivityHelper;
import com.m6.gocook.util.File.ImgUtils;
import com.m6.gocook.util.net.NetUtils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class RegisterFragment extends Fragment {

	/**
	 * Keep track of the register task to ensure we can cancel it if requested.
	 */
	private RegisterTask mRegisterTask;
	
	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;
	private String mUsername;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private EditText mPasswordRepeatView;
	private EditText mUsernameView;
	private TextView mStatusMessageView;
	private ImageView mAvatarImageView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_register, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		View root = getView();
		
		mAvatarImageView = (ImageView) root.findViewById(R.id.avatar);
		mUsernameView = (EditText) root.findViewById(R.id.username);
		mEmailView = (EditText) root.findViewById(R.id.email);
		mPasswordView = (EditText) root.findViewById(R.id.password);
		mPasswordRepeatView = (EditText) root.findViewById(R.id.password_repeat);
		mStatusMessageView = (TextView) getView().findViewById(R.id.status_message);
		mStatusMessageView.setText(R.string.login_progress_registering_in);
		
		root.findViewById(R.id.register).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				attemptRegister();
			}
		});
		
		mAvatarImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// DialogFragment.show() will take care of adding the fragment
		        // in a transaction.  We also want to remove any currently showing
		        // dialog, so make our own transaction and take care of that here.
		        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
		        Fragment prev = getChildFragmentManager().findFragmentByTag(AvatarFragment.class.getName());
		        if (prev != null) {
		            ft.remove(prev);
		        }
		        ft.addToBackStack(null);

		        // Create and show the dialog.
				AvatarFragment dialog = AvatarFragment.newInstance();
				dialog.show(ft, AvatarFragment.class.getName());
			}
		});
	}
	
	public void attemptRegister() {
		if (mRegisterTask != null) {
			return;
		}

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);
		mPasswordRepeatView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();
		mUsername = mUsernameView.getText().toString();
		
		String passwordRepeat = mPasswordRepeatView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid repeat password.
		if (TextUtils.isEmpty(passwordRepeat)) {
			mPasswordRepeatView.setError(getString(R.string.error_repeat_password_required));
			focusView = mPasswordRepeatView;
			cancel = true;
		} else if(!passwordRepeat.equals(mPassword)) {
			mPasswordRepeatView.setError(getString(R.string.error_consistency_password));
			focusView = mPasswordRepeatView;
			cancel = true;
		}
				
		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_password_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_email_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}
		
		// Check for a valid username.
		if(TextUtils.isEmpty(mUsername)) {
			mUsernameView.setError(getString(R.string.error_username_required));
			focusView = mUsernameView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mStatusMessageView.setText(R.string.login_progress_registering_in);
			showProgress(true);
			mRegisterTask = new RegisterTask();
			mRegisterTask.execute((Void) null);
		}
	}
	
	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		final View progressStatusView = getView().findViewById(R.id.progress_status);
		final View registerFormView = getView().findViewById(R.id.register_form);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			progressStatusView.setVisibility(View.VISIBLE);
			progressStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							progressStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			registerFormView.setVisibility(View.VISIBLE);
			registerFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							registerFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			progressStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			registerFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
	
	private class RegisterTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			mRegisterTask = null;
			showProgress(false);
			
			if(result) {
				
			} else {
				
			}
			
		}
		
		@Override
		protected void onCancelled() {
			mRegisterTask = null;
			showProgress(false);
		}
		
	}
	
	// 上传照片
	public static class AvatarFragment extends DialogFragment implements OnActivityAction {

		private final static int REQ_CAMERA = 0;
		private final static int REQ_PHOTO = 1;
		
		private UploadAvatarTask mUploadAvatarTask;
		
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
		
		private void uploadAvatar(Uri uri, Bitmap bitmap) {
			if(mUploadAvatarTask != null) {
				return;
			}
			
			mUploadAvatarTask = new UploadAvatarTask(uri, bitmap);
			mUploadAvatarTask.execute((Void) null);
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
				uploadAvatar(null, bitmap);
				return;
			} else if (REQ_PHOTO == requestCode) {
				if (resultCode != Activity.RESULT_OK) {
					return;
				}
				uploadAvatar(data != null ? data.getData() : null, null);
				return;
			}
		}
		
		private class UploadAvatarTask extends AsyncTask<Void, Void, String> {
			
			private Uri mUri;
			private Bitmap mBitmap;

			public UploadAvatarTask(Uri uri, Bitmap bitmap) {
				mUri = uri;
				mBitmap = bitmap;
			}
			
			@Override
			protected String doInBackground(Void... params) {
				String result = null;
				String headUrl = null;
	        	Bitmap headImg = null;
	        	FragmentActivity context = getActivity();
	        	if (mUri == null) {
	        		if (mBitmap == null) {
	        			return null;
	        		}
	        		headImg = ImgUtils.resizeBitmap(getActivity(), mBitmap, 120, 120);      		
	        	} else {
	        		String imgPath = null;
	        		Cursor cursor = context.getContentResolver().query(mUri, null,
	                        null, null, null);
	        		if (cursor != null && cursor.moveToFirst()) {
	        			imgPath = cursor.getString(1); // 图片文件路径
	        		}

	        		if (cursor != null) {
	        			cursor.close();
	        		}
	        		
	        		if (!TextUtils.isEmpty(imgPath)) {
	        			Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
	        			headImg = ImgUtils.resizeBitmap(context, bitmap, 120, 120); 
	        		}
	        	}
	        	
				if (headImg != null) {
					result = NetUtils.httpPostBitmap("", headImg);
				}
				return result;
			}
			
			@Override
			protected void onPostExecute(String result) {
				mUploadAvatarTask = null;
				AvatarFragment.this.dismiss();
			}
			
			@Override
			protected void onCancelled() {
				mUploadAvatarTask = null;
			}
		}
		
	}
	
}
