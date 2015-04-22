package com.cjj.nices.model;

import java.io.Serializable;
import java.util.ArrayList;
/**
 * 新闻列表实体类
 * @author cjj
 *
 */
public class NicesNewsListEntity implements Serializable {

	private static final long serialVersionUID = 4506450425687255230L;
	public String date;
	public ArrayList<NewsListEntity> stories;
	
	public static class NewsListEntity implements Serializable {

		private static final long serialVersionUID = 1L;

		public String title;
		public String share_url;
		public String ga_prefix;
		public ArrayList<String> images;
		public int type;
		public long id;

	}
}
