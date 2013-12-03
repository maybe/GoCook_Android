package com.m6.gocook.biz.profile;

import java.io.File;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.m6.gocook.R;
import com.m6.gocook.base.fragment.BaseFragment;
import com.m6.gocook.base.protocol.Protocol;
import com.m6.gocook.base.protocol.ProtocolUtils;
import com.m6.gocook.base.view.ActionBar;
import com.m6.gocook.biz.account.AccountModel;
import com.m6.gocook.biz.common.PhotoPickDialogFragment;
import com.m6.gocook.biz.common.PhotoPickDialogFragment.OnPhotoPickCallback;
import com.m6.gocook.util.model.ModelUtils;

public class ProfileEditFragment extends BaseFragment implements OnPhotoPickCallback {

	private Uri mAvatartUri;
	private Bitmap mAvatarBitmap;
	
	private UpdateAvatarTask mAvatarTask;
	
	// UI references
	private ImageView mAvatarImageView;
	private EditText mNameEditText;
	private EditText mAgeEditText;
	private Spinner mSexSpinner;
	private EditText mProfessionEditText;
	private EditText mCityEditText;
	private EditText mIntroEditText;
    
    private String mSex;
    private String mAge;
    private String mCity;
    private String mIntro;
    private String mUsername;
    private String mCareer;
	
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
		actionBar.setRightButton(R.string.biz_profile_edit_save, R.drawable.actionbar_btn_selector);
		
		View view = getView();
		mAvatarImageView = (ImageView) view.findViewById(R.id.avatar);
		mNameEditText = (EditText) view.findViewById(R.id.name);
		mAgeEditText = (EditText) view.findViewById(R.id.birth);
		mSexSpinner = (Spinner) view.findViewById(R.id.sex);
		mCityEditText = (EditText) view.findViewById(R.id.city);
		mProfessionEditText = (EditText) view.findViewById(R.id.profession);
		mIntroEditText = (EditText) view.findViewById(R.id.intro);

		setOnListeners();
		
		ImageView avatar = (ImageView) getView().findViewById(R.id.avatar);
		String url = AccountModel.getAvatarPath(getActivity());
		if(!TextUtils.isEmpty(url)) {
			mImageFetcher.loadImage(ProtocolUtils.getURL(url), avatar);
		}

		new BasicInfoTask(getActivity()).execute((Void) null);
	}
	
	private void initIntro() {
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				getActivity(), R.array.sex, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSexSpinner.setAdapter(adapter);
		
		mSex = ProfileModel.getSex(getActivity());
		mUsername = AccountModel.getUsername(getActivity());
		mAge = ProfileModel.getAge(getActivity());
		mIntro = ProfileModel.getIntro(getActivity());
		mCity = ProfileModel.getCity(getActivity());
		mCareer = ProfileModel.getCareer(getActivity());
		
		mNameEditText.setText(mUsername);
		mAgeEditText.setText(mAge);
		mCityEditText.setText(mCity);
		mIntroEditText.setText(mIntro);
		mProfessionEditText.setText(mCareer);
		
		if ("0".equals(mSex)) {
			mSexSpinner.setSelection(1);
		} else if ("1".equals(mSex)) {
			mSexSpinner.setSelection(2);
		} else {
			mSexSpinner.setSelection(0);
		}
		
	}
	
	private void setOnListeners() {
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
				dialog.setPhotoPickCallback(ProfileEditFragment.this);
				dialog.show(ft, PhotoPickDialogFragment.class.getName());
			}
		});
		
		getView().findViewById(R.id.upload_avatar).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mAvatarBitmap != null || mAvatartUri != null) {
					if (mAvatarTask == null) {
						mAvatarTask = new UpdateAvatarTask(getActivity());
						mAvatarTask.execute((Void) null);
						
						showProgressDialog(R.string.biz_profile_uploading);
					}
				}
			}
		});
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
		
		mAvatarImageView = null;
		mNameEditText = null;
		mAgeEditText = null;
		mSexSpinner = null;
		mProfessionEditText = null;
		mCityEditText = null;
		mIntroEditText = null;
