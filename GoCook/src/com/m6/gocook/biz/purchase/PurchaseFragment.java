package com.m6.gocook.biz.purchase;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.m6.gocook.R;
import com.m6.gocook.base.fragment.OnActionBarEventListener;
import com.m6.gocook.biz.main.MainActivityHelper;

public class PurchaseFragment extends Fragment implements
		OnActionBarEventListener {
	
	private View mFragmentContainerView;
	private ContentObserver mPurchaseRecipeObserver = null;
	private Cursor mPurchaseRecipeCursor = null;
	private Class<? extends Fragment> mCurrentFragmentClass = null;
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_purchase_tabcontent, null);
		mFragmentContainerView = view.findViewById(R.id.purchase_tabcontent);
		return view;
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		if(mPurchaseRecipeCursor == null) {
			mPurchaseRecipeCursor = PurchaseListModel.getRecipePurchaseCursor(getActivity());
			mPurchaseRecipeObserver = new ContentObserver(new Handler()) {
				@Override
		        public boolean deliverSelfNotifications() {
		            return true;
		        }

		        @Override
		        public void onChange(boolean selfChange) {
		        	mPurchaseRecipeCursor.requery();
		        	if(mPurchaseRecipeCursor.getCount() > 0) {
		            	mFragmentContainerView.setVisibility(View.VISIBLE);
		            } else {
		            	mFragmentContainerView.setVisibility(View.GONE);
		            }
		        }
			};
			mPurchaseRecipeCursor.registerContentObserver(mPurchaseRecipeObserver);
		}
		
		if(mPurchaseRecipeCursor.getCount() > 0) {
			mFragmentContainerView.setVisibility(View.VISIBLE);
		} else {
			mFragmentContainerView.setVisibility(View.GONE);
			return;
		}
		
		if(mCurrentFragmentClass == null) {
			launchFragment(PurchaseListFragment.class);
		}
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		MainActivityHelper.registerOnActionBarEventListener(this);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		MainActivityHelper.unRegisterOnActionBarEventListener(this);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(mPurchaseRecipeCursor != null) {
			mPurchaseRecipeCursor.close();
		}
	}

	@Override
	public void OnFragmentSwitch(Class<? extends Fragment> fragment) {
		launchFragment(fragment);
	}
	
	@Override
	public void OnActionBarClick(View view, int id) {
		
		switch (id) {
		case R.id.actionbar_left_button:
			if(mPurchaseRecipeCursor != null && mPurchaseRecipeCursor.getCount() > 0) {
				MyAlertDialogFragment conFragment = new MyAlertDialogFragment();
				conFragment.show(getChildFragmentManager(), MyAlertDialogFragment.class.getName());
			}
			break;
		default:
			break;
		}
	}
	
	private void launchFragment(Class<? extends Fragment> fragment) {
	
		if(fragment == null) {
			return;
		}
		
		FragmentManager fm = getChildFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		
		Fragment curF;
		if(mCurrentFragmentClass != null &&
			(curF = fm.findFragmentByTag(mCurrentFragmentClass.getName())) != null) {
			ft.hide(curF);
		}
		Fragment f = fm.findFragmentByTag(fragment.getName());
		if(f == null) {
			f = Fragment.instantiate(getActivity(), fragment.getName());
			ft.add(R.id.purchase_tabcontent, f, fragment.getName());
		} else {
			ft.show(f);
		}
		
		ft.commit();
		fm.executePendingTransactions();
		
		mCurrentFragmentClass = fragment;
	}
	
	
	public static class MyAlertDialogFragment extends DialogFragment {

	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        return new AlertDialog.Builder(getActivity())
	                .setTitle(R.string.biz_recipe_purchase_clear_tip_title)
	                .setMessage(R.string.biz_recipe_purchase_clear_tip_content)
	                .setPositiveButton(R.string.biz_recipe_purchase_clear_positivebutton,
	                    new DialogInterface.OnClickListener() {
	                        public void onClick(DialogInterface dialog, int whichButton) {
	                        	PurchaseListModel.removeAll(getActivity());
	                        }
	                    }
	                )
	                .setNegativeButton(R.string.biz_recipe_purchase_clear_nagetivebutton,
	                    new DialogInterface.OnClickListener() {
	                        public void onClick(DialogInterface dialog, int whichButton) {

	                        }
	                    }
	                )
	                .create();
	    }
	}

}
