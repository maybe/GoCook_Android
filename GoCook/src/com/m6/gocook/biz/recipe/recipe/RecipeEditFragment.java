package com.m6.gocook.biz.recipe.recipe;

import java.util.ArrayList;

import com.m6.gocook.R;
import com.m6.gocook.base.activity.BaseActivity;
import com.m6.gocook.base.entity.RecipeEntity;
import com.m6.gocook.base.entity.RecipeEntity.Material;
import com.m6.gocook.base.entity.RecipeEntity.Procedure;
import com.m6.gocook.base.fragment.BaseFragment;
import com.m6.gocook.base.fragment.FragmentHelper;
import com.m6.gocook.base.view.ActionBar;
import com.m6.gocook.biz.common.PhotoPickDialogFragment;
import com.m6.gocook.biz.common.PhotoPickDialogFragment.OnPhotoPickCallback;
import com.m6.gocook.biz.recipe.RecipeModel;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class RecipeEditFragment extends BaseFragment {

	private final String TAG = RecipeFragment.class.getCanonicalName();
	
	public static final String ARGUMENT_KEY_ACTION = "argument_key_action";
	public static final String ARGUMENT_KEY_RECIPE_ID = "argument_key_recipe_id";

	private Context mContext;
	private View mRootView;
	private String mRecipeId;
	private RecipeEntity mRecipeEntity;
	private LayoutInflater mInflater;
	private ImageView mCurrentImageView;
	private Mode mMode;
	
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
	
	private OnPhotoPickCallback mPhotoPickCallback = new OnPhotoPickCallback() {
		
		@Override
		public void onPhotoPickResult(Uri uri, Bitmap bitmap) {
			if(mCurrentImageView != null) {
				mCurrentImageView.setImageBitmap(bitmap);
				if(bitmap != null) {
					mCurrentImageView.setImageBitmap(bitmap);
				} else if (uri != null) {
					mCurrentImageView.setImageURI(uri);
				} else {
					mCurrentImageView.setImageResource(R.drawable.register_photo);
				}
			}
			
		}
	};
	
	@Override
	public View onCreateFragmentView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		mInflater = inflater;
		mRootView = inflater.inflate(R.layout.fragment_recipe_edit, null, false);
		
		mRootView.findViewById(R.id.cover_imageview).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCurrentImageView = (ImageView) v;
				PhotoPickDialogFragment.startForResult(getChildFragmentManager(), mPhotoPickCallback);
			}
		});
		
		
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
		
		new AchieveRecipeTask().execute();
		
		ActionBar action = getActionBar();
		
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

		final EditText tipsEditText = (EditText) findViewById(R.id.recipe_tips_edittext);
		tipsEditText.setOnTouchListener(new MyEditTextOnTouchListener(8));

		final LinearLayout materialLayout = (LinearLayout) findViewById(R.id.material_layout);
		for (int i = 0; i < 5; i++) {
			materialLayout.addView(mInflater.inflate(R.layout.adapter_recipe_edit_material_item, null));
		}

		((Button) findViewById(R.id.material_addmore_button))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						materialLayout.addView(mInflater.inflate(R.layout.adapter_recipe_edit_material_item,
								null));

					}
				});

		final LinearLayout procedureLayout = (LinearLayout) findViewById(R.id.procedure_layout);
		for (int i = 0; i < 5; i++) {
			procedureLayout.addView(mInflater.inflate(
					R.layout.adapter_recipe_edit_procedure_item, null));
		}

		((Button) findViewById(R.id.procedure_addmore_button))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						procedureLayout.addView(mInflater.inflate(
								R.layout.adapter_recipe_edit_procedure_item,
								null));
					}
				});
		
	}
	
	private void applyData() {
		
		EditText titleEditText = (EditText) findViewById(R.id.recipe_title_edittext);
		titleEditText.setText(mRecipeEntity.getName());
		
		EditText intrEditText = (EditText) findViewById(R.id.recipe_introduction_edittext);
		intrEditText.setText(mRecipeEntity.getDesc());
		
		EditText tipEditText = (EditText) findViewById(R.id.recipe_tips_edittext);
		tipEditText.setText(mRecipeEntity.getTips());
		
		applyMaterial();
		applyProcedure();
		
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
			View view = mInflater.inflate(R.layout.adapter_recipe_edit_material_item, null);
			
			applyDataToMaterialItem(view, material);
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
			View view = mInflater.inflate(R.layout.adapter_recipe_edit_procedure_item, null);
			
			applyDataToProcedurelItem(view, procedure);
		}
		return null;
	}
	
	private void applyDataToProcedurelItem(View view, Procedure procedure) {
		
		EditText desc = (EditText) view.findViewById(R.id.desc);
		desc.setText(procedure.getDesc());

		ImageView image = (ImageView) view.findViewById(R.id.image);
		mImageFetcher.loadImage(procedure.getImageURL(), image);
	}
	
	private class AchieveRecipeTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			showProgress(true);
		}

		@Override
		protected Void doInBackground(Void... params) {

			mRecipeEntity = RecipeModel.getRecipe(getActivity(), mRecipeId);
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
}
