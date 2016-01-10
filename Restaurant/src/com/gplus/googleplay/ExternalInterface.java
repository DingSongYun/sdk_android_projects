package com.gplus.googleplay;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.gplus.googleplay.GooglePlayBillingOperator.PurchaseListener;
import com.gplusplus.nekores.StartActivity;
import com.unity3d.player.UnityPlayer;

public class ExternalInterface {
	private final static String LOG_TAG = "ExternalInterface";
	
	private final static String EXTERMAL_AGENT = "ExternalInterface";
	private final static String UNITY_CALL_BACK_PAY = "OnPurchaseProduct";
	
	public static ExternalInterface instance = new ExternalInterface();
	
	private ExternalInterface () {}
	
	public void Purchase (final String productId) {
		StartActivity.instance.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				PayInfo payInfo = new PayInfo();
				payInfo.setProductId(productId);
				StartActivity.instance.GetPayOperator().pay(payInfo, new PurchaseListener() {
					
					@Override
					public void OnPurchase(boolean isSucc) {
						Log.d(LOG_TAG, "OnPurchase: " + isSucc);
						
						JSONObject jsonData = new JSONObject();
						try {
							jsonData.put(Constants.PurchaseFinishKey.PURCHASE_RESULT, isSucc ? 0 : 1);
							jsonData.put(Constants.PurchaseFinishKey.PRODUCT_ID, productId);
							
						} catch (JSONException e) {
							e.printStackTrace();
						} finally {
							CallUnity(UNITY_CALL_BACK_PAY, jsonData.toString());
						}
					}
				});
			}
		});
	}
	
	public void fetchProductsForIds (final String goodIds) {
		StartActivity.instance.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				StartActivity.instance.GetPayOperator().checkInventory(goodIds);
			}
		});
	}
	
	public void CallUnity (String method, String data) {
		UnityPlayer.UnitySendMessage(EXTERMAL_AGENT, method, data);
	}
	
	public boolean checkApplication(String packageName) {
		Log.d("Restaurant", "checkApplication: " + packageName);
		  if (packageName == null || "".equals(packageName)){
		      return false;
		  }
		  try {
		      ApplicationInfo info = StartActivity.instance.getPackageManager()
		    		  .getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
		      if (info != null) {
		    	  Log.d("Restaurant", "checkApplication => Exist Package" );
		    	  return true;
		      }

		  } catch (NameNotFoundException e) {
			  Log.d("Restaurant", "checkApplication => No Package" );
		      return false;
		  }
		  
		  return false;
	}
	
	private final static String TMP_SHARE_IMAGE_PATH = "/mnt/sdcard/";
	
	public void shareToFacebook (String text, String imagePath) {
		imagePath = imagePath + ".png";
		Log.e("Restaurant", "Share To Facebook => " + text + "|" + imagePath);
		String[] tmpStrs = imagePath.split("/");
		imagePath = tmpStrs [tmpStrs.length - 2] + "/" + tmpStrs[tmpStrs.length - 1];
				
		Intent intent = new Intent ();
		intent.setAction(Intent.ACTION_SEND);
		intent.setPackage("com.facebook.katana");
		intent.putExtra (Intent.EXTRA_TEXT, text);
		intent.setType("image/png");
		
		File file = new File (TMP_SHARE_IMAGE_PATH, "shareImage.png");

        try {
        	file.createNewFile();
        	
			InputStream rawFileStream = StartActivity.instance.getAssets().open(imagePath);
			FileOutputStream fileStream = new FileOutputStream(file, false);
			byte[] bt = new byte[1024];
            int len = -1;
            while((len = rawFileStream.read(bt)) != -1){
            	fileStream.write(bt, 0, len);
            }
            
            fileStream.flush();
            
            rawFileStream.close();
            fileStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Log.e("Restaurant", file.length() + "|" + file.getAbsolutePath() + "|" + imagePath);
		intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
		
		StartActivity.instance.startActivity(intent);
	}
	
	public void shareToTwitter (String text, String imagePath) {
		Log.e("Restaurant", "Share To Twitter => " + text + "|" + imagePath);

		Intent intent = new Intent ();
		intent.setAction(Intent.ACTION_SEND);
		intent.setPackage("com.twitter.android");
		
		if (TextUtils.isEmpty(imagePath)) {
			intent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_TEXT, text);
		} else {
			imagePath = imagePath + ".png";
			String[] tmpStrs = imagePath.split("/");
			imagePath = tmpStrs [tmpStrs.length - 2] + "/" + tmpStrs[tmpStrs.length - 1];
			
			intent.setType("image/png");
			intent.putExtra (Intent.EXTRA_TEXT, text);
			File file = new File (TMP_SHARE_IMAGE_PATH, "shareImage.png");
	
	        try {
	        	file.createNewFile();
	        	
				InputStream rawFileStream = StartActivity.instance.getAssets().open(imagePath);
				FileOutputStream fileStream = new FileOutputStream(file, false);
				byte[] bt = new byte[1024];
	            int len = -1;
	            while((len = rawFileStream.read(bt)) != -1){
	            	fileStream.write(bt, 0, len);
	            }
	            
	            fileStream.flush();
	            
	            rawFileStream.close();
	            fileStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        Log.e("Restaurant", file.length() + "|" + file.getAbsolutePath() + "|" + imagePath);
	        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
		}
		
		StartActivity.instance.startActivity(intent);
	}
}
