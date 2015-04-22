package com.cjj.nices.db;

import org.litepal.crud.DataSupport;

/**
 * 新闻列表表
 * @author cjj
 *
 */
public class NewsListModel extends DataSupport {
	
	private String time;
	private String content;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	

	
}
