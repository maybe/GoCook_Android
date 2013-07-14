package com.m6.gocook.biz.account;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.m6.gocook.R;
import com.m6.gocook.base.activity.BaseActivity;
import com.m6.gocook.base.fragment.FragmentHelper;
import com.m6.gocook.biz.common.PhotoPickDialogFragment;
import com.m6.gocook.biz.common.PhotoPickDialogFragment.AvatarCallback;
import com.m6.gocook.biz.profile.ProfileEditFragment;
import com.m6.gocook.biz.profile.ProfileModel;

public class RegisterFragment extends Fragment implements AvatarCallback {

	/**
	 * Keep track of the register task to ensure we can cancel it if requested.
	 */
	private RegisterTask mRegisterTask;
	
	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;
	private String mRePassword;
	private String mNickname;
	private Uri mAvatartUri;
	private Bitmap mAvatarBitmap;

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
		        Fragment prev = getChildFragmentManager().findFragmentByTag(PhotoPickDialogFragment.class.getName());
		        if (prev != null) {
		            ft.remove(prev);
		        }
		        ft.addToBackStack(null);

		        // Create and show the dialog.
				PhotoPickDialogFragment dialog = PhotoPickDialogFragment.newInstance();
				dialog.setAvatarCallback(RegisterFragment.this);
				dialog.show(ft, PhotoPickDialogFragment.class.getName());
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
        	FragmentActivity context = getActivity();
        	File avatarFile = ProfileModel.getAvatarFile(context, mAvatarBitmap, mAvatartUri);
			String result = AccountModel.register(context, mEmail, mPassword, mRePassword, mNickname, avatarFile);
			
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
				
				Bundle args = new Bundle();
				args.putString(AccountModel.RETURN_ICON, avatarUrl);
				args.putString(AccountModel.RETURN_USERNAME, userName);
				Intent intent = FragmentHelper.getIntent(context, BaseActivity.class, 
						ProfileEditFragment.class.getName(), ProfileEditFragment.class.getName(), args);
				if(isAdded()) {
					startActivity(intent);
				}
			} else {
				int errorCode = -1;
				try {
					errorCode = (Integer) result.get(AccountModel.RETURN_ERRORCODE);
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
	
	@Override
	public void onResume() {
		Fragment f = getChildFragmentManager().findFragmentByTag(PhotoPickDialogFragment.class.getName());
		if (f != null) {
			DialogFragment df = (DialogFragment) f;
			df.dismiss();
			getFragmentManager().beginTransaction().remove(f).commit();
		}
		super.onResume();
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mAvatarBitmap = null;
		mAvatarImageView = null;
		mEmailView = null;
		mPasswordView = null;
		mPasswordRepeatView = null;
		mUsernameView = null;
		mStatusMessageView = null;
		mAvatarImageView = null;
	}

	@Override
	public void onAvatarUpdate(Uri uri, Bitmap bitmap) {
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
	
}
