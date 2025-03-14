package cn.rongcloud.um.ui.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;

import cn.rongcloud.um.R;
import cn.rongcloud.um.base.BaseFragment;
import cn.rongcloud.um.base.Constants;
import cn.rongcloud.um.databinding.FragmentMineBinding;
import cn.rongcloud.um.ui.LanguageSettingActivity;
import cn.rongcloud.um.ui.LoginActivity;
import cn.rongcloud.um.ui.MainViewModel;
import cn.rongcloud.um.utils.SessionManager;
import io.rong.imkit.RongIM;
import io.rong.imkit.userinfo.RongUserInfoManager;
import io.rong.imkit.userinfo.model.GroupUserInfo;
import io.rong.imlib.ChannelClient;
import io.rong.imlib.IRongCoreCallback;
import io.rong.imlib.IRongCoreEnum;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.UserInfo;

// todo: 我的页面
public class MineFragment extends BaseFragment<MainViewModel, FragmentMineBinding> implements View.OnClickListener, RongUserInfoManager.UserDataObserver {
//   下文的 mViewData就是viewbinding的binding

    private boolean isBlock;
    private boolean isPushContent;

    @Override
    public int getContentViewId() {
        return R.layout.fragment_mine;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        getNotificationQuietHoursLevel();
        initListener();
        getUserPortrait();
        getPushContentShowStatus();
        RongUserInfoManager.getInstance().addUserDataObserver(this);
    }

    private void getNotificationQuietHoursLevel() {
        ChannelClient.getInstance().getNotificationQuietHoursLevel(new IRongCoreCallback.GetNotificationQuietHoursCallbackEx() {
            @Override
            public void onSuccess(String startTime, int spanMinutes, IRongCoreEnum.PushNotificationQuietHoursLevel level) {
                if (TextUtils.isEmpty(startTime)) {
                    isBlock = false;
                    mViewData.rcSwitchNotDisturb.setChecked(false);
                } else {
                    isBlock = true;
                    mViewData.rcSwitchNotDisturb.setChecked(true);
                }
            }

            @Override
            public void onError(IRongCoreEnum.CoreErrorCode coreErrorCode) {
                toast("获取免打扰时间失败 coreErrorCode： " + coreErrorCode.getValue());
            }
        });
    }

    private void getPushContentShowStatus() {
        RongIMClient.getInstance().getPushContentShowStatus(new RongIMClient.ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                mViewData.rcSwitchRemotePushContent.setChecked(aBoolean);
                isPushContent = aBoolean;
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {

            }
        });
    }

    // todo: 获取用户头像
    private void getUserPortrait() {
        String currentUserId = RongIM.getInstance().getCurrentUserId();
        if (currentUserId == null) return;
        mViewData.rcUserId.setText(currentUserId);
        UserInfo userInfo = RongUserInfoManager.getInstance()
                .getUserInfo(currentUserId);
        if (userInfo != null) {
            Uri uri = userInfo.getPortraitUri();
            // 加载头像
            if (uri != null) {
                Glide.with(mViewData.rcMinePortrait).load(uri).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(mViewData.rcMinePortrait);
            }
        }
    }

    private void initListener() {
        mViewData.rcLayout.setOnClickListener(this);
        mViewData.tvLanguageChoose.setOnClickListener(this);
        mViewData.rcSwitchNotDisturb.setOnClickListener(this);
        mViewData.rcSwitchRemotePushContent.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.rc_layout) {
            RongIM.getInstance().disconnect();
            RongIM.getInstance().logout();
            SessionManager.getInstance().put(Constants.SP_KEY_APP_TOKEN, "");
            requireActivity().finish();
            goActivity(LoginActivity.class);
        } else if (view.getId() == R.id.rc_switch_not_disturb) {
            if (isBlock) {
                ChannelClient.getInstance().removeNotificationQuietHours(new IRongCoreCallback.OperationCallback() {
                    @Override
                    public void onSuccess() {
                        isBlock = false;
                        mViewData.rcSwitchNotDisturb.setChecked(false);
                    }

                    @Override
                    public void onError(IRongCoreEnum.CoreErrorCode coreErrorCode) {
                        toast("移除失败，coreErrorCode： " + coreErrorCode.getValue());
                        mViewData.rcSwitchNotDisturb.setChecked(true);
                    }
                });
            } else {
                setNotificationQuietHoursLevel();
            }
        } else if (view.getId() == R.id.tv_language_choose) {
            goActivity(LanguageSettingActivity.class);
        } else if (view.getId() == R.id.rc_switch_remote_push_content) {
            RongIMClient.getInstance().setPushContentShowStatus(!isPushContent, new RongIMClient.OperationCallback() {
                @Override
                public void onSuccess() {
                    mViewData.rcSwitchRemotePushContent.setChecked(!isPushContent);
                    isPushContent = !isPushContent;
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    toast("修改失败，errorCode： " + errorCode.getValue());
                }
            });
        }
    }

    private void setNotificationQuietHoursLevel() {
        String startTime = "00:00:00";
        int spanMinutes = 1439;

        ChannelClient.getInstance().setNotificationQuietHoursLevel(startTime, spanMinutes,
                IRongCoreEnum.PushNotificationQuietHoursLevel.PUSH_NOTIFICATION_QUIET_HOURS_LEVEL_BLOCKED,
                new IRongCoreCallback.OperationCallback() {
                    @Override
                    public void onSuccess() {
                        isBlock = true;
                        mViewData.rcSwitchNotDisturb.setChecked(true);
                    }

                    @Override
                    public void onError(IRongCoreEnum.CoreErrorCode coreErrorCode) {
                        toast("设置失败，coreErrorCode： " + coreErrorCode.getValue());
                        mViewData.rcSwitchNotDisturb.setChecked(false);
                    }
                });
    }

    @Override
    public void onDestroy() {
        RongUserInfoManager.getInstance().removeUserDataObserver(this);
        super.onDestroy();
    }

    @Override
    public void onUserUpdate(UserInfo info) {
        getUserPortrait();
    }

    @Override
    public void onGroupUpdate(Group group) {

    }

    @Override
    public void onGroupUserInfoUpdate(GroupUserInfo groupUserInfo) {

    }
}
