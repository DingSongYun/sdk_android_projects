package com.gplus.googleplay;

import android.util.Log;

import com.gplus.googleplay.GooglePlayBillingOperator.PurchaseListener;
import com.gplusplus.nekores.StartActivity;

public class ExternalInterface {
	private final static String LOG_TAG = "ExternalInterface";
	
	public ExternalInterface instance = new ExternalInterface();
	
	private ExternalInterface () {}
	
	public void Purchase (int productId) {
		PayInfo payInfo = new PayInfo();
		payInfo.setProductId(productId + "");
		StartActivity.instance.GetPayOperator().pay(payInfo, new PurchaseListener() {
			
			@Override
			public void OnPurchase(boolean isSucc) {
				Log.d(LOG_TAG, "OnPurchase: " + isSucc);
			}
		});
	}
	
}
