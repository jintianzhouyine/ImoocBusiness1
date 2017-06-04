/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.youdu.zxing.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.example.mycomputer.imoocbusiness1.R;
import com.google.zxing.ResultPoint;

import com.youdu.zxing.camera.CameraManager;

import java.util.Collection;
import java.util.HashSet;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder
 * rectangle and partial transparency outside it, as well as the laser scanner
 * animation and result points.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class ViewfinderView extends View {

    private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192,
            128, 64};

    private final Paint paint;
    private Bitmap resultBitmap;
    private final int maskColor;
    private final int resultColor;
    private final int resultPointColor;
    private volatile Collection<ResultPoint> possibleResultPoints;
    private volatile Collection<ResultPoint> lastPossibleResultPoints;

    private static final long ANIMATION_DELAY = 10L;
    private static final int OPAQUE = 0xFF;


    private int ScreenRate;


    private static final int CORNER_WIDTH = 15;//设置扫码框四个角的八个矩形的宽度
    private static final int BOEDER_WIDTH = 15;//设置扫码框四个边框的宽度





    private static float density;
    private static final int TEXT_SIZE = 16;

    private static final int TEXT_PADDING_TOP = 30;

    private int slideTop;
    private static final int SPEEN_DISTANCE = 2;
    private int slideBottom;
    private boolean isFirst;

    // This constructor is used when the class is built from an XML resource.
    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Initialize these once for performance rather than calling them every
        // time in onDraw().
        paint = new Paint();
        Resources resources = getResources();
        maskColor = resources.getColor(R.color.viewfinder_mask);
        resultColor = resources.getColor(R.color.result_view);
        resultPointColor = resources.getColor(R.color.possible_result_points);
        possibleResultPoints = new HashSet<ResultPoint>(5);

        density = context.getResources().getDisplayMetrics().density;
        ScreenRate = (int) (20 * density);
    }

    /**
     * 在onDraw 方法在完成绘制
     * @param canvas
     */
    @Override
    public void onDraw(Canvas canvas) {
        //这里取扫描框
        Rect frame = CameraManager.get().getFramingRect();
        if (frame == null) {
            return;
        }

        if (!isFirst) {
            isFirst = true;
            slideTop = frame.top;
            slideBottom = frame.bottom;
        }
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        // Draw the exterior (i.e. outside the framing rect) darkened
        paint.setColor(resultBitmap != null ? resultColor : maskColor);
        /**
         * 这里绘制的是扫码框四周的遮挡
         */
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1,
                paint);
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);

        if (resultBitmap != null) {
            // Draw the opaque result bitmap over the scanning rectangle
            paint.setAlpha(OPAQUE);//设置画笔的透明度
            canvas.drawBitmap(resultBitmap, null, frame, paint);
        } else {

            paint.setColor(getResources().getColor(R.color.border));

            //绘制第一个小矩形
            canvas.drawRect(frame.left, frame.top, frame.left + ScreenRate,
                    frame.top + CORNER_WIDTH, paint);
            canvas.drawRect(frame.left, frame.top, frame.left + CORNER_WIDTH,
                    frame.top + ScreenRate, paint);
            canvas.drawRect(frame.right - ScreenRate, frame.top, frame.right,
                    frame.top + CORNER_WIDTH, paint);
            canvas.drawRect(frame.right - CORNER_WIDTH, frame.top, frame.right,
                    frame.top + ScreenRate, paint);
            canvas.drawRect(frame.left, frame.bottom - CORNER_WIDTH, frame.left
                    + ScreenRate, frame.bottom, paint);
            canvas.drawRect(frame.left, frame.bottom - ScreenRate, frame.left
                    + CORNER_WIDTH, frame.bottom, paint);
            canvas.drawRect(frame.right - ScreenRate, frame.bottom
                    - CORNER_WIDTH, frame.right, frame.bottom, paint);
            canvas.drawRect(frame.right - CORNER_WIDTH, frame.bottom
                    - ScreenRate, frame.right, frame.bottom, paint);
            //绘制四条线
            paint.setColor(getResources().getColor(R.color.border1));
            paint.setStrokeWidth(BOEDER_WIDTH);
            canvas.drawLine(frame.left,frame.top+BOEDER_WIDTH/2,frame.right,frame.top+BOEDER_WIDTH/2,paint);
            canvas.drawLine(frame.left,frame.bottom-BOEDER_WIDTH/2,frame.right,frame.bottom-BOEDER_WIDTH/2,paint);
            canvas.drawLine(frame.left+BOEDER_WIDTH/2,frame.top,frame.left+BOEDER_WIDTH/2,frame.bottom,paint);
            canvas.drawLine(frame.right-BOEDER_WIDTH/2,frame.top,frame.right-BOEDER_WIDTH/2,frame.bottom,paint);
            slideTop += SPEEN_DISTANCE;//定义好描线每秒的移动速度
            if (slideTop >= frame.bottom) {
                slideTop = frame.top;
            }
            Rect lineRect = new Rect();
            lineRect.left = frame.left;
            lineRect.right = frame.right;
            lineRect.top = slideTop;
            lineRect.bottom = slideTop + 18;
            //用来绘制Bitmap
            canvas.drawBitmap(((BitmapDrawable) (getResources()
                            .getDrawable(R.drawable.fle))).getBitmap(), null, lineRect,
                    paint);

            paint.setColor(Color.WHITE);
            paint.setTextSize(TEXT_SIZE * density);
            paint.setAlpha(0x40);
            paint.setTypeface(Typeface.create("System", Typeface.BOLD));
            String text = getResources().getString(R.string.scan_text);
            float textWidth = paint.measureText(text);

            canvas.drawText(
                    text,
                    (width - textWidth) / 2,
                    (float) (frame.bottom + (float) TEXT_PADDING_TOP * density),
                    paint);

            Collection<ResultPoint> currentPossible = possibleResultPoints;
            Collection<ResultPoint> currentLast = lastPossibleResultPoints;
            if (currentPossible.isEmpty()) {
                lastPossibleResultPoints = null;
            } else {
                possibleResultPoints = new HashSet<ResultPoint>(5);
                lastPossibleResultPoints = currentPossible;
                paint.setAlpha(OPAQUE);
                paint.setColor(resultPointColor);
                for (ResultPoint point : currentPossible) {
                    canvas.drawCircle(frame.left + point.getX(), frame.top
                            + point.getY(), 6.0f, paint);
                }
            }
            if (currentLast != null) {
                paint.setAlpha(OPAQUE / 2);
                paint.setColor(resultPointColor);
                for (ResultPoint point : currentLast) {
                    canvas.drawCircle(frame.left + point.getX(), frame.top
                            + point.getY(), 3.0f, paint);
                }
            }

            postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top,
                    frame.right, frame.bottom);
        }
    }

    public void drawViewfinder() {
        resultBitmap = null;
        invalidate();
    }

    /**
     * Draw a bitmap with the result points highlighted instead of the live
     * scanning display.
     *重新绘制扫描方框内的内容
     * @param barcode An image of the decoded barcode.
     */
    public void drawResultBitmap(Bitmap barcode) {
        resultBitmap = barcode;
        invalidate();
    }

    public void addPossibleResultPoint(ResultPoint point) {
        synchronized (possibleResultPoints) {
            possibleResultPoints.add(point);
        }
    }

}
