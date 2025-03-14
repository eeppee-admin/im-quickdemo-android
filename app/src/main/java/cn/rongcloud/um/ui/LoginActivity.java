package cn.rongcloud.um.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.um.R;
import cn.rongcloud.um.base.BaseActivity;
import cn.rongcloud.um.base.Constants;
import cn.rongcloud.um.base.MyApplication;
import cn.rongcloud.um.bean.GroupMemberInfo;
import cn.rongcloud.um.custom.CustomMessage;
import cn.rongcloud.um.custom.CustomMessageProvider;
import cn.rongcloud.um.custom.MyDataProcessor;
import cn.rongcloud.um.custom.MyExtensionConfig;
import cn.rongcloud.um.databinding.ActivityLoginBinding;
import cn.rongcloud.um.net.HttpQUtils;
import cn.rongcloud.um.utils.DataProcessorUtil;
import cn.rongcloud.um.utils.SessionManager;

import io.rong.imkit.GlideKitImageEngine;
import io.rong.imkit.IMCenter;
import io.rong.imkit.RongIM;
import io.rong.imkit.config.RongConfigCenter;
import io.rong.imkit.conversation.extension.RongExtensionManager;
import io.rong.imkit.feature.mention.IExtensionEventWatcher;
import io.rong.imkit.feature.mention.RongMentionManager;
import io.rong.imkit.picture.tools.ToastUtils;
import io.rong.imkit.userinfo.RongUserInfoManager;
import io.rong.imkit.userinfo.model.GroupUserInfo;
import io.rong.imkit.utils.RouteUtils;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.chatroom.base.RongChatRoomClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.MentionedInfo;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.push.PushEventListener;
import io.rong.push.PushType;
import io.rong.push.RongPushClient;
import io.rong.push.notification.PushNotificationMessage;
import io.rong.push.platform.PushAdapter;

/**
 * 登录页
 */
public class LoginActivity extends BaseActivity<MainViewModel, ActivityLoginBinding> {
    private final String TAG = LoginActivity.this.getClass().getName();

    private String appKeySp;

    @Override
    public int getContentViewId() {
        return R.layout.activity_login;
    }

    @Override
    public void setView() {
        // 拿到appkey
        appKeySp = SessionManager.getInstance().getString(Constants.SP_KEY_APP_KEY);
        // 拿到appsecret
        String appSecretSp = SessionManager.getInstance().getString(Constants.SP_KEY_APP_SECRET);
        // 拿到token
        String token = SessionManager.getInstance().getString(Constants.SP_KEY_APP_TOKEN);
        if (!TextUtils.isEmpty(token)) {
            // 初始化融云通过appkey
            initRC(appKeySp);
            // 通过token来连接融云
            connectIM(token);
            return;
        }

        mViewData.imageLoad.setVisibility(View.GONE);

        if (TextUtils.isEmpty(appKeySp)) {
            mViewData.editAppkey.setText(Constants.APP_KEY);
        } else {
            mViewData.editAppkey.setText(appKeySp);
        }

        if (TextUtils.isEmpty(appSecretSp)) {
            mViewData.editSecret.setText(Constants.SECRET);
        } else {
            mViewData.editSecret.setText(appSecretSp);
        }

        mViewData.editUserid.setText(Constants.USER_ID);
        initListener();
    }

    @Override
    public void setData() {

    }


    private void initListener() {
        mViewData.topBar.setOnRightIconClickListener(view -> goActivity(SettingActivity.class));
        // 登录
        mViewData.button.setOnClickListener(v -> {
            String appKey = mViewData.editAppkey.getText().toString();
            if (TextUtils.isEmpty(appKeySp) || !appKeySp.equals(appKey) || !IMCenter.getInstance().isInitialized()) {
                initRC(appKey);
            }
            getToken();
        });
    }

