package cn.rongcloud.um.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.um.R;
import cn.rongcloud.um.base.BaseActivity;
import cn.rongcloud.um.base.Constants;
import cn.rongcloud.um.databinding.ActivityMainBinding;
import cn.rongcloud.um.ui.adapter.ViewPagerAdapter;
import cn.rongcloud.um.ui.dialog.SentChatDialog;
import cn.rongcloud.um.ui.dialog.SentChatPopupWindow;
import cn.rongcloud.um.ui.fragment.DoMainListFragment;
import cn.rongcloud.um.ui.fragment.MineFragment;
import cn.rongcloud.um.ui.fragment.MyConversationListFragment;
import cn.rongcloud.um.ui.widget.BottomTabLayout;
import cn.rongcloud.um.utils.DataProcessorUtil;
import io.rong.imkit.IMCenter;
import io.rong.imkit.RongIM;
import io.rong.imkit.utils.ExecutorHelper;
import io.rong.imkit.utils.RouteUtils;
import io.rong.imlib.IRongCoreListener;
import io.rong.imlib.RongCoreClient;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;

public class MainActivity extends BaseActivity<MainViewModel, ActivityMainBinding> {
    private List<Fragment> mListFrg = new ArrayList<>();
    private SentChatPopupWindow sentChatPopupWindow;
    private SentChatDialog sentChatDialog;
    private long clickTime = 0; // 第一次点击的时间

    public static void startMainActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    public void setView() {
        initBottomTab();
        initListener();
    }

    private void initListener() {
        mViewData.topBar.setOnRightIconClickListener(view -> {
            showChatPopupWindow();
        });
        // 接收消息监听器
        RongIM.addOnReceiveMessageListener(onReceiveMessageWrapperListener);
        // 同步会话状态监听器
        IMCenter.getInstance().addSyncConversationReadStatusListener(syncConversationReadStatusListener);
        // 添加连接状态监听器
        RongCoreClient.addConnectionStatusListener(conversationStatusListener);
    }


    @Override
    protected void onResume() {
        super.onResume();
        getUnreadCount();
    }

    private void showChatPopupWindow() {
        if (sentChatPopupWindow == null) {
            sentChatPopupWindow = new SentChatPopupWindow(this);
            sentChatPopupWindow.setBackgroundDrawable(new BitmapDrawable());    // 必须不为null 否则点击外部取消无效
            sentChatPopupWindow.setItemOnClick(new SentChatPopupWindow.SentChatOnClickInterface() {
                @Override
                public void itemOnClick(int index) {
                    switch (index) {
                        case 0:
                            if (sentChatDialog == null) {
                                sentChatDialog = new SentChatDialog(MainActivity.this, R.layout.dialog_sent_chat,
                                        new int[]{R.id.tv_cancel, R.id.tv_sent_chat});
                                sentChatDialog.setOnItemClickListener(new SentChatDialog.OnItemClickListener() {
                                    @Override
                                    public void OnItemClick(SentChatDialog dialog, View view) {
                                        if (view.getId() == R.id.tv_sent_chat) {
                                            String conversationType = sentChatDialog.getEtConversationType().trim();
                                            String targetId = sentChatDialog.getEtTargetId().getText().toString().trim();
                                            if (TextUtils.isEmpty(conversationType) || TextUtils.isEmpty(targetId)
                                                    || !(Constants.PRIVATE.equals(conversationType) || Constants.GROUP.equals(conversationType))) {
                                                toast("conversationType不为1或3，或者targetId为空，无法发起会话");
                                                return;
                                            }
                                            if (Constants.PRIVATE.equals(conversationType)) {
                                                conversationType = Conversation.ConversationType.PRIVATE.getName().toLowerCase();

                                            } else if (Constants.GROUP.equals(conversationType)) {
                                                conversationType = Conversation.ConversationType.GROUP.getName().toLowerCase();
                                            }
                                            Intent intent = new Intent(MainActivity.this, MyConversationActivity.class);
                                            intent.putExtra(RouteUtils.CONVERSATION_TYPE, conversationType);
                                            intent.putExtra(RouteUtils.TARGET_ID, targetId);
                                            intent.putExtra(RouteUtils.DISABLE_SYSTEM_EMOJI, false);
                                            startActivity(intent);

                                        }
                                    }
                                });
                            }
                            if (!sentChatDialog.isShowing()) {
                                sentChatDialog.show();
                            }
                            break;
                    }
                }
            });
            // 设置此参数获得焦点，否则无法点击
            sentChatPopupWindow.setFocusable(true);
            sentChatPopupWindow.showAsDropDown(mViewData.topBar.getIvRight());
        }
        if (!sentChatPopupWindow.isShowing()) {
            sentChatPopupWindow.showAsDropDown(mViewData.topBar.getIvRight());
        }
    }

