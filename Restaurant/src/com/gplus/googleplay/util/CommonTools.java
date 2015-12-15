package com.gplus.googleplay.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Locale;
import java.util.zip.GZIPInputStream;

import jp.co.tamtam.nekomeshiya.R;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class CommonTools {
	public static final String LOG_TAG = "CommonTools";

	public static final String SDCARD_PATH = "/mnt/sdcard/";
	
	private static CustomProgressDialog mProgress;
	
	private CommonTools() {
	}
	
    public static void showToast(Context context, String text) {
        Toast toast = new Toast(context);

        TextView textView = new TextView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        textView.setText(text);
        textView.setTextSize(15);
        textView.setTextColor(context.getResources().getColor(R.color.white));
        textView.setGravity(Gravity.CENTER);
        LinearLayout layout = new LinearLayout(context);
        layout.setGravity(Gravity.CENTER);
        layout.setBackgroundColor(context.getResources().getColor(R.color.transparent_write));
        LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(layoutParams);
        layout.addView(textView);
        layout.setPadding(10, 10, 10, 10);
        toast.setView(layout);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }
    public static Dialog showProgress(Context context, final int styleID) {
    	return showProgress(context, styleID, null, null);
    }
    
	public static Dialog showProgress(Context context, final int styleID,
			final String title, final String message) {
		if (mProgress != null && mProgress.isShowing()) {
			Log.d(LOG_TAG, "Progress is under showing");
			return null;
		}
		
		mProgress = new CustomProgressDialog(context, styleID);
		mProgress.setTitle(title);
		mProgress.setMessage(message);
		mProgress.setCancelable(false);
		mProgress.show();
		
		return mProgress;
	}
    
    public static void dismissProgress() {
    	if (mProgress != null && mProgress.isShowing()) {
    		mProgress.dismiss();
    		return ;
    	}
    }
    
    public static boolean isSdCardExist() {
    	return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
    			 || new File(SDCARD_PATH).exists();
    }
    
    public interface IProgressMonitor <T> {
    	public void onProcessStart();
    	public T onProcessInBackground();
    	public void onProcessFinish(final T value);
    	public void onCancelled();
    }
    
    /**
     * This mothod should be invoke from UI thread
     * @param <T>
     * @param context
     * @return
     */
    public static <T> void doProgressTask(final Context context, final int styleID, final String title,
			final String message, final IProgressMonitor<T> monitor) {
    	new AsyncTask<Void, Void, T>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				showProgress(context, styleID, title, message);
				monitor.onProcessStart();
			}

			@Override
			protected T doInBackground(Void... params) {
				return monitor.onProcessInBackground();
			}

			protected void onCancelled() {
//				if (mProgressDialog != null && mProgressDialog.isShowing()) {
//					mProgressDialog.dismiss();
//				}
				dismissProgress();
			};

			protected void onPostExecute(T result) {
//				if (mProgressDialog != null)
//					mProgressDialog.dismiss();
				dismissProgress();
//				pCallback.onCallback(result);
				monitor.onProcessFinish(result);
			}
		}.execute((Void[]) null);
    }
    
	
	/**
	 * For Unity : Get System battery
	 */
	public static void getBattery(Context context, final int cmdid, String data) {
		final BroadcastReceiver receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
		            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
		            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
		            //ExternalCall.instance.callUnity(cmdid, ((float)level/scale)+"");
	            	context.unregisterReceiver(this);
		        }
			}
			
		};
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        context.registerReceiver(receiver, filter);
	}
	
	public static void chmod(String permission, String path) {
		try {
			String command = "chmod " + permission + " " + path;
			Runtime runtime = Runtime.getRuntime();
			runtime.exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
	
	/**
	 * @param pkgName the package name of application
	 * @return if the specific application is installed at device
	 */
	public static boolean isAppInstalled(Context context, String pkgName) {
		if (TextUtils.isEmpty(pkgName)) {
			Log.e(LOG_TAG, "Ivalid application package name");
			return false;
		}
		
		Log.d(LOG_TAG, "isApkInstalled(" + pkgName + ")");
		PackageManager manager = context.getPackageManager();
		List<PackageInfo> pkgList = manager.getInstalledPackages(0);
		for (int i = 0; i < pkgList.size(); i++) {
			PackageInfo pI = pkgList.get(i);
			if (pI.packageName.equalsIgnoreCase(pkgName))
				return true;
		}
		return false;
	}
	
	/**
	 * Get apk information
	 * 
	 * @param context
	 * @param archiveFilePath
	 *            APK File path, e.g. /sdcard/download/XX.apk
	 */
	public static PackageInfo getApkInfo(Context context, String archiveFilePath) {
		PackageManager pm = context.getPackageManager();
		PackageInfo apkInfo = pm.getPackageArchiveInfo(archiveFilePath,
				PackageManager.GET_META_DATA);
		return apkInfo;
	}
	
	/*
	public static void addShortcut(Context context) {
		Log.d(LOG_TAG, "check Shortcut.");
		SharedPreferences sharedPref =  context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		if (sharedPref != null) {
			int flag = sharedPref.getInt("happiplay.texas.shortcut", 0);
			if (flag != 1) {
				Log.d(LOG_TAG, "Shortcut have never been added.");
				
				Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");

				// name of shortcut
				shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getString(R.string.app_name));
				shortcut.putExtra("duplicate", false);

				// ???å®?å½???????Activityä¸ºå¿«??·æ?¹å???????¨ç??å¯¹è±¡: å¦? com.everest.video.VideoPlayer
				// æ³¨æ??: ComponentName???ç¬?äº?ä¸??????°å??é¡»å??ä¸???¹å??(.)ï¼???????å¿???·æ?¹å?????æ³??????¨ç?¸å??ç¨?åº?
				ComponentName comp = new ComponentName(context.getApplicationContext(), MainActivity.class);
				shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(Intent.ACTION_MAIN).setComponent(comp));

				// icon of shortcur
				ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(context, R.drawable.app_icon);
				shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);

				context.sendBroadcast(shortcut);

				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putInt("happiplay.texas.shortcut", 1);
				editor.commit();
			}
		}
	}
	*/

	public static String getDeviceName() {
		return Build.MODEL;
	}
	
	public static String getDeviceID(Context context) {
		String deviceId = Secure.getString(context.getContentResolver(),
				Secure.ANDROID_ID);
		return deviceId;
	}
	
	public static String getVersionName(Context context) {
		PackageInfo packageInfo;
		try {
			packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static boolean isSimReady(Context context) {
        TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        switch(tm.getSimState()){
                case TelephonyManager.SIM_STATE_ABSENT : 
                case TelephonyManager.SIM_STATE_UNKNOWN :
                case TelephonyManager.SIM_STATE_NETWORK_LOCKED :
                case TelephonyManager.SIM_STATE_PIN_REQUIRED :
                case TelephonyManager.SIM_STATE_PUK_REQUIRED :
                	return false;
                case TelephonyManager.SIM_STATE_READY :
                	return true;
                default:
                	break;
        }
		return true;
	}
	
	public static void setupOrientation(Activity activity, int orientation) {
		int curOrientation = activity.getRequestedOrientation();
		Log.d(LOG_TAG, "setupOrientation: " + curOrientation + "=>" + orientation);
		if (curOrientation != orientation) {
//			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}
	
	public static String OpenHttpConnection(String urlString) {
		return OpenHttpConnection(urlString, 0);
	}
	
	private static final int SOCKET_TIME_OUT = 30;
	public static String OpenHttpConnection(String urlString, int retryTimes) {
		if (urlString == null) {
			return null;
		}
		InputStream in = null;
		int response = -1;
//		logSI("http-result=", "" + urlString);
		try {
			URL url = new URL(urlString);
			URLConnection conn = url.openConnection();
			HttpURLConnection httpConn = (HttpURLConnection) conn;
			httpConn.setRequestProperty("Accept-Encoding", "gzip,deflate");
			httpConn.setConnectTimeout(30 * 1000);
			httpConn.setReadTimeout(30 * 1000);
			response = httpConn.getResponseCode();
			if (httpConn != null && response == HttpURLConnection.HTTP_OK) {
				in = httpConn.getInputStream();
				ByteArrayInputStream inByte = new ByteArrayInputStream(
						stream2byte(in));
				BufferedReader reader = null;
				String enconding = httpConn.getContentEncoding();
				GZIPInputStream unGzip = null;
				if (enconding != null
						&& enconding.toLowerCase().trim().contains("gzip")) {
					unGzip = new GZIPInputStream(inByte);
					reader = new BufferedReader(new InputStreamReader(unGzip,
							"utf-8"));
				} else {
					reader = new BufferedReader(new InputStreamReader(inByte,
							"utf-8"));
				}
				StringBuilder sb = new StringBuilder();
				while (reader.ready()) {
					sb.append(reader.readLine() + "\n");
				}
				in.close();
				inByte.close();
				if (reader != null) {
					reader.close();
				}
				if (unGzip != null) {
					unGzip.close();
				}
				String result = sb.toString();
				return result;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			
			Log.e(LOG_TAG, "time out:" + retryTimes);
			if (retryTimes > 0) {
				OpenHttpConnection(urlString, retryTimes-1);
			}
			return null;
		}
		return null;
	}
	

	public static byte[] stream2byte(InputStream inStream) throws Exception {
		byte[] buffer = new byte[1024];
		int len = -1;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();
		outStream.close();
		inStream.close();
		return data;
	}
}
