//package com.happiplay.business;
//
//import android.content.Context;
//import android.util.Log;
//
//import com.happiplay.tools.BuildUtils;
//import com.umeng.analytics.game.UMGameAgent;
//
//public class UmengOperator {
//	public static final String LOG_TAG = "UmengOperator";
//	public static void umengInit(Context context) {
//		UMGameAgent.setDebugMode(BuildUtils.isDebugMode());
//		UMGameAgent.init(context);
//	}
//	
//	public static void umengResume(Context context) {
//		UMGameAgent.onResume(context);
//	}
//	
//	public static void umengPause(Context context) {
//		UMGameAgent.onPause(context);
//	}
//	
//	public static void umengPayAnalisis(boolean success, double price) {
//		Log.d(LOG_TAG, "umengPayAnalisis() => success?: " + success);
//		if (BuildUtils.isDebugMode()) {
//			Log.d(LOG_TAG, "in debug mode, ignore this.");
//			return ;
//		}
//		
//		if (success) {
//			umengPayAnalisis(price);
//		}
//	}
//	
//	private static void umengPayAnalisis(double price) {
//		umengPayAnalisis(price, 2);
//	}
//	
//	private static void umengPayAnalisis(double price, int payType) {
//		UMGameAgent.pay(price, 0, 2);
//	}
//}
