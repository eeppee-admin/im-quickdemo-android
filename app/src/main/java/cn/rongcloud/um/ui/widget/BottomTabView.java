package cn.rongcloud.um.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.rongcloud.um.R;

// 底部视图
public class BottomTabView extends RelativeLayout {
    private static final String TAG = "BottomTabView";
    private Drawable drawableN;
    private Drawable drawableS;
    private ImageView tabIcon;
    private TextView tabText;
    private String text;
    private TextView tvCount;
    private int textColorNoraml = 0xff000000;
    private int textColorSelected = 0xff4caf65;
    private float tabTextSize;

    float defaultSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, getResources().getDisplayMetrics());

    public BottomTabView(@NonNull Context context) {
        this(context, null);
    }

    public BottomTabView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomTabView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.item_bottom, this, true);
        tabIcon = findViewById(R.id.tab_icon);
        tabText = findViewById(R.id.tab_text);
        tvCount = findViewById(R.id.tv_count);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BottomTabView);
        drawableN = typedArray.getDrawable(R.styleable.BottomTabView_tabIconNormal);
        drawableS = typedArray.getDrawable(R.styleable.BottomTabView_tabIconSelected);
        textColorNoraml = typedArray.getColor(R.styleable.BottomTabView_tabTextColorNormal, textColorNoraml);
        textColorSelected = typedArray.getColor(R.styleable.BottomTabView_tabTextColorSelected, textColorSelected);
        tabTextSize = typedArray.getDimension(R.styleable.BottomTabView_tabTextSize, defaultSize);
        tabText.setTextColor(textColorNoraml);
        text = typedArray.getString(R.styleable.BottomTabView_tabText);
        tabText.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSize);

        if (drawableN != null) {
            tabIcon.setImageDrawable(drawableN);
        }
        if (!TextUtils.isEmpty(text)) {
            tabText.setText(text);
        }
        typedArray.recycle();
    }

    public void setSelectState(boolean select) {
        setSelected(select);
        tabIcon.setImageDrawable(select ? drawableS : drawableN);
        tabText.setTextColor(select ? textColorSelected : textColorNoraml);
    }

    public String getTabText() {
        return tabText.getText().toString().trim();
    }

    public void setTabText(String text) {
        tabText.setText(text);
    }

    public void setCount(int count) {
        tvCount.setVisibility(count > 0 ? VISIBLE : GONE);
        if (count > 99) {
            tvCount.setText("99+");
            return;
        }
        tvCount.setText("" + count);
    }
}