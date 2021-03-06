package com.tochange.yang.floatladdin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tochange.yang.floatladdin.floatwindow.FloatWindowService;
import com.tochange.yang.lib.ui.ScreenLib;

public class LauncherActivity extends Activity {
    
    public static final String    KEY_X                                     = "x";
    public static final String    KEY_Y                                     = "y";
    public static final String    KEY_IMAGE                                 = "image";
    public static final String    DELETED_BUTTON_TEXT_CONTENT               = " × ";
    public static final int       DELETED_BUTTON_TEXT_SIZE                  = 25 ;
    public static final int       VERTICAL_OFFSET                           = 15;
    public static final int       ANIMATION_TIME_SHORT                      = 600;//600 
    public static final int       ANIMATION_TIME_LONG                       = 5000;//2000 
    public static final int       ANIMATION_SPLIT_HORIZONTAL                = 40; 
    public static final int       ANIMATION_SPLIT_VERTICAL                  = 40; 
    public static final int       BASE_SCREEN_WIDTH                         = 480; 
    public static final float     BASE_SCREEN_DENSITY                       = 1.5F; 
    
    public static boolean         DEBUG_MODE;      
    public static boolean         IS_TABLTE;        
    public static int             ANIMATION_TIME;      
    public static int             SCREEN_WIDTH;      
    public static int             SCREEN_HEIGHT;     
    
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = new Intent(LauncherActivity.this, FloatWindowService.class);
		startService(intent);
		 if(ScreenLib.isTabletDevice(this)){
		     IS_TABLTE = true;
		 }
		 ANIMATION_TIME = DEBUG_MODE?ANIMATION_TIME_LONG:ANIMATION_TIME_SHORT;
		 finish();
	}
}
