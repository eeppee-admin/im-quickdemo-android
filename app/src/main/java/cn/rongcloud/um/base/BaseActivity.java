package cn.rongcloud.um.base;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModelProvider;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.rong.imkit.picture.tools.ToastUtils;

/**
 * Activity基类
 *
 * @param <Vm>  数据控制
 * @param <Vdb> Ui控制
 */
public abstract class BaseActivity<Vm extends BaseViewModel, Vdb extends ViewDataBinding> extends AppCompatActivity {
    // 获取布局id
    public abstract int getContentViewId();

    public abstract void setView();//设置UI相关

    public abstract void setData();//设置数据相关

    public Vm mViewModel;
    public Vdb mViewData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if((getIntent().getFlags()&Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)!=0){
                finish();
                return;
        }
        // 设置布局
        setContentView(getContentViewId());
        mViewData = DataBindingUtil.setContentView(this, getContentViewId());
        mViewData.setLifecycleOwner(this);

        createViewModel();
        this.setView();
        this.setData();
    }

    @SuppressWarnings("unchecked")
    public void createViewModel() {
        if (mViewModel == null) {
            Class<BaseViewModel> modelClass;
            Type type = getClass().getGenericSuperclass();
            if (type instanceof ParameterizedType) {
                modelClass = (Class<BaseViewModel>) ((ParameterizedType) type).getActualTypeArguments()[0];
            } else {
                modelClass = BaseViewModel.class;
            }
            // 创建viewmodel
            mViewModel = (Vm) new ViewModelProvider(this).get(modelClass);

        }
    }

    protected void goActivity(Class<? extends Activity> clazz) {
        startActivity(new Intent(this, clazz));
    }

    protected void toast(String msg) {
        try {
            ToastUtils.s(this, msg);
        } catch (Exception e) {
            Looper.prepare();
            ToastUtils.s(this, msg);
            Looper.loop();
        }
    }
}
