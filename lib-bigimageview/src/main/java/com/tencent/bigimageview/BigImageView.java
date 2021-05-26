package com.tencent.bigimageview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;

/**
 * Author：岑胜德 on 2021/5/22 02:41
 * <p>
 * 说明：大图片加载解决方案：分块加载+内存复用
 */
public class BigImageView extends View {
    private final BitmapFactory.Options mOptions = new BitmapFactory.Options();
    private final GestureDetector mGestureDetector;
    private final GestureDetector.OnGestureListener mOnGestureListener = new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            if (!mScroller.isFinished()) {
                mScroller.forceFinished(true);
            }
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (distanceY >= mTouchSlop) {
                mScroller.startScroll(0, (int) e1.getY(), 0, (int) distanceY);
            }
            invalidate();
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            mScroller.fling(0, mVisiableRect.top, 0, (int) -velocityY, 0, 0,
                    0, (int) (mImageHeight - getMeasuredHeight() / mScale));
            return false;
        }
    };

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            mVisiableRect.top = mScroller.getCurrY();
            mVisiableRect.bottom = mVisiableRect.top + mVisiableHeight;
            /*向下滑动*/
            if (mVisiableRect.bottom > mImageHeight) {
                mVisiableRect.bottom = mImageHeight;
                mVisiableRect.top = mVisiableRect.bottom - mVisiableHeight;
            }
            /*向上滑动*/
            if (mVisiableRect.top < 0) {
                mVisiableRect.top = 0;
                mVisiableRect.bottom = mVisiableHeight;
            }
            postInvalidate();
        }
    }

    //图片的可视区域
    private final Rect mVisiableRect = new Rect();
    private final Scroller mScroller;
    private BitmapRegionDecoder mRegionDecoder;
    private float mScale;
    private Bitmap mBitmap;
    private Matrix mScaleMatrix;
    private int mImageWidth, mImageHeight;
    private int mVisiableHeight;
    private final int mTouchSlop;

    public BigImageView(Context context) {
        this(context, null);
    }

    public BigImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BigImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mGestureDetector = new GestureDetector(getContext(), mOnGestureListener);
        mScroller = new Scroller(context);
        mScaleMatrix = new Matrix();
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.setOnTouchListener(new OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mGestureDetector.onTouchEvent(event);
            }
        });
    }

    public void setImage(InputStream is) {
        if (is == null) return;
        mOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, mOptions);
        mImageWidth = mOptions.outWidth;
        mImageHeight = mOptions.outHeight;
        mOptions.inMutable = true;//开启内存复用
        mOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        mOptions.inJustDecodeBounds = false;
        try {
            mRegionDecoder = BitmapRegionDecoder.newInstance(is, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        requestLayout();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        final int imageWidth = mOptions.outWidth;
        mVisiableRect.left = 0;
        mVisiableRect.top = 0;
        mVisiableRect.right = imageWidth;
        mScale = width / (float) imageWidth;
        mVisiableHeight = (int) (height / mScale);
        mVisiableRect.bottom = mVisiableHeight;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mRegionDecoder == null) return;
        mOptions.inBitmap = mBitmap;
        mBitmap = mRegionDecoder.decodeRegion(mVisiableRect, mOptions);
        mScaleMatrix.reset();
        mScaleMatrix.setScale(mScale, mScale);
        canvas.drawBitmap(mBitmap, mScaleMatrix, null);

    }
}
