package com.cjj.nices.dao;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.cjj.nices.constant.Constants;
import com.cjj.nices.db.NicesDBHelper;
import com.cjj.nices.model.NicesDetailModel;
import com.cjj.nices.model.NicesNewsListEntity;
import com.cjj.nices.util.UrlUtils;
import com.cjj.volley.Response;
import com.cjj.volley.VolleyError;
import com.cjj.volley.callback.CallBackDataListener;
import com.cjj.volley.callback.CallBackStringDataListener;
import com.cjj.volley.custom.request.GsonRequest;
import com.cjj.volley.custom.request.XMLRequest;
import com.cjj.volley.me.CjjVolley;
import com.cjj.volley.utils.LogUtil;
/**
 * user 操作类
 * @author cjj
 *
 */
public class UserDao {
	/** 私有类对象 */
	private static UserDao instance;

	/** 单例模式 */
	public static UserDao getInstance() {
		if (instance == null) {
			instance = new UserDao();
		}
		return instance;
	}
	/**
	 * get gson data
	 * 
	 * @param callBackDataListener
	 */
	public void getNewsListGsonDataFromNet(boolean oldContentFlag,final String time,final CallBackDataListener callBackDataListener) {
		GsonRequest<NicesNewsListEntity> mRequest = new GsonRequest<NicesNewsListEntity>(
				oldContentFlag?UrlUtils.URL_OLD+time:UrlUtils.URL_LATEST, NicesNewsListEntity.class,
				new Response.Listener<NicesNewsListEntity>() {
					@Override
					public void onResponse(NicesNewsListEntity response) {
						callBackDataListener.callBack(response);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						callBackDataListener.error(error);
					}
				},new CallBackStringDataListener() {
					
					@Override
					public void callbackStringData(String result) {
						/**插入或者跟新数据到本地数据库*/
						NicesDBHelper.insertOrUpdateNewsList(time, result);
						
					}
				});
		
		CjjVolley.addRequest(mRequest, Constants.TAG_REQUEST_GSON_NEWS_LIST);
	}
	

	
	/**
	 * get detail data 
	 * @param id
	 */
	public void getDetaiDatabyIdFromNet(long id,final CallBackDataListener callBackDataListener)
	{
		GsonRequest<NicesDetailModel> mRequest = new GsonRequest<NicesDetailModel>(
				UrlUtils.URL_DETAIL+id, NicesDetailModel.class,
				new Response.Listener<NicesDetailModel>() {
					@Override
					public void onResponse(NicesDetailModel response) {
						callBackDataListener.callBack(response);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						callBackDataListener.error(error);
					}
				});
		
		CjjVolley.addRequest(mRequest, Constants.TAG_REQUEST_GSON_NEWS_DETAIL);
	}

}
