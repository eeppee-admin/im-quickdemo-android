package cn.rongcloud.um.ui;

import android.text.TextUtils;
import android.widget.ArrayAdapter;

import cn.rongcloud.um.R;
import cn.rongcloud.um.base.BaseActivity;
import cn.rongcloud.um.databinding.ActivityDoMainChatRoomBinding;

import java.util.List;
import java.util.Map;

import io.rong.imlib.IRongCoreCallback;
import io.rong.imlib.IRongCoreEnum;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.chatroom.base.RongChatRoomClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;


// todo: chatroom暂时不需要看
public class DoMainChatRoomActivity extends BaseActivity<MainViewModel, ActivityDoMainChatRoomBinding> {

    @Override
    public int getContentViewId() {
        return R.layout.activity_do_main_chat_room;
    }

    @Override
    public void setView() {
        // 设置数组Adapter, 就简单设置文字效果
        mViewData.contentListview.setAdapter(
                new ArrayAdapter<>(
                        this,
                        R.layout.picklist_text,
                        new String[]{"加入聊天室", "退出聊天室", "获取聊天室历史消息", "设置kv", "获取kv"}));
        initListener();
    }

    @Override
    public void setData() {

    }

    private void initListener() {
        mViewData.contentListview.setOnItemClickListener((adapterView, view, i, l) -> {
            switch (i) {
                case 0:
                    joinChatRoom();
                    break;
                case 1:
                    quitChatRoom();
                    break;
                case 2:
                    getHistoryMessages();
                    break;
                case 3:
                    forceSetChatRoomEntry();
                    break;
                case 4:
                    getChatRoomEntry();
                    break;
            }
        });
    }

    private void getChatRoomEntry() {
        String chatRoomId = mViewData.inputTextChatRoomId.getEtContentText();
        String key = "name";

        RongChatRoomClient.getInstance().getChatRoomEntry(chatRoomId, key, new IRongCoreCallback.ResultCallback<Map<String, String>>() {
            @Override
            public void onSuccess(Map<String, String> stringStringMap) {
                toast("获取单个kv成功");
            }

            @Override
            public void onError(IRongCoreEnum.CoreErrorCode e) {
                toast("code:  单个kv" + e);
            }
        });

    }

    private void forceSetChatRoomEntry() {
        String chatRoomId = mViewData.inputTextChatRoomId.getEtContentText();
        String key = "name";
        String value = "融融";
        String notificationExtra = "通知消息扩展";

        RongChatRoomClient.getInstance().forceSetChatRoomEntry(chatRoomId, key, value, false, false, notificationExtra, new IRongCoreCallback.OperationCallback() {

            @Override
            public void onSuccess() {
                toast("设置成功");
            }


            @Override
            public void onError(IRongCoreEnum.CoreErrorCode coreErrorCode) {
                toast("code:  " + coreErrorCode.getValue());
            }
        });
    }

    private void getHistoryMessages() {
        Conversation.ConversationType conversationType = Conversation.ConversationType.CHATROOM;
        String chatroomId = mViewData.inputTextChatRoomId.getEtContentText();
        int lastMessageId = -1;
        final int count = 10;

        final String targetId = chatroomId;
        final long recordTime = 0;

        RongIMClient.getInstance().getHistoryMessages(conversationType, targetId, lastMessageId, count,
                new RongIMClient.ResultCallback<List<Message>>() {

                    @Override
                    public void onSuccess(List<Message> messages) {
                        if (messages == null || messages.isEmpty()) {
                            RongChatRoomClient.getInstance().getChatroomHistoryMessages(targetId, recordTime, count, IRongCoreEnum.TimestampOrder.RC_TIMESTAMP_ASC,
                                    new IRongCoreCallback.IChatRoomHistoryMessageCallback() {

                                        @Override
                                        public void onSuccess(List<Message> messages, long syncTime) {
                                            toast("获取成功");
                                        }


                                        @Override
                                        public void onError(IRongCoreEnum.CoreErrorCode code) {
                                            toast("code:  " + code.getValue());
                                        }
                                    });

                        }
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                        toast("errorCode:  " + errorCode.getValue());
                    }
                });

    }

    private void quitChatRoom() {
        String chatroomId = mViewData.inputTextChatRoomId.getEtContentText();

        RongChatRoomClient.getInstance().quitChatRoom(chatroomId, new IRongCoreCallback.OperationCallback() {

            public void onSuccess() {
                toast("退出成功");
            }

            @Override
            public void onError(IRongCoreEnum.CoreErrorCode coreErrorCode) {
                toast("code:  " + coreErrorCode);
            }
        });

    }

    private void joinChatRoom() {
        String chatroomId = mViewData.inputTextChatRoomId.getEtContentText();
        if (TextUtils.isEmpty(chatroomId)) {
            toast("请输入聊天室id");
            return;
        }
        int defMessageCount = 50;

        RongChatRoomClient.getInstance().joinChatRoom(chatroomId, defMessageCount, new IRongCoreCallback.OperationCallback() {

            @Override
            public void onSuccess() {
                toast("加入成功");
            }

            @Override
            public void onError(IRongCoreEnum.CoreErrorCode coreErrorCode) {
                toast("code:  " + coreErrorCode);
            }
        });
    }
}