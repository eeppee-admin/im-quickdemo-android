package cn.rongcloud.um.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class NoScrollViewPager extends ViewPager {

    /**
     * todo: 默认不可滚动
     */
    private boolean mNoScroll = true;

    public NoScrollViewPager(@NonNull Context context) {
        super(context);
    }

    public NoScrollViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setNoScroll(boolean noScroll) {
        this.mNoScroll = noScroll;
    }

    // todo： 触摸事件
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //如果viewpager中的view不是viewgroup则会执行该方法 所以也需要进行处理
        if (mNoScroll) {
            return false;
        } else {
            return super.onTouchEvent(ev);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        /*
         * This method JUST determines whether we want to intercept thmotion.
         * If we return true, onMotionEvent will be called and we do thactual
         * scrolling there.
         */
        if (mNoScroll) {
            //不拦截 传递给子view去处理 这样左右滚动的事件将在子view中，而viewpager本身则不用处理
            return false;
        } else {
            return super.onInterceptTouchEvent(ev);
        }
    }
}
