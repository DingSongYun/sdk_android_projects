package com.happiplay.platform;

import com.happiplay.tools.ExternalCall;

public class Constants {
	
	public final static String server_url = "https://msp.alipay.com/x.htm";

	public enum PlatformType{
		PLATFORM_UNDEFINE			("undefine", 				"未知"),
		PLATFORM_GOOGLE   			("pf_google", 				"谷歌应用商店"),
		PLATFORM_GOOGLE_MAINLAND   	("pf_google_mainland", 		"谷歌应用商店（大陆）"),
		PLATFORM_BAIDU    			("pf_baidu", 				"百度应用"),
		PLATFORM_BAIDU_YOUGUO		("pf_baidu_youguo",			"百度应用（游果）"),
		PLATFORM_91_GAME  			("pf_91_game", 				"91手机助手"),
		PLATFORM_ANDROID_MK			("pf_android_mk", 			"安卓市场"),
		PLATFORM_QIHOO_360			("pf_qihoo_360", 			"360手机助手"),
		PLATFORM_TENCENT			("pf_tencent", 				"腾讯手机助手"),
		PLATFORM_CHINA_MM			("pf_china_mm",				"中国移动MM"),
		PLATFORM_MMY				("pf_mmy", 					"木蚂蚁"),
		PLATFORM_ANZHI				("pf_anzhi", 				"安智市场"),
		PLATFORM_YINGYH				("pf_yingyh", 				"应用汇"),
		PLATFORM_JIFENG				("pf_jifeng", 				"机锋"),
		PLATFORM_MEIZU				("pf_meizu", 				"魅族"),
		PLATFORM_DANGLE				("pf_dangle", 				"当乐"),
		PLATFORM_PP					("pf_pp",					"pp助手"),
		PLATFORM_VIVO				("pf_vivo",					"VIVO"),
		PLATFORM_OPPO				("pf_oppo",					"OPPO"),
		PLATFORM_YIYH				("pf_yiyh",					"易用汇"),
		PLATFORM_WANDJ				("pf_wandoujia",			"豌豆荚"),
		PLATFORM_XIAOCONG			("pf_xiaocong",				"小葱");
		
		private String mPlatform;
		private String mName;
		
		/**
		 * @param platform : platform code
		 * @name  name     : name of the platform
		 */
		private PlatformType(String platform, String name) {
			mPlatform = platform;
			mName = name;
		}
		
		public int getPlatformId() {
			return this.ordinal();
		}
		
		public String getPlatform() {
			return mPlatform;
		}
		
		public String getPlatformName() {
			return mName;
		}
		
		public String toString() {
			return mPlatform;
		}
		
	}
	public class CommonKey{
		public static final String STATUS = "status";
		public static final String DATA = "data";
		public static final String EXTRA_DATA = "extra_data";
	}
	
	public class PayDataKey{
		// Necessary params for payment
		public static final String PRODUCT_NAME = "product_name";
		public static final String PRODUCT_ID = "product_id";
		public static final String PRODUCT_ICON = "product_icon";
		public static final String PRICE = "price";
		public static final String NOTIFY_URI = "notify_uri";
		public static final String CONFRIM_URI = "confirm_uri";
		public static final String APP_USER_ID = "app_user_id";
		public static final String APP_USER_NAME = "app_user_name";
		public static final String PLATFORM_USER_ID = "pf_user_id";
		public static final String PLATFORM_USER_NAME = "pf_user_name";
		public static final String PLATFORM = "platform";
		public static final String DESCRIPTION = "description";
		
		// Optional params
		public static final String ORDER_ID = "order_id";
		public static final String EXCHAGE_RATE = "exchange_rate";
		public static final String EXTRA_DATA = "extra_data";
		public static final String PAY_CODE = "pay_code";
		public static final String MESSAGE = "msg";
		public static final String PAY_TYPE = "pay_type";
		public static final String MSG_EMAIL_FOR_GOODS = "email_for_goods";
	}
	
	public class PaymentConfirmKey{
		public static final String USER_ID = "user_id";
		public static final String ORDER_ID = "order_id";
		public static final String PRODUCT_ID = "product_id";
		public static final String PRICE = "price";
	}
	
	/**
	 * key of JSON data send to Unity after finish SDK login
	 * The callback data should follow the following format
	 * 		status : 0 : failed / 1 : successed
	 * 		data : JSON data => login result
	 * 			1. if status is 0 :
	 * 					error : errorMsg
	 * 			2 .if status is 1, the login result should contains userinfo as
	 * 					token : this can be Session / Token / Authorization Code
	 * 					user_id : userId or openId
	 * 					user_name : user nick name ?
	 * 					platform : platform string
	 */
	public class LoginDataKey{
		// Necessary params for login
		public static final String token = "token";
		public static final String user_id = "user_id";
		public static final String user_name = "user_name";
		public static final String platform = "platform";
		
