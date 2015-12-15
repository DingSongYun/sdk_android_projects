package com.happiplay.tools;

import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.happiplay.FeedbackPage;
import com.happiplay.platform.Constants;
import com.happiplay.platform.Constants.CommonKey;
import com.happiplay.platform.Constants.JPushDataKey;
import com.happiplay.platform.Constants.LoginDataKey;
import com.happiplay.platform.Constants.PlatformType;
import com.happiplay.platform.GameAppInfo;
import com.happiplay.platform.OpenSDKOperator;
import com.happiplay.platform.SDKOperatorFactory;
import com.unity3d.player.UnityPlayer;

public class ExternalCall {
	private final static String LOG_TAG = "ExternalCall";
   
	/**
	 * The const string should be replaced by corresponding in Constants
	 */
	public static final int CMDID_UNITY_AND_IOS = 0x0;
	public static final int CMDID_UNITY_AND_IOS_UPLOAD_PICTURE = CMDID_UNITY_AND_IOS + 5;
	public static final int CMDID_UNITY_AND_IOS_JPUSH_TAG = CMDID_UNITY_AND_IOS + 110;
	public static final int CMDID_UNITY_AND_IOS_GET_CURRENT_LOCAL_COUNTRY = CMDID_UNITY_AND_IOS + 14;
	public static final int CMDID_UNITY_AND_IOS_RUN_APP = CMDID_UNITY_AND_IOS + 21;
	public static final int CMDID_UNITY_AND_IOS_GET_BATTERY = CMDID_UNITY_AND_IOS+27;
	public static final int CMDID_UNITY_AND_ANDROID_DOWNLOAD = CMDID_UNITY_AND_IOS+100;
	public static final int CMDID_UNITY_AND_IOS_RATE_APP = CMDID_UNITY_AND_IOS + 30;
	public static final int CMDID_UNITY_AND_IOS_WECHAT_SHARE = CMDID_UNITY_AND_IOS +24;
	public static final int CMDID_UNITY_AND_IOS_QQ_INVITE = CMDID_UNITY_AND_IOS +11;
	public static final int CMDID_UNITY_AND_IOS_APPURL_CHECK = CMDID_UNITY_AND_IOS + 17;
	
    public static ExternalCall instance = null;
    private static Context mContext = null;
    
    public ExternalCall(Context context) {
    	mContext = context;
    }
    
    public static void makeInstance(Context context) {
    	if (instance != null) {
    		return ;
    	}
    	instance = new ExternalCall(context);
    }

    /**
     * @param cmd  CMD for kinds of operation
     * @param cmdid CMD callback id in Unity
     * @param cmddata CMD extra data
     */
    public void unityCall(int cmd, int cmdid, String cmddata) {
    	assert(mContext != null);
    	Log.d(LOG_TAG, "UnityCall:" + cmd + ", callback cmd:" + cmdid);
        switch (cmd) {
        case Constants.CMDID_UCT_CHANGE_AVATAR:
        	startChangeProfile(mContext, cmdid, cmddata);
        	break;
        case Constants.CMDID_UCT_SET_JPUSH_TAG:
//        	setJPushAliasAndTags(cmdid, cmddata);
        	break;
		case Constants.CMDID_UCT_GET_CURRENT_LOCAL_COUNTRY:
			CommonTools.getLocaleCountry(cmdid, cmddata);
			break;
		case Constants.CMDID_UCT_RUN_OTHER_APP:
			runOtherApp(cmdid, cmddata);
			break;
		case Constants.CMDID_UCT_GET_BATTERY:
			CommonTools.getBattery(mContext, cmdid, cmddata);
			break;
		case Constants.CMDID_UCT_DOWNLOAD:
			downloadApk(cmdid, cmddata);
			break;
		case Constants.CMDID_UCT_RATE_APP:
			rateApp();
			break;
        case Constants.CMDID_UCA_INVITE_FRIEND:
        	invite(PlatformType.PLATFORM_TENCENT, cmddata);
        	break;
        case Constants.CMDID_UCA_SHARE:
//        	ShareTools.share(mContext);
        	ShareTools.shareToQQ(mContext);
        	break;
        case Constants.CMDID_UCT_SHARE_APPLICATION:
        	share(cmddata, cmdid);
        	break;
		case CMDID_UNITY_AND_IOS_APPURL_CHECK:
			appCenter(cmdid, cmddata);
			break;
		case Constants.CMDID_UCA_INVENTORY_CHECKING_FOR_GOOGLE:
			checkGoogleInventory(cmdid, cmddata);
			break;
		default:
			break;
        }
    }

