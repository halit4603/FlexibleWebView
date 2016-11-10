package com.ljy.lambdademo;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;

/**
 * @Email:lijiayan_mail@163.com
 * @created_time 2016/11/02 14:41
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
public class FlexScrollView extends ScrollView {

    private static final String TAG = "FLEXSCROLLVIEW";

    /**
     * 可以滑动的最大距离
     */
    private static final int MAX_DISTANCE = 300;

    /**
     * child View
     */
    private View childView;

    /**
     * 手松开时界面回到原始位置的动画时间
     */
    private static final int ANIM_TIME = 300;

    /**
     * 手指按下时的Y值,用来计算移动中的移动距离
     * 如果手指按下时不能上拉或者下拉，则会在手指移动时更新为当前手指的Y值
     */
    private float startY;

    /**
     * 用来记录正常的布局位置
     */
    private Rect rect = new Rect();

    /**
     * 记录手指按下时是否可以下拉
     */
    private boolean canPullDown = false;

    /**
     * 记录手指按下时是否可以上拉
     */
    private boolean canPullUp = false;

    /**
     * 记录手指滑动的过程中是否移动了布局
     */
    private boolean isMoved = false;

    public FlexScrollView(Context context) {
        this(context, null);
    }

    public FlexScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlexScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 加载完XMl布局文件后获取childView
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 0) {
            childView = getChildAt(0);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (childView != null) {
            //scrollView 唯一的一个子View的位置信息,这个位置信息将在整个生命周期中保持不变
            rect.set(childView.getLeft(), childView.getTop(), childView.getRight(), childView.getBottom());
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (childView == null)
            return super.dispatchTouchEvent(ev);
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //手指按下时获取参数状态的初始值
                canPullDown = isCanPullDown();
                canPullUp = isCanPullUp();
                startY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (!canPullUp && !canPullDown) {
                    canPullDown = isCanPullDown();
                    canPullUp = isCanPullDown();
                    startY = ev.getY();
                    break;
                }
                float nowY = ev.getY();
                int deltaY = (int) (nowY - startY);
                //判断是否应该移动布局
                //1.可以下拉并且手指向下移动
                //2.可以上拉并且手指向上移动
                //3.既可以上拉也可以下拉，也就是childView的高度在ScrollView的高度范围内
                boolean shouldMove = (canPullDown && deltaY > 0) || (canPullUp && deltaY < 0) || (canPullUp && canPullDown);
                if (shouldMove) {
                    int offset = (int) (deltaY * 0.25f);
                    childView.layout(rect.left, rect.top + offset, rect.right, rect.bottom + offset);
                    isMoved = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!isMoved)
                    break;
                TranslateAnimation anim = new TranslateAnimation(0, 0, childView.getTop(), rect.top);
                // 设置动画时间
                anim.setDuration(ANIM_TIME);
                // 给view设置动画
                childView.setAnimation(anim);
                // 设置回到正常的布局位置
                childView.layout(rect.left, rect.top, rect.right, rect.bottom);
                // 将标志位重置
                canPullDown = false;
                canPullUp = false;
                isMoved = false;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 判断是否滚动到顶部
     *
     * @return
     */
    private boolean isCanPullDown() {
        return getScrollY() == 0 || childView.getHeight() < getScrollY() + getHeight();
    }

    /**
     * 判断是否滚动到底部
     *
     * @return
     */
    private boolean isCanPullUp() {
        return childView.getHeight() <= getScrollY() + getHeight();
    }
}