		// Optinal params
		public static final String error = "error";
		public static final String expire_at = "expire_at"; // Time token will expire
	}

	public class JPushDataKey {
		public static final String USER_ID = "user_id";
		public static final String LANGUAGE = "language";
		public static final String PLATFORM = "platform";
		public static final String VERSION = "version";
	}
	
	public class PayType {
		public static final String PAY_TYPE_CHINA_MM 		= 		"pt_china_mm";
		public static final String PAY_TYPE_CHINA_UNICOM 	= 		"pt_china_unicom";
		public static final String PAY_TYPE_CHINA_TELECOM 	= 		"pt_china_teltcom";	
		public static final String PAY_TYPE_ALIPAY 			= 		"pt_alipay";
	}
	
	public enum ShareType {
		SHARE_UNDEFINE,
		SHARE_TO_FACEBOOK,
		SHARE_TO_QQ,
		SHARE_TO_QZONE,
		SHARE_TO_WECHAT,
		SHARE_TO_WECHAT_TIMELINE,
		SHARE_TO_MAIL,
		SHARE_TO_CONTACTS	
	}

	public class ShareDataKey {
		public final static String SHARE_TYPE = "share_type";
		public final static String SHARE_TITLE = "share_title";
		public final static String SHARE_SUMMARY = "share_summary";
		public final static String SHARE_TARGET_URL = "target_url";
		public final static String SHARE_IMAGE_URL = "image_url";
	}
	
	
	// UCA : Unity call Android
	private static final int CMDID_BASE = 1000;
	public static final int CMDID_UCA_LOGIN = CMDID_BASE + 0;
	public static final int CMDID_UCA_PAY = CMDID_BASE + 1;
	public static final int CMDID_UCA_HTTP_LOGIN_FINISHED = CMDID_BASE + 2;
	public static final int CMDID_ACU_SDK_LOGIN_FINISH = CMDID_BASE + 3;
	public static final int CMDID_ACU_SDK_PAY_FINISH = CMDID_BASE + 4;	
	public static final int CMDID_ACU_NOTIFY_PAY_CHECKED_OVER = CMDID_BASE + 5;
	public static final int CMDID_UCA_GET_PRODUCT = CMDID_BASE + 6;
	public static final int CMDID_ACU_GET_PRODUCTS_FINISH = CMDID_BASE + 7;
	public static final int CMDID_UCT_SHARE_APPLICATION = CMDID_BASE + 8;
	public static final int CMDID_UCA_LOGOUT = CMDID_BASE + 9;
	public static final int CMDID_UCA_INVENTORY_CHECKING_FOR_GOOGLE = CMDID_BASE + 10;
	
	public static final int CMDID_ACU_CLIENT_LOGIN = CMDID_BASE + 11;
	public static final int CMDID_ACU_CLIENT_LOGOUT = CMDID_BASE + 12;
	
	// For Android, share rule should be as follow:
	// 	 QQ Login : Share to QQ
	//	 guest and platform login : show share screen for choose where to share
	public static final int CMDID_UCA_INVITE_FRIEND = ExternalCall.CMDID_UNITY_AND_IOS_QQ_INVITE;
	
	public static final int CMDID_UCA_SHARE = ExternalCall.CMDID_UNITY_AND_IOS_WECHAT_SHARE;
	
	// UCT : Unity call Terminal(Android / IOS)
	// CMD string for Unity to select or take photo through terminal APIs
	// and then upload the picture used as user avatar to server
	public static final int CMDID_UCT_CHANGE_AVATAR = ExternalCall.CMDID_UNITY_AND_IOS_UPLOAD_PICTURE;
	
	// CMD for Unity to set JPush TAG
	public static final int CMDID_UCT_SET_JPUSH_TAG = ExternalCall.CMDID_UNITY_AND_IOS_JPUSH_TAG;
	
	// CMD to get Current Country info
	public static final int CMDID_UCT_GET_CURRENT_LOCAL_COUNTRY = ExternalCall.CMDID_UNITY_AND_IOS_GET_CURRENT_LOCAL_COUNTRY;
	
	public static final int CMDID_UCT_RUN_OTHER_APP = ExternalCall.CMDID_UNITY_AND_IOS_RUN_APP;
	
	// CMD to get System Battery
	public static final int CMDID_UCT_GET_BATTERY = ExternalCall.CMDID_UNITY_AND_IOS_GET_BATTERY;
	
	// CMD to download in China
	public static final int CMDID_UCT_DOWNLOAD = ExternalCall.CMDID_UNITY_AND_ANDROID_DOWNLOAD;
	
	// CMD : This cmd is for what?????
	public static final int CMDID_UCT_RATE_APP = ExternalCall.CMDID_UNITY_AND_IOS_RATE_APP;
	
}
