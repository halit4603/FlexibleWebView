package com.ljy.lambdademo;

import android.content.Context;
import android.graphics.Rect;
import android.os.Looper;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

/**
 * @Email:lijiayan_mail@163.com
 * @created_time 2016/11/03 15:49
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * #                                                   #
 * #                       _oo0oo_                     #
 * #                      o8888888o                    #
 * #                      88" . "88                    #
 * #                      (| -_- |)                    #
 * #                      0\  =  /0                    #
 * #                    ___/`---'\___                  #
 * #                  .' \\|     |# '.                 #
 * #                 / \\|||  :  |||# \                #
 * #                / _||||| -:- |||||- \              #
 * #               |   | \\\  -  #/ |   |              #
 * #               | \_|  ''\---/''  |_/ |             #
 * #               \  .-\__  '-'  ___/-. /             #
 * #             ___'. .'  /--.--\  `. .'___           #
 * #          ."" '<  `.___\_<|>_/___.' >' "".         #
 * #         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       #
 * #         \  \ `_.   \_ __\ /__ _/   .-` /  /       #
 * #     =====`-.____`.___ \_____/___.-`___.-'=====    #
 * #                       `=---='                     #
 * #     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   #
 * #                                                   #
 * #               佛祖保佑         永无BUG             #
 * #                                                   #
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
public class DragScrollView extends ScrollView {

    private static final String TAG = "DragScrollView";

    private View contentView;

    private Rect rect = new Rect();

    private boolean canPullDown = false;

    private boolean canPullUp = false;

    private ViewDragHelper mHelper;

    public DragScrollView(Context context) {
        this(context, null);
    }

    public DragScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 0) {
            contentView = getChildAt(0);
            mHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {

                @Override
                public boolean tryCaptureView(View child, int pointerId) {
                    return child == contentView;
                }

                @Override
                public int clampViewPositionVertical(View child, int top, int dy) {
                    return top;
                }

                @Override
                public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                    canPullDown = top > rect.top;
                    canPullUp = top < -1 * (rect.height() - getHeight());
                }

                @Override
                public void onViewReleased(View releasedChild, float xvel, float yvel) {
                    if (releasedChild == contentView) {
                        if (canPullDown) {
                            mHelper.settleCapturedViewAt(rect.left, rect.top);
                        }
                        if (canPullUp) {
                            mHelper.settleCapturedViewAt(rect.left, -(rect.height() - getHeight()));
                        }
                        invalidateView();
                    }
                }
            });
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (getChildCount() > 0) {
            View view = getChildAt(0);
            rect.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        mHelper.processTouchEvent(ev);
        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mHelper.continueSettling(true)){
            invalidateView();
        }
    }

    // 重绘view
    private void invalidateView() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            invalidate();
        } else {
            postInvalidate();
        }
    }
}
