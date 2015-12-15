package com.happiplay.tools;

import java.lang.reflect.Method;
import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.happiplay.MainActivity;
import com.happiplay.MainActivity.IActivityResult;
import com.happiplay.platform.Constants.CommonKey;
import com.happiplay.platform.Constants.PlatformType;
import com.happiplay.platform.Constants.ShareDataKey;
import com.happiplay.platform.Constants.ShareType;
import com.happiplay.platform.GameAppInfo;
import com.happiplay.platform.OpenSDKOperator;
import com.happiplay.platform.SDKOperatorFactory;
import com.starcloudcasino.winthree.R;

public class ShareTools {
	private static final String LOG_TAG = "ShareTools";

//	private static final String WECHAT_APP_ID = "wx144f290aff2d8da7";
//	private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;

	private static final int TAG_REQUEST_CONTACT_INFO = 1;
	private static final int MSG_SHARE_APPLICATION_FINISHED = 0;
	
	static public class AppShareInfo{
		public String title;
		public String summary;
		public String target_url;
		public String image_url;
		
		public String toString() {
			return new StringBuffer().append("share title:").append(title).append(",\n").
					append("share summary:").append(summary).append(",\n").
					append("target url:").append(target_url).append(",\n").
					append("image_url").append(image_url).append(".").toString();
		}
	}
	
