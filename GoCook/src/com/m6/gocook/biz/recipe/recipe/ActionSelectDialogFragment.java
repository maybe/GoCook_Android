package com.m6.gocook.biz.recipe.recipe;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.m6.gocook.R;
import com.m6.gocook.biz.common.PhotoPickDialogFragment;
import com.m6.gocook.biz.recipe.recipe.RecipeEditFragment.Mode;


public class ActionSelectDialogFragment extends DialogFragment {

	private String mRecipeId;
	
	public static ActionSelectDialogFragment newInstance() {
		return new ActionSelectDialogFragment();
	}
	
	public static boolean startFragment(FragmentManager fragmentManager, String recipeId) {
		if(fragmentManager == null) {
			return false;
		}
		FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment prev = fragmentManager.findFragmentByTag(ActionSelectDialogFragment.class.getName());
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        ActionSelectDialogFragment dialog = ActionSelectDialogFragment.newInstance();
        dialog.setRecipeId(recipeId);
		dialog.show(ft, ActionSelectDialogFragment.class.getName());
		return true;
	}
	
	private void setRecipeId(String recipeId) {
		mRecipeId = recipeId;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		
		dialog.setCanceledOnTouchOutside(true);
		Window window = dialog.getWindow();
		window.getAttributes().dimAmount = 0.7f;
		window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		window.setGravity(Gravity.CENTER);
		window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
		window.setBackgroundDrawableResource(android.R.color.transparent);
		
		return dialog;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_action_select, container, false);
		
		view.findViewById(R.id.edit).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mRecipeId != null) {
					RecipeEditFragment.startInActivity(getActivity(), Mode.RECIPE_EDIT, mRecipeId);
				}
			}
		});
		
		view.findViewById(R.id.delete).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setMessage(getResources().getString(R.string.biz_recipe_edit_title_isdelete))
				       .setCancelable(false)
				       .setPositiveButton(R.string.biz_recipe_edit_title_deleteok, new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				                
				           }
				       })
				       .setNegativeButton(R.string.biz_recipe_edit_title_deleteno, new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				                dialog.cancel();
				           }
				       });
				AlertDialog alert = builder.create();
				alert.show();
				
			}
		});
		return view;
	}
	
}
