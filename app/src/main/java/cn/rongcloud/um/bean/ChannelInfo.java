package cn.rongcloud.um.bean;

// 频道信息
public class ChannelInfo {
    private String channelId;
    private String createTime;
    private int type;

    public ChannelInfo(String channelId, String createTime, int type) {
        this.channelId = channelId;
        this.createTime = createTime;
        this.type = type;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ChannelInfo{" +
                "channelId='" + channelId + '\'' +
                ", createTime='" + createTime + '\'' +
                ", type=" + type +
                '}';
    }
}
