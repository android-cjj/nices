package com.cjj.nices.fragment;

import static com.norbsoft.typefacehelper.TypefaceHelper.typeface;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.cjj.nices.activity.MainActivity;
import com.cjj.nices.activity.NewsDetailActivity;
import com.cjj.nices.activity.R;
import com.cjj.nices.adapter.NewsListAdapter;
import com.cjj.nices.constant.Constants;
import com.cjj.nices.dao.UserDao;
import com.cjj.nices.db.NicesDBHelper;
import com.cjj.nices.model.NicesApplication;
import com.cjj.nices.model.NicesNewsListEntity;
import com.cjj.nices.model.NicesNewsListEntity.NewsListEntity;
import com.cjj.nices.util.ListUtils;
import com.cjj.volley.VolleyError;
import com.cjj.volley.callback.CallBackDataListener;
import com.cjj.volley.me.CjjVolley;
import com.cjj.volley.utils.LogUtil;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
/**
 * 主页fragment类
 * @author cjj
 *
 */
@SuppressLint("NewApi")
public class HomeFragment extends Fragment implements OnScrollListener,OnItemClickListener,OnMenuItemClickListener, OnRefreshListener{
	private SwipeMenuListView mSwipeMenuListView;
	private SwipeRefreshLayout mSwipeLayout;
	private NewsListAdapter mAdapter;
	private Calendar mCalendar;
	private String todayTime;
	private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
	private View loadmoreView;
	private ArrayList<NewsListEntity> mArrayList_NewsListEntity = new ArrayList<NicesNewsListEntity.NewsListEntity>();
	private boolean isRefresh = false;
	
