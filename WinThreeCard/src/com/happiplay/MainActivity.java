package com.happiplay;

import java.util.ArrayList;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import cn.jpush.android.api.CustomPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;

import com.happiplay.platform.BaseAccessActivity;
import com.happiplay.tools.BuildUtils;
import com.starcloudcasino.winthree.R;
import com.unity3d.player.UnityPlayer;

public class MainActivity extends BaseAccessActivity{
	private static final String LOG_TAG = "MainActivity";
	
	public interface IActivityResult {
		public void onActivityResult(int requestCode, int resultCode, Intent data);
	}
	private ArrayList<IActivityResult> mOnResultRegistrants = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// For JPush
		Log.d(LOG_TAG, "init jpush");
		JPushInterface.init(this);
		JPushInterface.setDebugMode(BuildUtils.isDebugMode());
		CustomPushNotificationBuilder builder = new CustomPushNotificationBuilder(this,
			    R.layout.customer_notitfication_layout, R.id.icon, R.id.title, R.id.text);  // 指定定制的 Notification Layout
			builder.statusBarDrawable = R.drawable.app_icon;      // 指定最顶层状态栏小图标
			builder.layoutIconDrawable = R.drawable.app_icon;   // 指定下拉状态栏时显示的通知图标
		JPushInterface.setPushNotificationBuilder(2, builder);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		JPushInterface.onResume(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		JPushInterface.onPause(this);
	};
	
	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	public void onConfigurationChanged(Configuration conf) {
		Log.d(LOG_TAG, "MainActivity => onConfigurationChanged()");
		super.onConfigurationChanged(conf);
		UnityPlayer.UnitySendMessage("_MonoApplication", "OnConfigurationChanged", String.valueOf(conf.orientation));
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (mOnResultRegistrants != null && mOnResultRegistrants.size() > 0) {
			for(IActivityResult listener : mOnResultRegistrants) {
				listener.onActivityResult(requestCode, resultCode, data);
			}
		}
	}
	
	public void registerActivityResultListener(IActivityResult listener) {
		assert(listener != null);
		if (mOnResultRegistrants == null) {
			mOnResultRegistrants = new ArrayList<IActivityResult>();
		}
		
		if (mOnResultRegistrants.contains(listener)) {
			Log.d(LOG_TAG, "register ActivityResultListener failed, already registered.");
			return ;
		}
		
		mOnResultRegistrants.add(listener);
	}
	
	public void unRegisterActivityResultListener(IActivityResult listener) {
		assert(listener != null);
		int index = mOnResultRegistrants.indexOf(listener);
		if (index != -1) {
			mOnResultRegistrants.remove(index);
		}
	}
}
