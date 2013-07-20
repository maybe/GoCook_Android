package com.m6.gocook.biz.profile;

import java.io.File;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import android.app.DatePickerDialog;
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
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.m6.gocook.R;
import com.m6.gocook.base.constant.PrefKeys;
import com.m6.gocook.base.fragment.BaseFragment;
import com.m6.gocook.base.protocol.Protocol;
import com.m6.gocook.base.protocol.ProtocolUtils;
import com.m6.gocook.base.view.ActionBar;
import com.m6.gocook.biz.account.AccountModel;
import com.m6.gocook.biz.common.PhotoPickDialogFragment;
import com.m6.gocook.biz.common.PhotoPickDialogFragment.OnPhotoPickCallback;
import com.m6.gocook.util.model.ModelUtils;
import com.m6.gocook.util.preference.PrefHelper;

public class ProfileEditFragment extends BaseFragment implements OnPhotoPickCallback {

	private Uri mAvatartUri;
	private Bitmap mAvatarBitmap;
	
	// UI references
	private ImageView mAvatarImageView;
	private EditText mNameEditText;
	private EditText mAgeEditText;
	private Spinner mSexSpinner;
	private EditText mProfessionEditText;
	private EditText mCityEditText;
	private EditText mIntroEditText;
//	private DatePickerDialog mDatePickerDialog;
	
	// date
    private int mYear;
    private int mMonth;
    private int mDay;
    
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
		actionBar.setRightButton(null, R.drawable.actionbar_save_selector);
		
		View view = getView();
		FragmentActivity activity =  getActivity();
		mAvatarImageView = (ImageView) view.findViewById(R.id.avatar);
		mNameEditText = (EditText) view.findViewById(R.id.name);
		mAgeEditText = (EditText) view.findViewById(R.id.birth);
		mSexSpinner = (Spinner) view.findViewById(R.id.sex);
		mCityEditText = (EditText) view.findViewById(R.id.city);
		mProfessionEditText = (EditText) view.findViewById(R.id.profession);
		mIntroEditText = (EditText) view.findViewById(R.id.intro);

		setOnListeners();
		initIntro();
		
		ImageView avatar = (ImageView) getView().findViewById(R.id.avatar);
		String url = AccountModel.getAvatarPath(getActivity());
		if(!TextUtils.isEmpty(url)) {
			mImageFetcher.loadImage(ProtocolUtils.getURL(url), avatar);
		}
		
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
		
		String male = getString(R.string.biz_profile_edit_sex_male);
		String female = getString(R.string.biz_profile_edit_sex_female);
		if (!TextUtils.isEmpty(mSex)) {
			if (male.equals(mSex)) {
				mSexSpinner.setSelection(1);
			} else if (female.equals(mSex)) {
				mSexSpinner.setSelection(2);
			} else {
				mSexSpinner.setSelection(0);
			}
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

//		mBirthEditText.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				if (mDatePickerDialog == null) {
//					mDatePickerDialog = new DatePickerDialog(getActivity(), mDateSetListener, mYear, mMonth, mDay);
//				}
//				
//				if (!mDatePickerDialog.isShowing()) {
//					mDatePickerDialog.show();
//				}
//			}
//		});
		
	}
	
//	private void updateDisplay() {
//        mBirthEditText.setText(
//            new StringBuilder()
//                // Month is 0 based so add 1
//            	.append(mYear).append("-")
//		        .append(mMonth + 1).append("-")
//                .append(mDay));
//    }
	
//	private DatePickerDialog.OnDateSetListener mDateSetListener =
//            new DatePickerDialog.OnDateSetListener() {
//
//                public void onDateSet(DatePicker view, int year, int monthOfYear,
//                        int dayOfMonth) {
//                    mYear = year;
//                    mMonth = monthOfYear;
//                    mDay = dayOfMonth;
//                    updateDisplay();
//                }
//     };
	
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
		super.onActionBarRightButtonClick(v);
		
		if (!isOnProgressing()) {
			
			String name = mNameEditText.getText().toString();
			String birth = mAgeEditText.getText().toString();
			String city = mCityEditText.getText().toString();
			String career = mProfessionEditText.getText().toString();
			String intro = mIntroEditText.getText().toString();
			String sex = (String) mSexSpinner.getSelectedItem();
			if (!TextUtils.isEmpty(sex) && sex.equals("性别")) {
				sex = "2"; // 0：男，1：女，2：性别
			}
			
			UpdateProfileTask task = new UpdateProfileTask(getActivity(), 
					changedValue(mUsername, name), 
					changedValue(mSex, sex),
					changedValue(mAge, birth),
					changedValue(mCity, city),
					changedValue(mCareer, career),
					changedValue(mIntro, intro));
			
			if (mIsAnythingChanged) {
				showProgress(true);
				task.execute((Void) null);
			}
		}
	}
	
	private boolean mIsAnythingChanged = false;
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
			File avatarFile = ProfileModel.getAvatarFile(mContext, mAvatarBitmap, mAvatartUri);
			String result = ProfileModel.updateInfo(mContext, avatarFile, mParamUsername, mParamSex, mParamBirth, 
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
			showProgress(false);
			
			if(TextUtils.isEmpty(result)) {
				Toast.makeText(mContext, R.string.biz_profile_edit_fail_tip, Toast.LENGTH_SHORT).show();
			} else {
				getActivity().finish();
			}
		}
		
		@Override
		protected void onCancelled() {
			super.onCancelled();
			showProgress(false);
		}
		
	}
}
