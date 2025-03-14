package cn.rongcloud.um.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.rong.imkit.picture.tools.ToastUtils;

// 抽象类 Fragment使用
public abstract class BaseFragment<Vm extends BaseViewModel, Vdb extends ViewDataBinding> extends Fragment implements DataBindingProvider {
    public Vm mViewModel;
    public Vdb mViewData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewData = DataBindingUtil.inflate(inflater, getContentViewId(), container, false);
        return mViewData.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewData.setLifecycleOwner(this);
        createViewModel();
        initView(savedInstanceState);
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

            mViewModel = (Vm) new ViewModelProvider(this).get(modelClass);

        }
    }

    protected void goActivity(Class<? extends Activity> clazz) {
        startActivity(new Intent(getActivity(), clazz));
    }

    protected void toast(String msg) {
        ToastUtils.s(getActivity(), msg);
    }
}
