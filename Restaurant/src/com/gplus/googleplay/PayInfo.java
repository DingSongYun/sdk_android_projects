package com.gplus.googleplay;

import java.util.HashMap;

/**
 * @author Tdsy
 *	Base class for raw pay info
 */
public class PayInfo {
    private String mMoneyAmount;

    private String mProductName;

    private String mProductId;
    
    private String mProductIcon;

    private String mNotifyUri;
    
    private String mConfirmUri;

    private String mAppName;

    private String mAppUserName;

    private String mAppUserId;
    
    private String mPlatformUserId;
    
    private String mPlatformUserName;
    
    // Form ID for Order, usually be unique
    private String appOrderId;
    
    private String mProductDescription;
    
    private HashMap<String, String> mExtraData;

    public String getPrice() {
        return mMoneyAmount;
    }

    public void setPrice(String moneyAmount) {
        this.mMoneyAmount = moneyAmount;
    }

    public String getAppName() {
        return mAppName;
    }

    public void setAppName(String appName) {
        this.mAppName = appName;
    }

    public String getAppUserName() {
        return mAppUserName;
    }

    public void setAppUserName(String appUserName) {
        this.mAppUserName = appUserName;
    }

    public String getPlatformUserId() {
    	return this.mPlatformUserId;
    }
    
    public void setPlarformUserId(String userId) {
    	this.mPlatformUserId = userId;
    }
    
    public String getPlatformUserName() {
    	return this.mPlatformUserName;
    }
    
    public void setPlatformUserName(String userName) {
    	this.mPlatformUserName = userName;
    }
    
    public String getAppUserId() {
        return mAppUserId;
    }

    public void setAppUserId(String appUserId) {
        this.mAppUserId = appUserId;
    }

    public String getProductName() {
        return mProductName;
    }

    public void setProductName(String productName) {
        this.mProductName = productName;
    }

    public String getProductId() {
        return mProductId;
    }

    public void setProductId(String productId) {
        this.mProductId = productId;
    }

    public String getProductIcon() {
    	return mProductIcon;
    }
    
    public void setProductIcon(String productIcon) {
    	mProductIcon = productIcon;
    }
    
    public String getNotifyUri() {
        return mNotifyUri;
    }

    public void setNotifyUri(String notifyUri) {
        this.mNotifyUri = notifyUri;
    }

    public String getConfirmUri() {
    	return mConfirmUri;
    }
    
    public void setConfirmUri(String confirmUri) {
    	this.mConfirmUri = confirmUri;
    }
    public String getOrderId() {
        return appOrderId;
    }

    public void setOrderId(String appOrderId) {
        this.appOrderId = appOrderId;
    }
    
    public void setExtraData(HashMap<String, String> extra) {
    	mExtraData = extra;
    }
    
    public HashMap<String, String> getExtraData() {
    	return mExtraData;
    }
    
    public void setDescription(String desc) {
    	mProductDescription = desc;
    }
    
    public String getDescription() {
    	return mProductDescription;
    }
}
