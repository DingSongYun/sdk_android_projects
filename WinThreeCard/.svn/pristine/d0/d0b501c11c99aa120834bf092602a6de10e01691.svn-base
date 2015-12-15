package com.happiplay.business;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.happiplay.tools.CommonTools;

public class HappiplayAnalyzer {
	public static String LOG_TAG = "HappuplayAnalyzer";
	private static Context mContext;
	
	private static String URL_SCHEME = "http";
	private static String SERVER_URL = "texas.upupgame.com";
	private static String LAUNCH_DST_URL = "mobile.php";
	public static void init(Context context) {
		mContext = context;
		onAppLaunched();
	}
	
	private static void onAppLaunched() {
		Log.d(LOG_TAG, "onAppLaunched.");
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				HttpClient client = new DefaultHttpClient();
				try {
					Uri uri = new Uri.Builder().scheme(URL_SCHEME)
							.authority(SERVER_URL)
							.path(LAUNCH_DST_URL)
							.appendQueryParameter("touch", "1")
							.appendQueryParameter("deviceuid", CommonTools.getDeviceID(mContext))
							.appendQueryParameter("device", CommonTools.getDeviceName())
							.appendQueryParameter("f", CommonTools.getChannelId(mContext))
							.appendQueryParameter("bundleid", mContext.getPackageName())
							.appendQueryParameter("ver",CommonTools.getVersionName(mContext))
							.build();
					HttpGet httpReq = new HttpGet(uri.toString());
					Log.d(LOG_TAG, "Game Launched analysis url:" + httpReq.getURI().toString());
					client.execute(httpReq);
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}