//		mDatePickerDialog = null;
		
		mAvatarBitmap = null;
		mAvatartUri = null;
	}
	
	@Override
	public void onActionBarRightButtonClick(View v) {
		if (!isOnProgressing()) {
			String name = mNameEditText.getText().toString().trim();
			String birth = mAgeEditText.getText().toString().trim();
			String city = mCityEditText.getText().toString().trim();
			String career = mProfessionEditText.getText().toString().trim();
			String intro = mIntroEditText.getText().toString().trim();
			String sex = (String) mSexSpinner.getSelectedItem();
			// 0：男，1：女，2：保密
			String male = getString(R.string.biz_profile_edit_sex_male);
			String female = getString(R.string.biz_profile_edit_sex_female);
			if (male.equals(sex)) {
				sex = "0";
			} else if (female.equals(sex)) {
				sex = "1";
			} else {
				sex = "2";
			}
			
			if (TextUtils.isEmpty(name)) {
				Toast.makeText(getActivity(), R.string.biz_profile_edit_name_empty, Toast.LENGTH_SHORT).show();
				return;
			} else if (name.length() < 2) {
				Toast.makeText(getActivity(), R.string.biz_profile_edit_name_least_2, Toast.LENGTH_SHORT).show();
				return;
			}
			
			UpdateProfileTask task = new UpdateProfileTask(getActivity(), 
					changedValue(mUsername, name), 
					changedValue(mSex, sex),
					changedValue(mAge, birth),
					changedValue(mCity, city),
					changedValue(mCareer, career),
					changedValue(mIntro, intro));
			
			if (isAnythingChanged()) {
				showProgressDialog(R.string.biz_profile_updating);
				task.execute((Void) null);
			} else {
				Toast.makeText(getActivity(), R.string.biz_profile_edit_empty, Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private boolean mIsAnythingChanged = false;
	
	/**
	 * 执行更新个人信息的任务前判断是否有信息发生变化
	 * 
	 * @return
	 */
	private boolean isAnythingChanged() {
		return mIsAnythingChanged;
	}
	
	private String changedValue(String oldString, String newString) {
		if (TextUtils.isEmpty(oldString) && TextUtils.isEmpty(newString)) {
			return null;
		} else if (!TextUtils.isEmpty(oldString) && TextUtils.isEmpty(newString)) {
			mIsAnythingChanged = true;
			return newString;
		} if (TextUtils.isEmpty(oldString) && !TextUtils.isEmpty(newString)) {
			mIsAnythingChanged = true;
			return newString;
		} else {
			if (newString.equals(oldString)) {
				return null;
			} else {
				mIsAnythingChanged = true;
				return newString;
			}
		}
	}

	@Override
	public void onPhotoPickResult(Uri uri, Bitmap bitmap) {
		mAvatarBitmap = bitmap;
		mAvatartUri = uri;
		
		if(mAvatarBitmap != null) {
			mAvatarImageView.setImageBitmap(mAvatarBitmap);
		} else if (mAvatartUri != null) {
			mAvatarImageView.setImageURI(mAvatartUri);
		} else {
			mAvatarImageView.setImageResource(R.drawable.register_photo);
		}
		
		// 修改完头像直接上传
		if (mAvatarBitmap != null || mAvatartUri != null) {
			if (mAvatarTask == null) {
				mAvatarTask = new UpdateAvatarTask(getActivity());
				mAvatarTask.execute((Void) null);
				
				showProgressDialog(R.string.biz_profile_uploading);
			}
		}
	}
	
	private class BasicInfoTask extends AsyncTask<Void, Void, Boolean> {

		private Context mContext;
		
		public BasicInfoTask(Context context) {
			mContext = context.getApplicationContext();
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgress(true);
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			Map<String, Object> map = ProfileModel.getBasicInfo(mContext);
			if (map != null) {
				// 保存个人信息
				AccountModel.saveUsername(getActivity(), ModelUtils.getStringValue(map, ProfileModel.NICKNAME));
				ProfileModel.saveAge(getActivity(), ModelUtils.getStringValue(map, ProfileModel.AGE));
				int sexType = ModelUtils.getIntValue(map, ProfileModel.SEX, 0);
				String sex;
				if (sexType == 0) {
					sex = "男";
				} else if (sexType == 1) {
					sex = "女";
				} else {
					sex = "2";
				}
				ProfileModel.saveSex(getActivity(), sex);
				ProfileModel.saveCity(getActivity(), ModelUtils.getStringValue(map, ProfileModel.CITY));
				ProfileModel.saveCareer(getActivity(), ModelUtils.getStringValue(map, ProfileModel.CAREER));
				ProfileModel.saveIntro(getActivity(), ModelUtils.getStringValue(map, ProfileModel.INTRO));
				return true;
			}
			return false;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			if (isAdded()) {
				showProgress(false);
				if (result) {
					initIntro();
				} else {
					setEmptyMessage(R.string.biz_profile_info_failed);
					showEmpty(true);
				}
			}
		}
	}
	
	private class UpdateProfileTask extends AsyncTask<Void, Void, String> {

		private Context mContext;
		
		private String mParamSex;
	    private String mParamBirth;
	    private String mParamCity;
	    private String mParamIntro;
	    private String mParamUsername;
	    private String mParamCareer;
		
		public UpdateProfileTask(Context context, String name, String sex, String birth, String city, String career, String intro) {
			mContext = context;
			mParamSex = sex;
			mParamBirth = birth;
			mParamCity = city;
			mParamIntro = intro;
			mParamUsername = name;
			mParamCareer = career;
		}
		
		@Override
		protected String doInBackground(Void... params) {
			String result = ProfileModel.updateInfo(mContext, mParamUsername, mParamSex, mParamBirth, 
					mParamCareer, null, mParamCity, null, mParamIntro);
			if(!TextUtils.isEmpty(result)) {
				try {
					JSONObject json = new JSONObject(result);
					int responseCode = json.optInt(AccountModel.RETURN_RESULT);
					if (responseCode == Protocol.VALUE_RESULT_OK) {
						// 保存邮件、用户名和头像的本地路径
						if (!TextUtils.isEmpty(mParamUsername)) {
							AccountModel.saveUsername(mContext, mParamUsername);
						}
						// TODO  返回的结果没有icon等信息
//						AccountModel.saveAvatarPath(mContext, json.optString(AccountModel.RETURN_ICON));
						// 保存个人信息
						if (mParamBirth != null) {
							ProfileModel.saveAge(mContext, mParamBirth);
						}
						if (mParamSex != null) {
							ProfileModel.saveSex(mContext, mParamSex);
						}
						if (mParamCity != null) {
							ProfileModel.saveCity(mContext, mParamCity);
						}
//							ProfileModel.saveProvince(mContext, );
						if (mParamIntro != null) {
							ProfileModel.saveIntro(mContext, mParamIntro);
						}
						if (mParamCareer != null) {
							ProfileModel.saveCareer(mContext, mParamCareer);
						}
//							ProfileModel.saveTelephone(mContext, params[6]);
						return result;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			if (isAdded()) {
				dismissProgressDialog();
				if(TextUtils.isEmpty(result)) {
					Toast.makeText(mContext, R.string.biz_profile_edit_fail_tip, Toast.LENGTH_SHORT).show();
				} else {
					getActivity().finish();
				}
			}
		}
		
		@Override
		protected void onCancelled() {
			super.onCancelled();
			if (isAdded()) {
				dismissProgressDialog();
			}
		}
	}
	
	private class UpdateAvatarTask extends AsyncTask<Void, Void, String> {
		
		private Context mContext;
		
		public UpdateAvatarTask(Context context) {
			mContext = context.getApplicationContext();
		}
		
		@Override
		protected String doInBackground(Void... params) {
//			File avatarFile = ProfileModel.getAvatarFile(mContext, mAvatarBitmap, mAvatartUri);
			File avatarFile = ProfileModel.getAvatarFile(mContext, mAvatarImageView);
			return ProfileModel.updateAvatar(mContext, avatarFile);
		}
		
		@Override
		protected void onPostExecute(String result) {
			mAvatarTask = null;
			if (isAdded()) {
				dismissProgressDialog();
			}
			if (!TextUtils.isEmpty(result)) {
				try {
					JSONObject json = new JSONObject(result);
					int responseCode = json.optInt(AccountModel.RETURN_RESULT);
					if (responseCode == Protocol.VALUE_RESULT_OK) {
						AccountModel.saveAvatarPath(mContext, ProtocolUtils.getAvatarURL(json.optString(AccountModel.RETURN_AVATAR)));
						if (isAdded()) {
							Toast.makeText(mContext, R.string.biz_profile_update_avatar_success, Toast.LENGTH_SHORT).show();
						}
						
						mAvatarBitmap = null;
						mAvatartUri = null;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		
		@Override
		protected void onCancelled() {
			mAvatarTask = null;
			if (isAdded()) {
				dismissProgressDialog();
			}
		}
	}
}
