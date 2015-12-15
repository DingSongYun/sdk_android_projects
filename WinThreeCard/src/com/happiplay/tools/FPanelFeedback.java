package com.happiplay.tools;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.PluginState;

class WebViewPluginInterface
{
	public void call(String message)
	{
		if(message.contains("close")) {
			if(FPanelFeedback.instance != null) {
				FPanelFeedback.instance.closeFeedback();
			}
		}
	}
}

public class FPanelFeedback extends Activity {

	private WebView webView;
	private ValueCallback<Uri> mUploadMessage;
	
	public static FPanelFeedback instance;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		instance = this;
		
		Intent intent = getIntent();
		final String url = intent.getStringExtra("loadurl");
		
		FPanelFeedback.this.runOnUiThread(new Runnable() {
			public void run() {
				webView = new WebView(FPanelFeedback.this);
				
				WebSettings settings = webView.getSettings();
				settings.setJavaScriptEnabled(true);
				settings.setSupportZoom(false);
				settings.setDomStorageEnabled(true);
				settings.setPluginState(PluginState.ON);
				
				webView.loadUrl(url);  
				webView.addJavascriptInterface(new WebViewPluginInterface(), "Unity");
				webView.setFocusable(true);
				webView.setFocusableInTouchMode(true);
				webView.setClickable(true);
				webView.setEnabled(true);
				webView.setWebChromeClient(new MyWebChromeClient());
				webView.setWebViewClient(new WebViewClient());
				webView.requestFocus(View.FOCUS_DOWN);
				webView.setOnTouchListener(new OnTouchListener() {
		            @Override
		            public boolean onTouch(View v, MotionEvent event) {
		                    switch (event.getAction()) {
		            case MotionEvent.ACTION_DOWN:
		            case MotionEvent.ACTION_UP:
		                v.requestFocusFromTouch();
		                break;
		            }
		            return false;
		            }
				});

		        setContentView(webView);
			}
		});
		String evaljs =
	            "function callProxy(n){" +
	            "var proxy=App.Utils.getNativeProxy();" +
	            "var i=proxy.curIdx;" +
	            "var msg=n+'|'+proxy.args[i];" +
	            "window.Unity.call(msg);" +
	            "}";
		webView.loadUrl("javascript:" + evaljs);
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {  
			if(webView.canGoBack())
				webView.goBack(); //goBack()表示返回WebView的上一页面
			else
				finish();
            return true;  
        }  
		return super.onKeyUp(keyCode, event);
	}
	
	public void closeFeedback()
	{
		System.out.println("closeFeedback");
		finish();
	}
	
	class MyWebChromeClient extends WebChromeClient {
			
			/***************** android中使用WebView来打开本机的文件选择器 *************************/  
	        // js上传文件的<input type="file" name="fileField" id="fileField" />事件捕获  
	        // Android > 4.1.1 调用这个方法
	        public void openFileChooser(ValueCallback<Uri> uploadMsg,  
	                String acceptType, String capture) {  
	            openFileChooser(uploadMsg);
	        }  
	  
	        // 3.0 + 调用这个方法  
	        public void openFileChooser(ValueCallback<Uri> uploadMsg,  
	                String acceptType) {  
	            openFileChooser(uploadMsg);
	        }  
	  
	        // Android < 3.0 调用这个方法  
	        public void openFileChooser(final ValueCallback<Uri> uploadMsg) {
	        	final Activity a = FPanelFeedback.this; //UnityPlayer.currentActivity;
	        	a.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						mUploadMessage = uploadMsg;
			            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);  
			            intent.addCategory(Intent.CATEGORY_OPENABLE);  
			            intent.setType("image/*"); 
			            a.startActivityForResult(Intent.createChooser(intent, "Image Browser"), 1);
					}
				});
	        }  
		}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 1) {
			if (null == mUploadMessage)
				return;
			Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
			mUploadMessage.onReceiveValue(result);
			mUploadMessage = null;
		}
	}

}
