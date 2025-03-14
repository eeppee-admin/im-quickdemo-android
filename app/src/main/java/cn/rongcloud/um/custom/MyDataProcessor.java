package cn.rongcloud.um.custom;

import cn.rongcloud.um.utils.DataProcessorUtil;
import cn.rongcloud.um.utils.SessionManager;

import java.util.List;

import cn.rongcloud.um.base.Constants;
import io.rong.imkit.config.BaseDataProcessor;
import io.rong.imlib.model.Conversation;

// 数据处理
public class MyDataProcessor extends BaseDataProcessor<Conversation> {
    /**
     * 自定义会话列表页面支持的会话类型。
     */
    @Override
    public Conversation.ConversationType[] supportedTypes() {

        return DataProcessorUtil.conversationTypes;
    }

    /**
     * 对会话数据进行过滤。
     *
     * <p>从数据库批量拉取到会话列表时和在线收到消息产生新会话时都会回调此方法
     *
     * @param data 待过滤的数据
     * @return 过滤完的数据。
     */
    @Override
    public List<Conversation> filtered(List<Conversation> data) {
        return data;
    }

    /**
     * 自定义需要聚合显示的会话。此处设置系统会话聚合显示。
     */
    @Override
    public boolean isGathered(Conversation.ConversationType type) {
        int num = SessionManager.getInstance().getInt(Constants.SP_KEY_SUB_CONVERSATION_TYPE);
        if (type.equals(Conversation.ConversationType.SYSTEM)) {
            return num == 4 || num == 5 || num == 6 || num == 7;
        } else if (type.equals(Conversation.ConversationType.PRIVATE)) {
            return num == 1 || num == 3 || num == 5 || num == 7;
        } else if (type.equals(Conversation.ConversationType.GROUP)) {
            return num == 2 || num == 3 || num == 6 || num == 7;
        }
        return false;
    }
}
