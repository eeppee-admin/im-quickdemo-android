package cn.rongcloud.um.custom;

import android.os.Parcel;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import io.rong.common.ParcelUtils;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.UserInfo;
import io.rong.message.MediaMessageContent;




@MessageTag(value = "app:custom", flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
public class CustomMessage extends MediaMessageContent {

    private static final String TAG = "CustomMessage";
    // 自定义消息变量，可以有多个
    private String content;
    private String urlImage;

    private CustomMessage() {
    }

    public CustomMessage(Parcel in) {
        ParcelUtils.readFromParcel(in);
        content = ParcelUtils.readFromParcel(in);
        urlImage = ParcelUtils.readFromParcel(in);
        ParcelUtils.readFromParcel(in, UserInfo.class);
    }

    // 快速构建消息对象方法
    public static CustomMessage obtain(String content, String urlImage) {
        CustomMessage msg = new CustomMessage();
        msg.content = content;
        msg.urlImage = urlImage;
        return msg;
    }

    /**
     * 创建 CustomMessage(byte[] data) 带有 byte[] 的构造方法用于解析消息内容.
     */
    public CustomMessage(byte[] data) {
        if (data == null) {
            return;
        }
        String jsonStr = null;
        try {
            jsonStr = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "UnsupportedEncodingException ", e);
        }
        if (jsonStr == null) {
            Log.e(TAG, "jsonStr is null ");
            return;
        }

        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            // 消息携带用户信息时, 自定义消息需添加下面代码
            if (jsonObj.has("user")) {
                setUserInfo(parseJsonToUserInfo(jsonObj.getJSONObject("user")));
            }
            // 用于群组聊天, 消息携带 @ 人信息时, 自定义消息需添加下面代码
            if (jsonObj.has("mentionedInfo")) {
                setMentionedInfo(parseJsonToMentionInfo(jsonObj.getJSONObject("mentionedInfo")));
            }
            // 将所有自定义变量从收到的 json 解析并赋值
            if (jsonObj.has("content")) {
                content = jsonObj.optString("content");
            }
            if (jsonObj.has("urlimage")) {
                urlImage = jsonObj.optString("urlimage");
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSONException " + e.getMessage());
        }
    }

    public String getContent() {
        return content;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    /**
     * 将本地消息对象序列化为消息数据。
     *
     * @return 消息数据。
     */
    @Override
    public byte[] encode() {
        JSONObject jsonObj = new JSONObject();
        try {
            // 消息携带用户信息时, 自定义消息需添加下面代码
            if (getJSONUserInfo() != null) {
                jsonObj.putOpt("user", getJSONUserInfo());
            }
            // 用于群组聊天, 消息携带 @ 人信息时, 自定义消息需添加下面代码
            if (getJsonMentionInfo() != null) {
                jsonObj.putOpt("mentionedInfo", getJsonMentionInfo());
            }
            //  将所有自定义消息的内容，都序列化至 json 对象中
            jsonObj.put("content", this.content);
            jsonObj.put("urlimage", this.urlImage);
        } catch (JSONException e) {
            Log.e(TAG, "JSONException " + e.getMessage());
        }

        try {
            return jsonObj.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "UnsupportedEncodingException ", e);
        }
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        // 对消息属性进行序列化，将类的数据写入外部提供的 Parcel 中
        ParcelUtils.writeToParcel(dest, getExtra());
        ParcelUtils.writeToParcel(dest, content);
        ParcelUtils.writeToParcel(dest, urlImage);
        ParcelUtils.writeToParcel(dest, getUserInfo());
    }

    public static final Creator<CustomMessage> CREATOR =
            new Creator<CustomMessage>() {
                public CustomMessage createFromParcel(Parcel source) {
                    return new CustomMessage(source);
                }

                public CustomMessage[] newArray(int size) {
                    return new CustomMessage[size];
                }
            };
}