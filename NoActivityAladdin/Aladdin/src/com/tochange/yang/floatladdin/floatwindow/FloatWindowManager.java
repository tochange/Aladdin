package com.tochange.yang.floatladdin.floatwindow;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.tochange.yang.floatladdin.LauncherActivity;
import com.tochange.yang.floatladdin.R;
import com.tochange.yang.lib.log;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

public class FloatWindowManager
{

    private static LayoutParams customWindowParams;

    private static FloatWindowCustomView customWindow;

    private static WindowManager mWindowManager;

    private static ActivityManager mActivityManager;

    public static void createCustomWindow(Context context)
    {
        if (customWindow == null)
        {
            WindowManager windowManager = getWindowManager(context);
            customWindow = new FloatWindowCustomView(context);
            if (customWindowParams == null)
            {
                customWindowParams = new LayoutParams();
                customWindowParams.type = LayoutParams.TYPE_SYSTEM_ALERT;
                customWindowParams.format = PixelFormat.RGBA_8888;
                customWindowParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH 
                        | LayoutParams.FLAG_NOT_FOCUSABLE;
                customWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
                customWindowParams.width = LayoutParams.WRAP_CONTENT;
                customWindowParams.height = LayoutParams.WRAP_CONTENT;
                LauncherActivity.SCREEN_WIDTH = windowManager
                        .getDefaultDisplay().getWidth();
                LauncherActivity.SCREEN_HEIGHT = windowManager
                        .getDefaultDisplay().getHeight();
                customWindowParams.x = LauncherActivity.SCREEN_WIDTH / 4;
                customWindowParams.y = LauncherActivity.SCREEN_HEIGHT * 2 / 3;
            }
            customWindow.setParams(customWindowParams);
            windowManager.addView(customWindow, customWindowParams);
        }
    }

    public static void removeCustomWindow(Context context)
    {
        if (customWindow != null)
        {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(customWindow);
            customWindow = null;
        }
    }

    public static void updateUsedPercent(Context context)
    {
        if (customWindow != null)
        {
            TextView percentView = (TextView) customWindow
                    .findViewById(R.id.percent);
            if (percentView != null)
                percentView.setText(getUsedPercentValue(context));
        }
    }

    public static boolean isWindowShowing()
    {
        return customWindow != null;
    }

    private static WindowManager getWindowManager(Context context)
    {
        if (mWindowManager == null)
        {
            mWindowManager = (WindowManager) context
                    .getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }

    private static ActivityManager getActivityManager(Context context)
    {
        if (mActivityManager == null)
        {
            mActivityManager = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
        }
        return mActivityManager;
    }

    public static String getUsedPercentValue(Context context)
    {
        String dir = "/proc/meminfo";
        try
        {
            FileReader fr = new FileReader(dir);
            BufferedReader br = new BufferedReader(fr, 2048);
            String memoryLine = br.readLine();
            String subMemoryLine = memoryLine.substring(memoryLine
                    .indexOf("MemTotal:"));
            br.close();
            long totalMemorySize = Integer.parseInt(subMemoryLine.replaceAll(
                    "\\D+", ""));
            long availableSize = getAvailableMemory(context) / 1024;
            int percent = (int) ((totalMemorySize - availableSize)
                    / (float) totalMemorySize * 100);
            return percent + "%";
        }
        catch (IOException e)
        {
            log.e(e.toString());
        }
        return "floating windows";
    }

    private static long getAvailableMemory(Context context)
    {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        getActivityManager(context).getMemoryInfo(mi);
        return mi.availMem;
    }

}
