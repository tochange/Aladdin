package com.tochange.yang.floatladdin.floatwindow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tochange.yang.floatladdin.LauncherActivity;
import com.tochange.yang.floatladdin.R;
import com.tochange.yang.floatladdin.aladdin.MainView;
import com.tochange.yang.lib.ui.BadgeView;
import com.tochange.yang.lib.ui.ClearEditText;
import com.tochange.yang.lib.ui.ScreenLib;
import com.tochange.yang.lib.ui.Graphics;
import com.tochange.yang.lib.Utils;
import com.tochange.yang.lib.log;

public class FloatWindowCustomView extends LinearLayout
{
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
            if (v.getId() == R.id.percent)
            {
                if (et.getText().toString().trim().equals(""))
                    et.startAnimation(et.getShakeAnimation(5));
                LauncherActivity.DEBUG_MODE = !LauncherActivity.DEBUG_MODE;
            }
        }
    };

    AnimationListener mAnimationListener = new AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation)
        {
        }

        @Override
        public void onAnimationRepeat(Animation animation)
        {
        }

        @Override
        public void onAnimationEnd(Animation animation)
        {
            FloatWindowCustomView.this.removeAllViews();
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
        final int[] wh = getCustomWindowLayoutWidthAndHeight(customWindowLayout);
        setBadgeView(context, wh);
        setPercentView(context);
        setEditText(context);
    }

    ClearEditText et;

    private void setEditText(final Context context)
    {
        et = (ClearEditText) findViewById(R.id.edittext);
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
                    /*
                     * LayoutParams.FLAG_NOT_TOUCH_MODAL |
                     * LayoutParams.FLAG_NOT_FOCUSABLE |
                     */
                    lp.flags = 32 | 262144;
                }
                else
                {
                    m = "cann't";
                    /*
                     * LayoutParams.FLAG_NOT_TOUCH_MODAL |
                     * LayoutParams.FLAG_NOT_FOCUSABLE |
                     * LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
                     */
                    lp.flags = 32 | 8 | 262144;
                }
                FloatWindowCustomView.this.setLayoutParams(lp);
                Toast.makeText(context, m + " input.", Toast.LENGTH_LONG)
                        .show();
                windowManager.updateViewLayout(FloatWindowCustomView.this,
                        mParams);
                et.setOnFocusChangeListenerCallBack(v, hasFocus);
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
                .setText(LauncherActivity.DELETED_BUTTON_TEXT_CONTENT);
        badge.setTextSize(LauncherActivity.DELETED_BUTTON_TEXT_SIZE);
        badge.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                setButtonPressedEffect(badge);
                View rootView = LayoutInflater.from(context).inflate(
                        R.layout.aladdin_view, null);
                View child = FloatWindowCustomView.this.getFocusedChild();
                child.startAnimation(getAlphaAnimation(LauncherActivity.ANIMATION_TIME / 2));
                Bitmap b = Graphics.getBitmapFromView(child);
                int[] position = getPosition(wh);
                log.e("p=" + position[0] + " " + position[1]);
                MainView mainView = (MainView) rootView.findViewById(R.id.view);
                setParameter(mainView, position, b, rootView);
                windowManager.addView(rootView, mParams);
                mainView.startAnimation(false);
            }

            private Animation getAlphaAnimation(int i)
            {
                Animation alphaAnimation = new AlphaAnimation(1, 0);
                alphaAnimation.setDuration(i);
                alphaAnimation.setAnimationListener(mAnimationListener);
                return alphaAnimation;
            }

            private void setParameter(MainView mainView, int[] position,
                    Bitmap bitmap, View rootView)
            {
                setLeftMargin(position, rootView);
                setTopMargin(mainView, position);
                mainView.setBitmap(bitmap);
            }

            private void setTopMargin(MainView mainView, int[] position)
            {
                LayoutParams lp = (LayoutParams) mainView.getLayoutParams();
                // lp.leftMargin = position[0];
                // lp.leftMargin = 0;
                lp.topMargin = position[1] - LauncherActivity.VERTICAL_OFFSET;
                mainView.setLayoutParams(lp);

            }

            private void setLeftMargin(int[] position, View rootView)
            {
                TextView tv = (TextView) rootView.findViewById(R.id.space_h);
                LayoutParams lpp = (LayoutParams) tv.getLayoutParams();
                // log.e("position[0]=" + position[0]);
                float magic = LauncherActivity.SCREEN_WIDTH
                        - (float) (getDensity() / LauncherActivity.BASE_SCREEN_DENSITY)
                        * LauncherActivity.BASE_SCREEN_WIDTH;
                lpp.width = (int) (position[0] - magic);
                // lpp.height = position[1];
                tv.setLayoutParams(lpp);
            }

            private double getDensity()
            {
                DisplayMetrics m = new DisplayMetrics();
                windowManager.getDefaultDisplay().getMetrics(m);
                return m.density;
            }

            private void setButtonPressedEffect(BadgeView badge)
            {
                Animation a = new ScaleAnimation(1, 1.2F, 1, 1.2F,
                        Animation.RELATIVE_TO_SELF, 0.8f,
                        Animation.RELATIVE_TO_SELF, 0.8f);
                a.setDuration(100);
                badge.setBackgroundColor(Color.parseColor("#c00000"));
                badge.startAnimation(a);
                if (!LauncherActivity.DEBUG_MODE)
                    Utils.vibrate(context);
            }

            private int[] getPosition(int[] wh)
            {
                log.e("wh[0]=" + wh[0] + " wh[1]=" + wh[1]);
                int maxX = LauncherActivity.SCREEN_WIDTH - wh[0];
                int x = mParams.x > maxX ? maxX : mParams.x;
                x = x > 0 ? x : 0;

                int maxY = LauncherActivity.SCREEN_HEIGHT - wh[1];
                int magic = LauncherActivity.IS_TABLTE ? 0 : 2;
                int y = mParams.y > maxY ? maxY - magic
                        * LauncherActivity.VERTICAL_OFFSET : mParams.y;
                y = y > 0 ? y : 0;

                log.e("mParams.x=" + mParams.x + " mParams.y=" + mParams.y
                        + " x=" + x + " y=" + y);
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
        log.e("w=" + w + " h=" + h);
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
        mParams.x = (int) (xInScreen - xInView) > 0 ? (int) (xInScreen - xInView)
                : 0;
        mParams.y = (int) (yInScreen - yInView);
        // log.e("x=" + mParams.x + " y=" + mParams.y);
        windowManager.updateViewLayout(this, mParams);
    }
}
