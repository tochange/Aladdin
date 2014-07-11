/**
 * Copyright © 2013 CVTE. All Rights Reserved.
 */
package com.tochange.yang.aladdinanimation;

import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;

//import com.tochange.yang.lib.log;

    public  class BoundaryAnimation extends Animation {

        public interface IAnimationUpdateListener {
            public void onAnimUpdate(int index);
        }

        private int mFromIndex;
        private int mEndIndex;
        private boolean mReverse;
        private IAnimationUpdateListener mListener;

        public BoundaryAnimation(int fromIndex, int endIndex, boolean reverse,
                             IAnimationUpdateListener listener) {
            mFromIndex = fromIndex;
            mEndIndex = endIndex;
            mReverse = reverse;
            mListener = listener;
        }

        public boolean getTransformation(long currentTime, Transformation outTransformation) {
            return super.getTransformation(currentTime, outTransformation);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            Interpolator interpolator = this.getInterpolator();
            if (null != interpolator) {
                float value = interpolator.getInterpolation(interpolatedTime);
                interpolatedTime = value;
            }
            if (mReverse) {
                interpolatedTime = 1.0f - interpolatedTime;
            }
            
            int currentIndex = (int) (mFromIndex + (mEndIndex - mFromIndex) * interpolatedTime);

            if (null != mListener) { 
//                log.e("currentIndex=" + currentIndex);
                mListener.onAnimUpdate(currentIndex);
            }
        }
}