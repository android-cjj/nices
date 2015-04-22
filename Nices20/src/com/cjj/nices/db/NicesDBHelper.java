package com.cjj.nices.db;

import java.util.ArrayList;
import java.util.List;

import org.litepal.crud.DataSupport;

import android.database.Cursor;
import android.text.TextUtils;

import com.cjj.nices.model.NicesNewsListEntity;
import com.cjj.nices.model.NicesNewsListEntity.NewsListEntity;
import com.cjj.nices.util.ListUtils;
import com.cjj.volley.utils.LogUtil;
import com.google.gson.Gson;

/**
 * 数据库帮助类
 * @author cjj
 *
 */
public class NicesDBHelper {
	
	/**
	 * 查询数据
	 * @param time
	 * @return
	 */
	public static ArrayList<NewsListEntity> queryNewsList(String time)
	{
		ArrayList<NewsListEntity> data = null;
//		List<NewsListModel> list = DataSupport.findAll(NewsListModel.class);
		List<NewsListModel> list = DataSupport.where("time=?",time).find(NewsListModel.class);
		if(!ListUtils.isEmpty(list))
		{
			String content = list.get(0).getContent();
			LogUtil.LogMsg_I("query=====>"+content);
			if(!TextUtils.isEmpty(content))
			{
				LogUtil.LogMsg_I("query=====>find");
				Gson gson = new Gson();
				data = gson.fromJson(content, NicesNewsListEntity.class).stories;
				return data;
			}
		}
		
		return null;
	}
	
	
	/**
	 * 插入 or 更新数据库
	 * @param time
	 * @param content
	 */
	public static void insertOrUpdateNewsList(String time,String content)
	{
		if(TextUtils.isEmpty(content))return;
		if(isContentExist(time))
		{
			updateNewsList(time,content);
			LogUtil.LogMsg_I("iscontnt exist ture  update Success ");
		}else
		{
			LogUtil.LogMsg_I("iscontnt exist false insert Success");
			insertNewsList(time,content);
		}
	}

	/**
	 * 更新数据
	 * @param time
	 * @param content
	 */
	private static void updateNewsList(String time, String content) {
		NewsListModel model = new NewsListModel();
		model.setTime(time);
		model.setContent(content);
		model.updateAll("time=?",time);
		
		LogUtil.LogMsg_I("update =" +model.getContent());
	}

	/**
	 * 插入数据
	 * @param time
	 * @param content
	 */
	private static void insertNewsList(String time, String content) {
		NewsListModel model = new NewsListModel();
		model.setTime(time);
		model.setContent(content);
		boolean is = model.save();
		
		LogUtil.LOgMsg_W("insert = "+ is);
	}
	
	

	/**
	 * 判断内容是否存在
	 * @param time
	 * @return
	 */
	private static boolean isContentExist(String time) {
		Cursor cursor = DataSupport.findBySQL("select * from NewsListModel where time="+time);
		if(cursor!=null&&cursor.getCount()>0&&cursor.moveToFirst())
		{
			String content = cursor.getString(cursor.getColumnIndex("time"));
			if(!TextUtils.isEmpty(content))
			{
				return true;
			}
		}
		
		cursor.close();
		return false;
	}
	
	/********************************************  收藏的操作    ********************************************************/
	
	/**
	 * query 
	 * @param id
	 * @return
	 */
	public static boolean queryIsFavorite(String id)
	{
		Cursor cursor = DataSupport.findBySQL("select * from FavoriteTableModel where new_id="+id);
		
		
		if(cursor != null && cursor.getCount()>0 && cursor.moveToFirst())
		{
			LogUtil.LOgMsg_W("query true");
			return true;
		}
		
		cursor.close();
		LogUtil.LOgMsg_W("query false");
		return false;
	}
	
	/**
	 * 保存的收藏的content
	 * @param id
	 * @param title
	 * @param image
	 * @param url
	 */
	public static void insertFavoriteContent(String id,String title,String image,String url)
	{
		FavoriteTableModel model = new FavoriteTableModel();
		model.setNew_id(id);
		model.setTitle(title);
		model.setImage(image);
		model.setUrl(url);
		boolean is  = model.save();
		
		LogUtil.LOgMsg_W("id="+is);
		
	}
	
	/**
	 * 根据id取消收藏
	 * @param id
	 */
	public static void deleteFavoriteById(String id)
	{
		DataSupport.deleteAll(FavoriteTableModel.class,"new_id=?", id);
	}
	
	/**
	 * 查询所有数据
	 * @return
	 */
	public static ArrayList<NewsListEntity> queryAllData()
	{
		ArrayList<NewsListEntity> newsList = new ArrayList<NewsListEntity>();
		List<FavoriteTableModel> list = DataSupport.order("new_id desc").find(FavoriteTableModel.class);
		for (int i = 0; i < list.size(); i++) {
			NewsListEntity entity = new NewsListEntity();
			entity.title = list.get(i).getTitle();
			entity.share_url = list.get(i).getUrl();
//			entity.images.add(list.get(i).getImage());
			
			if (!TextUtils.isEmpty(entity.share_url)) {
				ArrayList<String> images = new ArrayList<String>();
				images.add(list.get(i).getImage());
				entity.images = images;
			}
			
			entity.id = Long.parseLong(list.get(i).getNew_id());
			
			newsList.add(entity);
		}
		
		return newsList;
	}
	
	
	
}
