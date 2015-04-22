package com.cjj.nices.model;

import org.litepal.LitePalApplication;

import com.cjj.volley.me.CjjVolley;
import com.norbsoft.typefacehelper.TypefaceCollection;
import com.norbsoft.typefacehelper.TypefaceHelper;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;

public class NicesApplication extends LitePalApplication{
	public static NicesApplication nicesApplication;
	
	/** Multiple custom typefaces support */
	private TypefaceCollection mKatongTypeface;
	/** Multiple custom typefaces support */
	private TypefaceCollection mHuayunTypeface;

	@Override
	public void onCreate() {
		super.onCreate();
		nicesApplication = this;
		initFont();
		initVolley();
		initImageLoader(getApplicationContext());
	}

	private void initFont() {
//		// Load helper with default custom typeface (single custom typeface)
//				TypefaceHelper.init(new TypefaceCollection.Builder()
//						.set(Typeface.NORMAL, Typeface.createFromAsset(getAssets(), "fonts/ubuntu/Ubuntu-R.ttf"))
//						.set(Typeface.BOLD, Typeface.createFromAsset(getAssets(), "fonts/ubuntu/Ubuntu-B.ttf"))
//						.set(Typeface.ITALIC, Typeface.createFromAsset(getAssets(), "fonts/ubuntu/Ubuntu-RI.ttf"))
//						.set(Typeface.BOLD_ITALIC, Typeface.createFromAsset(getAssets(), "fonts/ubuntu/Ubuntu-BI.ttf"))
//						.create());
		// Load helper with default custom typeface (single custom typeface)
//		TypefaceHelper.init(new TypefaceCollection.Builder()
//				.set(Typeface.NORMAL, Typeface.createFromAsset(getAssets(), "fonts/kaiti/Kaiti.ttf"))
//				.create());
		
//		// Load helper with default custom typeface (single custom typeface)
//		TypefaceHelper.init(new TypefaceCollection.Builder()
//				.set(Typeface.NORMAL, Typeface.createFromAsset(getAssets(), "fonts/huayun/Huayun.TTF"))
//				.create());
		// Load helper with default custom typeface (single custom typeface)
				TypefaceHelper.init(new TypefaceCollection.Builder()
						.set(Typeface.NORMAL, Typeface.createFromAsset(getAssets(), "fonts/katong/Katong.ttf"))
						.create());

				
				// Multiple custom typefaces support
				mKatongTypeface = new TypefaceCollection.Builder()
						.set(Typeface.NORMAL, Typeface.createFromAsset(getAssets(), "fonts/katong/Katong.ttf"))
						.create();
				
				// Multiple custom typefaces support
				mHuayunTypeface = new TypefaceCollection.Builder()
						.set(Typeface.NORMAL, Typeface.createFromAsset(getAssets(), "fonts/huayun/Huayun.TTF"))
						.create();
	}
	
	private void initVolley() {
		CjjVolley.init(this);
	}
	
	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you may tune some of them,
		// or you can create default configuration by
		//  ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.diskCacheSize(50 * 1024 * 1024) // 50 Mb
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}
	

	
	/** Multiple custom typefaces support */
	public TypefaceCollection getKatongTypeface() {
		return mKatongTypeface;
	}
	
	/** Multiple custom typefaces support */
	public TypefaceCollection getHuayunTypeface() {
		return mHuayunTypeface;
	}
}
