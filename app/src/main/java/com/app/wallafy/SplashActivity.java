package com.app.wallafy;
/****************
*
* @author 'Hitasoft Technologies'
* 
* Description:  
* This class is used for displaying splash screen
*
* Revision History:
* Version 1.0 - Initial Version
*
*****************/

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;

import java.util.Arrays;
import java.util.Locale;

public class SplashActivity extends Activity {

	private static int SPLASH_TIME_OUT = 2000;
	private static Dialog settingsDialog;
	public static SharedPreferences pref;
	public static Editor editor;
	String[] languages, langCode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);
		pref = getApplicationContext().getSharedPreferences("wallafyPref",
				MODE_PRIVATE);
		editor = pref.edit();

		languages = getResources().getStringArray(R.array.languages);
		langCode = getResources().getStringArray(R.array.languageCode);
		String selectedLang = pref.getString("language", "English");

		int index = Arrays.asList(languages).indexOf(selectedLang);
		String languageCode = Arrays.asList(langCode).get(index);
		Log.v("languageCode", "languageCode="+languageCode);

		setLocale(languageCode);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {

				Intent i = new Intent(SplashActivity.this,
						FragmentMainActivity.class);
				startActivity(i);
				finish();

				overridePendingTransition(R.anim.fade_in,
						R.anim.fade_out);
			}
		}, SPLASH_TIME_OUT);
	}

	@Override
	public void onBackPressed() {

	}

	public void setLocale(String lang) {
		Locale myLocale = new Locale(lang);
		Resources res = getResources();
		DisplayMetrics dm = res.getDisplayMetrics();
		Configuration conf = res.getConfiguration();
		conf.locale = myLocale;
		res.updateConfiguration(conf, dm);
	}
	
	
}
