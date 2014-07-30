package com.tochange.yang.floatladdin.floatwindow;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tochange.yang.floatladdin.LauncherActivity;
import com.tochange.yang.floatladdin.R;
import com.tochange.yang.floatladdin.aladdin.SettingLayoutActivity;
import com.tochange.yang.lib.ui.BadgeView;
import com.tochange.yang.lib.ui.Graphics;
import com.tochange.yang.lib.ui.ScreenLib;
import com.tochange.yang.lib.Utils;
import com.tochange.yang.lib.log;

public class FloatWindowCustomView extends LinearLayout
{
    public static int windowViewWidth;

    public static int windowViewHeight;

    private WindowManager windowManager;

    private LinearLayout customWindowLayout;

    private WindowManager.LayoutParams mParams;

    private float xInScreen;

    private float yInScreen;

    private float xInView;

    private float yInView;

    private Context mContext;

    private OnClickListener mGetFocusClickListenrt = new OnClickListener() {

        @Override
        public void onClick(View v)
        {
            v.setFocusable(true);
            v.setFocusableInTouchMode(true);
        }
    };

    public FloatWindowCustomView(final Context context)
    {
        super(context);
        mContext = context;
        windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater.from(context)
                .inflate(R.layout.float_window_layout, this);
        customWindowLayout = (LinearLayout) findViewById(R.id.custom_layout);
        windowViewWidth = customWindowLayout.getLayoutParams().width;
        windowViewHeight = customWindowLayout.getLayoutParams().height;

        final int[] wh = getCustomWindowLayoutWidthAndHeight(customWindowLayout);
        setBadgeView(context, wh);
        setPercentView(context);
        setEditText(context);
    }

    private void setEditText(final Context context)
    {
        final EditText et = (EditText) findViewById(R.id.edittext);
        et.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                android.view.WindowManager.LayoutParams lp = (android.view.WindowManager.LayoutParams) FloatWindowCustomView.this
                        .getLayoutParams();
                String m;
                if (hasFocus)
                {
                    m = "can";
                    lp.flags = 32;
                }
                else
                {
                    m = "cann't";
                    lp.flags = 32 | 8;
                }
                FloatWindowCustomView.this.setLayoutParams(lp);
                Toast.makeText(context, m + " input.", 5000).show();
                windowManager.updateViewLayout(FloatWindowCustomView.this, mParams);
            }
        });
        et.setOnClickListener(mGetFocusClickListenrt);
    }

    private void setPercentView(Context context)
    {
        final TextView percentView = (TextView) findViewById(R.id.percent);
        percentView.setText(FloatWindowManager.getUsedPercentValue(context));
        percentView.setOnClickListener(mGetFocusClickListenrt);
    }

    private void setBadgeView(final Context context, final int[] wh)
    {
        final ImageView view = (ImageView) findViewById(R.id.close);
        final BadgeView badge = new BadgeView(context, view);
        badge.setBadgePosition(wh[0] - view.getMeasuredHeight(), 0, 0, 0)
                .setBadgeBackgroundShape(BadgeView.BACKGROUND_SHAPE_RECTANGLE)
                .setText(" × ");
        badge.setTextSize(25);
        badge.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                Intent i = new Intent(context, SettingLayoutActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                int[] position = getPosition(wh);
                i.putExtra(LauncherActivity.KEY_X, position[0]);
                i.putExtra(LauncherActivity.KEY_Y, position[1]);
                i.putExtra(LauncherActivity.KEY_IMAGE,
                		Graphics.getBitmapFromView(FloatWindowCustomView.this));
                setButtonPressedEffect(badge);
                context.startActivity(i);
            }

            private void setButtonPressedEffect(BadgeView badge)
            {
                Animation a = new ScaleAnimation(1, 1.2F, 1, 1.2F,
                        Animation.RELATIVE_TO_SELF, 0.8f,
                        Animation.RELATIVE_TO_SELF, 0.8f);
                a.setDuration(100);
                badge.setBackgroundColor(Color.parseColor("#c00000"));
                badge.startAnimation(a);
//                if (!LauncherActivity.DEBUG_MODE)
//                    Utils.vibrate(context);
            }

            private int[] getPosition(int[] wh)
            {
                int maxX = windowManager.getDefaultDisplay().getWidth() - wh[0];
                int x = mParams.x > maxX ? maxX : mParams.x;

                int maxY = windowManager.getDefaultDisplay().getHeight()
                        - wh[1];
                int magic = LauncherActivity.IS_TABLTE ? 0 : 2;
                int y = mParams.y > maxY ? maxY - magic
                        * LauncherActivity.MAGIC_VERTICAL : mParams.y;
                y = y > 0 ? y : 0;

                int[] ret = { x, y };
                return ret;
            }
        });
        badge.show();

    }

    private int[] getCustomWindowLayoutWidthAndHeight(
            LinearLayout customWindowLayout2)
    {
        int widthMeasureSpec, heightMeasureSpec;
        widthMeasureSpec = heightMeasureSpec = View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        customWindowLayout.measure(widthMeasureSpec, heightMeasureSpec);
        int w = customWindowLayout.getMeasuredWidth();
        int h = customWindowLayout.getMeasuredHeight();
        int[] ret = { w, h };
        return ret;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction())
        {

            case MotionEvent.ACTION_DOWN:
                xInView = event.getX();
                yInView = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                xInScreen = event.getRawX();
                // if pad,then status bar will be in the bottom,can not minus
                // status bar height,but pad not ensure status bar in the bottom
                yInScreen = LauncherActivity.IS_TABLTE ? event.getRawY()
                        : event.getRawY() - ScreenLib.getStatusBarHeight(mContext);
                updateViewStatus();
                updateViewPosition();
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return true;
    }

    public void setParams(WindowManager.LayoutParams params)
    {
        mParams = params;
    }

    private void updateViewPosition()
    {
        mParams.x = (int) (xInScreen - xInView);
        mParams.y = (int) (yInScreen - yInView);
        windowManager.updateViewLayout(this, mParams);
    }

    private void updateViewStatus()
    {
        mParams.width = windowViewWidth;
        mParams.height = windowViewHeight;
        windowManager.updateViewLayout(this, mParams);
    }
}