    @Override
    public void setData() {

    }

    private void initBottomTab() {
        // 初始化Fragment页面内容以加载进入viewpager
        mListFrg.add(new MyConversationListFragment()); // 这个Fragment集成了Imkit的内容
        mListFrg.add(new DoMainListFragment()); // 这个是功能清单页面内容
        mListFrg.add(new MineFragment()); // 这个是我的页面内容


        // todo: 配置NoScrollViewPager
        ViewPagerAdapter mainFragmentAdapter = new ViewPagerAdapter(getSupportFragmentManager(), mListFrg);
        mViewData.container.setAdapter(mainFragmentAdapter);
        mViewData.container.setNoScroll(false); // 可以滑动
        mViewData.container.setOffscreenPageLimit(1);

        mViewData.mainBottomBarr.setUpWithViewPager(mViewData.container);

        // 设置头部标题栏
        mViewData.mainBottomBarr.setOnItemTabClickListener(new BottomTabLayout.OnItemTabClickListener() {
            @Override
            public boolean onItemTabClick(int position, View itemView) {
                switch (position) {
                    case 0:
                        mViewData.topBar.setCenterText("会话列表");
                        mViewData.topBar.getIvRight().setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        mViewData.topBar.setCenterText("功能清单");
                        mViewData.topBar.getIvRight().setVisibility(View.GONE);
                        break;
                    case 2:
                        mViewData.topBar.setCenterText("我的");
                        mViewData.topBar.getIvRight().setVisibility(View.GONE);
                        break;
                }
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        RongIM.removeOnReceiveMessageListener(onReceiveMessageWrapperListener);
        IMCenter.getInstance().removeSyncConversationReadStatusListeners(syncConversationReadStatusListener);
        RongCoreClient.removeConnectionStatusListener(conversationStatusListener);
        super.onDestroy();
    }

    RongIMClient.OnReceiveMessageWrapperListener onReceiveMessageWrapperListener = new RongIMClient.OnReceiveMessageWrapperListener() {
        @Override
        public boolean onReceived(Message message, int left, boolean hasPackage, boolean offline) {
            Log.i("message", message.toString());
            getUnreadCount();
            return false;
        }
    };
    IRongCoreListener.ConnectionStatusListener conversationStatusListener = new IRongCoreListener.ConnectionStatusListener() {
        @Override
        public void onChanged(ConnectionStatus status) {
            Log.e("ConversationStatus", "status: " + status);
        }
    };

    private void getUnreadCount() {

        RongIMClient.getInstance().getUnreadCount(DataProcessorUtil.conversationTypes, new RongIMClient.ResultCallback<Integer>() {
            @Override
            public void onSuccess(Integer integer) {
                if (Thread.currentThread().equals(Looper.getMainLooper().getThread())) {
                    mViewData.btvMessage.setCount(integer);
                } else {
                    ExecutorHelper.getInstance().mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            mViewData.btvMessage.setCount(integer);
                        }
                    });
                }

            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {
                toast("code: " + e);
            }
        });
    }

    RongIMClient.SyncConversationReadStatusListener syncConversationReadStatusListener = new RongIMClient.SyncConversationReadStatusListener() {
        @Override
        public void onSyncConversationReadStatus(Conversation.ConversationType type, String targetId) {
            getUnreadCount();
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 是否触发按键为back键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return true;
        } else { // 如果不是back键正常响应
            return super.onKeyDown(keyCode, event);
        }
    }

    private void exit() {
        if ((System.currentTimeMillis() - clickTime) > 2000) {
            toast("再按一次后退键退出程序");
            clickTime = System.currentTimeMillis();
        } else {
            this.finish();
            System.exit(0);
        }
    }
}