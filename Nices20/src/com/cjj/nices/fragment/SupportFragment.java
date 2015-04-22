package com.cjj.nices.fragment;

import java.util.Arrays;
import java.util.List;

import net.youmi.android.diy.DiyManager;
import u.aly.bu;
import uk.me.lewisdeane.ldialogs.CustomDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.cjj.nices.activity.R;
import com.cjj.nices.activity.R.color;
import com.cjj.nices.adapter.NewsListSwipeMenuListViewAdapter;

/**
 * 视频Fragment类
 * @author cjj
 */
public class SupportFragment extends Fragment implements OnItemClickListener{
	private ListView lv_support;
	
	public static SupportFragment newInstance() {
		SupportFragment fragment = new SupportFragment();
		return fragment;
	}
 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_support, null);
		return v;
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		lv_support = (ListView) view.findViewById(R.id.list_support);
		lv_support.setOnItemClickListener(this);
		super.onViewCreated(view, savedInstanceState);
	}
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		List list = Arrays.asList(getActivity().getResources().getStringArray(R.array.title));
		lv_support.setAdapter(new NewsListSwipeMenuListViewAdapter(getActivity(),list));
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		switch(position)
		{
		case 0:

        	DiyManager.showRecommendWall(getActivity());
        	
			break;
		case 1:
			showLDailog("关于我", "确定", "您可以加我qq:929178101或者微博:Android_CJJ了解吧！");
			break;
		case 2:
			showLDailog("关于NiceS", "确定", "自娱自乐的App,希望您可以学习到知识，呵呵。。。");
			break;
		}
	}
	
	protected void showLDailog(String title,String okbtn,String message) {
		CustomDialog.Builder builder = new CustomDialog.Builder(getActivity(), title, okbtn);
		// Now we can any of the following methods.
		builder.content(message);
		builder.titleColor(Color.RED);
		builder.contentColor(getActivity().getResources().getColor(R.color.red));
		builder.positiveColor(Color.RED);
//		builder.negativeText("取消");
		// Now we can build the dialog.
		CustomDialog customDialog = builder.build();
//		// Show the dialog.
//		customDialog.setClickListener(new CustomDialog.ClickListener() {
//            @Override
//            public void onConfirmClick() {
//            }
//
//            @Override
//            public void onCancelClick() {
//            	
//            }
//        });
		customDialog.show();
	}

	
}
