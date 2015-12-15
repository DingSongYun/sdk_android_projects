package com.happiplay.tools;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import com.starcloudcasino.winthree.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

/**
 * Utility class for choosing and uploading avatar 
 * @author Tdsy
 *
 */
public class AvatarHelper extends Activity /*implements MainActivity.IActivityResult*/{
	private static final String LOG_TAG = "AvatarHelper";

    public static final String MJ2P_TEMP_FOLDER = 
    		Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator
            + "mj2p" + File.separator + "temp";
    
    // when use this field to select photo, use this information
    public static final int REQUEST_CODE_SELECT_FROM_ALBUM = 100;
    public static final int REQUEST_CODE_TAKE_PHOTO = 101;
    public static final int REQUEST_CODE_ZOOM_PIC = 102;

    private static final int TIME_OUT = 30 * 1000;
    
    public static final String TAKE_PHOTO_TO_TEMP_FILE_PATH = MJ2P_TEMP_FOLDER
            + File.separator + "mj2p_temp.jpg";
    
    public static final String IMAGE_UNSPECIFIED = "image/*";
    
    public static final int MSG_UPLOAD_DONE = 0;
    public static final int MSG_DELAY_FINISH = 1;
	
	private int mCmdId;
	private String mUploadUrl;
	private Activity mActivity;
	/**
	 * Utility method for unity to change user avatar
	 * @param cmdId  For callback to Unity
	 * @param uploadUrl the url to upload new avatar to server
	 */
//	public void startChangeAvatar(MainActivity activity, int cmdId, String uploadUrl) {
//		Log.d(LOG_TAG, "startChangeAvatar => uploadUrl" + uploadUrl);
//		mCmdId = cmdId;
//		mUploadUrl = uploadUrl;
//		mActivity = activity;
////		activity.registerActivityResultListener(this);
//		showPickPhotoDialog(activity);
//	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(LOG_TAG, "onCreate()");
		Intent intent = getIntent();
		if (intent != null) {
			mUploadUrl = intent.getStringExtra("UploadUrl");
			mCmdId = intent.getIntExtra("cmdid", 0);
		}
		mActivity = this;
		showPickPhotoDialog(this);
	}
 
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i(LOG_TAG, "requestCode1=" + requestCode);
		if (resultCode == Activity.RESULT_CANCELED) {
			Log.d(LOG_TAG, "Canceled.");
			this.finish();
		}
		switch (requestCode) {
		case REQUEST_CODE_SELECT_FROM_ALBUM:
			if (resultCode == Activity.RESULT_OK) {
				zoomProfilePhoto(data.getData());
			}
			break;
		case REQUEST_CODE_TAKE_PHOTO:
			if (resultCode == Activity.RESULT_OK) {
				File capturePicture = new File(TAKE_PHOTO_TO_TEMP_FILE_PATH);
				zoomProfilePhoto(Uri.fromFile(capturePicture));
			}
			break;
		case REQUEST_CODE_ZOOM_PIC:
			if (resultCode == Activity.RESULT_OK) {
				Bundle extras = data.getExtras();
				Bitmap photoBitmap = extras.getParcelable("data");
				uploadPicture(photoBitmap);
			}
			break;
		default:
			break;
		}
	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == MSG_UPLOAD_DONE) {
				handleUploadResult(true, (String) msg.obj);
			} else if (msg.what == MSG_DELAY_FINISH) {
				AvatarHelper.this.finish();
			}
		}
	};
	
	 private void uploadPicture(final Bitmap photo) {
		CommonTools.doProgressTask(mActivity, ProgressDialog.STYLE_SPINNER,
				null, mActivity.getString(R.string.dialogcontent_waitting_up),
				new CommonTools.IProgressMonitor<String>() {

					@Override
					public void onProcessStart() {

					}

					@Override
					public String onProcessInBackground() {
						
						if (photo == null) {
							return null;
						}
						
//						Log.d(LOG_TAG, "zoomed photo is: " + photo.getByteCount());
						// Notice: Don't forget to delete the temp file
						File tempFile = new File(MJ2P_TEMP_FOLDER, "ipodfile.jpg");
						try {
							BufferedOutputStream bos = new BufferedOutputStream(
									new FileOutputStream(tempFile));
							photo.compress(Bitmap.CompressFormat.JPEG, 100, bos);// 0-100for quality
							bos.flush();
							bos.close();

							if (tempFile.isFile()) {
								return uploadFile(tempFile, mUploadUrl);
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							photo.recycle();
						}
							
						return "";
					}

					@Override
					public void onProcessFinish(String value) {
						if (!TextUtils.isEmpty(value)) {
							Log.d(LOG_TAG, "upload avatar successfull:" + value);
							handleUploadResult(true, value);
						} else {
							Log.d(LOG_TAG, "upload avatar failed.");
							handleUploadResult(false, "");
						}
					}

					@Override
					public void onCancelled() {
						Log.d(LOG_TAG, "Cancel upload avatar.");
						CommonTools.showToast(mActivity, mActivity
								.getString(R.string.upload_avatar_cancelled));
					}
				}
		);
	}

	private String getState(int status) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("status", status);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonObject.toString();
	}
	
	private void handleUploadResult(boolean successed, String data) {
		// state to identify the result of changing user avatar:
		// 		0 : successful
		//		-1 : failed
		Log.d(LOG_TAG, "handleUploadResult() : " + data);
		int state = 0;
		if (!successed) {
			state = -1;
			CommonTools.showToast(mActivity, mActivity
					.getString(R.string.upload_avatar_failed));
			ExternalCall.instance.callUnity(mCmdId, getState(state));
			return ;
		}
		try {
			int error = new JSONObject(data).getInt("error");
			Log.d(LOG_TAG, "Download error:" + error);
			if (error == 0) {
				CommonTools.showToast(mActivity,
						mActivity.getString(R.string.upload_avatar_successed));
				state = 0;
			} else if (error == 1) {
				CommonTools.showToast(mActivity,
						mActivity.getString(R.string.upload_avatar_failed));
				state = -1;
			} else if (error == 2) {
				CommonTools.showToast(mActivity, 
						mActivity.getString(R.string.upload_avatar_failed_data));
				state = -1;
			} else if (error == 3) {
				CommonTools.showToast(mActivity,
						mActivity.getString(R.string.upload_avatar_failed_user));
				state = -1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		ExternalCall.instance.callUnity(mCmdId, getState(state));
		this.finish();
	}
	
	private String uploadFile(File file, String uploadUrl) {
		assert(file != null && !TextUtils.isEmpty(uploadUrl));
		Log.d(LOG_TAG, "uploadFile to " + uploadUrl + "photo size:" + file.length());
		String result = null;
		String BOUNDARY = UUID.randomUUID().toString();
		String PREFIX = "--", LINE_END = "\r\n";

		final String CHARSET = "urf-8";
		try {
				URL url = new URL(uploadUrl);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				{
					final String CONTENT_TYPE = "multipart/form-data";
					conn.setReadTimeout(TIME_OUT);
					conn.setConnectTimeout(TIME_OUT);
					conn.setDoInput(true); 
					conn.setDoOutput(true);
					conn.setUseCaches(false);
					conn.setRequestMethod("POST");
					conn.setRequestProperty("Charset", CHARSET);
					conn.setRequestProperty("connection", "keep-alive");
					conn.setRequestProperty("Content-Type", CONTENT_TYPE + 
							";boundary=" + BOUNDARY);
				}

			if (file != null) {
				DataOutputStream output = new DataOutputStream(
						conn.getOutputStream());
				StringBuffer sb = new StringBuffer();
				sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
				sb.append("Content-Disposition: form-data; name=\"avatar\"; filename=\""
						+ file.getName() + "\"" + LINE_END);
				sb.append("Content-Type: image/jpeg; charset=" + CHARSET).append(LINE_END);
				sb.append(LINE_END);
				output.write(sb.toString().getBytes());
				InputStream is = new FileInputStream(file);
				byte[] bytes = new byte[1024];
				int len = 0;
				while ((len = is.read(bytes)) != -1) {
					output.write(bytes, 0, len);
				}
				is.close();
				output.write(LINE_END.getBytes());
				byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)
						.getBytes();
				output.write(end_data);
				output.flush();
				
				InputStream input = conn.getInputStream();
				StringBuffer inBuffer = new StringBuffer();
				int ss;
				while ((ss = input.read()) != -1) {
					inBuffer.append((char) ss);
				}
				result = inBuffer.toString();
				output.close();
				input.close();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (file.exists()) {
				file.delete();
			}
			clearTempFile();
		}
		return result;
	}
	
	private void clearTempFile() {
		// clear photo cache when already take a new photo
		File capturePicture = new File(TAKE_PHOTO_TO_TEMP_FILE_PATH);
		if (capturePicture.exists()) {
			capturePicture.delete();
		}
	}
	
	private void zoomProfilePhoto(Uri fileUri) {
		Log.d(LOG_TAG, "zoomProfilePhoto for photo: " + fileUri);
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(fileUri, IMAGE_UNSPECIFIED);
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 120);
		intent.putExtra("outputY", 120);
		intent.putExtra("return-data", true);
		mActivity.startActivityForResult(intent, REQUEST_CODE_ZOOM_PIC);		
	}

	/**
     * show dialog for the user to select photo to change avatar
     */
    private void showPickPhotoDialog(final Context context) {
        String[] options = new String[] {
                context.getResources().getString(R.string.take_photo),
                context.getResources().getString(R.string.pick_photo) };
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.select_photo));
        builder.setItems(options, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = null;
                File tempFolder = new File(MJ2P_TEMP_FOLDER);
                if (!tempFolder.exists()) {
                    tempFolder.mkdirs();
                }
                if (!CommonTools.isSdCardExist()) {
                	CommonTools.showToast(context, context.getString(R.string.pick_error));
                	return ;
                }
                switch (which) {
                case 0:
                	// Select avatar through take a new photo
                	Log.d(LOG_TAG, "Take a new photo as Avatar");
                    intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); 
                    File capturePicture = new File(TAKE_PHOTO_TO_TEMP_FILE_PATH);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(capturePicture));
                    ((Activity) context).startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO);
                    break;
                case 1:
                    // To open up a gallery browser
                	Log.d(LOG_TAG, "Pick up Avatar from gallery.");
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    ((Activity) context).startActivityForResult(intent, REQUEST_CODE_SELECT_FROM_ALBUM);
                    break;
                }
            }
        });
        builder.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface arg0) {
				AvatarHelper.this.finish();
			}
        	
        });
        builder.create().show();
    }
    
    @Override
    public void finish() {
    	super.finish();
    }
    
    @Override
    public void onConfigurationChanged(Configuration conf) {
		super.onConfigurationChanged(conf);
		Log.d(LOG_TAG, "AvatarHelper => onConfigurationChanged()");
	}
}