	private static int mCallbackCmd;
	private static AppShareInfo mAppShareInfo;
	private static Handler mHandler = new Handler() {
		
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SHARE_APPLICATION_FINISHED:
				Log.d(LOG_TAG, "share finished.");
				Context context = ExternalCall.getGameContext();
				ShareResult result = (ShareResult) msg.obj;
				boolean success = false;
				if (result == null) {
					Log.d(LOG_TAG, "share result is null.");
					return ;
				}
				
				if (result.getResponse() == ShareResult.RESULT_SUCCESS) {
					success = true;
					Toast.makeText(context, context.getString(R.string.share_successed), Toast.LENGTH_SHORT).show();
				} else {
					success = false;
					Toast.makeText(context, context.getString(R.string.share_failed), Toast.LENGTH_SHORT).show();
				}
				JSONObject callbackData = new JSONObject();
				try {
				callbackData.put(CommonKey.STATUS, success ? 1 : 0);
				} catch (Exception e) {
					e.printStackTrace();
				}
				ExternalCall.instance.callUnity(mCallbackCmd, callbackData.toString());
				break;
			default:
				break;
			}
		}
	};
	
	private ShareTools() {
		// Never instantiate this class.
	}
	
	public static void share(Context context) {
		Log.d(LOG_TAG, "Do Sharing.");
		
		// shhare to wechat
		shareToWechat(context);
		
	}
	public static void share(Context context, String data, int callbackCmd) {
		mCallbackCmd = callbackCmd;
		if (mAppShareInfo == null) {
			mAppShareInfo = new ShareTools.AppShareInfo();
		}
		try {
			JSONObject shareData = new JSONObject(data);
			ShareType shareType = ShareType.values()[shareData.getInt(ShareDataKey.SHARE_TYPE)];
			mAppShareInfo.title = shareData.getString(ShareDataKey.SHARE_TITLE);
			mAppShareInfo.summary = shareData.getString(ShareDataKey.SHARE_SUMMARY);
			mAppShareInfo.target_url = shareData.getString(ShareDataKey.SHARE_TARGET_URL);
			mAppShareInfo.image_url = shareData.getString(ShareDataKey.SHARE_IMAGE_URL);
			Log.d(LOG_TAG, "share info:" + mAppShareInfo.toString());
			switch (shareType) {
			case SHARE_TO_CONTACTS:
				if (CommonTools.isSimReady(context)) {
					shareToContacts(context);
				} else {
					Toast.makeText(context, context.getString(R.string.sms_unavailable), Toast.LENGTH_LONG).show();
				}
				break;
			case SHARE_TO_FACEBOOK:
				shareToFacebook(context);
				break;
			case SHARE_TO_MAIL:
				shareToEmail(context);
				break;
			case SHARE_TO_QQ:
				shareToQQ(context);
				break;
			case SHARE_TO_QZONE:
				shareToQzone(context);
				break;
			case SHARE_TO_WECHAT:
				shareToWechat(context);
				break;
			case SHARE_TO_WECHAT_TIMELINE:
				shareToWechatTimeline(context);
				break;
			default:
				Log.e(LOG_TAG, "Undefine shareType");
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void shareToWechat(Context context) {
		Log.d(LOG_TAG, "Wechat shared to friend");
		shareToWechat(context, true);
	}
	
	public static void shareToWechatTimeline(Context context) {
		Log.d(LOG_TAG, "Share to Wechat Friend Circle.");
		shareToWechat(context, false);
	}
	
	public static void shareToWechat(Context context, boolean toFriend) {
//		WeiChatOperator.shareToWechat(mAppShareInfo, context, toFriend);
		try {
			Class<?> wechatOperator = getOperatorClassByName(context, "com.happiplay.platform.tencent.WeChatOperator");
			Method share = wechatOperator.getMethod("shareToWechat", new Class[]{AppShareInfo.class, Context.class, boolean.class});
			share.invoke(wechatOperator, mAppShareInfo, context, toFriend);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static Class<?> getOperatorClassByName(Context context, String clsName) throws Exception{
		return Class.forName(clsName);
	}
	
	private static void executeOperatorShareMethod(Context context, String clsName, String methodName, GameAppInfo appInfo) {
		try {
    		Class<?> operatorCls = getOperatorClassByName(context, clsName);
    		Method doSDKShareToFriend = operatorCls.getDeclaredMethod(methodName, new Class[]{AppShareInfo.class, Handler.class, int.class});
    		
    		OpenSDKOperator operator = (OpenSDKOperator) operatorCls.getDeclaredConstructor(Context.class).newInstance(context);
    		operator.setAppInfo(appInfo);
    		operator.sdkInit((Activity)context);
			doSDKShareToFriend.invoke(
					operator,
					mAppShareInfo, mHandler,
					MSG_SHARE_APPLICATION_FINISHED);
			doSDKShareToFriend.invoke(
					operatorCls.getDeclaredConstructor(Context.class).newInstance(context),
					mAppShareInfo, mHandler,
					MSG_SHARE_APPLICATION_FINISHED);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
	}
	
    public static void shareToQQ(Context context) {
    	if (BuildUtils.getPlatformType() == PlatformType.PLATFORM_TENCENT) {
    		Log.d(LOG_TAG, "do mainplatform share");
    		OpenSDKOperator operator = SDKOperatorFactory.getSDKOperator();
    		if (operator != null) {
    			operator.doSDKShareToFriend(mAppShareInfo, mHandler, MSG_SHARE_APPLICATION_FINISHED);
    		}
    	} else {
    		final String clsName = "com.happiplay.platform.tencent.OpenTencentSDKOperator";
    		final String methodName = "doSDKShareToFriend";
    		executeOperatorShareMethod(context, clsName, methodName, BuildUtils.getGameAppInfo(PlatformType.PLATFORM_TENCENT));
    	}
	}
    
    
    public static void shareToQzone(Context context) {
    	final String clsName = "com.happiplay.platform.tencent.OpenTencentSDKOperator";
    	final String methodName = "shareToQQZone";
    	executeOperatorShareMethod(context, clsName, methodName, BuildUtils.getGameAppInfo(PlatformType.PLATFORM_TENCENT));
    }
    
    public static void shareToTencentWeibo(Context context) {
    	final String clsName = "com.happiplay.platform.tencent.OpenTencentSDKOperator";
    	final String methodName = "sharedToTencentWeibo";
    	executeOperatorShareMethod(context, clsName, methodName, BuildUtils.getGameAppInfo(PlatformType.PLATFORM_TENCENT));
    }
    
    public static void shareToContacts(Context context) {
    	Log.d(LOG_TAG, "share to contacts");
    	final MainActivity activity = (MainActivity)context; 
    	IActivityResult onActivityResultHandler = new IActivityResult() {

    		@Override
    		public void onActivityResult(int requestCode, int resultCode, Intent data) {
    			if (requestCode == TAG_REQUEST_CONTACT_INFO) {
    				if (resultCode == Activity.RESULT_OK) {
    					Uri contactData = data.getData();
    					Log.i(LOG_TAG, "contact uri:" + contactData.toString());
    					String contactId = contactData.getLastPathSegment();
    					String phoneNum = getContactPhoneNumber(contactId);
    					sendShareMessage(activity, phoneNum);
    				} else {
    					Log.e(LOG_TAG, "query contact info failed.");
    				}
    			}
    			
    			activity.unRegisterActivityResultListener(this);
    		}
    		
    		private String getContactPhoneNumber(String contactId) {
    			Cursor cursor = null;
    			String phoneNum = null;
    			try {
    				ContentResolver resolver = activity.getContentResolver();
					cursor = resolver.query(
							CommonDataKinds.Phone.CONTENT_URI,
							null,
							CommonDataKinds.Phone.CONTACT_ID + "=" + contactId,
							null, null);
					if (cursor.moveToFirst()) {
						phoneNum = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Phone.NUMBER));
					} else {
						Log.e(LOG_TAG, "can not get contact phone number.");
					}
    			} catch (Exception e) {
    				
    			} finally {
    				if (cursor != null) {
    					cursor.close();
    				}
    			}
    			return phoneNum;
    		}

    	};
    	
    	Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
    	activity.registerActivityResultListener(onActivityResultHandler);
    	activity.startActivityForResult(intent, TAG_REQUEST_CONTACT_INFO);
    }
    
    private final static String SEND_SHARE_MESSAGE_ACTION = "send_share_massage";
    private static void sendShareMessage(final Context context, String phoneNum) {
		if (TextUtils.isEmpty(phoneNum)) {
			Log.e(LOG_TAG, "phone number is empty, can not be shared.");
			return ;
		}
		try {
			ArrayList<String> message = getSharedMessage();
			Log.d(LOG_TAG, "sendShareMessage to :" + phoneNum + ", message:" + message);
			SmsManager smsManager = SmsManager.getDefault();
			ArrayList<PendingIntent> sendIntents = new ArrayList<PendingIntent>();
			PendingIntent sendPIntent = PendingIntent.getBroadcast(context, 0, new Intent(SEND_SHARE_MESSAGE_ACTION), 0);
			sendIntents.add(sendPIntent);
			
			context.registerReceiver(new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					if (intent.getAction() == SEND_SHARE_MESSAGE_ACTION) {
						if (getResultCode() == Activity.RESULT_OK) {
							sendShareCallbackMsg(mHandler, MSG_SHARE_APPLICATION_FINISHED, ShareResult.RESULT_SUCCESS);
						} else {
							String resultData = getResultData();
							if (resultData == null) {
								Log.d(LOG_TAG, "failed:" + resultData);
							}
							Log.d(LOG_TAG, "code:" + getResultCode());
							sendShareCallbackMsg(mHandler, MSG_SHARE_APPLICATION_FINISHED, ShareResult.RESULT_SUCCESS, resultData);
						}
					}
				}
			}, new IntentFilter(SEND_SHARE_MESSAGE_ACTION));
			
			smsManager.sendMultipartTextMessage(phoneNum, null, message, sendIntents, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
    }
    
    private static ArrayList<String> getSharedMessage() throws Exception{
    	if (mAppShareInfo == null) {
    		throw new Exception("Get shared message failed: application share info is empty.");
    	}
    	
    	ArrayList<String> msg = new ArrayList<String>();
    	msg.add(mAppShareInfo.title + "\n");
    	msg.add(mAppShareInfo.summary + "\n");
    	msg.add(mAppShareInfo.target_url);
    	return msg;
    }
    
    public static void shareToFacebook(Context context) {
    	Log.d(LOG_TAG, "share to facebook.");
    	if (BuildUtils.getPlatformType() == PlatformType.PLATFORM_GOOGLE) {
    		Log.d(LOG_TAG, "do mainplatform share");
    		OpenSDKOperator operator = SDKOperatorFactory.getSDKOperator();
    		if (operator != null) {
    			operator.doSDKShareToFriend(mAppShareInfo, mHandler, MSG_SHARE_APPLICATION_FINISHED);
    		}
    	} else {
	    	final String clsName = "com.happiplay.platform.googleplay.OpenFacebookSDKOperator";
	    	final String methodName = "doSDKShareToFriend";
			executeOperatorShareMethod(context, clsName, methodName,
					BuildUtils.getGameAppInfo(PlatformType.PLATFORM_GOOGLE));
    	}
    }
    
    public static void sendFacebookFeed(Context context) {
    	Log.d(LOG_TAG, "send facebook feed.");
    	final String clsName = "com.happiplay.platform.googleplay.OpenFacebookSDKOperator";
    	final String methodName = "sendFacebooFeed";
		executeOperatorShareMethod(context, clsName, methodName,
				BuildUtils.getGameAppInfo(PlatformType.PLATFORM_GOOGLE));
    }
    
    public static void shareToEmail(Context context) {
    	final MainActivity activity = (MainActivity)context; 
    	IActivityResult onActivityResultHandler = new IActivityResult() {

    		@Override
    		public void onActivityResult(int requestCode, int resultCode, Intent data) {
    			if (requestCode == TAG_REQUEST_CONTACT_INFO) {
    				if (resultCode == Activity.RESULT_OK) {
    					Uri contactData = data.getData();
    					Log.i(LOG_TAG, "contact uri:" + contactData.toString());
    					String contactId = contactData.getLastPathSegment();
    					String emailAddr = getContactEmailAddr(contactId);
    					sendShareEmail(activity, emailAddr);
    				} else {
    					Log.e(LOG_TAG, "query contact info failed.");
    				}
    			}
    			
    			activity.unRegisterActivityResultListener(this);
    		}
    		
    		private String getContactEmailAddr(String contactId) {
    			Cursor cursor = null;
    			String emailAddr = null;
    			try {
    				ContentResolver resolver = activity.getContentResolver();
					cursor = resolver.query(
							CommonDataKinds.Email.CONTENT_URI,
							null,
							CommonDataKinds.Email.CONTACT_ID + "=" + contactId,
							null, null);
					if (cursor.moveToFirst()) {
						emailAddr = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Phone.DATA1));
					} else {
						Log.e(LOG_TAG, "can not get contact phone number.");
					}
    			} catch (Exception e) {
    				e.printStackTrace();
    			} finally {
    				if (cursor != null) {
    					cursor.close();
    				}
    			}
    			return emailAddr;
    		}

    	};
    	
    	Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
    	activity.registerActivityResultListener(onActivityResultHandler);
    	activity.startActivityForResult(intent, TAG_REQUEST_CONTACT_INFO);
    }

    private static void sendShareEmail(Context context, String addr) {
		if (TextUtils.isEmpty(addr)) {
			Log.e(LOG_TAG, "phone number is empty, can not be shared.");
			return ;
		}
    	Log.d(LOG_TAG, "send share email to : " + addr);
    	Intent intent = new Intent(Intent.ACTION_SENDTO);
    	intent.setType("plain/text");
    	intent.setData(Uri.parse(addr));
    	intent.putExtra(Intent.EXTRA_EMAIL, new String[] {addr});
    	intent.putExtra(Intent.EXTRA_SUBJECT, mAppShareInfo.title);
    	intent.putExtra(Intent.EXTRA_TEXT, "text mail");
    	
    	((Activity)context).startActivityForResult(Intent.createChooser(intent, context.getString(R.string.choose_send_email_app)), 1001);
    }
	
    // This should be a puzzling method here
	// it should always be call as an unility method
	// for those who want call back to ShareTool throw Handler
	// when sharing operation completed
	public static void sendShareCallbackMsg(int responseCode) {
		sendShareCallbackMsg(mHandler, MSG_SHARE_APPLICATION_FINISHED, responseCode);
	}
	
	public static void sendShareCallbackMsg(int responseCode, String content) {
		sendShareCallbackMsg(mHandler, MSG_SHARE_APPLICATION_FINISHED, responseCode, content);
	}
	
	public static void sendShareCallbackMsg(Handler handler, int what, int responseCode) {
		sendShareCallbackMsg(handler, what, responseCode, null);
	}
	
    public static void sendShareCallbackMsg(Handler handler, int what, int responseCode, String content) {
		Message msg = new Message();
		msg.what = what;
		msg.obj = new ShareResult(responseCode, content);
		handler.sendMessage(msg);
    }
}
