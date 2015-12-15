package com.happiplay.tools;

public class ShareResult {
	public final static int RESULT_SUCCESS = 0x00000001;
	public final static int RESULT_FAILED = 0x00000002;
	public final static int RESULT_CANCELED = 0x00000003;
    private int mResponse;
    private String mMessage;

    public ShareResult(int response, String message) {
        mResponse = response;
        if (message == null || message.trim().length() == 0) {
            mMessage = getResponseDesc(response);
        }
        else {
            mMessage = message + " (response: " + getResponseDesc(response) + ")";
        }
    }
    
    public String getResponseDesc(int responseCode) {
    	if (responseCode == RESULT_SUCCESS) {
    		return "success";
    	} else if (responseCode == RESULT_FAILED) {
    		return "failed";
    	} else if (responseCode == RESULT_CANCELED) {
    		return "canceled";
    	}
    	return "";
    }
    
	public int getResponse() {
		return mResponse;
	}

	public String getMessage() {
		return mMessage;
	}

	public boolean isSuccess() {
		return mResponse == RESULT_SUCCESS;
	}

	public boolean isFailure() {
		return !isSuccess();
	}

	public String toString() {
		return "IabResult: " + getMessage();
	}
}
