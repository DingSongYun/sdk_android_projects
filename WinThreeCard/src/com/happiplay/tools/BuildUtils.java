package com.happiplay.tools;

import java.util.HashMap;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import android.util.SparseArray;

import com.happiplay.platform.Constants.PlatformType;
import com.happiplay.platform.GameAppInfo;

/**
 * @author Tdsy
 * Utilities to parse configuration for 
 * kinds of project and platform
 */
public class BuildUtils {
	private final static String LOG_TAG = "BuildUtils";
	private final static String CONFIGURE_FILE = "configure.xml";
	
	/** app info for main platform */
	private static GameAppInfo mMainPlatformInfo = null;
	
	/** app info for other platform which the mMainPlatformInfo may need*/
	private static SparseArray<GameAppInfo> mAppendPlatformInfo = null;
	
	public static String getAppName() {
		return mMainPlatformInfo.getAppName();
	}
	
	public static PlatformType getPlatformType() {
		return mMainPlatformInfo.getPlatform(); 
	}
	
	public static boolean getIsLandscape() {
		return mMainPlatformInfo.isLandscape();
	}
	
	public static GameAppInfo getGameAppInfo() {
		return getGameAppInfo(getPlatformType());
	}
	
	public static GameAppInfo getGameAppInfo(PlatformType type) {
		GameAppInfo app = null;
		Log.d(LOG_TAG, "GetGameAppInfo:" + type + ", main platform" + mMainPlatformInfo.getPlatform());
		if (type.equals(mMainPlatformInfo.getPlatform())) {
			app = mMainPlatformInfo;
			Log.d(LOG_TAG, "get main app info for :" + type);
			return app;
		}
		Log.d(LOG_TAG, "find app in append list:" + type.ordinal());
		app = mAppendPlatformInfo.get(type.ordinal());
		if (app  == null) {
			Log.d(LOG_TAG, "Can not find app info for " + type + ", please check the configuration.");
		}
		return app;
	}
	
	public static boolean isDebugMode() {
//		return true;
		return false;
	}
	
	/**
	 * load configuration from xml file under folder res/xml/
	 * this may cost some performance when the configuration file is 
	 * vary large, need do pre-load for that situation. Generally it is suggest 
	 * to exec when BaseActivity resumed.
	 */
	public static void loadConfiguration(Context context) {
		try {
			AssetManager assets = context.getAssets();
			Document document = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().parse(assets.open(CONFIGURE_FILE));
			Element root = document.getDocumentElement();
			
			// Parse main platform info
			String mainPlatform = ((Element) (root
					.getElementsByTagName("main_platform").item(0)))
					.getAttribute("platform");
			Log.d(LOG_TAG, "loadConfiguration() => main platform:" + mainPlatform);
			
			// Load game app configuration
			NodeList gameappNodes = root.getElementsByTagName("GameApp");
			for(int i = 0; i < gameappNodes.getLength(); i++) {
				Element appNode = (Element) gameappNodes.item(i);
				assert(appNode != null);
				
				//Check if this element is active
				String active = appNode.getAttribute("active");
				if (active != null && !active.isEmpty()) {
					// if the element is specific to false
					// then do not load this element
					if (!Boolean.parseBoolean(active)) {
						continue;
					}
				}
				
				// Check if app info in this element is for main platform
				String id = appNode.getAttribute("id");
				boolean isMainPlatform = false;
				if (mainPlatform.equals(id)) {
					isMainPlatform = true;
				}
				
				// load appinfo into GameAppInfo instance
				HashMap<String, String> nodeValues = getChildValue(appNode);
				assert(nodeValues != null && !nodeValues.isEmpty());
				String appName = nodeValues.get("appname");
				String platform = nodeValues.get("platform");
				String appid = nodeValues.get("appid");
				String appkey = nodeValues.get("appkey");
				String orientation = nodeValues.get("orientation");
				String operatorCls = nodeValues.get("operator");
				boolean isLandscape = orientation == null ? true : orientation.equals("landscape");
				GameAppInfo appInfo = new GameAppInfo(appid, appkey, platform, operatorCls, appName, isLandscape);
				if (nodeValues.containsKey("channel")) {
					appInfo.setChannel(nodeValues.get("channel"));
				}
				if (nodeValues.containsKey("market_addr")) {
					appInfo.setPlatformMarketAddr(nodeValues.get("market_addr"));
				}
				if (nodeValues.containsKey("appsecret")) {
					appInfo.setAppSecret(nodeValues.get("appsecret"));
				}
				if(nodeValues.containsKey("wechat-appid")) {
					appInfo.setWeChatAppId(nodeValues.get("wechat-appid"));
				}
				if (isMainPlatform) {
					mMainPlatformInfo = appInfo;
				} else {
					if (mAppendPlatformInfo == null) {
						mAppendPlatformInfo = new SparseArray<GameAppInfo>(3);
					}
					mAppendPlatformInfo.put(appInfo.getPlatform().ordinal(), appInfo);
//					Log.d(LOG_TAG, "add int append platform info:"
//							+ "index" + appInfo.getPlatform().ordinal()
//							+ " =>" + appInfo.toString());
				}
			}
			
			// Game can not continue without main platfor configuration.
			if (mMainPlatformInfo == null) {
				Log.e(LOG_TAG, "Can not find any app info for main platform, please check the configuration.");
				throw new Exception("Can not load mainplatform app info");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private static HashMap<String, String> getChildValue(Element appNode) {
		assert(appNode != null);
		NodeList children = appNode.getChildNodes();
		assert(children != null && children.getLength() > 0);
		HashMap<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				String name = child.getNodeName();
				String value = child.getFirstChild().getNodeValue();
				map.put(name, value);
			}
		}
		return map;
	}
}
