package com.happiplay.platform;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import com.happiplay.platform.Constants.PlatformType;
import com.happiplay.tools.BuildUtils;

/**
 * @author Tdsy
 * Use this class to get SDK operator
 */
public class SDKOperatorFactory {
	private final static String LOG_TAG = "SDKOperator";
	private static OpenSDKOperator mMainSDKOperator = null;
	
	/**
	 * make main platform SDKOperator through project configuration
	 */
	public static void makeMainSDKOperator(Context context) {
		PlatformType platform = BuildUtils.getPlatformType();
		Log.d(LOG_TAG, "makeMainSDKOperator for " + platform);
		mMainSDKOperator = makeSDKOperator(context, platform);
		if (mMainSDKOperator == null) {
			mMainSDKOperator = makeSDKOperator(context, PlatformType.PLATFORM_TENCENT);
		}
	}
	
	/**
	 * This will be useful when the main SDK platform
	 * also support other way to process(login & pay) the
	 * game APP
	 * @param type SDK platform type
	 * @return Specific SDK operator for different platform
	 */
	public static OpenSDKOperator makeSDKOperator(Context context, PlatformType platformType) {
		OpenSDKOperator operator = null;
		GameAppInfo appInfo = BuildUtils.getGameAppInfo(platformType);
		if (appInfo != null) {
			try {
				Class<?> operatorCls = Class.forName(appInfo.getOperatorClassName());
				operator = (OpenSDKOperator) operatorCls.getDeclaredConstructor(Context.class).newInstance(context);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (Exception e) {
				Log.e(LOG_TAG, "Can not get platform operator, have you implemented it?");
				e.printStackTrace();
			}
		} else {
			Log.e(LOG_TAG, "Not app info for this platform.");
		}
		if (operator != null) {
			operator.setAppInfo(appInfo);
			operator.sdkInit((Activity)context);
		}
		return operator;
	}

	/**
	 * Get SDK operator for main platform
	 * @return
	 */
	public static OpenSDKOperator getSDKOperator() {
		assert(mMainSDKOperator != null);
		return mMainSDKOperator;
	}
}
