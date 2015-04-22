package com.cjj.nices.adapter;

import static com.norbsoft.typefacehelper.TypefaceHelper.typeface;

import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cjj.nices.activity.R;
import com.cjj.nices.model.NicesApplication;

public class NewsListSwipeMenuListViewAdapter extends BaseAdapter{
	private Context context;
	private List<String> list;
	
	public NewsListSwipeMenuListViewAdapter(Context context,List<String> list){
		this.context = context;
		this.list = list;
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public String getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getViewTypeCount() {
		// menu type count
		return 3;
	}

	@Override
	public int getItemViewType(int position) {
		// current menu type
		return position % 3;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = View.inflate(context,
					R.layout.item_news_list, null);
			new ViewHolder(convertView);
		}
		ViewHolder holder = (ViewHolder) convertView.getTag();
		String item = getItem(position);
		holder.tv_name.setText(item);
		  typeface(holder.tv_name,NicesApplication.nicesApplication.getKatongTypeface());
		return convertView;
	}

	class ViewHolder {
		TextView tv_name;

		public ViewHolder(View view) {
			tv_name = (TextView) view.findViewById(R.id.tv_name);
			view.setTag(this);
		}
	}
}
