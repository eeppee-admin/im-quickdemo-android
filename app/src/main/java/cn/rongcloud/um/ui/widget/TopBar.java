package cn.rongcloud.um.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.ViewCompat;

import cn.rongcloud.um.R;

// 头部布局
public class TopBar extends Toolbar {

    private static final int DEFAULT_TITLE_SIZE_IN_SP = 16;

    private final TextView tvCenter;
    private final TextView tvRight;
    private final ImageView ivFinish;
    private final ImageView ivRight, iv_right_add;
    private final View divider;

    public RelativeLayout getMainView() {
        return mainView;
    }

    private final RelativeLayout mainView;


    public ImageView getIvFinish() {
        return ivFinish;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public TopBar(@NonNull final Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.top_bar_layout, this);
        tvCenter = findViewById(R.id.tv_center);
        tvRight = findViewById(R.id.tv_right);
        ivFinish = findViewById(R.id.iv_finish);
        ivRight = findViewById(R.id.iv_right);
        iv_right_add = findViewById(R.id.iv_right_add);
        mainView = findViewById(R.id.top_bar_relative);
        divider = findViewById(R.id.divider);
        tvCenter.requestFocus();

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TopBar);
        String centerText = typedArray.getString(R.styleable.TopBar_centerText);
        boolean isadd_more = typedArray.getBoolean(R.styleable.TopBar_is_add_more, false);
        Drawable addDrawable = typedArray.getDrawable(R.styleable.TopBar_right_add_icon);
        Drawable background = typedArray.getDrawable(R.styleable.TopBar_top_bar_background);
        //底部分割线
        boolean isShowDivider = typedArray.getBoolean(R.styleable.TopBar_isShowDivider, false);
        //如果显示底部分割线
        if (isShowDivider) {
            divider.setVisibility(VISIBLE);
        }
        Drawable finishdrawable = typedArray.getDrawable(R.styleable.TopBar_finishimage);
        if (finishdrawable != null) {
            ivFinish.setImageDrawable(finishdrawable);
        }
        if (background != null) {
            mainView.setBackground(background);
        }

        int CenterTextSize = typedArray.getInt(R.styleable.TopBar_centerTextSize, DEFAULT_TITLE_SIZE_IN_SP);
        tvCenter.setTextSize(CenterTextSize);

        Drawable drawable = typedArray.getDrawable(R.styleable.TopBar_rightIcon);
        if (drawable != null) {
            ivRight.setVisibility(VISIBLE);
            ivRight.setImageDrawable(drawable);
        }

        if (isadd_more) {
            tvRight.setVisibility(GONE);
            iv_right_add.setVisibility(VISIBLE);
            if (addDrawable != null) {
                iv_right_add.setImageDrawable(addDrawable);
            }
        }
        boolean isHideBack = typedArray.getBoolean(R.styleable.TopBar_hideBack, false);

        if (!isHideBack) {
            //点击返回关闭当前Activity
            ivFinish.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (context instanceof Activity) {
                        ((Activity) context).onBackPressed();
                    }
                }
            });
        } else {//隐藏返回键
            ivFinish.setVisibility(GONE);
        }

        if (!TextUtils.isEmpty(centerText)) {
            tvCenter.setText(centerText);
        }
        String rightText = typedArray.getString(R.styleable.TopBar_rightText);
        if (!TextUtils.isEmpty(rightText)) {
            tvRight.setText(rightText);
        }

        int color = typedArray.getColor(R.styleable.TopBar_right_textColor, getResources().getColor(R.color.color_3));
        tvRight.setTextColor(color);


        typedArray.recycle();

        TypedArray array = context.obtainStyledAttributes(new int[]{R.attr.topbar_background, android.R.attr.homeAsUpIndicator});
        int backgroundColor = array.getColor(0, context.getResources().getColor(R.color.topbar_background_color));
        Drawable homeAsUpIndicator = array.getDrawable(1);
        array.recycle();
//        ivFinish.setImageDrawable(homeAsUpIndicator);
        setBackgroundColor(backgroundColor);

        ViewCompat.setElevation(this, 0);
        setElevation(0);//设置阴影效果,不设为0会有下划线
        setContentInsetsAbsolute(0, 0);
        setTitleMargin(0, 0, 0, 0);
//        setPadding(0, getStatusBarHeight(), 0, 0);
        setMinimumHeight(0);
        setMinimumWidth(0);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public void setTitle(CharSequence title) {
        setCenterText(title);
    }

    @Override
    public void setTitle(int resId) {
        setCenterText(getContext().getResources().getString(resId));
    }

    @Override
    public void setSubtitle(int resId) {
//        super.setSubtitle(resId);
    }

    @Override
    public void setSubtitle(CharSequence subtitle) {
//        super.setSubtitle(subtitle);
    }

    @Override
    public void setLogo(int resId) {
//        super.setLogo(resId);
    }

    @Override
    public void setLogo(Drawable drawable) {
//        super.setLogo(drawable);
    }

    @Override
    public void setNavigationIcon(int resId) {
//        super.setNavigationIcon(resId);
    }

    @Override
    public void setNavigationIcon(@Nullable Drawable icon) {
//        super.setNavigationIcon(icon);
    }

    @Override
    public void setContentInsetStartWithNavigation(int insetStartWithNavigation) {
//        super.setContentInsetStartWithNavigation(insetStartWithNavigation);
    }

    public TextView getTvCenter() {
        return tvCenter;
    }

    public ImageView getIvRight() {
        return ivRight;
    }

    public TextView getTvRight() {
        return tvRight;
    }

    public void setRighIconVisible(boolean visible) {
        ivRight.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setRightIcon(int imgRes) {
        ivRight.setVisibility(View.VISIBLE);
        ivRight.setImageResource(imgRes);
    }

    public void setRightText(String text) {
        tvRight.setText(text);
        tvRight.setEnabled(!TextUtils.isEmpty(text));
    }

    public void setOnRightIconClickListener(OnClickListener listener) {
        ivRight.setOnClickListener(listener);
    }

    public void setCenterText(CharSequence text) {
        tvCenter.setText(text);
    }

    public void setOnRightClickListener(OnClickListener listener) {
        tvRight.setOnClickListener(listener);
    }

    public void setOnLeftClickListener(OnClickListener listener) {
        ivFinish.setOnClickListener(listener);
    }

    public void setRightAddImageListener(OnClickListener listener) {
        iv_right_add.setOnClickListener(listener);
    }

    public void setOnCenterTextClick(OnClickListener listener) {
        tvCenter.setOnClickListener(listener);
    }

    public void showDivider() {
        divider.setVisibility(View.VISIBLE);
    }
}
