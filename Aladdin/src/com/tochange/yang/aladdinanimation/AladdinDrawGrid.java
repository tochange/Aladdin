package com.tochange.yang.aladdinanimation;

import com.tochange.yang.lib.log;

import android.graphics.Path;
import android.graphics.PathMeasure;

public class AladdinDrawGrid extends AbstractDrawGrid
{

    private Path mFirstPath;

    private Path mSecondPath;

    private PathMeasure mFirstPathMeasure;

    private PathMeasure mSecondPathMeasure;

    public AladdinDrawGrid(int width, int height)
    {
        super(width, height);
        // log.e("");
        mFirstPath = new Path();
        mSecondPath = new Path();
        mFirstPathMeasure = new PathMeasure();
        mSecondPathMeasure = new PathMeasure();
    }

    @Override
    public void buildPaths(float endX, float endY)
    {

        if (mBmpWidth <= 0 || mBmpHeight <= 0)
        {
            throw new IllegalArgumentException(
                    "Bitmap size must be > 0, did you call setBitmapSize(int, int) method?");
        }
        mFirstPathMeasure.setPath(mFirstPath, false);
        mSecondPathMeasure.setPath(mSecondPath, false);

        float w = mBmpWidth;
        float h = mBmpHeight;

        mFirstPath.reset();
        mSecondPath.reset();

        mFirstPath.moveTo(0, VERTICAL_OFFSET);// start point of bezier
        mSecondPath.moveTo(w, VERTICAL_OFFSET);

        mFirstPath.lineTo(0, h + VERTICAL_OFFSET);
        mSecondPath.lineTo(w, h + VERTICAL_OFFSET);

        log.e(endX + "..." + endY);
        log.e(w + "..." + h);
        
        mFirstPath.cubicTo(0, 2 * (endY + VERTICAL_OFFSET) / 3, endX / 2,
                2 * (endY + VERTICAL_OFFSET) / 3, endX, endY);
        mSecondPath.cubicTo(endX / 2, 2 * (endY + VERTICAL_OFFSET) / 3, endX,
                endY, endX, endY);

        // mFirstPath.quadTo(0, (endY + h + VERTICAL_OFFSET) / 2, endX,
        // endY);//at beginning
        // mSecondPath.quadTo(w, (endY + h + VERTICAL_OFFSET) / 2, endX, endY);

    }

    @Override
    public void buildMeshes(int timeIndex)
    {
        // log.e("timeIndex=" + timeIndex + "   mVerticalSplit=" +
        // mVerticalSplit);
        if (mBmpWidth <= 0 || mBmpHeight <= 0)
        {
            throw new IllegalArgumentException(
                    "Bitmap size must be > 0, did you call setBitmapSize(int, int) method?");
        }
        mFirstPathMeasure.setPath(mFirstPath, false);
        mSecondPathMeasure.setPath(mSecondPath, false);

        int index = 0;
        float[] pos1 = { 0.0f, 0.0f };
        float[] pos2 = { 0.0f, 0.0f };
        float firstLen = mFirstPathMeasure.getLength();// all length of the
                                                       // curve
        float secondLen = mSecondPathMeasure.getLength();

        float len1 = firstLen / mVerticalSplit;
        float len2 = secondLen / mVerticalSplit;

        float firstPointDist = timeIndex * len1;
        float secondPointDist = timeIndex * len2;
        float height = mBmpHeight;

        mFirstPathMeasure.getPosTan(firstPointDist, pos1, null);
        mFirstPathMeasure.getPosTan(firstPointDist + height, pos2, null);

        float x1 = pos1[0];
        float x2 = pos2[0];
        float y1 = pos1[1];
        float y2 = pos2[1];
        float firstDist = (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2)
                * (y1 - y2));
        float firstSplitDist = firstDist / mVerticalSplit;

        mSecondPathMeasure.getPosTan(secondPointDist, pos1, null);
        mSecondPathMeasure.getPosTan(secondPointDist + height, pos2, null);
        x1 = pos1[0];
        x2 = pos2[0];
        y1 = pos1[1];
        y2 = pos2[1];

        float secondDist = (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2)
                * (y1 - y2));
        float secondSplitDist = secondDist / mVerticalSplit;

        // vertical,mVerticalSplit lines
        for (int y = 0; y <= mVerticalSplit; y++)
        {
            mFirstPathMeasure.getPosTan(y * firstSplitDist + firstPointDist,
                    pos1, null);
            mSecondPathMeasure.getPosTan(y * secondSplitDist + secondPointDist,
                    pos2, null);

            float fx1 = pos1[0];
            float fx2 = pos2[0];
            float fy1 = pos1[1];
            float fy2 = pos2[1];

            float dy = fy2 - fy1;
            float dx = fx2 - fx1;

            // horizon,mHorizontalSplit pixels
            for (int x = 0; x <= mHorizontalSplit; x++, index++)
            {
                float fx = dx * x / mHorizontalSplit;
                float fy = dy * x / mHorizontalSplit;

                mVertices[index * 2 + 0] = fx + fx1;
                mVertices[index * 2 + 1] = fy + fy1;
            }
        }
    }

    public Path[] getPaths()
    {
        return new Path[] { mFirstPath, mSecondPath };
    }
}