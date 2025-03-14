package cn.rongcloud.um.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import cn.rongcloud.um.base.BaseViewModel;

import java.util.ArrayList;
import java.util.List;

// 主页viewmodel
public class MainViewModel extends BaseViewModel {
    public MainViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<String>> getInfo(){
        MutableLiveData<List<String>> liveData = new MutableLiveData<>();
        liveData.postValue(new ArrayList<>());
        return liveData;
    }
}
