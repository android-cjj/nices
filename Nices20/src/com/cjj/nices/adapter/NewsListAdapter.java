package com.cjj.nices.adapter;

import static com.norbsoft.typefacehelper.TypefaceHelper.typeface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cjj.nices.activity.NewsDetailActivity;
import com.cjj.nices.activity.R;
import com.cjj.nices.db.NicesDBHelper;
import com.cjj.nices.model.NicesApplication;
import com.cjj.nices.model.NicesNewsListEntity.NewsListEntity;
import com.cjj.nices.util.ImageConfigBuilder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class NewsListAdapter extends SimpleBaseAdapter<NewsListEntity>{
	private boolean isFavorite;
	private boolean isDeleteFlag;
	private ImageLoadingListener mAnimateFirstListener = new AnimateFirstDisplayListener();
	public NewsListAdapter(Context context, ArrayList<NewsListEntity> list) {
		super(context, list);
	}
	
	public NewsListAdapter(Context context, ArrayList<NewsListEntity> list,boolean isFavorite) {
		super(context, list);
		this.isFavorite = isFavorite;
	}

	@Override
	public int getItemResourceId() {
		return isFavorite?R.layout.item_favorite:R.layout.item_new_list;
	}
	
	public void updateData(ArrayList<NewsListEntity> newsList) {
		this.mDataList = newsList;
		this.notifyDataSetChanged();
	}
	
	public void setIsDeleteFlag(boolean isDeleteFlag){
		this.isDeleteFlag = isDeleteFlag;
		this.notifyDataSetChanged();
	}

	@Override
	public View getItemView(final int position, View convertView,
			com.cjj.nices.adapter.SimpleBaseAdapter.ViewHolder holder) {
		ImageView newsImageView = (ImageView) holder.getView(R.id.list_item_image);
		TextView newsTitleView = (TextView) holder.getView(R.id.list_item_title);
		
		if(isFavorite)
		{
			final ImageView deleteIv = (ImageView) holder.getView(R.id.item_delete);
			if(isDeleteFlag)
			{
				deleteIv.setVisibility(View.VISIBLE);
				deleteIv.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						NicesDBHelper.deleteFavoriteById(String.valueOf(mDataList.get(position).id));
						isDeleteFlag = false;
						NewsListAdapter.this.mDataList = NicesDBHelper.queryAllData();
						NewsListAdapter.this.notifyDataSetChanged();
					}
				});
			}else
			{
				deleteIv.setVisibility(View.GONE);
			}
		}
		
		typeface(convertView,NicesApplication.nicesApplication.getKatongTypeface());
		NewsListEntity entity = mDataList.get(position);
		newsTitleView.setText(entity.title);
		
		if (entity.images != null && entity.images.size() >= 1) {
			newsImageView.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(entity.images.get(0), newsImageView, ImageConfigBuilder.USER_HEAD_HD_OPTIONS, mAnimateFirstListener);
				} else {
			newsImageView.setVisibility(View.GONE);
		}
		return convertView;
	}
	
	private static class AnimateFirstDisplayListener extends
			SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections
				.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}

}
