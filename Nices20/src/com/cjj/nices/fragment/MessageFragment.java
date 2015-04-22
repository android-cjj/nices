package com.cjj.nices.fragment;

import static com.norbsoft.typefacehelper.TypefaceHelper.typeface;

import com.cjj.nices.activity.R;
import com.cjj.nices.model.NicesApplication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
/**
 * 消息fragment类
 * @author cjj
 *
 */
public class MessageFragment extends Fragment{
	
	public static MessageFragment newInstance() {
		MessageFragment fragment = new MessageFragment();
		return fragment;
	}
 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_message, null);
		return v;
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		TextView tv_tip = (TextView) view.findViewById(R.id.tv_tip_message);
		typeface(tv_tip,NicesApplication.nicesApplication.getKatongTypeface());
		super.onViewCreated(view, savedInstanceState);
	}
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}
	
}
