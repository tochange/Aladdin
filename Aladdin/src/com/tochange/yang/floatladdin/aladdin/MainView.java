package com.tochange.yang.floatladdin.aladdin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

import com.tochange.yang.floatladdin.LauncherActivity;
import com.tochange.yang.floatladdin.R;
import com.tochange.yang.lib.log;

public class MainView extends View
{

    private static final int HORIZONTAL_SPLIT = 40;

    private static final int VERTICAL_SPLIT = 40;

    private Bitmap mBitmap;

    private Paint mPaint;

    private float[] mInhalePoint;

    private SettingLayoutActivity mMainActivity;

    private boolean clearCanvas = true;

    private AladdinDrawGrid mAladdinDrawGrid;

    public MainView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mPaint = new Paint();
        mInhalePoint = new float[] { 0, 0 };
        mAladdinDrawGrid = new AladdinDrawGrid(HORIZONTAL_SPLIT, VERTICAL_SPLIT);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
//        log.e("");
        float bitmapWidth = mBitmap.getWidth();
        float bitmapHeight = mBitmap.getHeight();

        buildPaths(w, h);
        mAladdinDrawGrid.buildMeshes(bitmapWidth, bitmapHeight);
    }

    private void buildPaths(float endX, float endY)
    {
        mInhalePoint[0] = endX;
        mInhalePoint[1] = endY;
        mAladdinDrawGrid.buildPaths(endX, endY);
    }

    private boolean clearCanvas(Canvas canvas)
    {
        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC));
//        log.e("clearing canvas");
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        if (clearCanvas && clearCanvas(canvas))
        {
            clearCanvas = false;
            return;
        }
        canvas.drawBitmapMesh(mBitmap, mAladdinDrawGrid.getWidth(),
                mAladdinDrawGrid.getHeight(), mAladdinDrawGrid.getVertices(),
                0, null, 0, mPaint);

        if (LauncherActivity.DEBUG_MODE)
        {
            setTextAndPointPaint(mPaint);
            canvas.drawTextOnPath(
                    this.getResources().getString(R.string.path_words),
                    mAladdinDrawGrid.getPaths()[1], 0, 0, mPaint);
            canvas.drawPoints(mAladdinDrawGrid.getVertices(), mPaint);

            setPathPaint(mPaint);
            Path[] paths = mAladdinDrawGrid.getPaths();
            for (Path path : paths)
                canvas.drawPath(path, mPaint);
        }
    }

    private void setPathPaint(Paint mPaint2)
    {
        mPaint.setColor(Color.CYAN);
        mPaint.setStrokeWidth(1);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);

    }

    private void setTextAndPointPaint(Paint mPaint)
    {
        mPaint.setColor(Color.MAGENTA);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(25);

    }

    public void setBitmap(Bitmap bitmap)
    {
        if (bitmap != null)
        {
            mBitmap = bitmap;
            mAladdinDrawGrid.setBitmapSize(mBitmap.getWidth(),
                    mBitmap.getHeight());
        }
    }

    public boolean startAnimation(boolean reverse,
            SettingLayoutActivity mainActivity)
    {
        if (mainActivity != null)
            mMainActivity = mainActivity;
        startAnimationImp(reverse);
        return true;
    }

    private void startAnimationImp(boolean reverse)
    {
        Animation anim = this.getAnimation();
        if (null != anim && !anim.hasEnded())
            return;
        final AladdinAnimation animation = new AladdinAnimation(0,
                VERTICAL_SPLIT + 1, reverse,
                new AladdinAnimation.IAnimationUpdateListener() {
                    @Override
                    public void onAnimUpdate(int index)
                    {
                        mAladdinDrawGrid.buildMeshes(index);
                        invalidate();
                    }
                });

        if (null != animation)
        {
//            log.e("will clear canvas");
            clearCanvas = true;
            invalidate();

            animation
                    .setDuration(LauncherActivity.DEBUG_MODE ? LauncherActivity.ANIMATION_TIME_LONG
                            : LauncherActivity.ANIMATION_TIME_SHORT);
            animation.setAnimationListener(new AnimationListener() {

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
                    if (mMainActivity != null)
                        mMainActivity.finish();
                }
            });

            MainView.this.startAnimation(animation);
        }
    }
}