	public void unityCall(final int cmd, final String cmddata) {
    	Log.d(LOG_TAG, "UnityCall:" + cmd);
        switch (cmd) {
        case Constants.CMDID_UCA_LOGIN:
        	login(getPlatformFromCalling(cmddata), cmddata);
        	break;
        case Constants.CMDID_UCA_LOGOUT:
        	logout(getPlatformFromCalling(cmddata), cmddata);
        	break;
        case Constants.CMDID_UCA_PAY:
        	pay(getPlatformFromCalling(cmddata), cmddata);
        	break;
        case Constants.CMDID_UCA_GET_PRODUCT/*CMDID_UCA_GET_PRODUCTS*/:
        	getProducts(getPlatformFromCalling(cmddata), cmddata);
        	break;
        case Constants.CMDID_UCA_HTTP_LOGIN_FINISHED:
        	notifyLoginFinished(getPlatformFromCalling(cmddata), cmddata);
        	break;
        default:
            break;
        }
    }
    
    private PlatformType getPlatformFromCalling(String data) {
    	PlatformType platform = PlatformType.PLATFORM_UNDEFINE;
    	try {
	    	JSONObject jsonData = new JSONObject(data);
	    	String platformStr = null;
	    	if (jsonData.has(LoginDataKey.platform)) {
	    		platformStr = jsonData.getString(LoginDataKey.platform);
	    	} else {
	    		if (jsonData.has(CommonKey.DATA)) {
	    			JSONObject content = new JSONObject(jsonData.getString(CommonKey.DATA));
	    			platformStr = content.getString(LoginDataKey.platform);
	    		}
	    	}
	    	platform = GameAppInfo.parsePlatformType(platformStr);
    	} catch (JSONException e) {
    		e.printStackTrace();
    	}
    	return platform;
    }
    
	/**
	 * Internal pay that will dispatch to SDK pay action
     * @param platform The platform user will login
     * @param cmdid CMD callback id in Unity.
     * @param cmddata CMD extra data
     */
    private void pay(PlatformType platform, String cmddata) {
    	Log.i(LOG_TAG, "ExternlCall pay in platform " + platform);
    	if(platform == PlatformType.PLATFORM_UNDEFINE) {
    		Log.e(LOG_TAG, "Undifine platform. Please check project configuration.");
    		return;
    	}
    	OpenSDKOperator operator = null;
    	if (platform == BuildUtils.getPlatformType()) {
    		Log.d(LOG_TAG, "pay in main platfrom.");
    		operator = SDKOperatorFactory.getSDKOperator();
    	} else {
    		Log.d(LOG_TAG, "pay in other platform");
    		operator = SDKOperatorFactory.makeSDKOperator(mContext, platform);
    	}
    	
    	operator.doSDKPay(cmddata, GameAppInfo.PayMode.FIXED_PAYMENT);
	}

	/**
	 * Internal pay that will dispatch to SDK pay action
     * @param platform The platform user will login
     * @param cmdid CMD callback id in Unity.
     * @param cmddata CMD extra data
     */
    private void login(PlatformType platform, String cmddata) {
    	Log.i(LOG_TAG, "ExternlCall login in platform " + platform);
    	if(platform == PlatformType.PLATFORM_UNDEFINE) {
    		Log.e(LOG_TAG, "Undifine platform. Please check project configuration.");
    		return;
    	}
    	
    	if (platform == BuildUtils.getPlatformType()) {
    		Log.d(LOG_TAG, "Login in main platfrom.");
    		SDKOperatorFactory.getSDKOperator().doSDKLogin(cmddata);
    	} else {
    		Log.d(LOG_TAG, "Login in other platform");
    		OpenSDKOperator operator = SDKOperatorFactory.makeSDKOperator(mContext, platform);
    		operator.doSDKLogin(cmddata);
    	}
    	
    }
    
