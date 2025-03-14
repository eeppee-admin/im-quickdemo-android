package cn.rongcloud.um.custom;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import cn.rongcloud.um.R;

import java.util.List;

import io.rong.imkit.conversation.messgelist.provider.BaseMessageItemProvider;
import io.rong.imkit.model.UiMessage;
import io.rong.imkit.widget.adapter.IViewProviderListener;
import io.rong.imkit.widget.adapter.ViewHolder;
import io.rong.imlib.model.MessageContent;

// todo: 实现融云类
public class CustomMessageProvider extends BaseMessageItemProvider<CustomMessage> {
    public CustomMessageProvider() {
        mConfig.showReadState = true;
    }

    @Override
    protected ViewHolder onCreateMessageContentViewHolder(ViewGroup viewGroup, int i) {
        // 聊天 + 号点击自定义消息   item_custom.xml
        View mView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_custom, viewGroup, false);
        return new ViewHolder(viewGroup.getContext(), mView);
    }

    @Override
    protected void bindMessageContentViewHolder(ViewHolder viewHolder, ViewHolder viewHolder1, CustomMessage customMessage, UiMessage uiMessage, int i, List<UiMessage> list, IViewProviderListener<UiMessage> iViewProviderListener) {
        ImageView ivImage = viewHolder.getView(R.id.iv_image);
        TextView tvName = viewHolder.getView(R.id.tv_name);
        TextView tvExpansion = viewHolder.getView(R.id.tv_expansion);
        Glide.with(ivImage).load(customMessage.getUrlImage()).into(ivImage);
        tvName.setText("姓名：" + customMessage.getContent());
        tvExpansion.setText("点击消息可更新扩展: " + uiMessage.getExpansion().get("key1"));
    }

    @Override
    protected boolean onItemClick(ViewHolder viewHolder, CustomMessage customMessage, UiMessage uiMessage, int i, List<UiMessage> list, IViewProviderListener<UiMessage> iViewProviderListener) {
        return false;
    }

    @Override
    protected boolean isMessageViewType(MessageContent messageContent) {
        return messageContent instanceof CustomMessage;
    }

    @Override
    public Spannable getSummarySpannable(Context context, CustomMessage customMessage) {
        return new SpannableString("自定义消息");
    }
}
