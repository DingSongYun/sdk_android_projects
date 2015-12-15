//package com.happiplay.tools;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Locale;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.content.BroadcastReceiver;
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.SharedPreferences;
//import android.content.Intent.ShortcutIconResource;
//import android.content.pm.PackageInfo;
//import android.content.pm.PackageManager;
//import android.net.wifi.WifiManager;
//import android.os.BatteryManager;
//import android.provider.Settings.Secure;
//import android.util.Log;
//
//import com.happiplay.ddz.AliPay;
//import com.happiplay.ddz.FPanelDownloadApk;
//import com.happiplay.ddz.GoogleCheckOut;
//import com.happiplay.ddz.ProductDetail;
//import com.happiplay.ddz.UpdateProfile;
//import com.happiplay.ddz.ExternalCall.BatteryInfoBroadcastReceiver;
//import com.unity3d.player.UnityPlayer;
//
//public class ExternalCallUtils {
	
//    public static String CMD_UNITY_AND_IOS_FACEBOOK_LOGIN = "facebooklogin";
//    public static String CMD_UNITY_AND_IOS_FACEBOOK_INVITE_ALL = "inviteall";
//    public static String CMD_UNITY_AND_IOS_FACEBOOK_INVITE = "invite";
//    public static String CMD_UNITY_AND_IOS_FACEBOOK_SEND_FEED = "publish_feed";
//
//    public static final int CMDID_UNITY_AND_IOS = 0x0;
//    public static final int CMDID_UNITY_AND_IOS_FACEBOOK_LOGIN = CMDID_UNITY_AND_IOS + 1;
//    public static final int CMDID_UNITY_AND_IOS_FACEBOOK_INVITE_ALL = CMDID_UNITY_AND_IOS + 2;
//    public static final int CMDID_UNITY_AND_IOS_FACEBOOK_INVITE = CMDID_UNITY_AND_IOS + 3;
//    public static final int CMDID_UNITY_AND_IOS_FACEBOOK_SEND_FEED = CMDID_UNITY_AND_IOS + 4;
//    public static final int CMDID_UNITY_AND_IOS_UPLOAD_PICTURE = CMDID_UNITY_AND_IOS + 5;
//    // photo
//    // data:{url:"upload address"}
//    public static final int CMDID_UNITY_AND_IOS_GET_IAP_LIST = CMDID_UNITY_AND_IOS + 6;
//    public static final int CMDID_UNITY_AND_IOS_PAY_ITEM = CMDID_UNITY_AND_IOS + 7;
//    public static final int CMDID_UNITY_AND_IOS_PAY_ITEM_CHECK = CMDID_UNITY_AND_IOS + 8;
//    public static final int CMDID_UNITY_AND_IOS_STAT = CMDID_UNITY_AND_IOS + 9;
//
//    public static final int CMDID_UNITY_AND_IOS_QQ_LOGIN = CMDID_UNITY_AND_IOS + 10;
//    public static final int CMDID_UNITY_AND_IOS_QQ_INVITE = CMDID_UNITY_AND_IOS + 11;
//    public static final int CMDID_UNITY_AND_IOS_SEND_SINA = CMDID_UNITY_AND_IOS + 12;
//    public static final int CMDID_UNITY_AND_IOS_SEND_QQ = CMDID_UNITY_AND_IOS + 13;
//    public static final int CMDID_UNITY_AND_IOS_PAY_ALIPAY = CMDID_UNITY_AND_IOS + 222;//14;
//    public static final int CMDID_UNITY_AND_IOS_GETDIVICE_NAME = CMDID_UNITY_AND_IOS + 15;
//    public static final int CMDID_UNITY_AND_IOS_APPURL_CHECK = CMDID_UNITY_AND_IOS + 17;
//    public static final int CMDID_UNITY_AND_IOS_GET_CURRENT_LOCAL_COUNTRY = CMDID_UNITY_AND_IOS + 14;//20;
//    public static final int CMDID_UNITY_AND_IOS_RUN_APP = CMDID_UNITY_AND_IOS + 21;
//    public static final int CMDID_UNITY_AND_IOS_Logout_FACEBOOK = CMDID_UNITY_AND_IOS + 22;
//
//    public static final int CMDID_UNITY_AND_IOS_GET_BATTERY = CMDID_UNITY_AND_IOS + 27;//获得IOS系统电量
//    public static final int CMDID_UNITY_AND_ANDROID_DOWNLOAD = CMDID_UNITY_AND_IOS + 100;//android本地下载
//    public static final int CMDID_UNITY_AND_ANDROID_BAIDU_LOGIN = CMDID_UNITY_AND_IOS + 110;
//    public static final int CMDID_UNITY_AND_ANDROID_BAIDU_PAY = CMDID_UNITY_AND_IOS + 111;
//    
//	public static final int CMDID_UNITY_AND_ANDROID_360_LOGIN = CMDID_UNITY_AND_IOS + 120;
//	public static final int CMDID_UNITY_AND_ANDROID_360_PAY = CMDID_UNITY_AND_IOS + 121;

