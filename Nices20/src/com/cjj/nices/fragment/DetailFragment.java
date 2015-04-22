package com.cjj.nices.fragment;

import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.cjj.callback.ReLoadCallbackListener;
import com.cjj.loading.LoadingCjjLayout;
import com.cjj.nices.activity.R;
import com.cjj.nices.constant.Constants;
import com.cjj.nices.dao.UserDao;
import com.cjj.nices.model.NicesDetailModel;
import com.cjj.nices.util.AssetsUtils;
import com.cjj.volley.VolleyError;
import com.cjj.volley.callback.CallBackDataListener;
import com.cjj.volley.me.CjjVolley;
import com.cjj.volley.utils.LogUtil;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
/**
 * 新闻详情类
 * @author cjj
 *
 */
@SuppressLint("NewApi") public class DetailFragment extends Fragment implements ReLoadCallbackListener , OnRefreshListener{
	private WebView mWebView;
	private long mNewsId = 0;
	private LoadingCjjLayout loadingCjjLayout;
	private SwipeRefreshLayout mSwipeLayout; 
	private boolean isRefresh = false;
	private ArrayList<String> mDetailImageList = new ArrayList<String>();
	

	public static DetailFragment newInstance(long id) {
		DetailFragment fragment = new DetailFragment();
		Bundle args = new Bundle();
		args.putLong("id", id);
		fragment.setArguments(args);
		return fragment;
	}
 
	@Override
	public void onCreate(Bundle savedInstanceState) {

		if (savedInstanceState == null) {
			Bundle bundle = getArguments();
			mNewsId = bundle != null ? bundle.getLong("id") : 0;
		} else {
			mNewsId = savedInstanceState.getLong(Constants.ID);
		}
		
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_detail, null);
		loadingCjjLayout = new LoadingCjjLayout(getActivity(), v);
		loadingCjjLayout.setReLoadCallbackListener(this);
		return loadingCjjLayout;
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		mWebView = (WebView) view.findViewById(R.id.webview);
		
		mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
		mSwipeLayout.setOnRefreshListener(this);
		mSwipeLayout.setColorSchemeResources(android.R.color.holo_red_light,
				android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_blue_bright
				);
		super.onViewCreated(view, savedInstanceState);
	}
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		loadingCjjLayout.show_LoadingView();
		setWebView(mWebView);
		mWebView.setWebViewClient(mWebViewClient);
		LogUtil.LogMsg_I("id="+mNewsId);
		getDetaiDatabyId(mNewsId);
		super.onActivityCreated(savedInstanceState);
	}
	
	private void getDetaiDatabyId(long mNewsId) {
		UserDao.getInstance().getDetaiDatabyIdFromNet(mNewsId,new CallBackDataListener() {
			
			@Override
			public void error(VolleyError error) {
				if(isRefresh)
				{
					isRefresh = false;
					mSwipeLayout.setRefreshing(false);
					showCrouton("刷新失败", Style.ALERT,  Configuration.DEFAULT);
				}else
				{
					toastMsg("网络错误");
					loadingCjjLayout.show_FailView();
				}
				
			}
			
			@Override
			public void callBack(Object data) {
				if(data instanceof NicesDetailModel)
				{
					if(isRefresh)
					{
						mSwipeLayout.setRefreshing(false);
						showCrouton("刷新成功", Style.TRANSPARENT,  Configuration.DEFAULT);
						isRefresh = false;
					}
					loadingCjjLayout.show_ContentView();
					NicesDetailModel model = (NicesDetailModel) data;
					showWebView(model);
				}
			}
		});
	}

	
	protected void showWebView(NicesDetailModel model) {
		String html = AssetsUtils.loadText(getActivity(), "www/template.html");
		html = html.replace("{content}", model.body);
		String headerDef = model.image;
		StringBuilder sb = new StringBuilder();
		sb.append("<div class=\"img-wrap\">")
				.append("<h1 class=\"headline-title\">")
				.append(model.title).append("</h1>")
				.append("<span class=\"img-source\">")
				.append(model.image_source).append("</span>")
				.append("<img src=\"").append(headerDef)
				.append("\" alt=\"\">")
				.append("<div class=\"img-mask\"></div>");
		
		html = html.replace("<div class=\"img-place-holder\">", sb.toString());
		
		if (Build.VERSION.SDK_INT >= 11) {
			mWebView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
		}
		mWebView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
	}

	private void setWebView(WebView webView) {
		webView.addJavascriptInterface(new JavaScriptObject(getActivity()), "injectedObject");
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		//设置加载进来的页面自适应手机屏幕 
		webView.getSettings().setUseWideViewPort(true);
		webView.getSettings().setLoadWithOverviewMode(true);
		webView.setVerticalScrollBarEnabled(false);
		webView.setHorizontalScrollBarEnabled(false);
		// 支持通过js打开新的窗口
		webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		webView.setWebChromeClient(new WebChromeClient()
		{
			@Override
			public boolean onJsAlert(WebView view, String url, String message,
					JsResult result) {
				Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();  
	            result.cancel();  
				return super.onJsAlert(view, url, message, result);
			}
			
			@Override
			public boolean onJsConfirm(WebView view, String url,
					String message, JsResult result) {
				return super.onJsConfirm(view, url, message, result);
			}
		})	;	
	}


	@Override
	public void onDestroyView() {
		CjjVolley.cancelAll(Constants.TAG_REQUEST_GSON_NEWS_DETAIL);
		super.onDestroyView();
	}
	
	public static class JavaScriptObject
	{

		private Activity mInstance;

		public JavaScriptObject(Activity instance) 
		{
			mInstance = instance;
		}
		
		public void openImage(String url)
		{
			Toast.makeText(mInstance, "open image",1).show();
		}
	}
	
	private WebViewClient mWebViewClient = new WebViewClient()
	{
		public boolean shouldOverrideUrlLoading(WebView view, String url) 
		{
			Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(url));
			startActivity(intent);
			return true;
		};
		
		public void onPageFinished(WebView view, String url) 
		{
			LogUtil.LogMsg_I("onPageFinished : " + url);
		};
	};

	private void toastMsg(String str)
	{
		Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
	}
	
	private void showCrouton(String croutonText, Style croutonStyle,
			Configuration configuration) {
		Crouton.makeText(getActivity(), croutonText, croutonStyle).show();
	}

	@Override
	public void onReLoadCallback() {
		loadingCjjLayout.show_LoadingView();
		getDetaiDatabyId(mNewsId);
	}

	@Override
	public void onRefresh() {
		isRefresh = true;
		getDetaiDatabyId(mNewsId);
	}
	
}
	