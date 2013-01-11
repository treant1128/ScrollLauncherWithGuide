package org.treant.scrollgrid2_mutiscreens;

import org.treant.scrollgrid2_mutiscreens.util.Configure;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

public class LoadingActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.layout_loading);
		intentToSkip();
	}

	private void intentToSkip() {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				Class<?> c = (getPreferences(Context.MODE_PRIVATE).getBoolean(
						Configure.PREFERENCES_GUIDE, false)) ? MainActivity.class
						: GuideActivity.class;
				intent.setClass(LoadingActivity.this, c);
				startActivity(intent);
				overridePendingTransition(R.anim.anim_fromright_toup6,
						R.anim.anim_down_toleft6);
				finish();
			}

		}, 1250);

	}
}
