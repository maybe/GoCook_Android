package com.m6.gocook.biz.recipe.recipe;

import java.io.File;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.m6.gocook.R;
import com.m6.gocook.base.activity.BaseActivity;
import com.m6.gocook.base.entity.RecipeEntity;
import com.m6.gocook.base.entity.RecipeEntity.Material;
import com.m6.gocook.base.entity.RecipeEntity.Procedure;
import com.m6.gocook.base.fragment.BaseFragment;
import com.m6.gocook.base.fragment.FragmentHelper;
import com.m6.gocook.base.protocol.Protocol;
import com.m6.gocook.base.view.ActionBar;
import com.m6.gocook.biz.common.PhotoPickDialogFragment;
import com.m6.gocook.biz.common.PhotoPickDialogFragment.OnPhotoPickCallback;
import com.m6.gocook.biz.profile.ProfileModel;
import com.m6.gocook.biz.recipe.RecipeModel;
import com.m6.gocook.biz.recipe.recipe.UploadImageLayout.UploadAsyncTask;
import com.m6.gocook.util.File.ImgUtils;
import com.m6.gocook.util.log.Logger;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RecipeEditFragment extends BaseFragment implements OnClickListener, OnPhotoPickCallback {

	private final String TAG = RecipeFragment.class.getCanonicalName();
	
	public static final String ARGUMENT_KEY_ACTION = "argument_key_action";
	public static final String ARGUMENT_KEY_RECIPE_ID = "argument_key_recipe_id";

	private Context mContext;
	private View mRootView;
	private String mRecipeId;
	private RecipeEntity mRecipeEntity;
	private LayoutInflater mInflater;
	private Mode mMode;
	
	private ImageView mCurrentImageView;
	private Uri mCurrentSelectedUri;
	private Bitmap mCurrentSelectedBitmap;
	
	public enum Mode {
		RECIPE_NEW, RECIPE_EDIT
	}
	
	public static void startInActivity(Context context, Mode mode, String recipeId) {
		Bundle argument = new Bundle();
		argument.putSerializable(RecipeEditFragment.ARGUMENT_KEY_ACTION,
				mode);
		argument.putString(RecipeEditFragment.ARGUMENT_KEY_RECIPE_ID, recipeId);
        Intent intent = FragmentHelper.getIntent(context, BaseActivity.class, 
        		RecipeEditFragment.class.getName(), 
        		RecipeEditFragment.class.getName(), argument);
        context.startActivity(intent);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// set softinputmode for activity
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
	}
	
	
	@Override
	public View onCreateFragmentView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		mInflater = inflater;
		mRootView = inflater.inflate(R.layout.fragment_recipe_edit, null, false);
		mRootView.findViewById(R.id.cover_imageview).setOnClickListener(this);
		return mRootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		if(mContext == null) {
			mContext = getActivity();
			doCreate();
		}
	}
	
	private View findViewById(int id) {
		if(mRootView != null) {
			return mRootView.findViewById(id);
		} else {
			return null;
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();

		Fragment f = getChildFragmentManager().findFragmentByTag(PhotoPickDialogFragment.class.getName());
		if (f != null) {
			DialogFragment df = (DialogFragment) f;
			df.dismiss();
			getFragmentManager().beginTransaction().remove(f).commit();
		}
	}
	
	private class MyEditTextOnTouchListener implements OnTouchListener {

		private int mLineNumber = Integer.MAX_VALUE;
		
		public MyEditTextOnTouchListener(int lineNumber) {
			mLineNumber = lineNumber;
		}
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if(((EditText)v).getLineCount() > mLineNumber) {
				v.getParent().requestDisallowInterceptTouchEvent(true);
				switch (event.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_CANCEL:
					v.getParent().requestDisallowInterceptTouchEvent(false);
					break;
				}
			}
			return false;
		}
	}

	private void doCreate() {
		
		ActionBar action = getActionBar();
		
		action.setRightButton(R.string.biz_recipe_edit_post, R.drawable.edit);
		
		Bundle arg = getArguments();
		if(arg != null) {
			//set action mode and title
			mMode= (Mode)arg.getSerializable(ARGUMENT_KEY_ACTION);
			if(mMode == Mode.RECIPE_NEW) {
				action.setTitle(R.string.biz_recipe_edit_title_new);
			} else if (mMode == Mode.RECIPE_EDIT) {
				action.setTitle(R.string.biz_recipe_edit_title_edit);
				mRecipeId = arg.getString(RecipeEditFragment.ARGUMENT_KEY_RECIPE_ID);
			}
		}
		
		if(mMode == Mode.RECIPE_EDIT) {
			new AchieveRecipeTask().execute();
		}

		final EditText descEditText = (EditText) findViewById(R.id.recipe_introduction_edittext);
		descEditText.setOnTouchListener(new MyEditTextOnTouchListener(8));
		
		final EditText tipsEditText = (EditText) findViewById(R.id.recipe_tips_edittext);
		tipsEditText.setOnTouchListener(new MyEditTextOnTouchListener(8));

		final LinearLayout materialLayout = (LinearLayout) findViewById(R.id.material_layout);
		for (int i = 0; i < 5; i++) {
			materialLayout.addView(createMaterialView());
		}

		((Button) findViewById(R.id.material_addmore_button))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						materialLayout.addView(createMaterialView());
					}
				});

		final LinearLayout procedureLayout = (LinearLayout) findViewById(R.id.procedure_layout);
		for (int i = 0; i < 5; i++) {
			procedureLayout.addView(createProcedureView());
		}

		((Button) findViewById(R.id.procedure_addmore_button))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						procedureLayout.addView(createProcedureView());
					}
				});
		
	}
	
	private void applyData() {
		
		ImageView cover = (ImageView) findViewById(R.id.cover_imageview);
		mImageFetcher.loadImage(mRecipeEntity.getCoverImgURL(), cover);
		
		EditText titleEditText = (EditText) findViewById(R.id.recipe_title_edittext);
		titleEditText.setText(mRecipeEntity.getName());
		
		EditText intrEditText = (EditText) findViewById(R.id.recipe_introduction_edittext);
		intrEditText.setText(mRecipeEntity.getDesc());
		
		EditText tipEditText = (EditText) findViewById(R.id.recipe_tips_edittext);
		tipEditText.setText(mRecipeEntity.getTips());
		
		applyMaterial();
		applyProcedure();
		
	}
	
	private View createMaterialView() {
		return mInflater.inflate(R.layout.adapter_recipe_edit_material_item,
				null);
	}
	
	private View createProcedureView() {

		final View view = mInflater.inflate(
				R.layout.adapter_recipe_edit_procedure_item,
				null);
		
		final ImageView imageView = (ImageView) view.findViewById(R.id.image);
		imageView.setOnClickListener(this);
		
		view.findViewById(R.id.button_upload).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new UploadAsyncTask(imageView).execute();
			}
		});
		
		view.findViewById(R.id.button_remove).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				view.findViewById(R.id.button_layout).setVisibility(View.GONE);
				imageView.setImageResource(R.drawable.landscape_photo);
				}
		});
		
		return view;
	}
	
	private View applyMaterial() {
		final LinearLayout materialLayout = (LinearLayout) findViewById(R.id.material_layout);
		
		ArrayList<Material> materials = mRecipeEntity.getMaterials();
		int materialCount = materials.size();
		int materialFlag = 0;

		for (int i = 0; i < materialLayout.getChildCount(); i++, materialFlag++) {

			if (materialFlag < materialCount) {

				Material material = materials.get(materialFlag);
				View view = materialLayout.getChildAt(i);

				applyDataToMaterialItem(view, material);
			}
		}

		for (; materialFlag < materialCount; materialFlag++) {
			
			Material material = materials.get(materialFlag);
			View view = createMaterialView();
			
			applyDataToMaterialItem(view, material);
			materialLayout.addView(view);
		}
		return null;
	}
	
	private void applyDataToMaterialItem(View view, Material material) {
		
		EditText name = (EditText) view.findViewById(R.id.name);
		name.setText(material.getName());

		EditText remark = (EditText) view.findViewById(R.id.remark);
		remark.setText(material.getRemark());
	}
	
	private View applyProcedure() {
		final LinearLayout procedureLayout = (LinearLayout) findViewById(R.id.procedure_layout);
		
		ArrayList<Procedure> procedures = mRecipeEntity.getProcedures();
		int procedureCount = procedures.size();
		int procedureFlag = 0;

		for (int i = 0; i < procedureLayout.getChildCount(); i++, procedureFlag++) {

			if (procedureFlag < procedureCount) {

				Procedure procedure = procedures.get(procedureFlag);
				View view = procedureLayout.getChildAt(i);

				applyDataToProcedurelItem(view, procedure);
			}
		}

		for (; procedureFlag < procedureCount; procedureFlag++) {
			
			Procedure procedure = procedures.get(procedureFlag);
			View view = createProcedureView();
			
			applyDataToProcedurelItem(view, procedure);
			procedureLayout.addView(view);
		}
		return null;
	}
	
	private void applyDataToProcedurelItem(View view, Procedure procedure) {
		
		EditText desc = (EditText) view.findViewById(R.id.desc);
		desc.setText(procedure.getDesc());

		if(!TextUtils.isEmpty(procedure.getImageURL())) {
			ImageView image = (ImageView) view.findViewById(R.id.image);
			mImageFetcher.loadImage(procedure.getImageURL(), image);
			
			view.findViewById(R.id.button_layout).setVisibility(View.VISIBLE);
			view.findViewById(R.id.button_upload).setEnabled(false);
		}
	}
	
	private void showUploadingProgressBar(boolean show) {
		findViewById(R.id.progressbar_layout).setVisibility(
				show ? View.VISIBLE : View.GONE);
	}
	
	private class AchieveRecipeTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			showProgress(true);
		}

		@Override
		protected Void doInBackground(Void... params) {

			mRecipeEntity = RecipeModel.getRecipe(getActivity(), mRecipeId, true);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if(isAdded()) {
				showProgress(false);
				if(mRecipeEntity != null) {
					applyData();
				} else {
					getActivity().finish();
				}
			}
			super.onPostExecute(result);
		}

	}

	@Override
	public void onClick(View v) {
		mCurrentImageView = (ImageView) v;
		PhotoPickDialogFragment.startForResult(getChildFragmentManager(), this);
	}

	@Override
	public void onPhotoPickResult(final Uri uri, final Bitmap bitmap) {
		if(mCurrentImageView != null) {
			mCurrentImageView.setImageBitmap(bitmap);
			if(bitmap != null) {
				mCurrentImageView.setImageBitmap(bitmap);
			} else if (uri != null) {
				mCurrentImageView.setImageURI(uri);
			} else {
				mCurrentImageView.setImageResource(R.drawable.register_photo);
			}
			
			// After select new picture, set ImageView tag to null
			mCurrentImageView.setTag(null);
			
			if(mCurrentImageView.getId() == R.id.cover_imageview) {
				new UploadAsyncTask(mCurrentImageView).execute();
			} else {
				LinearLayout parent = (LinearLayout) mCurrentImageView.getParent();
				parent.findViewById(R.id.button_layout).setVisibility(View.VISIBLE);
				parent.findViewById(R.id.button_upload).setVisibility(View.VISIBLE);
			}
			
		}
	}
	
	@Override
	public void onActionBarRightButtonClick(View v) {
		postRecipe();
	}
	
	private void postRecipe() {
		if(mRecipeEntity == null) {
			mRecipeEntity = new RecipeEntity();
		}
		
		if(findViewById(R.id.cover_imageview).getTag() != null) {
			mRecipeEntity.setCoverImgURL(findViewById(R.id.cover_imageview).getTag().toString());
		} else {
			Toast.makeText(mContext, R.string.biz_recipe_edit_nocover, Toast.LENGTH_SHORT).show();
			return;
		}
		
		EditText title = (EditText) findViewById(R.id.recipe_title_edittext);
		if(!TextUtils.isEmpty(title.getText().toString().trim())) {
			mRecipeEntity.setName(title.getText().toString().trim());
		} else {
			Toast.makeText(mContext, R.string.biz_recipe_edit_nocover, Toast.LENGTH_SHORT).show();
			title.setSelected(true);
			return;
		}
		
		EditText desc = (EditText) findViewById(R.id.desc);
		mRecipeEntity.setDesc(desc.getText().toString().trim());
		
		final LinearLayout materialLayout = (LinearLayout) findViewById(R.id.material_layout);

		for (int i = 0; i < materialLayout.getChildCount(); i++) {

			View view = materialLayout.getChildAt(i);
			EditText name = (EditText) view.findViewById(R.id.name);
			if(!TextUtils.isEmpty(name.getText().toString().trim())) {
				EditText remark = (EditText) view.findViewById(R.id.remark);
				Material material = new Material(name.getText().toString().trim(),
						remark.getText().toString().trim(), false);
				mRecipeEntity.getMaterials().add(material);
			}
		}
		if(mRecipeEntity.getMaterials().size() == 0) {
			Toast.makeText(mContext, R.string.biz_recipe_edit_nomaterial, Toast.LENGTH_SHORT).show();
			return;
		}
		
		final LinearLayout procedureLayout = (LinearLayout) findViewById(R.id.procedure_layout);
		
		for (int i = 0; i < procedureLayout.getChildCount(); i++) {
			
			View view = procedureLayout.getChildAt(i);
			
			EditText proceduDesc = (EditText) view.findViewById(R.id.desc);
			if(!TextUtils.isEmpty(proceduDesc.getText().toString().trim())) {
				
				String imageURL = "";
				if(view.findViewById(R.id.image).getTag() != null) {
					imageURL = view.findViewById(R.id.image).getTag().toString();
				}
				
				Procedure procedure = new Procedure(proceduDesc.getText().toString().trim(), imageURL);
				mRecipeEntity.getProcedures().add(procedure);
			}
		}
		
		if(mRecipeEntity.getProcedures().size() == 0) {
			Toast.makeText(mContext, R.string.biz_recipe_edit_noprocedure, Toast.LENGTH_SHORT).show();
		}
		
		EditText tips = (EditText) findViewById(R.id.recipe_tips_edittext);
		if(!TextUtils.isEmpty(tips.getText().toString().trim())) {
			mRecipeEntity.setTips(tips.getText().toString().trim());
		}
		
	
		new PostAsyncTast().execute();
	}
	
	public class PostAsyncTast extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			showUploadingProgressBar(true);
			super.onPreExecute();
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			String result = RecipeModel.postRecipe(mContext, mRecipeEntity);
			
			if (!TextUtils.isEmpty(result)) {
				try {
					JSONObject json = new JSONObject(result);
					int responseCode = json.optInt(Protocol.KEY_RESULT);
					if (responseCode == Protocol.VALUE_RESULT_OK) {
						return true;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return false;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			
			super.onPostExecute(result);
			if(isAdded()) {
				showUploadingProgressBar(false);
			}
			
			if(result) {
				
			} else {
				Toast.makeText(mContext, R.string.biz_recipe_edit_post_failed, Toast.LENGTH_SHORT).show();
			}
		}
		
	}
	
	public class UploadAsyncTask extends AsyncTask<Void, Void, String> {

		private ImageView mImageView;
		public UploadAsyncTask(ImageView imageView) {
			mImageView = imageView;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showUploadingProgressBar(true);
		}
		
		@Override
		protected String doInBackground(Void... params) {
			
			if(mImageView == null) {
				Logger.e(TAG, "mImageView is null");
				return null;
			}
			
			File file = ImgUtils.createBitmapFile("pic" + System.currentTimeMillis(),
					((BitmapDrawable)mImageView.getDrawable()).getBitmap());
			
			if(file != null) {
				if(mImageView.getId() == R.id.cover_imageview) {
					return RecipeModel.uploadRecipeCoverImage(mContext, file);
				} else {
					return RecipeModel.uploadRecipeStepImage(mContext, file);
				}
			}
			return null;
		}		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(isAdded()){
				if(mCurrentImageView != null) {
					mCurrentImageView.setTag(result);
				}
				showUploadingProgressBar(false);
				
				if(mImageView.getId() != R.id.cover_imageview) {
					LinearLayout parent = (LinearLayout) mCurrentImageView.getParent();
					
					if(TextUtils.isEmpty(result)) {
						parent.findViewById(R.id.button_upload).setVisibility(View.VISIBLE);
					} else {
						parent.findViewById(R.id.button_upload).setVisibility(View.GONE);
					}
				}
				
				if(TextUtils.isEmpty(result)) {
					Toast.makeText(mContext, R.string.biz_recipe_edit_upload_failed, Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(mContext, R.string.biz_recipe_edit_upload_ok, Toast.LENGTH_SHORT).show();
				}
				
			}
			
		}
		
		@Override
		protected void onCancelled() {
			super.onCancelled();
			if(isAdded()){
				showUploadingProgressBar(false);
			}
		}
		
	}
	
}
