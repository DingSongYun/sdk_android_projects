package com.happiplay.platform;

import android.text.TextUtils;
import android.util.Log;

import com.happiplay.platform.Constants.PlatformType;


/**
 * @author dingsongyun
 * raw data of APP for specific platform
 * should always instant this from configuration file
 */
public class GameAppInfo {
	private static final String LOG_TAG = "GameAppInfo";
	
	private String mAppName;
	private String mAppId;
	private String mAppKey;
	private PlatformType mPlatform;
	private boolean mIsLandscape;
	private String mOperatorClass;
	
	// Optional attribute
	private String mChannelId;
	private String mPFMarketAddr;
	private String mAppSecret;

	private String mWeChatAppId;
	
	public enum PayMode {
		FIXED_PAYMENT,  // The pay mode should always be this at present
		PAYMENT_BY_INSTALLMENTS
	}
	
	public GameAppInfo(String appId, String appKey, PlatformType platform, String operatorCls) {
		this(appId, appKey, platform,  operatorCls, "");
	}
	
	public GameAppInfo(String appId, String appKey, String platform, String operatorCls) {
		this(appId, appKey, parsePlatformType(platform), operatorCls);
	} 
	
	public GameAppInfo(String appId, String appKey, PlatformType platform, String operatorCls, String name) {
		this(appId, appKey, platform, operatorCls, name, true);
	}

	public GameAppInfo(String appId, String appKey, String platform, String operatorCls, String name, boolean isLandscape) {
		this(appId, appKey, parsePlatformType(platform), operatorCls, name, isLandscape);
	}
	
	public GameAppInfo(String appId, String appKey, PlatformType platform, String operatorCls, String name, boolean isLandscape) {
		mAppName = name;
		mAppId = appId;
		mAppKey = appKey;
		mPlatform = platform;
		mIsLandscape = isLandscape;
		mOperatorClass = operatorCls;
	}

	public static PlatformType parsePlatformType(String platform) {
		Log.d(LOG_TAG, "parsePlatformType: " + platform);
		if (TextUtils.isEmpty(platform)) {
			Log.e(LOG_TAG, "platform string is null, can not parse to a valid platform-type");
			return PlatformType.PLATFORM_UNDEFINE;
		}
		for(PlatformType type : PlatformType.values()) {
			if (type.toString().equals(platform)) {
				return type;
			}
		}
		Log.e(LOG_TAG, "Parse failed.");
		return PlatformType.PLATFORM_UNDEFINE;
	}
	
	public String getAppName() {
		return mAppName;
	}
	
	public String getAppId() {
		return mAppId;
	}
	
	public String getAppKey() {
		return mAppKey;
	}
	
	public boolean isLandscape() {
		return mIsLandscape;
	}
	
	public PlatformType getPlatform() {
		return mPlatform;
	}
	
	public String getOperatorClassName() {
		return mOperatorClass;
	}
	
	public String getChannel() {
		return mChannelId;
	}
	
	public void setChannel(String channelId) {
		mChannelId = channelId;
	}
	
	public String getPlatformMarketAddr() {
		return mPFMarketAddr;
	}
	
	public void setPlatformMarketAddr(String addr) {
		mPFMarketAddr = addr;
	}
	
	public void setAppSecret(String string) {
		mAppSecret = string;
	}
	
	public void setWeChatAppId(String string) {
		mWeChatAppId = string;
	}
	
	public String getWeChatAppId() {
		return mWeChatAppId;
	}

	
	public String getAppSecret() {
		return mAppSecret;
	}
	
	public String toString() {
		return "gameInfo:" + "appname(" + mAppName + ")," +
					"appId(" + mAppId + ")," +
					"appKey(" + mAppKey + ")" +
					"platform(" + mPlatform + ")" +
					"isLandscape(" + mIsLandscape + ")" +
					"operator(" + mOperatorClass + ")";
	}
}