package com.gplus.googleplay;


public class Constants {
	
	public final static String server_url = "https://msp.alipay.com/x.htm";

	public enum PlatformType{
		PLATFORM_UNDEFINE			("undefine", 				"??????"),
		PLATFORM_GOOGLE   			("pf_google", 				"è°·æ??åº???¨å??åº?"),
		PLATFORM_GOOGLE_MAINLAND   	("pf_google_mainland", 		"è°·æ??åº???¨å??åº?ï¼?å¤§é??ï¼?"),
		PLATFORM_BAIDU    			("pf_baidu", 				"??¾åº¦åº????"),
		PLATFORM_BAIDU_YOUGUO		("pf_baidu_youguo",			"??¾åº¦åº????ï¼?æ¸¸æ??ï¼?"),
		PLATFORM_91_GAME  			("pf_91_game", 				"91?????ºå?©æ??"),
		PLATFORM_ANDROID_MK			("pf_android_mk", 			"å®????å¸????"),
		PLATFORM_QIHOO_360			("pf_qihoo_360", 			"360?????ºå?©æ??"),
		PLATFORM_TENCENT			("pf_tencent", 				"??¾è???????ºå?©æ??"),
		PLATFORM_CHINA_MM			("pf_china_mm",				"ä¸???½ç§»???MM"),
		PLATFORM_MMY				("pf_mmy", 					"??¨è?????"),
		PLATFORM_ANZHI				("pf_anzhi", 				"å®???ºå?????"),
		PLATFORM_YINGYH				("pf_yingyh", 				"åº???¨æ??"),
		PLATFORM_JIFENG				("pf_jifeng", 				"??ºé??"),
		PLATFORM_MEIZU				("pf_meizu", 				"é­????"),
		PLATFORM_DANGLE				("pf_dangle", 				"å½?ä¹?"),
		PLATFORM_PP					("pf_pp",					"pp??©æ??"),
		PLATFORM_VIVO				("pf_vivo",					"VIVO"),
		PLATFORM_OPPO				("pf_oppo",					"OPPO"),
		PLATFORM_YIYH				("pf_yiyh",					"?????¨æ??"),
		PLATFORM_WANDJ				("pf_wandoujia",			"è±?è±????"),
		PLATFORM_XIAOCONG			("pf_xiaocong",				"å°????");
		
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
	
	public class PurchaseFinishKey {
		public static final String PURCHASE_RESULT = "result";
		public static final String PRODUCT_ID = "productId";
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
}
