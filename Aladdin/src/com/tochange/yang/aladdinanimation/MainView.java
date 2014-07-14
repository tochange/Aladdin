package com.tochange.yang.aladdinanimation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;

import com.tochange.yang.lib.BadgeView;
import com.tochange.yang.lib.Utils;

public  class MainView extends View {

        private static final int WIDTH = 40;
        private static final int HEIGHT = 40;
        private static long ANIMATION_TIME = 0;//500

        private Bitmap mBitmap;
        private boolean mIsDebug;
        private Paint mPaint;
        private float[] mInhalePoint;
        private AladdinDrawGrid mAladdinDrawGrid;
        
        public MainView(Context context, AttributeSet attrs) {
            super(context, attrs);
            setFocusable(true);      

            mBitmap = BitmapFactory.decodeResource(getResources(),
                    R.drawable.pic);

            mPaint = new Paint();
            mInhalePoint = new float[]{0, 0};
            mAladdinDrawGrid = new AladdinDrawGrid(WIDTH, HEIGHT);
            mAladdinDrawGrid.setBitmapSize(mBitmap.getWidth(), mBitmap.getHeight());
        }
        private int mLastPointX = 0;
        private int mLastPointY = 0;
        private BadgeView badge;//yangxj@20140523

        @Override
        public boolean onTouchEvent(MotionEvent event) {
      
            
            //fasten inhale position,yangxj@20140523
//            int x = (int)event.getX();
//            int y = (int)event.getY();
//            if (event.getAction() == MotionEvent.ACTION_UP) {
//                if (mLastPointX != x || mLastPointY != y) {
//                    mLastPointX = x;
//                    mLastPointY = y;
//                    buildPaths(x,y);
//                    invalidate();
//                }
//            }
           if(startAnimation(true)){
               class SleepTask extends AsyncTask<String, Integer, String>
               {

                   @Override
                   protected String doInBackground(String... arg0)
                   {
                       Utils.sleep(Integer.parseInt(arg0[0]));
                       return null;
                   }

                @Override
                protected void onPostExecute(String result)
                {
                    super.onPostExecute(result);
                    badge.show();
                }
               }
              new SleepTask().execute(ANIMATION_TIME+"");
           }
            
            
            return true;
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            
            float bitmapWidth = mBitmap.getWidth();
            float bitmapHeight = mBitmap.getHeight();
            
            buildPaths(w, h);//initial end point position,yangxj@20140523
//            buildPaths(bitmapWidth / 2, h - 20);//initial end point position
            //call at the beginning to create image's position
            mAladdinDrawGrid.buildMeshes(bitmapWidth,bitmapHeight );
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawColor(0xFFCCFFFF);
            canvas.drawBitmapMesh(mBitmap,
                    mAladdinDrawGrid.getWidth(),
                    mAladdinDrawGrid.getHeight(),
                    mAladdinDrawGrid.getVertices(),
                    0, null, 0, mPaint);
            // Draw the target point.
            mPaint.setColor(Color.MAGENTA);
            mPaint.setStrokeWidth(2);
            mPaint.setAntiAlias(true);
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(mInhalePoint[0], mInhalePoint[1], 5, mPaint);//end point

            if (mIsDebug) {
            	mPaint.setTextAlign(Paint.Align.CENTER);
            	mPaint.setTextSize(25);
            	mPaint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawTextOnPath("往这条路径收缩",  mAladdinDrawGrid.getPaths()[1], 0, 0, mPaint);
                
                // Draw the mesh vertices.
                canvas.drawPoints(mAladdinDrawGrid.getVertices(), mPaint);
                // Draw the paths
                mPaint.setColor(Color.CYAN);
                mPaint.setStyle(Paint.Style.STROKE);
                Path[] paths = mAladdinDrawGrid.getPaths();
                for (Path path : paths) {
                    canvas.drawPath(path, mPaint);
                }
            }
        }
        public void setIsDebug(boolean isDebug) {
            mIsDebug = isDebug;
            invalidate();
            if (mIsDebug)
                ANIMATION_TIME = 3000;
            else 
                ANIMATION_TIME = 500;
        }
        
        public void setDeleteButton(BadgeView badge) {
            this.badge = badge;
        }
        
        boolean lastReverse = true;//add by yangxj@20140523
        public boolean startAnimation(boolean reverse) {
            if (lastReverse == reverse)
                return false;
            startAnimationImp(reverse); 
            lastReverse = reverse;
            return true;
        }
        
        private void startAnimationImp(boolean reverse){
            Animation anim = this.getAnimation();
            if (null != anim && !anim.hasEnded()) {
                return ;
            }
            BoundaryAnimation animation = new BoundaryAnimation(0, HEIGHT + 1, reverse,
                    new BoundaryAnimation.IAnimationUpdateListener() {
                        @Override
                        public void onAnimUpdate(int index) {
                            mAladdinDrawGrid.buildMeshes(index);
                            invalidate();
                        }
                    });
        
            if (null != animation) {
                animation.setDuration(ANIMATION_TIME);
                this.startAnimation(animation);
            }
        }
        private void buildPaths(float endX, float endY) {
            mInhalePoint[0] = endX;
            mInhalePoint[1] = endY;
            mAladdinDrawGrid.buildPaths(endX, endY);
        }

}