    private void logout(PlatformType platform, String cmddata) {
    	Log.i(LOG_TAG, "ExternlCall login out platform " + platform);
    	if(platform == PlatformType.PLATFORM_UNDEFINE) {
    		Log.e(LOG_TAG, "Undifine platform. Please check project configuration.");
    		return;
    	}
    	
    	if (platform == BuildUtils.getPlatformType()) {
    		Log.d(LOG_TAG, "Login out main platfrom.");
    		SDKOperatorFactory.getSDKOperator().doSDKLogout(cmddata);
    	} else {
    		Log.d(LOG_TAG, "Login out other platform");
    		OpenSDKOperator operator = SDKOperatorFactory.makeSDKOperator(mContext, platform);
    		if (operator != null) {
    			operator.doSDKLogout(cmddata);
    		}
    	}
    	
    }
    
    private void share(String data, int callbackCmd) {
    	Log.i(LOG_TAG, "share application:" + data);
    	try {
    		ShareTools.share(mContext, URLDecoder.decode(data,"unicode"), callbackCmd);
    		JSONObject jsonData = new JSONObject(URLDecoder.decode(data,"unicode"));
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    private void getProducts(PlatformType platform, String cmddata) {
    	Log.i(LOG_TAG, "ExternlCall login in platform " + platform);
    	if(platform == PlatformType.PLATFORM_UNDEFINE) {
    		Log.e(LOG_TAG, "Undifine platform. Please check project configuration.");
    		return;
    	}
    	OpenSDKOperator operator = null;
    	if (platform == BuildUtils.getPlatformType()) {
    		Log.d(LOG_TAG, "get main platfrom product list.");
    		operator = SDKOperatorFactory.getSDKOperator();
    	} else {
    		Log.d(LOG_TAG, "get other platform product list.");
    		operator = SDKOperatorFactory.makeSDKOperator(mContext, platform);
    	}
    	
    	operator.getProducts(cmddata);
    }

    /**
     * Unity should need to call this method when it finish login action
     * TODO: param should contains result state of login.
     * TODO: finish  this later.
     */
    private void notifyLoginFinished(PlatformType platform, String cmddata) {
    	Log.i(LOG_TAG, "login finished on " + platform);
    	if(platform == PlatformType.PLATFORM_UNDEFINE) {
    		Log.e(LOG_TAG, "Undifine platform. Please check project configuration.");
    		return;
    	}
    	
    	if (platform == BuildUtils.getPlatformType()) {
    		Log.d(LOG_TAG, "Finish login in main platfrom.");
    		SDKOperatorFactory.getSDKOperator().onLoginFinished(cmddata);
    	} else {
    		Log.d(LOG_TAG, "Login in other platform");
    		OpenSDKOperator operator = SDKOperatorFactory.makeSDKOperator(mContext, platform);
    		operator.onLoginFinished(cmddata);
    	}
    }
    
    private void invite(PlatformType platform, String cmddata) {
    	Log.d(LOG_TAG, "invite friend from : " + platform.getPlatform());
    	if (platform == PlatformType.PLATFORM_UNDEFINE) {
    		Log.e(LOG_TAG, "Undifine platform, can not invite othres.");
    		return ;
    	}
    	
    	if (platform == BuildUtils.getPlatformType()) {
//    		SDKOperatorFactory.getSDKOperator().doSDKInviteFriend();
    	} else {
//    		SDKOperatorFactory.makeSDKOperator(mContext, platform).doSDKInviteFriend();
    	}
		
	}
    
    public void callUnity(int cmdid, String backData) {
        try {
            JSONObject json = new JSONObject();
            json.put("cmdid", cmdid);
            json.put("data", backData);
            UnityPlayer.UnitySendMessage("_EXTERN_AGENT", "androidCallBack",
                    json.toString());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void callUnity(int cmdid, JSONObject backData) {
        try {
            JSONObject json = new JSONObject();
            json.put("cmdid", cmdid);
            json.put("data", backData);
            UnityPlayer.UnitySendMessage("_EXTERN_AGENT", "ExternalCall",
                    json.toString());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void sdkLoginFinish(JSONObject data) {
    	Log.d(LOG_TAG, "sdkLoginFinish()");
    	callUnity(Constants.CMDID_ACU_SDK_LOGIN_FINISH, data);
    }
    
    public void sdkPayFinish(JSONObject data) {
    	Log.d(LOG_TAG, "sdkPayFinish()");
    	callUnity(Constants.CMDID_ACU_SDK_PAY_FINISH, data);
    }
    
    public void sdkGetProductsFinish(JSONObject data) {
    	Log.d(LOG_TAG, "Get product done.)");
    	callUnity(Constants.CMDID_ACU_GET_PRODUCTS_FINISH, data);
    }
    
	/**
	 * What does this method to do?
	 * @param mContext
	 * @param cmdid
	 * @param data
	 */
	public void startChangeProfile(Context mContext, int cmdid, String data) {
		Log.d(LOG_TAG, "startChangeProfile()");
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(data);
			String uploadUrl = jsonObject.getString("uploadurl");
			Intent intent = new Intent(mContext, AvatarHelper.class);
			intent.putExtra("UploadUrl", uploadUrl);
			intent.putExtra("cmdid", cmdid);
			mContext.startActivity(intent);
//			new AvatarHelper().startChangeAvatar((MainActivity)mContext, cmdid, uploadUrl);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
	private void checkGoogleInventory(int cmdid, String cmddata) {
		if (BuildUtils.getPlatformType() != PlatformType.PLATFORM_GOOGLE) {
			Log.d(LOG_TAG, "Unnecessary for this platform.");
			return ;
		}
		OpenSDKOperator operator = SDKOperatorFactory.getSDKOperator();
		// OpenFacebookSDKOperator
		if (operator != null) {
			try {
			Method checkInventory = Class.forName("com.happiplay.platform.googleplay.OpenFacebookSDKOperator")
					.getDeclaredMethod("checkGoogleInventory", new Class[]{int.class, String.class});
			checkInventory.invoke(operator, cmdid, cmddata);
//			operator.checkGoogleInventory(cmdid, cmddata);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void appCenter(int cmdid, String data){
		System.out.println("appcenter data = "+data);
		try {
			JSONArray jsonArray = new JSONArray(data);
			System.out.println("tostring="+jsonArray.toString());
			if(jsonArray!=null) {
				ArrayList<JSONObject> list = new ArrayList<JSONObject>();
				for(int i=0;i<jsonArray.length();i++){
					JSONObject obj = jsonArray.getJSONObject(i);
					String appPkgName = obj.getString("appurl").toString();
					if(CommonTools.isAppInstalled(mContext, appPkgName))
						obj.put("isInstalled", "1");
					else
						obj.put("isInstalled", "0");
					list.add(obj);
				}
				
				for(int i=0;i<list.size();i++) {
					boolean isThisApp = false;
					if (mContext.getPackageName().equals(list.get(i).getString("appurl"))) {
						list.remove(i);
					}
				}
				System.out.println("json array = "+list.toString());
				callUnity(cmdid, list.toString());
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Context getGameContext() {
		return mContext;
	}
	
    /***********************Utility Method for Unity*******************************/
	public static String getPackageName() {
		return UnityPlayer.currentActivity.getPackageName();
	}
	
	public static String getDeviceID() {
		return CommonTools.getDeviceID(mContext);
	}
	
	
	public static String encryptSHA(String content) {
		Log.d(LOG_TAG, "encryptSHA: " + content);
		return EncryptionUtils.encryptSHA(content);
	}
	
	public static String getSimType() {
		String mobileType = "unknow";
		TelephonyManager telManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		if (telManager != null) {
			String imsi = telManager.getSubscriberId();
			if (TextUtils.isEmpty(imsi)) {
				return mobileType;
			}
			System.out.println("imsi=" + imsi);
			if (imsi.startsWith("46000") || imsi.startsWith("46002")) { // china mobile
				mobileType = "mobile";
			} else if (imsi.startsWith("46001")) { // china unicom
				mobileType = "unicom";
			} else if (imsi.startsWith("46003")) { // china telecom
				mobileType = "telecom";
			} else {
				mobileType = "unknow";
			}
		}
		return mobileType;
	}
	
	/**
	 * Get Android version with format
	 * 		"Android: " + verNum, e.g. Android: 1.0
	 * @return
	 */
	public static String getSystemVersion() {
		return "Android: " + android.os.Build.VERSION.SDK_INT;
	}
	
	public static int getFakePlatform() {
		String platform = /*SystemProperties.get("happiplay.debug.platform", "1")*/"1";
		return Integer.parseInt(platform);
	}
	
	/**
	 * For Url encryption by Unity3D
	 * @param urlstring
	 * @return
	 */
	static{  
        System.loadLibrary("LoginEncrypt");  
    }
	public static native String happiLoginEncode(String urlstring);
	public static String LoginEncode(String urlstring){
		if (TextUtils.isEmpty(urlstring)) {
			return "";
		}
		String encodestr= happiLoginEncode(urlstring);
		Log.d(LOG_TAG, "url:" + urlstring + ", encoded url:" + encodestr);
		return encodestr;
	}
	
    /**
     * For JPush
     * @param cmdid
     * @param langType
     */
    private static void setJPushAliasAndTags(String data) {
    	Log.d(LOG_TAG, "setJPushAliaAndTags()." + data);
    	if (TextUtils.isEmpty(data)) {
    		Log.e(LOG_TAG, "data is null.");
    		return ;
    	}
    	Set<String> tagSet = new LinkedHashSet<String>();
    	try {
    		JSONObject jsonData = new JSONObject(data);
	    	String user_id = jsonData.getString(JPushDataKey.USER_ID);
	    	String platform = jsonData.getString(JPushDataKey.PLATFORM);
	    	String lang = jsonData.getString(JPushDataKey.LANGUAGE);
	    	String ver = "v" + jsonData.getString(JPushDataKey.VERSION);
	    	ver = ver.replaceAll("\\.", "_");
	    	Log.d(LOG_TAG, "userID:" + user_id + ", platform:" + platform + ", lang:" + lang + ", ver:" + ver);
	    	
	    	tagSet.add(lang);
	    	tagSet.add(platform);
	    	tagSet.add(ver);
	    	JPushInterface.setAliasAndTags(mContext, user_id, tagSet, new TagAliasCallback() {
	    		@Override
	    		public void gotResult(int i, String s, Set<String> strings) {
	    			if(i == 0) {
	    				Log.d(LOG_TAG, "Set Jpush Alias and tag successfull.");
	    			} else {
	    				Log.e(LOG_TAG, "Set Jpush Alias and tag falied.(" + i + ", " + s + ")");
	    			}
	    		}
	    	});
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    /**
     * For Unity : Run other App
     * @param cmdid
     * @param data
     */
	private void runOtherApp(int cmdid, String data){
		Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(data); 
		if(intent != null)
			mContext.startActivity(intent); 
	}
	
	/**
	 * TODO : complete this method!!! 
	 */
	private void downloadApk(int cmdid, String data) {
		System.out.println("downloadApk="+data);
		if(data==null || data=="")
			return;
//		new FPanelDownloadApk(mContext, cmdid, data);
	}
	
	/**
	 * rating out game
	 */
	private void rateApp() {
		// This should do in Unity
		Log.e(LOG_TAG, "App rating should be done in Unity.");
	}
	
	public void getDName(int cmdid, String data) {
		Log.d(LOG_TAG, "getDName() => what this method for??");
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("cmd", cmdid);
			jsonObject.put("name", android.os.Build.MODEL);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		callUnity(cmdid, jsonObject.toString());
	}
	
	public String getLocaleSymbole() {
		Locale locale = mContext.getResources().getConfiguration().locale;
		NumberFormat format = DecimalFormat.getCurrencyInstance(locale);
		return format.getCurrency().getSymbol();
	}
	
	public static void startUserFeedbackPage(final String url) {
		if (TextUtils.isEmpty(url)) {
			Log.e(LOG_TAG, "load user feedback page failed, url is empty.");
			return;
		}
		((Activity)mContext).runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Intent intent = new Intent(mContext, FeedbackPage.class);
				intent.putExtra("loadurl", url);
				mContext.startActivity(intent);
			}
		});
	}
	
	public static void showAndriodProgress() {
		((Activity)mContext).runOnUiThread(new Runnable() {

			@Override
			public void run() {
				CommonTools.showProgress(mContext, ProgressDialog.STYLE_SPINNER, null, null);
			}
			
		});
	}
	
	public static void dismissAndroidProgress() {
		((Activity)mContext).runOnUiThread(new Runnable() {

			@Override
			public void run() {
				CommonTools.dismissProgress();
			}
			
		});
	}
	
	public static String getChannelId() {
		return CommonTools.getChannelId(mContext);
	}
	
	public static void requestOrientation(final int orientation) {
		final Activity activity = (Activity)mContext;
		activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				CommonTools.setupOrientation(activity, orientation);				
			}
		});
	}
}
