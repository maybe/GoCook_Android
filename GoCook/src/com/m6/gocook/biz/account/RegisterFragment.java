package com.m6.gocook.biz.account;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.m6.gocook.R;
import com.m6.gocook.base.constant.PrefKeys;
import com.m6.gocook.base.fragment.OnActivityAction;
import com.m6.gocook.biz.main.MainActivityHelper;
import com.m6.gocook.biz.profile.ProfileActivity;
import com.m6.gocook.util.File.ImgUtils;
import com.m6.gocook.util.preference.PrefHelper;

public class RegisterFragment extends Fragment {

	/**
	 * Keep track of the register task to ensure we can cancel it if requested.
	 */
	private RegisterTask mRegisterTask;
	
	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;
	private String mRePassword;
	private String mNickname;
	private static Uri mAvatartUri;
	private static Bitmap mAvatarBitmap;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private EditText mPasswordRepeatView;
	private EditText mUsernameView;
	private TextView mStatusMessageView;
	private static ImageView mAvatarImageView;
	
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
		mRePassword = mPasswordRepeatView.getText().toString();
		mNickname = mUsernameView.getText().toString();
		
		boolean cancel = false;
		View focusView = null;

		// Check for a valid repeat password.
		if (TextUtils.isEmpty(mRePassword)) {
			mPasswordRepeatView.setError(getString(R.string.error_repeat_password_required));
			focusView = mPasswordRepeatView;
			cancel = true;
		} else if(!mRePassword.equals(mPassword)) {
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
		
		// Check for a valid nickname.
		if(TextUtils.isEmpty(mNickname)) {
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
	
	private class RegisterTask extends AsyncTask<Void, Void, Map<String, Object>> {
		
		@Override
		protected Map<String, Object> doInBackground(Void... params) {
        	Bitmap avatarBitmap = null;
        	FragmentActivity context = getActivity();
        	if (mAvatartUri == null) {
        		if (mAvatarBitmap != null) {
        			avatarBitmap = ImgUtils.resizeBitmap(getActivity(), mAvatarBitmap, 120, 120);
        		}
        	} else {
        		String imgPath = null;
        		Cursor cursor = context.getContentResolver().query(mAvatartUri, null,
                        null, null, null);
        		if (cursor != null && cursor.moveToFirst()) {
        			imgPath = cursor.getString(1); // 图片文件路径
        		}

        		if (cursor != null) {
        			cursor.close();
        		}
        		
        		if (!TextUtils.isEmpty(imgPath)) {
        			Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
        			avatarBitmap = ImgUtils.resizeBitmap(context, bitmap, 120, 120);
        		}
        	}
        	File avatarFile = ImgUtils.createBitmapFile("avatar" + System.currentTimeMillis(), avatarBitmap);
			String result = AccountModel.register(mEmail, mPassword, mRePassword, mNickname, avatarFile);
			
			if(!TextUtils.isEmpty(result)) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				try {
					JSONObject json = new JSONObject(result);
					int responseCode = json.optInt(AccountModel.RETURN_RESULT);
					if (responseCode == AccountModel.SUCCESS) {
						String icon = json.optString(AccountModel.RETURN_ICON);
						String username = json.optString(AccountModel.RETURN_USERNAME);
						map.put(AccountModel.RETURN_ICON, icon);
						map.put(AccountModel.RETURN_USERNAME, username);
						
						// 保存邮件、用户名和头像的本地路径
						AccountModel.saveAccount(context, mEmail);
						AccountModel.saveUsername(context, username);
						AccountModel.saveAvatarPath(context, avatarFile != null ? avatarFile.getPath() : "");
						
					} else {
						map.put(AccountModel.RETURN_ERRORCODE, json.optInt(AccountModel.RETURN_ERRORCODE));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return map;
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Map<String, Object> result) {
			mRegisterTask = null;
			if(mAvatarBitmap != null) {
				mAvatarBitmap.recycle();
				mAvatarBitmap = null;
			}
			showProgress(false);
			Context context  = getActivity();
			if (result != null && !result.isEmpty() && !result.containsKey(AccountModel.RETURN_ERRORCODE)) {
				String avatarUrl = (String) result.get(AccountModel.RETURN_ICON);
				String userName = (String) result.get(AccountModel.RETURN_USERNAME);
				AccountModel.onRegister(mEmail, avatarUrl, userName);
				Toast.makeText(context, R.string.biz_account_register_success, Toast.LENGTH_LONG).show();
				
				Intent intent = new Intent(context, ProfileActivity.class);
				Bundle args = new Bundle();
				args.putString(AccountModel.RETURN_ICON, avatarUrl);
				args.putString(AccountModel.RETURN_USERNAME, userName);
				context.startActivity(intent.putExtras(args));
			} else {
				int errorCode = -1;
				try {
					errorCode = Integer.valueOf((String) result.get(AccountModel.RETURN_ERRORCODE));
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
				
				if(errorCode == AccountModel.ERRORCODE_FAILURE) {
					Toast.makeText(context, R.string.biz_account_register_errorcode_failure, Toast.LENGTH_LONG).show();
				} else if(errorCode == AccountModel.ERRORCODE_EMAIL) {
					Toast.makeText(context, R.string.biz_account_register_errorcode_email, Toast.LENGTH_LONG).show();
				} else if(errorCode == AccountModel.ERRORCODE_NICKNAME) {
					Toast.makeText(context, R.string.biz_account_register_errorcode_nickname, Toast.LENGTH_LONG).show();
				} else if(errorCode == AccountModel.ERRORCODE_PASSWORD) {
					Toast.makeText(context, R.string.biz_account_register_errorcode_password, Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(context, R.string.biz_account_register_errorcode_others, Toast.LENGTH_LONG).show();
				}
			}
			
		}
		
		@Override
		protected void onCancelled() {
			mRegisterTask = null;
			if(mAvatarBitmap != null) {
				mAvatarBitmap.recycle();
				mAvatarBitmap = null;
			}
			showProgress(false);
		}
	}
	
	private static void updateAvatar(Uri uri, Bitmap bitmap) {
		mAvatarBitmap = bitmap;
		mAvatartUri = uri;
		
		if(mAvatarBitmap != null) {
			mAvatarImageView.setImageBitmap(mAvatarBitmap);
		} else if (mAvatartUri != null) {
			mAvatarImageView.setImageURI(mAvatartUri);
		} else {
			mAvatarImageView.setImageResource(R.drawable.register_photo);
		}
	}
	
	@Override
	public void onResume() {
		Fragment f = getChildFragmentManager().findFragmentByTag(AvatarFragment.class.getName());
		if (f != null) {
			DialogFragment df = (DialogFragment) f;
			df.dismiss();
			getFragmentManager().beginTransaction().remove(f).commit();
		}
		super.onResume();
	}
	
	
	// 上传照片
	public static class AvatarFragment extends DialogFragment implements OnActivityAction {

		private final static int REQ_CAMERA = 0;
		private final static int REQ_PHOTO = 1;
		
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
				updateAvatar(null, bitmap);
			} else if (requestCode == REQ_PHOTO) {
				if (resultCode != Activity.RESULT_OK) {
					return;
				}
				updateAvatar(data != null ? data.getData() : null, null);
			}
		}
		
	}
	
}
