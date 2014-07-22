package com.tochange.yang.floatladdin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tochange.yang.floatladdin.floatwindow.FloatWindowService;
import com.tochange.yang.lib.Utils;

public class LauncherActivity extends Activity {
    
    public static boolean IS_TABLTE                 = false;//not final type!
    public static final boolean DEBUG_MODE          = false;
    public static final String KEY_X                = "x";
    public static final String KEY_Y                = "y";
    public static final String KEY_IMAGE            = "image";
    public static final int MAGIC_VERTICAL          = 15; 
    public static final int ANIMATION_TIME_SHORT    = 600; 
    public static final int ANIMATION_TIME_LONG     = 2000; 
    
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = new Intent(LauncherActivity.this, FloatWindowService.class);
		startService(intent);
		 if(Utils.isTabletDevice(this)){
		     IS_TABLTE = true;
		 }
		finish();
	}
}
