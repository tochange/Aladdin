package com.tochange.yang.floatladdin.aladdin;

import com.tochange.yang.lib.log;

import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;

public class AladdinAnimation extends Animation
{

    public interface IAnimationUpdateListener
    {
        public void onAnimUpdate(int index);
    }

    private int mFromIndex;

    private int mEndIndex;

    private boolean mReverse;

    private IAnimationUpdateListener mListener;

    public AladdinAnimation(int fromIndex, int endIndex, boolean reverse,
            IAnimationUpdateListener listener)
    {
        mFromIndex = fromIndex;
        mEndIndex = endIndex;
        mReverse = reverse;
        mListener = listener;
    }

    public boolean getTransformation(long currentTime,
            Transformation outTransformation)
    {
        return super.getTransformation(currentTime, outTransformation);
    }

    int lastTimeIndex;

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t)
    {
        Interpolator interpolator = this.getInterpolator();
        if (null != interpolator)
        {
            float value = interpolator.getInterpolation(interpolatedTime);
            interpolatedTime = value;
        }
        // log.e(""+(mReverse = true));
        if (mReverse)
            interpolatedTime = 1.0f - interpolatedTime;

        int currentIndex = (int) (mFromIndex + (mEndIndex - mFromIndex)
                * interpolatedTime);
        if (currentIndex == lastTimeIndex)
            return;
        lastTimeIndex = currentIndex;

        if (null != mListener)
            mListener.onAnimUpdate(currentIndex);
    }
}