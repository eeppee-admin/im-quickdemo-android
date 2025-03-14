package cn.rongcloud.um.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Map;

import cn.rongcloud.um.custom.CustomMessage;
import io.rong.imkit.IMCenter;
import io.rong.imkit.config.ConversationClickListener;
import io.rong.imkit.conversation.ConversationFragment;
import io.rong.imkit.event.actionevent.RefreshEvent;
import io.rong.imkit.picture.tools.ToastUtils;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;

// 我的聊天页面，具体可以发送消息的聊天页面
public class MyConversationFragment extends ConversationFragment {
    //todo: 为什么这里没有布局
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initListener();
    }

    private void initListener() {
        IMCenter.setConversationClickListener(new ConversationClickListener() {
            // 头像点击事件
            @Override
            public boolean onUserPortraitClick(Context context, Conversation.ConversationType conversationType, UserInfo user, String targetId) {
                ToastUtils.s(getContext(), "点击了头像");
                return true;
            }

            // 头像长按事件
            @Override
            public boolean onUserPortraitLongClick(Context context, Conversation.ConversationType conversationType, UserInfo user, String targetId) {
                ToastUtils.s(getContext(), "长按了头像");
                return true;
            }

            // 聊天消息点击事件
            @Override
            public boolean onMessageClick(Context context, View view, Message message) {
                if (message.getContent() instanceof CustomMessage) {
                    Map<String, String> expansion = message.getExpansion();
                    int num = Integer.parseInt(expansion.get("key1").replace("value", ""));
                    num++;
                    expansion.put("key1", "value" + num);
                    RongIMClient.getInstance().updateMessageExpansion(expansion, message.getUId(), new RongIMClient.OperationCallback() {
                        @Override
                        public void onSuccess() {
                            // 使用IMKit 内置的对消息更新
                            mMessageViewModel.onRefreshEvent(new RefreshEvent(message));
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {

                        }
                    });
                }
                return false;
            }

            @Override
            public boolean onMessageLongClick(Context context, View view, Message message) {
                return false;
            }

            @Override
            public boolean onMessageLinkClick(Context context, String link, Message message) {
                return false;
            }

            @Override
            public boolean onReadReceiptStateClick(Context context, Message message) {
                return false;
            }
        });
    }

}
