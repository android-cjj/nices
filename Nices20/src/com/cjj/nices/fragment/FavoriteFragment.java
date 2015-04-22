package com.cjj.nices.fragment;

import static com.norbsoft.typefacehelper.TypefaceHelper.typeface;


import java.util.ArrayList;

import net.youmi.android.banner.AdSize;
import net.youmi.android.banner.AdView;

import org.litepal.crud.DataSupport;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.support.v7.internal.widget.AdapterViewCompat.OnItemClickListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import in.srain.cube.views.GridViewWithHeaderAndFooter;
import uk.me.lewisdeane.ldialogs.CustomDialog;
import com.cjj.custom.CustomPopWindow;
import com.cjj.custom.CustomPopWindow.ItemSelectListener;
import com.cjj.nices.activity.NewsDetailActivity;
import com.cjj.nices.activity.R;
import com.cjj.nices.adapter.NewsListAdapter;
import com.cjj.nices.db.FavoriteTableModel;
import com.cjj.nices.db.NicesDBHelper;
import com.cjj.nices.model.NicesApplication;
import com.cjj.nices.model.NicesNewsListEntity.NewsListEntity;
/**
 * favorite fragment类
 * @author cjj
 *
 */
public class FavoriteFragment extends Fragment implements android.widget.AdapterView.OnItemClickListener{
	private GridViewWithHeaderAndFooter gv_favorite;
	private NewsListAdapter mAdapter;
	private TextView tv_tip;
	private View view_deve;
	private boolean isEmpty = false;
	
	
	public static FavoriteFragment newInstance() {
		FavoriteFragment fragment = new FavoriteFragment();
		return fragment;
	}
 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setHasOptionsMenu(true);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.delete_favorite, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.delete)
		{
			if(isEmpty)
			{
				toastMsg("您还未收藏哦");
			}else{
				showPopWindow();
			}
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void showPopWindow()
	{
		CustomPopWindow popWindow = new CustomPopWindow(getActivity());
		typeface(popWindow.getLL(), NicesApplication.nicesApplication.getKatongTypeface());
		popWindow.showPopupWindow(view_deve);
		popWindow.setItemSelectListener(new ItemSelectListener() {
			
			@Override
			public void selectItem() {
				mAdapter.setIsDeleteFlag(true);
			}
			
			@Override
			public void selectAll() {
				showLDailog();
			}
		});
	}
	
	protected void showLDailog() {
//		// Create the builder with required paramaters - Context, Title, Positive Text
//		CustomDialog.Builder builder = new CustomDialog.Builder(Context context, String title, String positiveText);
//
//		// Now we can any of the following methods.
//		builder.content(String content);
//		builder.negativeText(String negativeText);
//		builder.darkTheme(boolean isDark);
//		builder.typeface(Typeface typeface);
//		builder.titleTextSize(int size);
//		builder.contentTextSize(int size);
//		builder.buttonTextSize(int size);
//		builder.titleAlignment(Alignment alignment); // Use either Alignment.LEFT, Alignment.CENTER or Alignment.RIGHT
//		builder.titleColor(String hex); // int res, or int colorRes parameter versions available as well.
//		builder.contentColor(String hex); // int res, or int colorRes parameter versions available as well.
//		builder.positiveColor(String hex); // int res, or int colorRes parameter versions available as well.
//		builder.negativeColor(String hex); // int res, or int colorRes parameter versions available as well.
//		builder.positiveBackground(Drawable drawable); // int res parameter version also available.
//		builder.rightToLeft(boolean rightToLeft); // Enables right to left positioning for languages that may require so.
//
//		// Now we can build the dialog.
//		CustomDialog customDialog = builder.build();
//
//		// Show the dialog.
//		customDialog.show();
		
		CustomDialog.Builder builder = new CustomDialog.Builder(getActivity(), "温馨提示", "确定");
		// Now we can any of the following methods.
		builder.content("您确定清空所有收藏记录吗？");
		builder.negativeText("取消");
		// Now we can build the dialog.
		CustomDialog customDialog = builder.build();
//		// Show the dialog.
		customDialog.setClickListener(new CustomDialog.ClickListener() {
            @Override
            public void onConfirmClick() {
            	DataSupport.deleteAll(FavoriteTableModel.class);
            	mAdapter.updateData(null);
            	mAdapter.notifyDataSetChanged();
            	tv_tip.setVisibility(View.VISIBLE);
            	isEmpty = true;
            }

            @Override
            public void onCancelClick() {
            	
            }
        });
		customDialog.show();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_favorite, null);
		return v;
		
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		gv_favorite = (GridViewWithHeaderAndFooter) view.findViewById(R.id.gv_favorite);
		gv_favorite.setOnItemClickListener(this);
		tv_tip = (TextView) view.findViewById(R.id.tv_tip);
		typeface(tv_tip,NicesApplication.nicesApplication.getKatongTypeface());
		view_deve = view.findViewById(R.id.view_deve);
		
//		 将广告条adView添加到需要展示的layout控件中
		 LinearLayout adLayout = (LinearLayout) view.findViewById(R.id.adLayout);
		 AdView adView = new AdView(getActivity(), AdSize.FIT_SCREEN);
		 adLayout.addView(adView);

		super.onViewCreated(view, savedInstanceState);
	}
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		new FavoriteDataAsyncTask().execute();
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}
	
	@SuppressLint("NewApi") 
	private class FavoriteDataAsyncTask extends AsyncTask<Void, Void,ArrayList<NewsListEntity>>
	{

		@Override
		protected ArrayList<NewsListEntity> doInBackground(Void... params) {
			ArrayList<NewsListEntity> list = NicesDBHelper.queryAllData();
			return list;
		}
		
		@Override
		protected void onPostExecute(ArrayList<NewsListEntity> result) {
			super.onPostExecute(result);
			if(result!=null&&result.size()>0)
			{
				isEmpty = false;
				if(mAdapter == null)
				{
					mAdapter = new NewsListAdapter(getActivity(), result, true);
					gv_favorite.setAdapter(mAdapter);
				}else
				{
					mAdapter.updateData(result);
				}
			}else
			{
				isEmpty = true;
				tv_tip.setVisibility(View.VISIBLE);
			}
			
		}
		
	}
	
	private void toastMsg(String str)
	{
		Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
	}

	

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		NewsListEntity entity = (NewsListEntity) mAdapter.getItem(position);
		if (entity == null)
			return;
		
		Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
		intent.putExtra("id", entity.id);
		intent.putExtra("newsEntity", entity);
		startActivity(intent);
	}
	
	
}
