package com.happiplay.platform;

import android.os.Bundle;
import android.util.Log;

import com.happiplay.business.HappiplayAnalyzer;
import com.happiplay.tools.BuildUtils;
import com.happiplay.tools.CommonTools;
import com.happiplay.tools.ExternalCall;
import com.unity3d.player.UnityPlayerActivity;

/**
 * @author Tdsy
 * Base launch activity, other custom activity launch
 * should extend this.In general, this activity will
 * do SDK initialize and some default operation with SDK
 */
public class BaseAccessActivity extends UnityPlayerActivity implements IUnityInteraction {
	private static final String LOG_TAG = "BaseAccessActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		new Thread(new Runnable() {

			@Override
			public void run() {
				Log.d(LOG_TAG, "Init Thread");
				// TODO Auto-generated method stub
				ExternalCall.makeInstance(BaseAccessActivity.this);
				BuildUtils.loadConfiguration(BaseAccessActivity.this);
				
				BaseAccessActivity.this.runOnUiThread(new Runnable(){
					
					@Override
					public void run() {
						Log.d(LOG_TAG, "Init SDK Operator.");
						SDKOperatorFactory.makeMainSDKOperator(BaseAccessActivity.this);	
						CommonTools.addShortcut(BaseAccessActivity.this);
					}
				});
				
				// For Umeng Analysis
//				UmengOperator.umengInit(BaseAccessActivity.this);
				
				HappiplayAnalyzer.init(BaseAccessActivity.this);
				
			}
		}).start();
		
	}

	@Override
	protected void onResume() {
		Log.d(LOG_TAG, "onResume.");
		super.onResume();
//		UmengOperator.umengResume(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
//		UmengOperator.umengPause(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		SDKOperatorFactory.getSDKOperator().sdkDestroy();
	}

	
	@Override
	public void unityCall(final int cmd, final int cmdid, final String cmddata) {
		assert(ExternalCall.instance  != null);
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				ExternalCall.instance.unityCall(cmd, cmdid, cmddata);
			}
		});
	}
	
	@Override
	public void unityCall(final int cmd, final String cmddata) {

		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
			ExternalCall.instance.unityCall(cmd, cmddata);
			}
		});
	}

	@Override
	public void callUnity(final int cmdid, final String backData) {
		assert(ExternalCall.instance != null);
		ExternalCall.instance.callUnity(cmdid, backData);
	}
}
