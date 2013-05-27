package com.m6.gocook.biz.profile;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
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

import com.m6.gocook.R;
import com.m6.gocook.base.constant.PrefKeys;
import com.m6.gocook.base.fragment.BaseFragment;
import com.m6.gocook.base.view.ActionBar;
import com.m6.gocook.biz.account.AccountModel;
import com.m6.gocook.biz.profile.AvatarFragment.AvatarCallback;
import com.m6.gocook.util.preference.PrefHelper;

public class ProfileEditFragment extends BaseFragment implements AvatarCallback {

	private Uri mAvatartUri;
	private Bitmap mAvatarBitmap;
	
	// UI references
	private ImageView mAvatarImageView;
	private EditText mNameEditText;
	private EditText mBirthEditText;
	private EditText mSexeEditText;
	private EditText mProfessionEditText;
	private EditText mCityEditText;
	private EditText mIntroEditText;
	
	@Override
	public View onCreateFragmentView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_profile_edit, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		ActionBar actionBar = getActionBar();
		actionBar.setTitle(R.string.biz_profile_edit_title);
		actionBar.setRightButton(null, R.drawable.actionbar_save_selector);
		
		View view = getView();
		FragmentActivity activity =  getActivity();
		mAvatarImageView = (ImageView) view.findViewById(R.id.avatar);
		mNameEditText = (EditText) view.findViewById(R.id.name);
		mBirthEditText = (EditText) view.findViewById(R.id.birth);
		mSexeEditText = (EditText) view.findViewById(R.id.sex);
		mCityEditText = (EditText) view.findViewById(R.id.city);
		mProfessionEditText = (EditText) view.findViewById(R.id.profession);
		mIntroEditText = (EditText) view.findViewById(R.id.intro);
		
		
		// 取本地数据
		String avatarPath = PrefHelper.getString(activity, PrefKeys.ACCOUNT_AVATAR, "");
		if(!TextUtils.isEmpty(avatarPath)) {
			mAvatarImageView.setImageBitmap(BitmapFactory.decodeFile(avatarPath));
		}
		
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
				dialog.setAvatarCallback(ProfileEditFragment.this);
				dialog.show(ft, AvatarFragment.class.getName());
			}
		});
		
		String username = AccountModel.getUsername(activity);
		mNameEditText.setText(username);
		
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
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		
		mAvatarImageView = null;
		mNameEditText = null;
		mBirthEditText = null;
		mSexeEditText = null;
		mProfessionEditText = null;
		mCityEditText = null;
		mIntroEditText = null;
		
		mAvatarBitmap = null;
		mAvatartUri = null;
	}
	
	@Override
	public void onActionBarRightButtonClick(View v) {
		super.onActionBarRightButtonClick(v);
		
		showProgress(true);
		new UpdateProfileTask(getActivity()).execute(mNameEditText.getText().toString(),
													mBirthEditText.getText().toString(),
													mSexeEditText.getText().toString(),
													mProfessionEditText.getText().toString(),
													mCityEditText.getText().toString(),
													mIntroEditText.getText().toString());
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
	
	
	private class UpdateProfileTask extends AsyncTask<String, Void, Void> {

		private Context mContext;
		
		public UpdateProfileTask(Context context) {
			mContext = context;
		}
		
		@Override
		protected Void doInBackground(String... params) {
			if(params != null && params.length > 0) {
				File avatarFile = ProfileModel.getAvatarFile(mContext, mAvatarBitmap, mAvatartUri);
				String result = ProfileModel.UpdateProfile(params[0], params[1], params[2], params[3], params[4], params[5], avatarFile);
				if(!TextUtils.isEmpty(result)) {
					try {
						JSONObject json = new JSONObject(result);
						int responseCode = json.optInt(AccountModel.RETURN_RESULT);
						if (responseCode == AccountModel.SUCCESS) {
							String username = json.optString(AccountModel.RETURN_USERNAME);
							
							// 保存邮件、用户名和头像的本地路径
							AccountModel.saveUsername(mContext, username);
							AccountModel.saveAvatarPath(mContext, avatarFile != null ? avatarFile.getPath() : "");
							
						} else {
							
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			showProgress(false);
		}
		
		@Override
		protected void onCancelled() {
			super.onCancelled();
			showProgress(false);
		}
		
	}
}