//    // CMDID_UNITY_AND_IOS_FACEBOOK_LOGIN
//    // ����status:1�ɹ�0ʧ��,token:fb��access_token
//    public static final int Success = 1;
//    public static final int Error = 0;
//    public static Context mContext;
//
////	private boolean isChina = false;
//
//    // ������ݣ�{status:(-1���ʧ��,0���ɹ�)}
//    // ������ 17:20:01
//    // CMDID_UNITY_AND_IOS_PAY_ITEM
//    // unity�������:{ProductName:��Ʒ��,Price:�۸�,uid:�û�id,ProductID:"com.happi��..��Ʒ��id"}
//    // ���أ�{status:0} ���ɹ�
//    // ������ 17:22:06
//    // CMDID_UNITY_AND_IOS_STAT
//    // ͳ��unity���ͣ�{"point":pointName�����,"value":pointvalue���ֵ,"cate":cateName�������
//    // ����Ҫ������ݣ���ֱ�ӵ���callUnity����
//
//    // CMDID_UNITY_AND_IOS_FACEBOOK_SEND_FEED
//    // ������Ϸ��feed������facebook��½��Ч��android�����ж��Ƿ��õ����û�����Ϣ
//    // unity�������:"name":name,"description":description,"action":actionText,
//    // "toids":toids,"caption",captionText,"feedid",������Ϸ��fno���,"message":��Ӧfb�ӿ�˵����,
//    // "picture":feedͼ���ַ,"couldpop":�Ƿ��ܵ���:"refer":refer,"uid":uid,"nickname":nickname}
//
//    public ExternalCall(Context mContext) {
//        this.mContext = mContext;
//    }
//
//    static {
//        System.loadLibrary("LoginEncrypt");
//    }
//
//    public static native String happiLoginEncode(String urlstring);
//
//    public static String LoginEncode(String urlstring) {
//        System.out.println(urlstring);
//        String encodestr = happiLoginEncode(urlstring);
//        System.out.println(encodestr);
//        return encodestr;
//    }
//
//    public static String getMAC() {
//        String address = "";
//        try {
//            WifiManager wifiMgr = (WifiManager) UnityPlayer.currentActivity
//                    .getSystemService(Context.WIFI_SERVICE);
//            address = wifiMgr.getConnectionInfo().getMacAddress();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return address;
//    }
//
//    public static String getIMEI() {
//
////		TelephonyManager teleManager = (TelephonyManager) UnityPlayer.currentActivity
////				.getSystemService(Context.TELEPHONY_SERVICE);
//        String IMEI = "";
////		if (teleManager != null) {
////			try {
////				IMEI = teleManager.getDeviceId();
////			} catch (Exception e) {
////				e.printStackTrace();
////
////			}
////			teleManager = null;
////		}
////
////		String androidId = null;
////		try {
////			androidId = android.provider.Settings.Secure.getString(
////					UnityPlayer.currentActivity.getContentResolver(),
////					android.provider.Settings.Secure.ANDROID_ID);
////		} catch (Exception e) {
////			e.printStackTrace();
////
////		}
////		if ((IMEI == null || IMEI.equals("")) && androidId != null)
////			IMEI = androidId;
//        addShortcut();
//        return IMEI;
//
//    }
//
//    /**
//     * 为程序创建桌面快捷方式 1:已经创建过 0:没有创建过
//     */
//    private static void addShortcut() {
//        SharedPreferences sharedata = UnityPlayer.currentActivity
//                .getSharedPreferences("data", 0);
//        if (sharedata != null) {
//            int flag = sharedata.getInt("shortcut", 0);
//            if (flag != 1) {
//                Intent shortcut = new Intent(
//                        "com.android.launcher.action.INSTALL_SHORTCUT");
//
//                // 快捷方式的名称
//                shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,
//                        UnityPlayer.currentActivity
//                                .getString(R.string.app_name));
//                shortcut.putExtra("duplicate", false); // 不允许重复创建
//
//                // 指定当前的Activity为快捷方式启动的对象: 如 com.everest.video.VideoPlayer
//                // 注意: ComponentName的第二个参数必须加上点号(.)，否则快捷方式无法启动相应程序
//                ComponentName comp = new ComponentName(
//                        UnityPlayer.currentActivity.getPackageName(),
//                        ".HappiDDZProxyActivity");
//                shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(
//                        Intent.ACTION_MAIN).setComponent(comp));
//
//                // 快捷方式的图标
//                ShortcutIconResource iconRes = Intent.ShortcutIconResource
//                        .fromContext(UnityPlayer.currentActivity,
//                                R.drawable.app_icon);
//                shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
//
//                UnityPlayer.currentActivity.sendBroadcast(shortcut);
//
//                SharedPreferences.Editor editor = UnityPlayer.currentActivity
//                        .getSharedPreferences("data", 0).edit();
//                editor.putInt("shortcut", 1);
//                editor.commit();
//            }
//        }
//    }
//    
//    
//    
//    public void StartCheckOut(Context mContext, int cmdid, String data) {
//        JSONObject jsonObject;
//        try {
//            jsonObject = new JSONObject(data);
//            String checkOutId = jsonObject.getString("ProductID");
//            int uid = jsonObject.getInt("uid");
//            String url = jsonObject.getString("url");
//            // Log.i("������ַ", "������ַurl=" + url);
//            // CMDID_UNITY_AND_IOS_PAY_ITEM
//            // unity�������:{ProductName:��Ʒ��,Price:�۸�,uid:�û�id,ProductID:"com.happi��..��Ʒ��id"}
//            // ���أ�{status:0} ���ɹ�
//
//            Intent intent = new Intent(mContext, GoogleCheckOut.class);
//            intent.putExtra("checkOutId", checkOutId);
//            intent.putExtra("cmdid", cmdid);
//            intent.putExtra("uid", uid);
//            intent.putExtra("url", url);
//            mContext.startActivity(intent);
//
//        } catch (JSONException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
//
//
//    /**
//     * ����֧����֧��
//     */
//    public void StartAliPay(Context mContext, int cmdid, String data) {
//        JSONObject jsonObject;
//        try {
//            jsonObject = new JSONObject(data);
//            String url = jsonObject.getString("url");
//            Log.i("������ַ", "������ַurl=" + url);
//            String subject = jsonObject.getString("ProductName");
//            String body = subject;
//            String price = jsonObject.getString("Price");
//            int resId = jsonObject.getInt("uid");
//            String proId = jsonObject.getString("ProductID");
//            AliPay aliPay = new AliPay(mContext, cmdid);
//            ProductDetail detail = aliPay.new ProductDetail(subject, body,
//                    price, resId, proId);
//            detail.notifyUrl = url;
//            aliPay.payWithAlipay(detail);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void getDName(int cmdid, String data) {
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("cmd", cmdid);
//            jsonObject.put("name", android.os.Build.MODEL);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        callUnity(cmdid, jsonObject.toString());
//    }
//
//    public void getLocaleCountry(int cmdid, String data) {
//        String country = "Other";
////		if (IpChina.ipIsChinaInland())
////			country = "China";
////		System.out.println("country=" + country);
//        String lang = Locale.getDefault().toString();
//        if ("zh_CN".equalsIgnoreCase(lang)) {
//            callUnity(
//                    cmdid,
//                    "{\"country\":\""
//                            + country
//                            + "\",\"langareacode\":\"zh_CN\",\"appleLang\":\"zh-Hant\"}");
//        } else if ("zh_TW".equalsIgnoreCase(lang)
//                || "zh_HK".equalsIgnoreCase(lang)) {
//            callUnity(
//                    cmdid,
//                    "{\"country\":\""
//                            + country
//                            + "\",\"langareacode\":\"zh_TW\",\"appleLang\":\"zh-Hans\"}");
//        } else {
//            callUnity(cmdid, "{\"country\":\"" + country
//                    + "\",\"langareacode\":\"en_US\",\"appleLang\":\"en\"}");
//        }
//    }
//
//    public void appCenter(int cmdid, String data) {
//        System.out.println("appcenter data = " + data);
//        try {
//            JSONArray jsonArray = new JSONArray(data);
//            System.out.println("tostring=" + jsonArray.toString());
//            if (jsonArray != null) {
//                ArrayList<JSONObject> list = new ArrayList<JSONObject>();
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONObject obj = jsonArray.getJSONObject(i);
//                    String appPkgName = obj.getString("appurl").toString();
//                    if (isInstalled(appPkgName))
//                        obj.put("isInstalled", "1");
//                    else
//                        obj.put("isInstalled", "0");
//                    list.add(obj);
//                }
//                for (int i = 0; i < list.size(); i++) {
//                    if (isMyApp(list.get(i).getString("appurl")))
//                        list.remove(i);
//                }
//                System.out.println("json array = " + list.toString());
//                callUnity(cmdid, list.toString());
//            }
//        } catch (JSONException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
//
//    private void runOtherApp(int cmdid, String data) {
//        Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(data);
//        if (intent != null)
//            mContext.startActivity(intent);
//    }
//
//    private void logoutFb(int cmdid, String data) {
//        System.out.println("logout facebook!");
//        FacebookPlugin.instance().logoutFb(1);
//    }
//
//    private boolean isInstalled(String pkgName) {
//        PackageManager pageManage = mContext.getPackageManager();
//        List<PackageInfo> packages = pageManage.getInstalledPackages(0);
//        for (int i = 0; i < packages.size(); i++) {
//            PackageInfo packageInfo = packages.get(i);
//            String name = packageInfo.packageName;
//            if (pkgName.equals(name))
//                return true;
//        }
//        return false;
//    }
//
//    private boolean isMyApp(String pkgName) {
//        String myPackageName = mContext.getPackageName();
//        if (myPackageName.equals(pkgName))
//            return true;
//        return false;
//    }
//
//    private int batteryCmdid = 0;
//    private BatteryInfoBroadcastReceiver receiver = null;
//
//    private void getBattery(int cmdid, String data) {
//        batteryCmdid = cmdid;
//        receiver = new BatteryInfoBroadcastReceiver();
//        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
//        mContext.registerReceiver(receiver, filter);
//    }
//
//    private class BatteryInfoBroadcastReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            // TODO Auto-generated method stub
//            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
//                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);// 获取当前电量
//                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);// 获取总电量
//                callUnity(batteryCmdid, ((float) level / scale) + "");
//                if (receiver != null) {
//                    mContext.unregisterReceiver(receiver);
//                    receiver = null;
//                }
//                batteryCmdid = 0;
//            }
//        }
//    }
//
//    public static boolean isOnLogin = false;
//
//    private void LoginQQ2(int cmdid, String data) {
////        Intent intent = new Intent(mContext, FPanelLoginQQ2.class);
////        intent.putExtra("cmdid", cmdid);
////        isOnLogin = true;
////        mContext.startActivity(intent);
//    }
//
//    private void InviteQQFriend(int cmdid, String data) {
////        Intent intent = new Intent(mContext, FPanelShare2QQ.class);
////        intent.putExtra("cmdid", cmdid);
////        isOnLogin = true;
////        mContext.startActivity(intent);
//    }
//
//    private void downloadApk(int cmdid, String data) {
//        System.out.println("downloadApk="+data);
//        if(data==null || data=="")
//            return;
//        new FPanelDownloadApk(mContext, cmdid, data);
//    }
//
//
//}
