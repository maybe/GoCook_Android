package com.m6.gocook.biz.recipe.recipe;

import java.security.PublicKey;
import java.security.spec.ECField;

import com.m6.gocook.R;
import com.m6.gocook.base.activity.BaseActivity;
import com.m6.gocook.base.entity.RecipeEntity;
import com.m6.gocook.base.fragment.BaseFragment;
import com.m6.gocook.base.fragment.FragmentHelper;
import com.m6.gocook.biz.account.RegisterFragment;
import com.m6.gocook.biz.common.PhotoPickDialogFragment;
import com.m6.gocook.biz.common.PhotoPickDialogFragment.OnPhotoPickCallback;

import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class RecipeEditFragment extends BaseFragment {

	private final String TAG = RecipeFragment.class.getCanonicalName();
	
	public static final String ARGUMENT_KEY_RECIPE_ID = "intent_key_recipe_id";

	private Context mContext;
	private View mRootView;
	private String mRecipeId;
	private RecipeEntity mRecipeEntity;
	private LayoutInflater mInflater;
	private ImageView mCurrentImageView;;
	
	public static void startInActivity(Context context, String recipeId) {
		Bundle argument = new Bundle();
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

		Bundle argument = getArguments();
		mRecipeId = argument.getString(RecipeFragment.ARGUMENT_KEY_RECIPE_ID);

		final EditText tipsEditText = (EditText) findViewById(R.id.recipe_tips_edittext);
		tipsEditText.setOnTouchListener(new MyEditTextOnTouchListener(8));
		
		final LinearLayout materialLayout = (LinearLayout) findViewById(R.id.material_layout);
		materialLayout.addView(mInflater.inflate(R.layout.adapter_recipe_edit_material_item, null));
		materialLayout.addView(mInflater.inflate(R.layout.adapter_recipe_edit_material_item, null));
		materialLayout.addView(mInflater.inflate(R.layout.adapter_recipe_edit_material_item, null));
		materialLayout.addView(mInflater.inflate(R.layout.adapter_recipe_edit_material_item, null));
		materialLayout.addView(mInflater.inflate(R.layout.adapter_recipe_edit_material_item, null));
		
		((Button) findViewById(R.id.material_addmore_button)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				materialLayout.addView(mInflater.inflate(R.layout.adapter_recipe_edit_material_item, null));
				
			}
		});
		
		final LinearLayout procedureLayout = (LinearLayout) findViewById(R.id.procedure_layout);
		procedureLayout.addView(mInflater.inflate(R.layout.adapter_recipe_edit_procedure_item, null));
		procedureLayout.addView(mInflater.inflate(R.layout.adapter_recipe_edit_procedure_item, null));
		procedureLayout.addView(mInflater.inflate(R.layout.adapter_recipe_edit_procedure_item, null));
		procedureLayout.addView(mInflater.inflate(R.layout.adapter_recipe_edit_procedure_item, null));
		
		((Button) findViewById(R.id.procedure_addmore_button)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				procedureLayout.addView(mInflater.inflate(R.layout.adapter_recipe_edit_procedure_item, null));
			}
		});
		
	}
}
