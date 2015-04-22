package com.cjj.custom;


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.ViewPager.LayoutParams;
import android.support.v7.appcompat.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.PopupWindow;
import android.widget.TextView;



public class CustomPopWindow extends PopupWindow{  
    private View conentView; 
    private int w,h;
    private TextView tv_selectItem,tv_deleteAll;
    private View ll;
  
    public CustomPopWindow(final Activity context) {  
        LayoutInflater inflater = (LayoutInflater) context  
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        conentView = inflater.inflate(R.layout.view_pop_window, null);  
         h = context.getWindowManager().getDefaultDisplay().getHeight();  
         w = context.getWindowManager().getDefaultDisplay().getWidth();  
        // 设置SelectPicPopupWindow的View  
        this.setContentView(conentView);  
        // 设置SelectPicPopupWindow弹出窗体的宽  
        this.setWidth(w / 2-10);  
        // 设置SelectPicPopupWindow弹出窗体的高  
        this.setHeight(LayoutParams.WRAP_CONTENT);  
        // 设置SelectPicPopupWindow弹出窗体可点击  
        this.setFocusable(true);  
        this.setOutsideTouchable(true);  
        // 刷新状态  
        this.update();  
        // 实例化一个ColorDrawable颜色为半透明  
        ColorDrawable dw = new ColorDrawable(0000000000);  
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作  
        this.setBackgroundDrawable(dw);  
        // mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);  
        // 设置SelectPicPopupWindow弹出窗体动画效果  
        this.setAnimationStyle(R.style.AnimationPreview);
        
        tv_deleteAll = (TextView) conentView.findViewById(R.id.tv_deleteAll);
        tv_selectItem = (TextView) conentView.findViewById(R.id.tv_selectItem);
        tv_deleteAll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(listener!=null)
				{
					listener.selectAll();
				}
				CustomPopWindow.this.dismiss();
			}
		});
        tv_selectItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(listener!=null)
				{
					listener.selectItem();
				}
				CustomPopWindow.this.dismiss();
			}
		});
        
        ll = conentView.findViewById(R.id.pop_layout);
    }  
    
    public View getLL()
    {
    	return ll;
    }
    
    public interface ItemSelectListener
    {
    	public void selectItem();
    	public void selectAll();
    }
    
    private ItemSelectListener listener;
    public void setItemSelectListener(ItemSelectListener listener)
    {
    	this.listener = listener;
    }
  
    /** 
     * 显示popupWindow 
     *  
     * @param parent 
     */  
    public void showPopupWindow(View parent) {  
        if (!this.isShowing()) {  
            // 以下拉方式显示popupwindow  
            this.showAsDropDown(parent,w/2, 0);  
        } else {  
            this.dismiss();  
        }  
    }

}  