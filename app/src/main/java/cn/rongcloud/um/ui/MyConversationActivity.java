package cn.rongcloud.um.ui;

import android.text.TextUtils;
import android.view.View;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Locale;

import cn.rongcloud.um.R;
import cn.rongcloud.um.base.BaseActivity;
import cn.rongcloud.um.databinding.RcActivityMyConversationBinding;
import cn.rongcloud.um.ui.fragment.MyConversationFragment;
import io.rong.imkit.IMCenter;
import io.rong.imkit.utils.RouteUtils;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;


// 具体聊天对话页面
public class MyConversationActivity extends BaseActivity<MainViewModel, RcActivityMyConversationBinding> {
    protected String mTargetId;
    protected Conversation.ConversationType mConversationType;
    private MyConversationFragment conversationFragment;

    // 容器布局
    @Override
    public int getContentViewId() {
        return R.layout.rc_activity_my_conversation;
    }

    @Override
    public void setView() {
        if (getIntent() != null) {
            mTargetId = getIntent().getStringExtra(RouteUtils.TARGET_ID);
            String type = getIntent().getStringExtra(RouteUtils.CONVERSATION_TYPE);
            if (!TextUtils.isEmpty(type)) {
                mConversationType =
                        Conversation.ConversationType.valueOf(type.toUpperCase(Locale.US));
            } else {
                return;
            }
            // 设置为对方的用户id, userId
            if (!TextUtils.isEmpty(mTargetId)) {
                mViewData.topBar.setCenterText(mTargetId);
            }
        }
        // 添加会话界面
        conversationFragment = new MyConversationFragment();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        // 替换页面
        transaction.replace(R.id.container, conversationFragment);
        transaction.commit();
    }


    @Override
    public void setData() {

    }
}
