package com.happiplay.platform.googleplay;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.happiplay.MainActivity;
import com.happiplay.MainActivity.IActivityResult;
import com.happiplay.platform.GameAppInfo.PayMode;
import com.happiplay.platform.OpenSDKOperator;
import com.happiplay.platform.PayInfo;
import com.happiplay.tools.CommonTools;
import com.happiplay.tools.ExternalCall;
import com.happiplay.tools.ShareResult;
import com.happiplay.tools.ShareTools;
import com.happiplay.tools.ShareTools.AppShareInfo;
import com.starcloudcasino.winthree.R;

/**
 * For applications in google play, we use Facebook as preferred login platform,
 * and user can also login through QQ platform. Besides, payment should use goolge
 * In-app Billing, see from bellow url for more detail: 
 * 		<a href="http://developer.android.com/training/in-app-billing/preparing-iab-app.html"/> 
 * @author Tdsy
 *
 */
public class OpenFacebookSDKOperator extends OpenSDKOperator implements IActivityResult {
	private static final String LOG_TAG = "OpenFacebookSDKOperator";

	private final int MSG_LOGIN_TIME_OUT = 1000;
	private final int MSG_DISMISS_SHARE_DIALOG = 1001;
	private final int TIME_OUT = 60 * 1000;
	private WebDialog mFbShareDialog = null;
	private boolean mIsShowingLigonProgress = false;
	
	private Handler mHandler = new Handler() {
		
		public void handleMessage(Message msg) {
			if (msg.what == MSG_LOGIN_TIME_OUT) {
				Log.d(LOG_TAG, "Facebook login time out, login failed.");
				Toast.makeText(mContext, mContext.getString(R.string.login_time_out), Toast.LENGTH_LONG).show();
				ExternalCall.instance.sdkLoginFinish(
						OpenFacebookSDKOperator.this.createLoginCbDataToUnity(
										false, "", "", "", mAppInfo.getPlatform()));
				if (mIsShowingLigonProgress) {
					CommonTools.dismissProgress();
					mIsShowingLigonProgress = false;
				}
			} else if (msg.what == MSG_DISMISS_SHARE_DIALOG) {
				Log.d(LOG_TAG, "Dismiss facabook share dialog.");
//				if (mFbShareDialog.isShowing()) {
					mFbShareDialog.dismiss();
//				}
			}
		}
	};
	
	public GooglePlayBillingOperator mBillingOperator;
	
