package cn.rongcloud.um.base;

import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;


// 数据绑定
public interface DataBindingProvider {

     @LayoutRes
    int getContentViewId();

     void initView(@Nullable Bundle savedInstanceState);

}
