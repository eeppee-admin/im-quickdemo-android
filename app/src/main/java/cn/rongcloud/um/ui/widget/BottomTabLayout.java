package cn.rongcloud.um.ui.widget;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;


// 自定义底部Tab布局，我自己不用设置
public class BottomTabLayout extends LinearLayout {
    private static final String TAG = "BottomTabLayout";
    private OnItemTabClickListener onItemTabClickListener;
    private int lastSelectedItemPos = 0;
    private ViewPager viewPager;
    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            selectItem(position);
            if (onItemTabClickListener != null) {
                onItemTabClickListener.onItemTabClick(position, null);

            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    public BottomTabLayout(Context context) {
        this(context, null);
    }

    public BottomTabLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomTabLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(HORIZONTAL);//水平方向
        ViewCompat.setElevation(this, 5);
    }

    private void setListeners() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final int position = i;
            final View child = getChildAt(i);
            child.setOnClickListener(v -> {
                if (onItemTabClickListener != null) {
                    //防止选中的tab被重复点击
                    int indexOfChild = indexOfChild(v);
                    if (lastSelectedItemPos == indexOfChild) {
                        return;
                    }
                    if (viewPager != null) {
                        viewPager.setCurrentItem(indexOfChild, false);
                    }
                    if (onItemTabClickListener.onItemTabClick(position, v)) {
                        return;
                    }
                    selectItem(indexOfChild);
                }
            });
        }
    }

    public void setUpWithViewPager(ViewPager viewPager) {
        if (viewPager != null && viewPager.getAdapter() != null) {
            this.viewPager = viewPager;
            viewPager.addOnPageChangeListener(onPageChangeListener);
            setOnItemTabClickListener((position, itemView) -> {
                BottomTabLayout.this.selectItem(itemView);
                return false;
            });
        }
    }

    public void setOnItemTabClickListener(OnItemTabClickListener onItemTabClickListener) {
        this.onItemTabClickListener = onItemTabClickListener;
    }

    public void selectItem(View itemView) {
        int index = indexOfChild(itemView);
        selectItem(index);
    }

    public void selectItem(int position) {
        if (position < getChildCount()) {
            View lastItem = getChildAt(lastSelectedItemPos);
            //让上一个被选中的tab恢复原来状态
            changeItemSelectState(lastItem, false);
            lastSelectedItemPos = position;
            View selectedItem = getChildAt(lastSelectedItemPos);
            //选中itemView
            changeItemSelectState(selectedItem, true);
        }
    }

    //改变tab 选中状态
    private void changeItemSelectState(View view, boolean selected) {
        if (view instanceof BottomTabView) {
            ((BottomTabView) view).setSelectState(selected);
        } else {
            view.setSelected(selected);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (viewPager != null) {
            viewPager.removeOnPageChangeListener(onPageChangeListener);
        }
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable parcelable = super.onSaveInstanceState();
        if (parcelable != null) {
            SavedState ss = new SavedState(parcelable);
            ss.pos = lastSelectedItemPos;
            return ss;
        }
        return null;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState) state;
        int pos = ss.pos;
        selectItem(pos);
        super.onRestoreInstanceState(state);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 0) {
            View childAt = getChildAt(0);
            selectItem(childAt);
        }
        setListeners();
    }

    public interface OnItemTabClickListener {
        /**
         * @param position position of item
         * @param itemView child at position
         * @return weather consumer event
         */
        boolean onItemTabClick(int position, View itemView);
    }

    public static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        int pos;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public SavedState(Parcel source) {
            super(source);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(pos);
        }
    }
}