	public OpenFacebookSDKOperator(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void doSDKLogin(String data) {
	    // start Facebook Login
		if (mIsShowingLigonProgress) {
			Log.d(LOG_TAG, "Login under processing.");
			return ;
		}
		mIsShowingLigonProgress = true;
		Dialog progress = CommonTools.showProgress(mContext, ProgressDialog.STYLE_HORIZONTAL, null, "Facebook Login...");
		progress.setCancelable(true);
	    Session.openActiveSession((Activity)mContext, true, new Session.StatusCallback() {

			/* 
			 * Callback when session status changed This will be called several times, 
			 * and we should tell unity login result while session is opened or login
			 * time out
			 */
			@SuppressWarnings("deprecation")
			@Override
			public void call(Session session, SessionState state,
					Exception exception) {
				mHandler.sendEmptyMessageDelayed(MSG_LOGIN_TIME_OUT, TIME_OUT);
				Log.d(LOG_TAG, "Facebook login callback => session is open?  " + session.isOpened());
				if (exception != null) {
					Log.d(LOG_TAG, "login exception:" + exception.getClass() + ", " + exception.toString());
					if (exception instanceof FacebookOperationCanceledException) {
						Log.d(LOG_TAG, "login canceled.");
						mHandler.removeMessages(MSG_LOGIN_TIME_OUT);
						if (mIsShowingLigonProgress) {
							CommonTools.dismissProgress();
							mIsShowingLigonProgress = false;
						}
						
						ExternalCall.instance.sdkLoginFinish(
								OpenFacebookSDKOperator.this.createLoginCbDataToUnity(
										true, null, null, null, mAppInfo.getPlatform()));
					}
					return ;
				}
				
				if (session.isOpened()) {
					mHandler.removeMessages(MSG_LOGIN_TIME_OUT);
					final String accessToken = session.getAccessToken();
					
					Log.d(LOG_TAG, "query facebook user info.");
					Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
						
						@Override
						public void onCompleted(GraphUser user, Response response) {
							Log.d(LOG_TAG, "query facebook user info successfully.");
							String userName = "";
							String userId = "";
							if (user != null) {
								userName = user.getName();
								userId = user.getId();
							} else {
								Log.d(LOG_TAG, "Can not get userinfo.");
							}
							ExternalCall.instance.sdkLoginFinish(
									OpenFacebookSDKOperator.this.createLoginCbDataToUnity(
											true, userId, userName, accessToken, mAppInfo.getPlatform()));
							if (mIsShowingLigonProgress) {
								CommonTools.dismissProgress();
								mIsShowingLigonProgress = false;
							}
						}
					});
					
					return ;
				}
			}
	    });
	}

	@Override
	public void doSDKPay(String data, PayMode payMode) {
		Log.d(LOG_TAG, "fackbook do not support payment.");
		PayInfo payInfo = new PayInfo();
		parsePayInfo(payInfo, data);
		mBillingOperator.pay(payInfo);
	}

	public void getProducts(String data) {
		Log.d(LOG_TAG, "Get product list.");
		mBillingOperator.getProducts();
	}
	
	@Override
	public void doSDKLogout(String data) {
//        Session session = Session.getActiveSession();
//        if (session.isOpened() && !session.isClosed()) {
//            session.closeAndClearTokenInformation();
//        }
	}

	@Override
	public void onLoginFinished(String data) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sdkInit(Activity activity) {
		mBillingOperator = new GooglePlayBillingOperator(activity, this);
		mBillingOperator.init();
		
		com.facebook.AppEventsLogger.activateApp(activity);
		((MainActivity)activity).registerActivityResultListener(this);
	}

	@Override
	public void sdkDestroy() {
		mBillingOperator.dispose();
	}

	public void doSDKShareToFriend(AppShareInfo shareInfo, final Handler callback, final int callbackMsg) {
		mFbShareDialog = new WebDialog.RequestsDialogBuilder(mContext,
				Session.getActiveSession(), null)
				.setTitle(shareInfo.title)
				.setMessage(shareInfo.summary)
				.build();
		mFbShareDialog.setOnCompleteListener(new OnCompleteListener() {
			
			@Override
			public void onComplete(Bundle values, FacebookException error) {
				if (error != null) {
					if (error instanceof FacebookOperationCanceledException) {
						ShareTools.sendShareCallbackMsg(callback, callbackMsg, ShareResult.RESULT_CANCELED, error.getMessage());
					} else {
						ShareTools.sendShareCallbackMsg(callback, callbackMsg, ShareResult.RESULT_FAILED, error.getMessage());
					}
				} else {
					String requestId = values.getString("request");
					Log.d(LOG_TAG, "facebook share requestId:" + requestId);
					if (TextUtils.isEmpty(requestId)) {
						ShareTools.sendShareCallbackMsg(callback, callbackMsg, ShareResult.RESULT_CANCELED);
					} else {
						ShareTools.sendShareCallbackMsg(callback, callbackMsg, ShareResult.RESULT_SUCCESS);
					}
				}
				
				mHandler.sendEmptyMessageDelayed(MSG_DISMISS_SHARE_DIALOG, 100);
			}
		});
		mFbShareDialog.show();
	}
	
	public void sendFacebookFeed(AppShareInfo shareInfo, final Handler callback, final int callbackMsg) {
		mFbShareDialog = new WebDialog.FeedDialogBuilder(mContext,
				Session.getActiveSession(), null)
				.setName(shareInfo.title)
				.setDescription(shareInfo.summary)
				.setPicture(shareInfo.image_url)
				.setLink(shareInfo.target_url)
				.build();
		mFbShareDialog.setOnCompleteListener(new OnCompleteListener() {
			
			@Override
			public void onComplete(Bundle values, FacebookException error) {
				if (error != null) {
					if (error instanceof FacebookOperationCanceledException) {
						ShareTools.sendShareCallbackMsg(callback, callbackMsg, ShareResult.RESULT_CANCELED, error.getMessage());
					} else {
						ShareTools.sendShareCallbackMsg(callback, callbackMsg, ShareResult.RESULT_FAILED, error.getMessage());
					}
				} else {
					String requestId = values.getString("request");
					Log.d(LOG_TAG, "facebook share requestId:" + requestId);
					if (TextUtils.isEmpty(requestId)) {
						ShareTools.sendShareCallbackMsg(callback, callbackMsg, ShareResult.RESULT_CANCELED);
					} else {
						ShareTools.sendShareCallbackMsg(callback, callbackMsg, ShareResult.RESULT_SUCCESS);
					}
				}
				
				mHandler.sendEmptyMessageDelayed(MSG_DISMISS_SHARE_DIALOG, 100);
			}
		});
		mFbShareDialog.show();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(LOG_TAG, "onActivityResult():" + requestCode);
		if (requestCode == 64206) {
			// On facebook Activity result.
			Session.getActiveSession().onActivityResult((Activity)mContext, requestCode, resultCode, data);
		} else if (requestCode == GooglePlayBillingOperator.GOOGLE_PURCHASE_REQUEST_CODE){
			mBillingOperator.onPurchaseActivityOnResult(requestCode, resultCode, data);
		}
//		((MainActivity)mContext).unRegisterActivityResultListener(this);
	}
	
	public void checkGoogleInventory(int cbId, String data) {
		mBillingOperator.checkInventory(cbId, data);
	}
}
