package com.cjj.nices.activity;

import static com.norbsoft.typefacehelper.TypefaceHelper.typeface;

import com.cjj.nices.constant.Constants;
import com.cjj.nices.db.NicesDBHelper;
import com.cjj.nices.fragment.DetailFragment;
import com.cjj.nices.model.NicesApplication;
import com.cjj.nices.model.NicesNewsListEntity.NewsListEntity;
import com.norbsoft.typefacehelper.ActionBarHelper;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;

/**
 * 新闻详情
 * 
 * @author cjj
 * 
 */
@SuppressLint("NewApi") 
public class NewsDetailActivity extends ActionBarActivity {
	private long news_id ;
	private NewsListEntity entity; 
	private GestureDetector mGestureDetector;
	/**收藏的标志*/
	private boolean isFavoriteFlag = false;
	private MenuItem item;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		initActionBar();
		mGestureDetector = new GestureDetector(this, mGestureListener);
		if(savedInstanceState == null)
		{
			news_id = getIntent().getLongExtra("id", 0);
			entity = (NewsListEntity) getIntent().getSerializableExtra("newsEntity");
			
		}else
		{
			entity = (NewsListEntity) savedInstanceState.getSerializable(Constants.NEWS_ENTIRY);
			news_id = savedInstanceState.getLong(Constants.NEWS_ID);
		}
		
		Fragment fragment = DetailFragment.newInstance(news_id);
		getSupportFragmentManager().beginTransaction().replace(R.id.fl_container, fragment).commit();
		
		isFavoriteFlag = NicesDBHelper.queryIsFavorite(String.valueOf(news_id));
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.favorite, menu);
		 item = menu.findItem(R.id.favorite);
		if(isFavoriteFlag)
		{
			item.setIcon(R.drawable.favorite_press);
			item.setTitle("已收藏");
		}else
		{
			item.setIcon(R.drawable.favorite_nor);
			item.setTitle("取消收藏");
		}
		
		return super.onCreateOptionsMenu(menu);
	}
	
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putLong(Constants.NEWS_ID, news_id);
		outState.putSerializable(Constants.NEWS_ENTIRY, entity);
		super.onSaveInstanceState(outState);
	}

	private void initActionBar() {
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		// 显示logo
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		// 可以自定义actionbar
		getSupportActionBar().setDisplayShowCustomEnabled(true);
//		getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));
		ActionBarHelper.setTitle(getSupportActionBar(), typeface(this, R.string.app_name,NicesApplication.nicesApplication.getHuayunTypeface(),Typeface.NORMAL));
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case android.R.id.home:
			this.finish();
			break;
			
		case R.id.favorite:
			
			favoriteDao();
			
			break;
			
		case R.id.share:
			
			MainActivity.instance.showShare(entity.title);			
			
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void favoriteDao() {
		
		if(isFavoriteFlag)
		{
			NicesDBHelper.deleteFavoriteById(String.valueOf(news_id));
			item.setIcon(R.drawable.favorite_nor);
			item.setTitle("取消收藏");
			isFavoriteFlag = false;
			Toast.makeText(NewsDetailActivity.this,"已取消收藏", Toast.LENGTH_SHORT).show();
		}else
		{
			String title = entity.title;
			String image = entity.images.get(0);
			String id = String.valueOf(news_id);
			
			if(!TextUtils.isEmpty(id)&&!TextUtils.isEmpty(title)&&!TextUtils.isEmpty(image))
			{
				NicesDBHelper.insertFavoriteContent(id, title, image, "cjj");
				Toast.makeText(NewsDetailActivity.this,"添加收藏成功", Toast.LENGTH_SHORT).show();
				item.setIcon(R.drawable.favorite_press);
				item.setTitle("已收藏");
				isFavoriteFlag = true;
			}else
			{
				Toast.makeText(NewsDetailActivity.this,"添加收藏失败", Toast.LENGTH_SHORT).show();
			}
			
		}
		
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		try
		{
			mGestureDetector.onTouchEvent(ev);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return super.dispatchTouchEvent(ev);
	}
	
	/**
	 * 手势处理
	 */
	// 手指在屏幕滑动，X轴最小变化值
	private static final int FLING_MIN_DISTANCE_X = 150;

	// 手指在屏幕滑动，Y轴最小变化值
	private static final int FLING_MIN_DISTANCE = 10;

	// 手指在屏幕滑动，最小速度
	private static final int FLING_MIN_VELOCITY = 1;
	public SimpleOnGestureListener mGestureListener = new SimpleOnGestureListener()
	{
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			boolean isXWell = Math.abs(e2.getX() - e1.getX()) < FLING_MIN_DISTANCE_X ? true : false;
			if (isXWell && e1.getY() - e2.getY() > FLING_MIN_DISTANCE && Math.abs(velocityY) > FLING_MIN_VELOCITY) {
				getSupportActionBar().hide();
			} else if (isXWell && e2.getY() - e1.getY() > FLING_MIN_DISTANCE && Math.abs(velocityY) > FLING_MIN_VELOCITY) {
				getSupportActionBar().show();
			}
			
			if(!isXWell)
			{
				NewsDetailActivity.this.finish();
			}
			return false;
		};
	};
}