    private void initRC(String appKey) {
        // todo: 这里主要做初始化
        if (IMCenter.getInstance().isInitialized()) {
            RongIM.getInstance().disconnect();
            // 初始化融云SDK
            RongIMClient.init(getApplication(), appKey);
            RongExtensionManager.init(getApplication(), appKey);
        } else {
            RongIM.init(getApplication(), appKey);
        }

        // 在打开会话列表页面之前，调用设置自定义的数据处理器
        DataProcessorUtil.conversationTypes = DataProcessorUtil.supportedTypes();
        // 配置
        RongConfigCenter.conversationListConfig().setDataProcessor(new MyDataProcessor());
        // 消息
        ArrayList<Class<? extends MessageContent>> myMessages = new ArrayList<>();
        myMessages.add(CustomMessage.class);
        // 注册消息类型
        RongIMClient.registerMessageType(myMessages);
        RongConfigCenter.conversationConfig().addMessageProvider(new CustomMessageProvider());
        RongExtensionManager.getInstance().setExtensionConfig(new MyExtensionConfig());
        // todo: 注册 Activity
        RouteUtils.registerActivity(RouteUtils.RongActivityType.ConversationListActivity, MainActivity.class);
        RouteUtils.registerActivity(RouteUtils.RongActivityType.ConversationActivity, MyConversationActivity.class);
        // 初始化用户和群组信息内容提供者
        initInfoProvider();
        sendAllPerson();
        // 设置头像信息
        initRongPortrait();

    }

    /**
     * todo: 获取token
     */
    private void getToken() {
        // 网络获取token, 传入appkey, appsecret, userId 以获得token
        HttpQUtils.getToken(
                mViewData.editAppkey.getText().toString(),
                mViewData.editSecret.getText().toString(),
                mViewData.editUserid.getText().toString(),
                new HttpQUtils.GetTokenCallback() {
                    @Override
                    public void onGetTokenSuccess(@NonNull String token) {
                        // 从EditText获取
                        String appKey = mViewData.editAppkey.getText().toString();
                        String appSecret = mViewData.editSecret.getText().toString();
                        if (TextUtils.isEmpty(appKeySp)) {
                            SessionManager.getInstance().put(Constants.SP_KEY_APP_KEY, appKey);
                            SessionManager.getInstance().put(Constants.SP_KEY_APP_SECRET, appSecret);
                        } else {
                            if (!appKeySp.equals(appKey)) {
                                SessionManager.getInstance().put(Constants.SP_KEY_APP_KEY, appKey);
                                SessionManager.getInstance().put(Constants.SP_KEY_APP_SECRET, appSecret);
                            }
                        }

                        Log.i("token", token);
                        connectIM(token);
                    }

                    @Override
                    public void onGetTokenFailed(@NonNull String err) {
                        Log.i("onGetTokenFailed", err);
                    }

                });
    }

