package com.happiplay.platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.happiplay.platform.Constants.CommonKey;
import com.happiplay.platform.Constants.LoginDataKey;
import com.happiplay.platform.Constants.PayDataKey;
import com.happiplay.platform.Constants.PaymentConfirmKey;
import com.happiplay.platform.Constants.PlatformType;
import com.happiplay.platform.GameAppInfo.PayMode;
import com.happiplay.tools.BuildUtils;
import com.happiplay.tools.ExternalCall;
import com.happiplay.tools.ShareTools;
import com.happiplay.tools.ShareTools.AppShareInfo;
import com.starcloudcasino.winthree.R;

/**
 * @author Tdsy
 * Base class for platform SDK operation
 */
public abstract class OpenSDKOperator {
	protected final static String LOG_TAG = "OpenSDKOperator";
	protected Context mContext = null;
	protected GameAppInfo mAppInfo = null;
	protected UserInfo mUserInfo = new UserInfo();
	
	public class UserInfo {
		public String userId = "";
		public String userName = "";
		public String token = "";
		public String expire_at = "";
		
		public boolean isTokenValide() {
			if(TextUtils.isEmpty(expire_at)) {
				return true;
			}
			long expireDate = Long.parseLong(expire_at);
			return System.currentTimeMillis() <= expireDate;
		}
		
		public String toString() {
			return "userInfo => " + 
					"id: " + (userId == null ? "NULL" : userId) +
					", name:" + (userName == null ? "NULL" : userName) + 
					", token:" + (token == null ? "NULL" : token);
		}
	}
	
	public OpenSDKOperator(Context context) {
		mContext = context;
	}
	
	public abstract void doSDKLogin(String data);
	
	public abstract void doSDKPay( String data, PayMode payMode);
	
	public abstract void doSDKLogout( String data);
	
	public abstract void onLoginFinished(String data);
	
	/**
	 * This will be usefull later
	 */
	public void getProducts(String data) {
		Log.d(LOG_TAG, "getProducts() => Please implement this method in subclass");
	}
	
	public void doPaymentCheck(final PayInfo bill) {
		final int QUERY_INTEVER = 3 * 1000;
		final int LIMIT_TIME = 20; // at least for 20s * 3 = 1min, is it OK?
		new Thread(new Runnable() {

			@Override
			public void run() {
				long ticker = System.currentTimeMillis();
				int queryTimes = 0;
				HttpClient client = new DefaultHttpClient();
				if (TextUtils.isEmpty(bill.getConfirmUri())) {
					return ;
				}
				HttpGet getReq = new HttpGet(bill.getConfirmUri() + "?"
						+ PaymentConfirmKey.USER_ID + "=" + bill.getAppUserId() + "&"
						+ PaymentConfirmKey.ORDER_ID + "=" + bill.getOrderId() + "&"
						+ PaymentConfirmKey.PRODUCT_ID + "=" + bill.getProductId() + "&"
						+ PaymentConfirmKey.PRICE + "=" + bill.getPrice());
				Log.d(LOG_TAG, "doPaymentCheck: " + getReq.getURI().toString());
				while(true) {
					try {
						Log.d(LOG_TAG, "Start to check payment");
						HttpResponse response = client.execute(getReq);
						if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
							Log.d(LOG_TAG, "Successfully request server.");
							
							BufferedReader reader = new BufferedReader(
									new InputStreamReader(
											response.getEntity().getContent()));
							StringBuffer responseStr = new StringBuffer();
							String line = null;
							while((line = reader.readLine()) != null) {
								responseStr.append(line);
							}
							reader.close();
							
							if (TextUtils.isEmpty(responseStr)) {
								Log.e(LOG_TAG, "Server response is empty");
								continue;
							}
							Log.d(LOG_TAG, "Get Response:" + responseStr.toString());
							// check orderId
							
							// check payment result
							boolean isPaySuccessed = "success".equals(responseStr.toString());
							
							if (isPaySuccessed) {
								Log.d(LOG_TAG, "Pay successed, Notify Unity Game to update user info");
								notifyUnityPayResult(true, bill);
								break;
							}
							
							if ((++queryTimes) >= LIMIT_TIME) {
								Log.d(LOG_TAG, "Payment checking over-time, predicate it failed.");
								notifyUnityPayResult(false, bill);
								break;
							}
							
							long curTime = System.currentTimeMillis();
							long sleepTime = QUERY_INTEVER - (curTime - ticker) % QUERY_INTEVER;
							ticker = curTime;
							Thread.sleep(sleepTime);
						}
					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
				}
			}}).start();
	}
	
