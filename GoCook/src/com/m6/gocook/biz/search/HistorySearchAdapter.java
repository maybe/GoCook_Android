package com.m6.gocook.biz.search;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.m6.gocook.R;

public class HistorySearchAdapter extends BaseAdapter{

	private List<String> mList;
	private LayoutInflater mInflater;
	
	public HistorySearchAdapter(Context context, List<String> list){
		mList = list;
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(null == convertView){
			convertView = mInflater.inflate(R.layout.fragment_search_history_item, null);
			HolderView holder = new HolderView();
			holder.historyText = (TextView) convertView;
			convertView.setTag(holder);
		}
		HolderView holder = (HolderView) convertView.getTag();
		
		String historyStr = (String) getItem(position);
		holder.historyText.setText(historyStr);
		return convertView;
	}
	
	class HolderView{
		TextView historyText;
	}

}
