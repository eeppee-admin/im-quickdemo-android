package cn.rongcloud.um.base;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import cn.rongcloud.um.ui.LoginActivity;
import cn.rongcloud.um.utils.SessionManager;
import io.rong.imlib.RongIMClient;
import io.rong.push.PushEventListener;
import io.rong.push.PushType;
import io.rong.push.RongPushClient;
import io.rong.push.notification.PushNotificationMessage;
import io.rong.push.pushconfig.PushConfig;

public class MyApplication extends Application {
    private static final String TAG = MyApplication.class.getSimpleName();
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        SessionManager.initContext(this);
        if (SessionManager.getInstance().getInt(Constants.SP_KEY_CONVERSATION_TYPE) == 0) {
            SessionManager.getInstance().put(Constants.SP_KEY_CONVERSATION_TYPE, 7);
            SessionManager.getInstance().put(Constants.SP_KEY_SUB_CONVERSATION_TYPE, 4);
        }
        // 如果是连接到私有云需要在此配置服务器地址  如果是公有云则不需要调用此方法
        if (!TextUtils.isEmpty(SessionManager.getInstance().getString(Constants.SP_KEY_NAV_URL))) {
            RongIMClient
                    .setServerInfo(SessionManager.getInstance().getString(Constants.SP_KEY_NAV_URL), SessionManager.getInstance().getString(Constants.SP_KEY_FILE_URL));
        }
        // 初始化推送
        initPush();
        RongPushClient.setPushEventListener(new PushEventListener() {
            @Override
            public boolean preNotificationMessageArrived(Context context, PushType pushType, PushNotificationMessage notificationMessage) {
                return false;
            }

            @Override
            public void afterNotificationMessageArrived(Context context, PushType pushType, PushNotificationMessage notificationMessage) {

            }

            @Override
            public boolean onNotificationMessageClicked(Context context, PushType pushType, PushNotificationMessage notificationMessage) {

                // 去到登录
                Intent intent = new Intent(getContext(),LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            }

            @Override
            public void onThirdPartyPushState(PushType pushType, String action, long resultCode) {

            }
        });
    }


    /**
     * 初始化推送
     */
    private void initPush() {
        /*
         * 配置 融云 IM 消息推送
         * 根据需求配置各个平台的推送
         * 配置推送需要在初始化 融云 SDK 之前
         */
        PushConfig config =
                new PushConfig.Builder()
                        .enableFCM(false) // 在 google-services.json 文件中进行配置
                        .enableHWPush(
                                true) // 在 AndroidManifest.xml 中搜索 com.huawei.hms.client.appid 进行设置
                        .enableMiPush(
                                "2882303761520021123",
                                "5202002195123")
                        .enableMeiZuPush(
                                "143576",
                                "c306e9e3de484148a781e63d666ec565")
                        .enableVivoPush(true) // 在 AndroidManifest.xml 中搜索 com.vivo.push.api_key 和com.vivo.push.app_id 进行设置
                        .enableOppoPush(
                                "2310a3b186e2469c82f8e3f3e5b90e61",
                                "61aa1ce93a7849f2a1f8dddc6b16aac4")
                        .build();
        RongPushClient.setPushConfig(config);

    }

}
