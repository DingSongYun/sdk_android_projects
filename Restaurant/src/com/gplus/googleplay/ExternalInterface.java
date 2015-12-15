package com.gplus.googleplay;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.gplus.googleplay.GooglePlayBillingOperator.PurchaseListener;
import com.gplusplus.nekores.StartActivity;
import com.unity3d.player.UnityPlayer;

public class ExternalInterface {
	private final static String LOG_TAG = "ExternalInterface";
	
	private final static String EXTERMAL_AGENT = "ExternalInterface";
	private final static String UNITY_CALL_BACK_PAY = "OnPurchaseProduct";
	
	public ExternalInterface instance = new ExternalInterface();
	
	private ExternalInterface () {}
	
	public void Purchase (final int productId) {
		PayInfo payInfo = new PayInfo();
		payInfo.setProductId(productId + "");
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
	
	public void CallUnity (String method, String data) {
		UnityPlayer.UnitySendMessage(EXTERMAL_AGENT, method, data);
	}
}