	public void notifyUnityPayResult(boolean succeed, PayInfo payInfo) throws JSONException {
		Log.d(LOG_TAG, "notifyUnityPayResult");
		JSONObject checkResult = new JSONObject();
		try {
			checkResult.put(CommonKey.STATUS, succeed ? 1 : 0);
			JSONObject data = new JSONObject();
			assert(payInfo != null);
			data.put(PayDataKey.PRODUCT_ID, payInfo.getProductId());
			data.put(PayDataKey.PRODUCT_NAME, payInfo.getProductName());
			data.put(PayDataKey.PRODUCT_ICON, payInfo.getProductIcon());
			
			data.put(PayDataKey.APP_USER_ID, payInfo.getAppUserId());
			data.put(PayDataKey.APP_USER_NAME, payInfo.getAppUserName());
			data.put(PayDataKey.PLATFORM_USER_ID, payInfo.getPlatformUserId());
			data.put(PayDataKey.PLATFORM_USER_NAME, payInfo.getPlatformUserName());
			
			data.put(PayDataKey.PLATFORM, BuildUtils.getPlatformType().getPlatform());
			
			data.put(PayDataKey.PRICE, payInfo.getPrice());
			data.put(PayDataKey.ORDER_ID, payInfo.getOrderId());
			
			data.put(PayDataKey.MESSAGE, mContext.getString(R.string.sms_pay_hint));
			checkResult.put(CommonKey.DATA, data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ExternalCall.instance.callUnity(Constants.CMDID_ACU_NOTIFY_PAY_CHECKED_OVER, checkResult);
	}
	
	public void doSDKShareToFriend(AppShareInfo shareInfo, Handler callback, int callbackMsg) {
		Log.d(LOG_TAG, "doSDKShareToFrient() => Please implement this method in subclass,"
				+ "or nothing will happend here");
	}
	
	public void doSDKInviteFriend(AppShareInfo shareInfo, Handler callback, int callbackMsg) {
		Log.d(LOG_TAG, "doSDKInviteFriend() => Please implement this method in subclass,"
				+ "or nothing will happend here");
	}

	
	/**
	 * TO DO: The param Activity is necessary? or it can be cast from mCotext 
	 * @param activity
	 */
	public abstract void sdkInit(Activity activity);
	
	public abstract void sdkDestroy();
	
	public void setAppInfo(GameAppInfo info) {
		if (info == null) {
			Log.e(LOG_TAG, "GameAppInfo is null, this may cause app crash!!!");
		}
		mAppInfo = info;
	}
	
	
	/**
	 * Fill PayInfo with data
	 * @param data JSON data catch product info
	 * @return PayInfo
	 */
	public void parsePayInfo(PayInfo payInfo, String data) {
		try {
			Log.d(LOG_TAG, "parsePayInfo:" + data);
			JSONObject jsonData = new JSONObject(data);
			payInfo.setAppName(BuildUtils.getAppName());
			
			payInfo.setProductId(jsonData.getString(PayDataKey.PRODUCT_ID));
			payInfo.setProductName(jsonData.getString(PayDataKey.PRODUCT_NAME));
			payInfo.setProductIcon(jsonData.getString(PayDataKey.PRODUCT_ICON));
			
			payInfo.setAppUserId(jsonData.getString(PayDataKey.APP_USER_ID));
			payInfo.setAppName(jsonData.getString(PayDataKey.APP_USER_NAME));
			payInfo.setPlarformUserId(jsonData.getString(PayDataKey.PLATFORM_USER_ID));
			payInfo.setPlatformUserName(jsonData.getString(PayDataKey.PLATFORM_USER_NAME));
			
			payInfo.setNotifyUri(jsonData.getString(PayDataKey.NOTIFY_URI));
			payInfo.setConfirmUri(jsonData.getString(PayDataKey.CONFRIM_URI));
			
			payInfo.setPrice(jsonData.getString(PayDataKey.PRICE));
			payInfo.setOrderId(jsonData.getString(PayDataKey.ORDER_ID));
			payInfo.setDescription(jsonData.getString(PayDataKey.DESCRIPTION));
			
			
			// Parse extra data.
			if (jsonData.has(CommonKey.EXTRA_DATA) && !jsonData.isNull(CommonKey.EXTRA_DATA)) {
				Log.d(LOG_TAG, "Get extra data from payInfo:");
				JSONObject extra = new JSONObject(jsonData.getString(CommonKey.EXTRA_DATA));
				Log.d(LOG_TAG, "extra json:" + extra.toString());
				HashMap<String, String> hashData = new HashMap<String, String>();
				for(Iterator<?> iter = extra.keys(); iter.hasNext();) {
					String key = (String)iter.next();
					String value = extra.getString(key);
					hashData.put(key, value);
				}
				Log.d(LOG_TAG, "extra data:" + hashData.toString());
				payInfo.setExtraData(hashData);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public JSONObject createLoginCbDataToUnity(boolean succeed, String userId,
			String username, String token, PlatformType platform) {
		JSONObject backData = new JSONObject();
		
		try {
	        backData.put(CommonKey.STATUS, succeed ? 1 :  0);
	        JSONObject data = new JSONObject();
	        if (succeed) {
	            data.put(LoginDataKey.token, token);
	            data.put(LoginDataKey.user_id, userId);
	            data.put(LoginDataKey.user_name, username);
	            data.put(LoginDataKey.platform, platform.getPlatform());
	        } else {
	        	// TODO: ErrorMsg~
	        	data.put(LoginDataKey.error, mContext.getString(R.string.login_failed));
	        }
	        backData.put(CommonKey.DATA, data);
		} catch (Exception e) {
			e.printStackTrace();
		}
        return backData;
	}
	
	public JSONObject createPayCbDataToUnity(boolean succeed, PayInfo payInfo) {
		JSONObject callbackData = new JSONObject();
		try {
			callbackData.put(CommonKey.STATUS, succeed ? 1 : 0);
			JSONObject data = new JSONObject();
			if (succeed) {
				assert(payInfo != null);
				data.put(PayDataKey.PRODUCT_ID, payInfo.getProductId());
				data.put(PayDataKey.PRODUCT_NAME, payInfo.getProductName());
				data.put(PayDataKey.PRODUCT_ICON, payInfo.getProductIcon());
				
				data.put(PayDataKey.APP_USER_ID, payInfo.getAppUserId());
				data.put(PayDataKey.APP_USER_NAME, payInfo.getAppUserName());
				data.put(PayDataKey.PLATFORM_USER_ID, payInfo.getPlatformUserId());
				data.put(PayDataKey.PLATFORM_USER_NAME, payInfo.getPlatformUserName());
				
				data.put(PayDataKey.PLATFORM, BuildUtils.getPlatformType().getPlatform());
				
				data.put(PayDataKey.PRICE, payInfo.getPrice());
				data.put(PayDataKey.ORDER_ID, payInfo.getOrderId());
				
//				data.put(PayDataKey.MESSAGE, mContext.getString(R.string.sms_pay_hint));
			}
			callbackData.put(CommonKey.DATA, data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return callbackData;
		
	}
	
}