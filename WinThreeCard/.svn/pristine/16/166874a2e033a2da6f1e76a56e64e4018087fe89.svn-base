package com.happiplay.tools;

import com.starcloudcasino.winthree.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomProgressDialog extends Dialog {

	public CustomProgressDialog(Context context, int theme) {
		super(context, R.style.dialog_custome);
		this.setContentView(R.layout.custome_progress_dialog);
		this.getWindow().getAttributes().gravity = Gravity.CENTER;
	}

	public void onWindowFocusChanged(boolean hasFocus) {
		ImageView imageView = (ImageView) this
				.findViewById(R.id.progress_image);
		try {
			AnimationDrawable animationDrawable = (AnimationDrawable) imageView
					.getBackground();
			animationDrawable.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setMessage(String strMessage) {
		TextView tvMsg = (TextView) this.findViewById(R.id.progress_text);
		if (tvMsg != null) {
			tvMsg.setText(strMessage);
		}
	}
}