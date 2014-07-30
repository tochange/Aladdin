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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;

import com.tochange.yang.floatladdin.LauncherActivity;
import com.tochange.yang.floatladdin.R;
import com.tochange.yang.floatladdin.floatwindow.FloatWindowService;
import com.tochange.yang.lib.log;

public class MainView extends View {

	private Bitmap mBitmap;

	private Paint mPaint;

	private float[] mInhalePoint;

	private boolean clearCanvas = true;

	private AladdinDrawGrid mAladdinDrawGrid;

	public MainView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint = new Paint();
		mInhalePoint = new float[] { 0, 0 };
		mAladdinDrawGrid = new AladdinDrawGrid(LauncherActivity.ANIMATION_SPLIT_HORIZONTAL, LauncherActivity.ANIMATION_SPLIT_VERTICAL);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return true;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		log.d("");
		buildPaths(w, h);
		// not need to draw the original image,yangxj@20140723
		// float bitmapWidth = mBitmap.getWidth();
		// float bitmapHeight = mBitmap.getHeight();
		// mAladdinDrawGrid.buildMeshes(bitmapWidth, bitmapHeight);
	}

	private void buildPaths(float endX, float endY) {
		mInhalePoint[0] = endX;
		mInhalePoint[1] = endY;
		mAladdinDrawGrid.buildPaths(endX, endY);
	}

	private boolean clearCanvas(Canvas canvas) {
		log.d("clearing canvas.");
		Paint paint = new Paint();
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		canvas.drawPaint(paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC));
		return true;
	}

	public boolean startAnimation(boolean reverse) {
		log.d("will clear canvas.");
		clearCanvas = true;
		invalidate();// add
		startAnimationImp(reverse);
		return true;
	}

	private void startAnimationImp(boolean reverse) {
		Animation anim = this.getAnimation();
		if (null != anim && !anim.hasEnded())
			return;
		AnimationSet animationSet = new AnimationSet(getContext(), null);

		final AladdinAnimation animation = new AladdinAnimation(0,
				LauncherActivity.ANIMATION_SPLIT_VERTICAL + 1, reverse,
				new AladdinAnimation.IAnimationUpdateListener() {
					@Override
					public void onAnimUpdate(int index) {
						// log.e("build-" + (index) + "");
						mAladdinDrawGrid.buildMeshes(index);
						invalidate();
					}
				});

		if (null != animation) {
			setAnimationTimeAndListener(animation);
			animationSet.addAnimation(getAlphaAnimation());
			animationSet.addAnimation(animation);
			startAnimation(animationSet);
		}
	}

	private Animation getAlphaAnimation() {
		Animation alphaAnimation = new AlphaAnimation(0, 1);
		alphaAnimation.setDuration(LauncherActivity.ANIMATION_TIME / 2);
		return alphaAnimation;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (clearCanvas && clearCanvas(canvas)) {
			clearCanvas = false;
			return;
		}
		if (mBitmap == null && clearCanvas(canvas)) {
			return;
		}
		// log.e("");
		if (LauncherActivity.DEBUG_MODE) {
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
		canvas.drawBitmapMesh(mBitmap, mAladdinDrawGrid.getWidth(),
				mAladdinDrawGrid.getHeight(), mAladdinDrawGrid.getVertices(),
				0, null, 0, mPaint);
	}

	private void setPathPaint(Paint mPaint2) {
		mPaint.setColor(Color.CYAN);
		mPaint.setStrokeWidth(1);
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.STROKE);

	}

	private void setTextAndPointPaint(Paint mPaint) {
		mPaint.setColor(Color.MAGENTA);
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setTextAlign(Paint.Align.CENTER);
		mPaint.setTextSize(25);

	}

	public void setBitmap(Bitmap bitmap) {
		if (bitmap != null) {
			mBitmap = bitmap;
			mAladdinDrawGrid.setBitmapSize(mBitmap.getWidth(),
					mBitmap.getHeight());
		}
	}

	private void setAnimationTimeAndListener(AladdinAnimation animation) {
		animation
				.setDuration(LauncherActivity.DEBUG_MODE ? LauncherActivity.ANIMATION_TIME_LONG
						: LauncherActivity.ANIMATION_TIME_SHORT);
		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				FloatWindowService.mFloatWindowService.onDestroy();
				System.exit(0);
			}
		});
	}
}