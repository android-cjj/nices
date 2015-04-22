package com.cjj.nices.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 新闻详情实体类
 * @author cjj
 *
 */
public class NicesDetailModel implements Serializable {

	private static final long serialVersionUID = 1L;
	public String body;
	public String image_source;
	public String title;
	public String image;
	public String share_url;
	public ArrayList<String> js;
	public int type;
	public String ga_prefix;
	public long id;
	public ArrayList<String> css;
}
