package com.cjj.nices.db;

import org.litepal.crud.DataSupport;

public class FavoriteTableModel extends DataSupport{
	private String title;
	private String image;
	private String url;
	private String new_id;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getNew_id() {
		return new_id;
	}
	public void setNew_id(String new_id) {
		this.new_id = new_id;
	}
	
	
	
}
