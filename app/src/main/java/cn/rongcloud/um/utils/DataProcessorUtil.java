package cn.rongcloud.um.utils;

import static cn.rongcloud.um.base.Constants.SP_KEY_CONVERSATION_TYPE;

import io.rong.imlib.model.Conversation;

// 会话类型处理
public class DataProcessorUtil {
    public static Conversation.ConversationType[] conversationTypes;

    public static Conversation.ConversationType[] supportedTypes() {
        Conversation.ConversationType[] types;
        // 会话类型
        switch (SessionManager.getInstance().getInt(SP_KEY_CONVERSATION_TYPE)) {
            case 1:
                types = new Conversation.ConversationType[]{Conversation.ConversationType.PRIVATE};
                break;
            case 2:
                types = new Conversation.ConversationType[]{Conversation.ConversationType.GROUP};
                break;
            case 3:
                types = new Conversation.ConversationType[]{Conversation.ConversationType.PRIVATE, Conversation.ConversationType.GROUP};
                break;
            case 4:
                types = new Conversation.ConversationType[]{Conversation.ConversationType.SYSTEM};
                break;
            case 5:
                types = new Conversation.ConversationType[]{Conversation.ConversationType.PRIVATE, Conversation.ConversationType.SYSTEM};
                break;
            case 6:
                types = new Conversation.ConversationType[]{Conversation.ConversationType.GROUP, Conversation.ConversationType.SYSTEM};
                break;
            case 7:
            default:
                types = new Conversation.ConversationType[]{Conversation.ConversationType.PRIVATE, Conversation.ConversationType.GROUP, Conversation.ConversationType.SYSTEM};
                break;
        }
        return types;
    }

}
