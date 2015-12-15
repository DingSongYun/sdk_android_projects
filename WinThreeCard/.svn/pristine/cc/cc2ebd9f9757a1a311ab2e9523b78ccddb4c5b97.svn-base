package com.happiplay.platform.googleplay;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.ads.identifier.AdvertisingIdClient.Info;
import com.mobileapptracker.MobileAppTracker;

public class MobileAppTrackerForGoogle {
	private static final String LOG_TAG = "MobileAppTrackerForGoogle";
	private final String ADVERTISER_ID_TEXAS = "20850";
	private final String ADV_CONVERSION_KEY_TEXAS = "ff89abd7743a55d3284857713a484919";
	private MobileAppTracker mAppTracker;
	
	public static MobileAppTrackerForGoogle sInstance;
	public static MobileAppTrackerForGoogle setupTracker(Activity activity) {
		if (sInstance == null) {
			sInstance = new MobileAppTrackerForGoogle();
			sInstance.init(activity);
		}
		
		return sInstance;
	}
	
	public void init(final Activity activity) {
		Log.d(LOG_TAG, "init mobile app tracker.");
		MobileAppTracker.init(activity.getApplicationContext(), ADVERTISER_ID_TEXAS, ADV_CONVERSION_KEY_TEXAS);
		mAppTracker = MobileAppTracker.getInstance();
		mAppTracker.setReferralSources(activity);
		
		new Thread(new Runnable(){

			@Override
			public void run() {
				// Collect google Play Advertising ID
				try {
					Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(activity.getApplicationContext());
					mAppTracker.setGoogleAdvertisingId(adInfo.getId(), adInfo.isLimitAdTrackingEnabled());
					mAppTracker.setAllowDuplicates(false);
					activity.runOnUiThread(new Runnable(){
						@Override
						public void run() {
							startMeasure();		
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}}).start();
	}
	
	public void startMeasure() {
		Log.d(LOG_TAG, "Start App Measure.");
		if (mAppTracker != null) {
			mAppTracker.measureSession();
		}
	}
}
