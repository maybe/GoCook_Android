package com.m6.gocook.biz.profile;

import java.io.File;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

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
import com.m6.gocook.base.view.ActionBar;
import com.m6.gocook.biz.account.AccountModel;
import com.m6.gocook.biz.profile.AvatarFragment.AvatarCallback;
import com.m6.gocook.util.model.ModelUtils;
import com.m6.gocook.util.preference.PrefHelper;

public class ProfileEditFragment extends BaseFragment implements AvatarCallback {

	private Uri mAvatartUri;
	private Bitmap mAvatarBitmap;
	
	// UI references
	private ImageView mAvatarImageView;
	private EditText mNameEditText;
	private EditText mBirthEditText;
	private Spinner mSexeSpinner;
	private EditText mProfessionEditText;
	private EditText mCityEditText;
	private EditText mIntroEditText;
	private DatePickerDialog mDatePickerDialog;
	
	// date
    private int mYear;
    private int mMonth;
    private int mDay;
	
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
		mSexeSpinner = (Spinner) view.findViewById(R.id.sex);
		mCityEditText = (EditText) view.findViewById(R.id.city);
		mProfessionEditText = (EditText) view.findViewById(R.id.profession);
		mIntroEditText = (EditText) view.findViewById(R.id.intro);

		setOnListeners();
		
		// 取本地数据
		String avatarPath = PrefHelper.getString(activity, PrefKeys.ACCOUNT_AVATAR, "");
		if(!TextUtils.isEmpty(avatarPath)) {
			mAvatarImageView.setImageBitmap(BitmapFactory.decodeFile(avatarPath));
		}
		
		new BasicInfoTask(getActivity()).execute((Void) null);
		showProgress(true);
	}
	
	private void initIntro(Map<String, Object> info) {
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				getActivity(), R.array.sex, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSexeSpinner.setAdapter(adapter);
		
		FragmentActivity activity =  getActivity();
		String sex = null;
		if (info != null) {
			mNameEditText.setText(ModelUtils.getStringValue(info, ProfileModel.NICKNAME));
			mBirthEditText.setText(ModelUtils.getStringValue(info, ProfileModel.AGE));
			mCityEditText.setText(ModelUtils.getStringValue(info, ProfileModel.CITY));
			mIntroEditText.setText(ModelUtils.getStringValue(info, ProfileModel.INTRO));
			sex = ModelUtils.getStringValue(info, ProfileModel.SEX);
		} else {
//			mNameEditText.setText(AccountModel.getUsername(activity));
//			mBirthEditText.setText(ProfileModel.getAge(activity));
//			mCityEditText.setText(ProfileModel.getCity(activity));
//			mIntroEditText.setText(ProfileModel.getIntro(activity));
//			sex = ProfileModel.getSex(activity);
			showEmpty(true);
		}
		
		String male = getString(R.string.biz_profile_edit_sex_male);
		String female = getString(R.string.biz_profile_edit_sex_female);
		if (TextUtils.isEmpty(sex)) {
			if (male.equals(sex)) {
				mSexeSpinner.setSelection(0);
			} else if (female.equals(sex)) {
				mSexeSpinner.setSelection(1);
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

		mBirthEditText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mDatePickerDialog == null) {
					mDatePickerDialog = new DatePickerDialog(getActivity(), mDateSetListener, mYear, mMonth, mDay);
				}
				
				if (!mDatePickerDialog.isShowing()) {
					mDatePickerDialog.show();
				}
			}
		});
		
	}
	
	private void updateDisplay() {
        mBirthEditText.setText(
            new StringBuilder()
                // Month is 0 based so add 1
            	.append(mYear).append("-")
		        .append(mMonth + 1).append("-")
                .append(mDay));
    }
	
	private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year, int monthOfYear,
                        int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;
                    updateDisplay();
                }
     };
	
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
		mSexeSpinner = null;
		mProfessionEditText = null;
		mCityEditText = null;
		mIntroEditText = null;
		mDatePickerDialog = null;
		
		mAvatarBitmap = null;
		mAvatartUri = null;
	}
	
	@Override
	public void onActionBarRightButtonClick(View v) {
		super.onActionBarRightButtonClick(v);
		
		showProgress(true);
		new UpdateProfileTask(getActivity()).execute(mNameEditText.getText().toString(),
													(String) mSexeSpinner.getSelectedItem(),
													mBirthEditText.getText().toString(),
													mProfessionEditText.getText().toString(),
													mCityEditText.getText().toString(),
													"", "",
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
	
	
	private class BasicInfoTask extends AsyncTask<Void, Void, Map<String, Object>> {

		private Context mContext;
		
		public BasicInfoTask(Context context) {
			mContext = context.getApplicationContext();
		}
		
		@Override
		protected Map<String, Object> doInBackground(Void... params) {
			return ProfileModel.getBasicInfo(mContext);
		}
		
		@Override
		protected void onPostExecute(Map<String, Object> result) {
			if (isAdded()) {
				showProgress(false);
				if (result != null) {
					initIntro(result);
				}
			}
		}
	}
	
	private class UpdateProfileTask extends AsyncTask<String, Void, String> {

		private Context mContext;
		
		public UpdateProfileTask(Context context) {
			mContext = context;
		}
		
		@Override
		protected String doInBackground(String... params) {
			if(params != null && params.length > 0) {
				File avatarFile = ProfileModel.getAvatarFile(mContext, mAvatarBitmap, mAvatartUri);
				String result = ProfileModel.updateInfo(mContext, avatarFile, params[0], params[1], params[2], 
						params[3], params[4], params[5], params[6], params[7]);
				if(!TextUtils.isEmpty(result)) {
					try {
						JSONObject json = new JSONObject(result);
						int responseCode = json.optInt(AccountModel.RETURN_RESULT);
						if (responseCode == AccountModel.SUCCESS) {
							// 保存邮件、用户名和头像的本地路径
							AccountModel.saveUsername(mContext, params[0]);
							AccountModel.saveAvatarPath(mContext, avatarFile != null ? avatarFile.getPath() : "");
							// 保存个人信息
							ProfileModel.saveAge(mContext, params[2]);
							ProfileModel.saveSex(mContext, params[1]);
							ProfileModel.saveCity(mContext, params[5]);
							ProfileModel.saveProvince(mContext, params[4]);
							ProfileModel.saveIntro(mContext, params[7]);
							ProfileModel.saveCareer(mContext, params[3]);
							ProfileModel.saveTelephone(mContext, params[6]);
							return result;
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
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