    // todo: 根据token来连接到IM中心的, token可能随时变化
    private void connectIM(@NonNull String token) {
        // todo: 连接
        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onSuccess(String t) {
                Log.i("connect_success", t);
                // token存入SP
                SessionManager.getInstance().put(Constants.SP_KEY_APP_TOKEN, token);
                goActivity(MainActivity.class);
                finish();
            }

            @Override
            public void onError(RongIMClient.ConnectionErrorCode e) {
                Log.i("connect_error", e.name());
                    //连接已存在 直接跳转到主页面
                if (e.equals(RongIMClient.ConnectionErrorCode.RC_CONNECTION_EXIST)) {
                    goActivity(MainActivity.class);
                    finish();
                }

            }

            @Override
            public void onDatabaseOpened(RongIMClient.DatabaseOpenStatus code) {
                // 连接数据库
                Log.i("connect_database", code.name());
            }
        });
    }

    //
    private void sendAllPerson() {
        RongExtensionManager.getInstance()
                .addExtensionEventWatcher(
                        new IExtensionEventWatcher() {
                            @Override
                            public void onTextChanged(
                                    Context context,
                                    Conversation.ConversationType type,
                                    String targetId,
                                    int cursorPos,
                                    int count,
                                    String text) {
                            }

                            @Override
                            public void onSendToggleClick(Message message) {
                                if (message != null
                                        && message.getContent() != null
                                        && message.getContent().getMentionedInfo() != null
                                        && message.getContent()
                                        .getMentionedInfo()
                                        .getMentionedUserIdList()
                                        != null
                                        && message.getContent()
                                        .getMentionedInfo()
                                        .getMentionedUserIdList()
                                        .size()
                                        > 0
                                        && message.getContent()
                                        .getMentionedInfo()
                                        .getMentionedUserIdList()
                                        .get(0)
                                        .equals(String.valueOf(-1))) {
                                    message.getContent()
                                            .getMentionedInfo()
                                            .setType(MentionedInfo.MentionedType.ALL);
                                }
                            }

                            @Override
                            public void onDeleteClick(
                                    Conversation.ConversationType type,
                                    String targetId,
                                    EditText editText,
                                    int cursorPos) {
                            }

                            @Override
                            public void onDestroy(
                                    Conversation.ConversationType type, String targetId) {
                            }
                        });
    }

    public void initInfoProvider() {

        // 单聊用户信息提供者
        RongUserInfoManager.getInstance().setUserInfoProvider(s -> {
            int num = Integer.parseInt(String.valueOf(s.hashCode()).substring((String.valueOf(s.hashCode())).length() - 1));
            Log.e(TAG, "num: " + num);
            // 此处应异步调用，从应用后台获取数据
            UserInfo userInfo;
            if (num < 10) {
                userInfo = new UserInfo(s, "name" + s, Uri.parse(Constants.DefaultAvatar[num]));
            } else {
                userInfo = new UserInfo(s, "name" + s, null);
            }
            RongUserInfoManager.getInstance().refreshUserInfoCache(userInfo);
            return null;
        }, true);
        // 群聊用户信息提供者
        RongUserInfoManager.getInstance().setGroupInfoProvider(groupId -> {
            // 此处为异步请求群信息逻辑，需要开发者实现
//                Group group = new Group(groupId, "group" + groupId, Uri.parse("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fp5.itc.cn%2Fq_70%2Fimages03%2F20200816%2F074283744b4b46f3aaeb9d93d2d3fb5f.jpeg&refer=http%3A%2F%2Fp5.itc.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1657867592&t=6698d1d70ee8e953860728a71ff55d27"));
//                RongUserInfoManager.getInstance().refreshGroupInfoCache(group);
            return null;
        }, true);
        // 设置群成员昵称
        RongUserInfoManager.getInstance().setGroupUserInfoProvider((groupId, userId) -> {
            //异步获取群昵称
            GroupUserInfo groupUserInfo = new GroupUserInfo(groupId, userId, "nickname" + userId);
            RongUserInfoManager.getInstance().refreshGroupUserInfoCache(groupUserInfo);
            return null; //同步返回群成员昵称。
        }, true);

        // 设置群组成员信息
        RongMentionManager.getInstance().setGroupMembersProvider((groupId, callback) -> {
//            List<GroupMember> data = new ArrayList<>();
//            GroupMember groupMember = new GroupMember(groupId, "-1", "所有人");

//            data.add(groupMember);
            HttpQUtils.queryGroup(groupId, new HttpQUtils.QueryGroupCallBack() {
                @Override
                public void onSuccess(List<GroupMemberInfo> groupMemberInfos) {
                    List<UserInfo> userInfoList =
                            new ArrayList<>();
                    userInfoList.add(new UserInfo(
                            "-1",
                            "所有人",
                            null));
                    for (GroupMemberInfo groupMemberInfo : groupMemberInfos) {
                        String name = RongUserInfoManager.getInstance().getGroupUserInfo(groupId, groupMemberInfo.getId()).getNickname();
                        if (TextUtils.isEmpty(name)) {
                            name = "name" + groupMemberInfo.getId();
                        }

                        UserInfo info =
                                new UserInfo(
                                        groupMemberInfo.getId(),
                                        name,
                                        null);
                        userInfoList.add(info);
                    }
                    //此处为应用层获取群组成员逻辑
                    callback.onGetGroupMembersResult(userInfoList); // 调用 callback 的 onGetGroupMembersResult 回传群组信息
                }

                @Override
                public void onFailed(@NonNull String err) {
                    Looper.prepare();
                    toast("获取群成员列表 err : " + err);

                    Looper.loop();
                }
            });

        });
//        UserInfo userInfo = new UserInfo("hao", "hao1", getMipMapToUri(R.mipmap.icon_plus));
//        RongUserInfoManager.getInstance().refreshUserInfoCache(userInfo);
        RongChatRoomClient.setChatRoomMemberListener((chatRoomMemberActions, roomId) -> {

        });
    }

    /**
     * todo: 融云头像设置圆形，但是这里的都想url没有配置
     */
    public void initRongPortrait() {
        RongConfigCenter.featureConfig().setKitImageEngine(new GlideKitImageEngine() {
            @Override
            public void loadConversationPortrait(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView, Message message) {
                // Glide 加载
                Glide.with(imageView).load(url)

                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))

                        .into(imageView);
            }

            @Override
            public void loadConversationListPortrait(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView, Conversation conversation) {
                Glide.with(imageView).load(url)

                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))

                        .into(imageView);
            }
        });
    }
}