	public static HomeFragment newInstance() {
		HomeFragment fragment = new HomeFragment();
//		Bundle args = new Bundle();
//		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
//		fragment.setArguments(args);
		return fragment;
	}
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		this.setHasOptionsMenu(true);
		mCalendar = Calendar.getInstance();
		mCalendar.add(Calendar.DAY_OF_YEAR, 1);
		todayTime =  mSimpleDateFormat.format(mCalendar.getTime());
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_home, null);
		loadmoreView = inflater.inflate(R.layout.view_load_more, null);
		typeface(loadmoreView,NicesApplication.nicesApplication.getKatongTypeface());
		return v;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.main, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
 
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    int id = item.getItemId();
        if (id == R.id.action_refresh) {
    		mSwipeLayout.setRefreshing(true);
    		/**
    		 * 获取网上数据
    		 */
    		isRefresh = true;
    		
    		getNewsListGsonDataFromNet(false,todayTime);
    		
            return true;
        }
		return super.onOptionsItemSelected(item);
	}
	
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		mSwipeMenuListView = (SwipeMenuListView) view.findViewById(R.id.swipe_list_view);
		// Close Interpolator
		mSwipeMenuListView.setCloseInterpolator(new BounceInterpolator());
		mSwipeMenuListView.addFooterView(loadmoreView);
		
		mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
		mSwipeLayout.setOnRefreshListener(this);
		mSwipeLayout.setColorSchemeResources(android.R.color.holo_red_light,
				android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_blue_bright
				);
		super.onViewCreated(view, savedInstanceState);
	}
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
					createMenu(menu);
			}
		};
		// set creator
		mSwipeMenuListView.setMenuCreator(creator);
		mSwipeMenuListView.setOnItemClickListener(this);
		mSwipeMenuListView.setOnMenuItemClickListener(this);
		mSwipeMenuListView.setOnScrollListener(this);
		
		/**
		 * 获取本地数据
		 */
		getNewsListDataFromLocal(todayTime);
		
		/**
		 * 获取网上数据
		 */
		getNewsListGsonDataFromNet(false,todayTime);
		
		hideFootView();
		
		super.onActivityCreated(savedInstanceState);
	}
	
	private void getNewsListDataFromLocal(String todayTime) {
		new LocalNewsListAsyncTask().execute(todayTime);
	}


	private void getNewsListGsonDataFromNet(boolean oldContentFlag,String time) {
		UserDao.getInstance().getNewsListGsonDataFromNet(oldContentFlag,time,new CallBackDataListener() {
			@Override
			public void error(VolleyError error) {
				toastMsg("网络错误");
//				toastMsg(VolleyErrorHelper.getMessage(error, getActivity()));
				setReSetting();
			}
			
			@Override
			public void callBack(Object data) {
				if(data instanceof NicesNewsListEntity)
				{
					NicesNewsListEntity nicesNewsListEntity = (NicesNewsListEntity) data;
					
					if(mAdapter==null)
					{
						mArrayList_NewsListEntity =  nicesNewsListEntity.stories;
						mAdapter = new NewsListAdapter(getActivity(), mArrayList_NewsListEntity);
						mSwipeMenuListView.setAdapter(mAdapter);
					}else
					{
						if(isLoadMroeDataFlag)
						{
							LogUtil.LOgMsg_W("cjj isloadmore");
							mArrayList_NewsListEntity.addAll(nicesNewsListEntity.stories);
							mAdapter.updateData(mArrayList_NewsListEntity);
						}else{
							if(isRefresh)
							showCrouton("刷新成功", Style.TRANSPARENT,  Configuration.DEFAULT);
							mArrayList_NewsListEntity = nicesNewsListEntity.stories;
							mAdapter.updateData(mArrayList_NewsListEntity);
							mListViewPreLast = 0;
						}
						
						
					}
				}
				
				setReSetting();
				
			}
		});
	}
	
	
	public void setReSetting()
	{
		isRefresh = false;
		mSwipeLayout.setRefreshing(false);
		isLoadMroeDataFlag = false;
		hideFootView();
	}
	

	@Override
	public void onDestroyView() {
		CjjVolley.cancelAll(Constants.TAG_REQUEST_GSON_NEWS_LIST);
		super.onDestroyView();
	}
	
	private void createMenu(SwipeMenu menu) {
		SwipeMenuItem item1 = new SwipeMenuItem(
				getActivity().getApplicationContext());
		item1.setBackground(new ColorDrawable(Color.rgb(0xE5, 0x18,
				0x5E)));
		item1.setWidth(dp2px(90));
		item1.setIcon(R.drawable.ic_action_share);
		menu.addMenuItem(item1);
		SwipeMenuItem item2 = new SwipeMenuItem(
				getActivity().getApplicationContext());
		item2.setBackground(new ColorDrawable(Color.rgb(0xE5, 0xE0,0x3F)));
		item2.setWidth(dp2px(90));
		item2.setIcon(R.drawable.ic_action_important);
		menu.addMenuItem(item2);
	}
	
	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}

	@Override
	public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
		NewsListEntity entity = (NewsListEntity) mAdapter.getItem(position);
		switch (index) {
		case 0:
			MainActivity.instance.showShare(entity.title);
			break;
		case 1:
			favoriteDao(entity, entity.id);
			break;
		}
		return false;
	}
	
	private void favoriteDao(NewsListEntity entity, long news_id) {
		if(NicesDBHelper.queryIsFavorite(String.valueOf(news_id))){
			Toast.makeText(getActivity(), "已收藏", Toast.LENGTH_SHORT).show();
		}else{
			String title = entity.title;
			String image = entity.images.get(0);
			String id = String.valueOf(news_id);
			
			LogUtil.LogMsg_I(title);
			LogUtil.LogMsg_I(image);
			LogUtil.LogMsg_I(id);

			if (!TextUtils.isEmpty(id) && !TextUtils.isEmpty(title)
					&& !TextUtils.isEmpty(image) ) {
				NicesDBHelper.insertFavoriteContent(id, title, image,"cjj");
				Toast.makeText(getActivity(), "添加收藏成功", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getActivity(), "添加收藏失败", Toast.LENGTH_SHORT).show();
			}
		}

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
	
	@Override
	public void onRefresh() {
		isRefresh = true;

		/**
		 * 获取网上数据
		 */
		getNewsListGsonDataFromNet(false,todayTime);
	}
	
	private void toastMsg(String str)
	{
		Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * 获取本地数据 异步处理
	 */
	public class LocalNewsListAsyncTask extends AsyncTask<String, Integer, ArrayList<NewsListEntity>>{

		@Override
		protected ArrayList<NewsListEntity> doInBackground(String... params) {
			ArrayList<NewsListEntity> list = NicesDBHelper.queryNewsList(params[0]);
			Log.i("cjj", "today = "+params[0]);
			if (ListUtils.isEmpty(list)) {
				Calendar calendar = Calendar.getInstance();
				
				String yesterday = mSimpleDateFormat.format(calendar.getTime());
				Log.i("cjj", "yesterday = "+yesterday);
				list = NicesDBHelper.queryNewsList(yesterday);
			} 
			
			if (ListUtils.isEmpty(list)) {
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.DAY_OF_YEAR, -1);
				
				String dayBeforeYesterday = mSimpleDateFormat.format(calendar.getTime());
				Log.i("cjj", "daybeforeyesterday = "+dayBeforeYesterday);
				list = NicesDBHelper.queryNewsList(dayBeforeYesterday);
			}
			return list;
		}
		
		@Override
		protected void onPostExecute(ArrayList<NewsListEntity> result) {
			super.onPostExecute(result);
			
			if(mAdapter == null)
			{
				if(result != null)
				{
					mArrayList_NewsListEntity = result;
					mAdapter = new NewsListAdapter(getActivity(), mArrayList_NewsListEntity);
					mSwipeMenuListView.setAdapter(mAdapter);
				}
			}else
			{
				if(result!=null)
				{
					mArrayList_NewsListEntity = result;
					mAdapter.updateData(mArrayList_NewsListEntity);
				}
			}
			
		}
		
	}
	
	/**
	 * 获取以往新闻
	 */
	public class OldNewListAsyncTask extends AsyncTask<String, Integer,ArrayList<NewsListEntity>>
	{
		String time;
		public OldNewListAsyncTask(String time){
			this.time = time;
		};

		@Override
		protected ArrayList<NewsListEntity> doInBackground(String... params) {
			String time = params[0];
			ArrayList<NewsListEntity> list_old = NicesDBHelper.queryNewsList(time);
			return list_old;
		}
		
		@Override
		protected void onPostExecute(ArrayList<NewsListEntity> result) {
			super.onPostExecute(result);
			
			if(mArrayList_NewsListEntity!=null)
			{
//				if(ListUtils.isEmpty(result))
//				{
//					if(result!=null)
//					mArrayList_NewsListEntity.addAll(result);
//					mAdapter.updateData(mArrayList_NewsListEntity);
//					hideFootView();
//				}else{
					loadMoreData(time);
//				}
			}else{
				LogUtil.LOgMsg_W("mArrayList_NewsListEntity===>"+time);
			}
		}
		
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		
	}


	/**
	 * 处理listView加载更多
	 * @param view
	 * @param firstVisibleItem
	 * @param visibleItemCount
	 * @param totalItemCount
	 */
	private int mListViewPreLast = 0;
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		final int lastItem = firstVisibleItem + visibleItemCount;
		
		if (lastItem == totalItemCount) {
			if (mListViewPreLast != lastItem) { /**防止多次调用*/
				
				mCalendar.add(Calendar.DAY_OF_YEAR, -1);
				
				String formatedDate = mSimpleDateFormat.format(mCalendar.getTime());
				
//				toastMsg("more");
				
				showFootView();
				
				new OldNewListAsyncTask(formatedDate).execute(formatedDate);
				
				mListViewPreLast = lastItem;
				
			}
		}
	}
	
	private boolean isLoadMroeDataFlag = false;
	public void loadMoreData(String time)
	{
		isLoadMroeDataFlag = true;
		getNewsListGsonDataFromNet(true,time);
	}
	
	/**
	 * show footview
	 */
	private void showFootView()
	{
		if(loadmoreView!=null)
		{
			loadmoreView.setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * hide footView
	 */
	private void hideFootView()
	{
		if(loadmoreView!=null)
		{
			loadmoreView.setVisibility(View.GONE);
		}
	}
	
	
	private void showCrouton(String croutonText, Style croutonStyle,
			Configuration configuration) {
		Crouton.makeText(getActivity(), croutonText, croutonStyle).show();
	}

	
}
