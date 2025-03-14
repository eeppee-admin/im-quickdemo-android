package cn.rongcloud.um.net;

import android.os.Build;

import androidx.annotation.RequiresApi;

import cn.rongcloud.um.base.Constants;
import cn.rongcloud.um.bean.ChannelInfo;
import cn.rongcloud.um.bean.GroupMemberInfo;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;

public class HttpQUtils {
    private static String mAppSecret;
    private static String mAppkey;

    // 获取注册用户token
    @NotNull
    public static void getToken(@NotNull final String appKey, @NotNull final String appSecret, @NotNull final String userId, @NotNull final GetTokenCallback callback) {
        Intrinsics.checkNotNullParameter(appKey, "appKey");
        Intrinsics.checkNotNullParameter(appSecret, "appSecret");
        Intrinsics.checkNotNullParameter(userId, "userId");
        Intrinsics.checkNotNullParameter(callback, "callback");
        mAppSecret = appSecret;
        mAppkey = appKey;
        // 开启一个线程
        (new Thread((Runnable) (new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public final void run() {
                long timestamp = System.currentTimeMillis();
                int nonce = (new Random()).nextInt(9999) + 10000;
                String signature = sha1(appSecret + nonce + timestamp);
                InputStream input = (InputStream) null;
                OutputStream output = (OutputStream) null;
                ByteArrayOutputStream message = (ByteArrayOutputStream) null;

                try {
                    GetTokenCallback var10000;
                    String var10001;
                    try {
                        // 请求
                        URLConnection var21 = (new URL(Constants.BASE_URL + "/user/getToken.json")).openConnection();
                        if (var21 == null) {
                            throw new NullPointerException("null cannot be cast to non-null type java.net.HttpURLConnection");
                        }

                        HttpURLConnection conn = (HttpURLConnection) var21;
                        conn.setRequestMethod("POST");
                        conn.setReadTimeout(5000);
                        conn.setConnectTimeout(5000);
                        conn.setDoOutput(true);
                        conn.setDoInput(true);
                        conn.setUseCaches(false);
                        conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                        conn.addRequestProperty("Timestamp", String.valueOf(timestamp));
                        conn.addRequestProperty("Nonce", String.valueOf(nonce));
                        conn.addRequestProperty("Signature", signature);
                        conn.addRequestProperty("App-Key", appKey);
                        String data = "userId=" + userId;
                        output = conn.getOutputStream();
                        Charset var11 = Charsets.UTF_8;
                        if (data == null) {
                            throw new NullPointerException("null cannot be cast to non-null type java.lang.String");
                        }

                        byte[] var22 = data.getBytes(var11);
                        Intrinsics.checkNotNullExpressionValue(var22, "(this as java.lang.String).getBytes(charset)");
                        output.write(var22);
                        output.flush();
                        if (conn.getResponseCode() == 200) {
                            input = conn.getInputStream();
                            message = new ByteArrayOutputStream();
                            int len = 0;
                            byte[] buffer = new byte[1024];

                            while (true) {
                                int var12 = input.read(buffer);
                                int var14 = 0;
                                if (var12 == -1) {
                                    byte[] var10002 = message.toByteArray();
                                    Intrinsics.checkNotNullExpressionValue(var10002, "message.toByteArray()");
                                    byte[] var13 = var10002;
                                    JSONObject jsonObject = new JSONObject(new String(var13, Charsets.UTF_8));
                                    if (jsonObject.optInt("code") == 200) {
                                        var10000 = callback;
                                        var10001 = jsonObject.optString("token");
                                        Intrinsics.checkNotNullExpressionValue(var10001, "jsonObject.optString(\"token\")");
                                        var10000.onGetTokenSuccess(var10001);
                                    } else {
                                        callback.onGetTokenFailed("content code = " + jsonObject.optInt("code"));
                                    }
                                    break;
                                }

                                message.write(buffer, 0, var12);
                            }
                        } else {
                            callback.onGetTokenFailed("http code = " + conn.getResponseCode());
                        }
                    } catch (Exception var17) {
                        var17.printStackTrace();
                        var10000 = callback;
                        var10001 = var17.getMessage();
                        Intrinsics.checkNotNull(var10001);
                        var10000.onGetTokenFailed(var10001);
                    }
                } finally {
                    if (input != null) {
                        try {
                            input.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (output != null) {
                        try {
                            output.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (message != null) {
                        try {
                            message.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }

            }
        }))).start();
    }

    // byte[]转换成hex String
    @RequiresApi(api = Build.VERSION_CODES.N)
    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        int var6 = data.length;

        for (int var4 = 0; var4 < var6; ++var4) {
            byte b = data[var4];
            int halfByte = b >>> 4 & 15;
            int var8 = 0;

            do {
                buf.append(halfByte <= 9 ? (char) (Character.hashCode('0') + halfByte) : (char) (Character.hashCode('a') + halfByte - 10));
                halfByte = b & 15;
            } while (var8++ < 1);
        }

        String var9 = buf.toString();
        Intrinsics.checkNotNullExpressionValue(var9, "buf.toString()");
        return var9;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static String sha1(String text) {
        MessageDigest var3;
        try {
            MessageDigest var10000 = MessageDigest.getInstance("SHA-1");
            Intrinsics.checkNotNullExpressionValue(var10000, "MessageDigest.getInstance(\"SHA-1\")");
            var3 = var10000;
        } catch (NoSuchAlgorithmException var5) {
            var5.printStackTrace();
            return null;
        }

        Charset var10001 = StandardCharsets.ISO_8859_1;
        Intrinsics.checkNotNullExpressionValue(var10001, "StandardCharsets.ISO_8859_1");
        Charset var4 = var10001;
        if (text == null) {
            throw new NullPointerException("null cannot be cast to non-null type java.lang.String");
        } else {
            byte[] var6 = text.getBytes(var4);
            Intrinsics.checkNotNullExpressionValue(var6, "(this as java.lang.String).getBytes(charset)");
            var3.update(var6, 0, text.length());
            byte[] sha1hash = var3.digest();
            Intrinsics.checkNotNullExpressionValue(sha1hash, "sha1hash");
            return convertToHex(sha1hash);
        }
    }

    // 获取token的回调
    public interface GetTokenCallback {
        void onGetTokenSuccess(@NotNull String token);

        void onGetTokenFailed(@NotNull String err);
    }

    // 创建超级群
    @NotNull
    public static void createUltraGroup(@NotNull final String userId, @NotNull final String groupId, @NotNull final String groupName, @NotNull final CreateUltraGroupCallBack callback) {
        Intrinsics.checkNotNullParameter(userId, "userId");
        Intrinsics.checkNotNullParameter(groupId, "groupId");
        Intrinsics.checkNotNullParameter(groupName, "groupName");
        Intrinsics.checkNotNullParameter(callback, "callback");
        // 开启线程
        (new Thread((Runnable) (new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public final void run() {
                long timestamp = System.currentTimeMillis();
                int nonce = (new Random()).nextInt(9999) + 10000;
                String signature = sha1(mAppSecret + nonce + timestamp);
                InputStream input = (InputStream) null;
                OutputStream output = (OutputStream) null;
                ByteArrayOutputStream message = (ByteArrayOutputStream) null;

                try {
                    CreateUltraGroupCallBack var10000;
                    String var10001;
                    try {
                        URLConnection var21 = (new URL(Constants.BASE_URL + "/ultragroup/create.json")).openConnection();
                        if (var21 == null) {
                            throw new NullPointerException("null cannot be cast to non-null type java.net.HttpURLConnection");
                        }
                        HttpURLConnection conn = (HttpURLConnection) var21;
                        conn.setRequestMethod("POST");
                        conn.setReadTimeout(5000);
                        conn.setConnectTimeout(5000);
                        conn.setDoOutput(true);
                        conn.setDoInput(true);
                        conn.setUseCaches(false);
                        conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                        conn.addRequestProperty("Timestamp", String.valueOf(timestamp));
                        conn.addRequestProperty("Nonce", String.valueOf(nonce));
                        conn.addRequestProperty("Signature", signature);
                        conn.addRequestProperty("App-Key", mAppkey);
                        String data = "userId=" + userId + "&groupId=" + groupId + "&groupName=" + groupName;
                        output = conn.getOutputStream();
                        Charset var11 = Charsets.UTF_8;
                        if (data == null) {
                            throw new NullPointerException("null cannot be cast to non-null type java.lang.String");
                        }

                        byte[] var22 = data.getBytes(var11);
                        Intrinsics.checkNotNullExpressionValue(var22, "(this as java.lang.String).getBytes(charset)");
                        output.write(var22);
                        output.flush();
                        if (conn.getResponseCode() == 200) {
                            input = conn.getInputStream();
                            message = new ByteArrayOutputStream();
                            int len = 0;
                            byte[] buffer = new byte[1024];

                            while (true) {
                                int var12 = input.read(buffer);
                                int var14 = 0;
                                if (var12 == -1) {
                                    byte[] var10002 = message.toByteArray();
                                    Intrinsics.checkNotNullExpressionValue(var10002, "message.toByteArray()");
                                    byte[] var13 = var10002;
                                    JSONObject jsonObject = new JSONObject(new String(var13, Charsets.UTF_8));
                                    if (jsonObject.optInt("code") == 200) {
                                        var10000 = callback;
                                        var10000.onSuccess();
                                    } else {
                                        callback.onFailed("content code = " + jsonObject.optInt("code"));
                                    }
                                    break;
                                }

                                message.write(buffer, 0, var12);
                            }
                        } else {
                            callback.onFailed("http code = " + conn.getResponseCode());
                        }
                    } catch (Exception var17) {
                        var17.printStackTrace();
                        var10000 = callback;
                        var10001 = var17.getMessage();
                        Intrinsics.checkNotNull(var10001);
                        var10000.onFailed(var10001);
                    }
                } finally {
                    if (input != null) {
                        try {
                            input.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (output != null) {
                        try {
                            output.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (message != null) {
                        try {
                            message.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }

            }
        }))).start();
    }

    // 超级群回调
    public interface CreateUltraGroupCallBack {
        void onSuccess();

        void onFailed(@NotNull String err);
    }

    // 创建频道
    @NotNull
    public static void createChannel(@NotNull final String groupId, @NotNull final String busChannel, @NotNull final CreateChannelCallBack callback) {
        Intrinsics.checkNotNullParameter(groupId, "groupId");
        Intrinsics.checkNotNullParameter(busChannel, "busChannel");
        Intrinsics.checkNotNullParameter(callback, "callback");
        (new Thread((Runnable) (new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public final void run() {
                long timestamp = System.currentTimeMillis();
                int nonce = (new Random()).nextInt(9999) + 10000;
                String signature = sha1(mAppSecret + nonce + timestamp);
                InputStream input = (InputStream) null;
                OutputStream output = (OutputStream) null;
                ByteArrayOutputStream message = (ByteArrayOutputStream) null;

                try {
                    CreateChannelCallBack var10000;
                    String var10001;
                    try {
                        URLConnection var21 = (new URL(Constants.BASE_URL + "/ultragroup/channel/create.json")).openConnection();
                        if (var21 == null) {
                            throw new NullPointerException("null cannot be cast to non-null type java.net.HttpURLConnection");
                        }
                        HttpURLConnection conn = (HttpURLConnection) var21;
                        conn.setRequestMethod("POST");
                        conn.setReadTimeout(5000);
                        conn.setConnectTimeout(5000);
                        conn.setDoOutput(true);
                        conn.setDoInput(true);
                        conn.setUseCaches(false);
                        conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                        conn.addRequestProperty("Timestamp", String.valueOf(timestamp));
                        conn.addRequestProperty("Nonce", String.valueOf(nonce));
                        conn.addRequestProperty("Signature", signature);
                        conn.addRequestProperty("App-Key", mAppkey);
                        String data = "groupId=" + groupId + "&busChannel=" + busChannel;
                        output = conn.getOutputStream();
                        Charset var11 = Charsets.UTF_8;
                        if (data == null) {
                            throw new NullPointerException("null cannot be cast to non-null type java.lang.String");
                        }

                        byte[] var22 = data.getBytes(var11);
                        Intrinsics.checkNotNullExpressionValue(var22, "(this as java.lang.String).getBytes(charset)");
                        output.write(var22);
                        output.flush();
                        if (conn.getResponseCode() == 200) {
                            input = conn.getInputStream();
                            message = new ByteArrayOutputStream();
                            int len = 0;
                            byte[] buffer = new byte[1024];

                            while (true) {
                                int var12 = input.read(buffer);
                                int var14 = 0;
                                if (var12 == -1) {
                                    byte[] var10002 = message.toByteArray();
                                    Intrinsics.checkNotNullExpressionValue(var10002, "message.toByteArray()");
                                    byte[] var13 = var10002;
                                    JSONObject jsonObject = new JSONObject(new String(var13, Charsets.UTF_8));
                                    if (jsonObject.optInt("code") == 200) {
                                        var10000 = callback;
                                        var10000.onSuccess();
                                    } else {
                                        callback.onFailed("content code = " + jsonObject.optInt("code"));
                                    }
                                    break;
                                }

                                message.write(buffer, 0, var12);
                            }
                        } else {
                            callback.onFailed("http code = " + conn.getResponseCode());
                        }
                    } catch (Exception var17) {
                        var17.printStackTrace();
                        var10000 = callback;
                        var10001 = var17.getMessage();
                        Intrinsics.checkNotNull(var10001);
                        var10000.onFailed(var10001);
                    }
                } finally {
                    if (input != null) {
                        try {
                            input.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (output != null) {
                        try {
                            output.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (message != null) {
                        try {
                            message.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }

            }
        }))).start();
    }

    public interface CreateChannelCallBack {
        void onSuccess();

        void onFailed(@NotNull String err);
    }

    // 加入超级群聊
    @NotNull
    public static void joinUltraGroup(@NotNull final String userId, @NotNull final String groupId, @NotNull final JoinUltraGroupCallBack callback) {
        Intrinsics.checkNotNullParameter(userId, "userId");
        Intrinsics.checkNotNullParameter(groupId, "groupId");
        Intrinsics.checkNotNullParameter(callback, "callback");
        (new Thread((Runnable) (new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public final void run() {
                long timestamp = System.currentTimeMillis();
                int nonce = (new Random()).nextInt(9999) + 10000;
                String signature = sha1(mAppSecret + nonce + timestamp);
                InputStream input = (InputStream) null;
                OutputStream output = (OutputStream) null;
                ByteArrayOutputStream message = (ByteArrayOutputStream) null;

                try {
                    JoinUltraGroupCallBack var10000;
                    String var10001;
                    try {
                        URLConnection var21 = (new URL(Constants.BASE_URL + "/ultragroup/join.json")).openConnection();
                        if (var21 == null) {
                            throw new NullPointerException("null cannot be cast to non-null type java.net.HttpURLConnection");
                        }
                        HttpURLConnection conn = (HttpURLConnection) var21;
                        conn.setRequestMethod("POST");
                        conn.setReadTimeout(5000);
                        conn.setConnectTimeout(5000);
                        conn.setDoOutput(true);
                        conn.setDoInput(true);
                        conn.setUseCaches(false);
                        conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                        conn.addRequestProperty("Timestamp", String.valueOf(timestamp));
                        conn.addRequestProperty("Nonce", String.valueOf(nonce));
                        conn.addRequestProperty("Signature", signature);
                        conn.addRequestProperty("App-Key", mAppkey);
                        String data = "userId=" + userId + "&groupId=" + groupId;
                        output = conn.getOutputStream();
                        Charset var11 = Charsets.UTF_8;
                        if (data == null) {
                            throw new NullPointerException("null cannot be cast to non-null type java.lang.String");
                        }

                        byte[] var22 = data.getBytes(var11);
                        Intrinsics.checkNotNullExpressionValue(var22, "(this as java.lang.String).getBytes(charset)");
                        output.write(var22);
                        output.flush();
                        if (conn.getResponseCode() == 200) {
                            input = conn.getInputStream();
                            message = new ByteArrayOutputStream();
                            int len = 0;
                            byte[] buffer = new byte[1024];

                            while (true) {
                                int var12 = input.read(buffer);
                                int var14 = 0;
                                if (var12 == -1) {
                                    byte[] var10002 = message.toByteArray();
                                    Intrinsics.checkNotNullExpressionValue(var10002, "message.toByteArray()");
                                    byte[] var13 = var10002;
                                    JSONObject jsonObject = new JSONObject(new String(var13, Charsets.UTF_8));
                                    if (jsonObject.optInt("code") == 200) {
                                        var10000 = callback;
                                        var10000.onSuccess();
                                    } else {
                                        callback.onFailed("content code = " + jsonObject.optInt("code"));
                                    }
                                    break;
                                }

                                message.write(buffer, 0, var12);
                            }
                        } else {
                            callback.onFailed("http code = " + conn.getResponseCode());
                        }
                    } catch (Exception var17) {
                        var17.printStackTrace();
                        var10000 = callback;
                        var10001 = var17.getMessage();
                        Intrinsics.checkNotNull(var10001);
                        var10000.onFailed(var10001);
                    }
                } finally {
                    if (input != null) {
                        try {
                            input.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (output != null) {
                        try {
                            output.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (message != null) {
                        try {
                            message.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }

            }
        }))).start();
    }

    public interface JoinUltraGroupCallBack {
        void onSuccess();

        void onFailed(@NotNull String err);
    }

    // 获取频道列表
    @NotNull
    public static void getChannelList(@NotNull final String groupId, @NotNull final int page, @NotNull final int limit, @NotNull final GetChannelInfosCallBack callback) {
        Intrinsics.checkNotNullParameter(groupId, "groupId");
        Intrinsics.checkNotNullParameter(callback, "callback");
        (new Thread((Runnable) (new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public final void run() {
                long timestamp = System.currentTimeMillis();
                int nonce = (new Random()).nextInt(9999) + 10000;
                String signature = sha1(mAppSecret + nonce + timestamp);
                InputStream input = (InputStream) null;
                OutputStream output = (OutputStream) null;
                ByteArrayOutputStream message = (ByteArrayOutputStream) null;

                try {
                    GetChannelInfosCallBack var10000;
                    String var10001;
                    try {
                        URLConnection var21 = (new URL(Constants.BASE_URL + "/ultragroup/channel/get.json")).openConnection();
                        if (var21 == null) {
                            throw new NullPointerException("null cannot be cast to non-null type java.net.HttpURLConnection");
                        }

                        HttpURLConnection conn = (HttpURLConnection) var21;
                        conn.setRequestMethod("POST");
                        conn.setReadTimeout(5000);
                        conn.setConnectTimeout(5000);
                        conn.setDoOutput(true);
                        conn.setDoInput(true);
                        conn.setUseCaches(false);
                        conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                        conn.addRequestProperty("Timestamp", String.valueOf(timestamp));
                        conn.addRequestProperty("Nonce", String.valueOf(nonce));
                        conn.addRequestProperty("Signature", signature);
                        conn.addRequestProperty("App-Key", mAppkey);
                        String data = "groupId=" + groupId + "&page=" + page + "&limit=" + limit;
                        output = conn.getOutputStream();
                        Charset var11 = Charsets.UTF_8;
                        if (data == null) {
                            throw new NullPointerException("null cannot be cast to non-null type java.lang.String");
                        }

                        byte[] var22 = data.getBytes(var11);
                        Intrinsics.checkNotNullExpressionValue(var22, "(this as java.lang.String).getBytes(charset)");
                        output.write(var22);
                        output.flush();
                        if (conn.getResponseCode() == 200) {
                            input = conn.getInputStream();
                            message = new ByteArrayOutputStream();
                            int len = 0;
                            byte[] buffer = new byte[1024];

                            while (true) {
                                int var12 = input.read(buffer);
                                int var14 = 0;
                                if (var12 == -1) {
                                    byte[] var10002 = message.toByteArray();
                                    Intrinsics.checkNotNullExpressionValue(var10002, "message.toByteArray()");
                                    byte[] var13 = var10002;
                                    JSONObject jsonObject = new JSONObject(new String(var13, Charsets.UTF_8));
                                    if (jsonObject.optInt("code") == 200) {
                                        var10000 = callback;
                                        var10001 = jsonObject.optString("channelList");
                                        Intrinsics.checkNotNullExpressionValue(var10001, "jsonObject.optString(\"channelList\")");
                                        JSONArray jsonArray = new JSONArray(var10001);
                                        List<ChannelInfo> channelInfos = new ArrayList<>();
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                            ChannelInfo channelInfo = new ChannelInfo(jsonObject1.optString("channelId"), jsonObject1.optString("createTime"), jsonObject1.optInt("type", 0));
                                            channelInfos.add(channelInfo);
                                        }

                                        var10000.onSuccess(channelInfos);
                                    } else {
                                        callback.onFailed("content code = " + jsonObject.optInt("code"));
                                    }
                                    break;
                                }

                                message.write(buffer, 0, var12);
                            }
                        } else {
                            callback.onFailed("http code = " + conn.getResponseCode());
                        }
                    } catch (Exception var17) {
                        var17.printStackTrace();
                        var10000 = callback;
                        Intrinsics.checkNotNull(var17.getMessage());
                        var10000.onFailed(var17.getMessage());
                    }
                } finally {
                    if (input != null) {
                        try {
                            input.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (output != null) {
                        try {
                            output.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (message != null) {
                        try {
                            message.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }

            }
        }))).start();
    }

    public interface GetChannelInfosCallBack {
        void onSuccess(List<ChannelInfo> channelInfos);

        void onFailed(@NotNull String err);
    }

    // todo: 创建群组， 这个需要
    @NotNull
    public static void createGroup(@NotNull final String userId, @NotNull final String groupId, @NotNull final CreatGroupCallBack callback) {
        Intrinsics.checkNotNullParameter(userId, "userId");
        Intrinsics.checkNotNullParameter(groupId, "groupId");
        Intrinsics.checkNotNullParameter(callback, "callback");
        (new Thread((Runnable) (new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public final void run() {
                long timestamp = System.currentTimeMillis();
                int nonce = (new Random()).nextInt(9999) + 10000;
                String signature = sha1(mAppSecret + nonce + timestamp);
                InputStream input = (InputStream) null;
                OutputStream output = (OutputStream) null;
                ByteArrayOutputStream message = (ByteArrayOutputStream) null;

                try {
                    CreatGroupCallBack var10000;
                    String var10001;
                    try {
                        URLConnection var21 = (new URL(Constants.BASE_URL + "/group/create.json")).openConnection();
                        if (var21 == null) {
                            throw new NullPointerException("null cannot be cast to non-null type java.net.HttpURLConnection");
                        }

                        HttpURLConnection conn = (HttpURLConnection) var21;
                        conn.setRequestMethod("POST");
                        conn.setReadTimeout(5000);
                        conn.setConnectTimeout(5000);
                        conn.setDoOutput(true);
                        conn.setDoInput(true);
                        conn.setUseCaches(false);
                        conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                        conn.addRequestProperty("Timestamp", String.valueOf(timestamp));
                        conn.addRequestProperty("Nonce", String.valueOf(nonce));
                        conn.addRequestProperty("Signature", signature);
                        conn.addRequestProperty("App-Key", mAppkey);
                        String data = "userId=" + userId + "&groupId=" + groupId;
                        output = conn.getOutputStream();
                        Charset var11 = Charsets.UTF_8;
                        if (data == null) {
                            throw new NullPointerException("null cannot be cast to non-null type java.lang.String");
                        }

                        byte[] var22 = data.getBytes(var11);
                        Intrinsics.checkNotNullExpressionValue(var22, "(this as java.lang.String).getBytes(charset)");
                        output.write(var22);
                        output.flush();
                        if (conn.getResponseCode() == 200) {
                            input = conn.getInputStream();
                            message = new ByteArrayOutputStream();
                            int len = 0;
                            byte[] buffer = new byte[1024];

                            while (true) {
                                int var12 = input.read(buffer);
                                int var14 = 0;
                                if (var12 == -1) {
                                    byte[] var10002 = message.toByteArray();
                                    Intrinsics.checkNotNullExpressionValue(var10002, "message.toByteArray()");
                                    byte[] var13 = var10002;
                                    JSONObject jsonObject = new JSONObject(new String(var13, Charsets.UTF_8));
                                    if (jsonObject.optInt("code") == 200) {
                                        var10000 = callback;

                                        var10000.onSuccess();
                                    } else {
                                        callback.onFailed("content code = " + jsonObject.optInt("code"));
                                    }
                                    break;
                                }

                                message.write(buffer, 0, var12);
                            }
                        } else {
                            callback.onFailed("http code = " + conn.getResponseCode());
                        }
                    } catch (Exception var17) {
                        var17.printStackTrace();
                        var10000 = callback;
                        Intrinsics.checkNotNull(var17.getMessage());
                        var10000.onFailed(var17.getMessage());
                    }
                } finally {
                    if (input != null) {
                        try {
                            input.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (output != null) {
                        try {
                            output.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (message != null) {
                        try {
                            message.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }

            }
        }))).start();
    }

    public interface CreatGroupCallBack {
        void onSuccess();

        void onFailed(@NotNull String err);
    }

    // todo: 加入群组，这个需要
    @NotNull
    public static void joinGroup(@NotNull final String userId, @NotNull final String groupId, @NotNull final JoinGroupCallBack callback) {
        Intrinsics.checkNotNullParameter(userId, "userId");
        Intrinsics.checkNotNullParameter(groupId, "groupId");
        Intrinsics.checkNotNullParameter(callback, "callback");
        (new Thread((Runnable) (new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public final void run() {
                long timestamp = System.currentTimeMillis();
                int nonce = (new Random()).nextInt(9999) + 10000;
                String signature = sha1(mAppSecret + nonce + timestamp);
                InputStream input = (InputStream) null;
                OutputStream output = (OutputStream) null;
                ByteArrayOutputStream message = (ByteArrayOutputStream) null;

                try {
                    JoinGroupCallBack var10000;
                    String var10001;
                    try {
                        URLConnection var21 = (new URL(Constants.BASE_URL + "/group/join.json")).openConnection();
                        if (var21 == null) {
                            throw new NullPointerException("null cannot be cast to non-null type java.net.HttpURLConnection");
                        }

                        HttpURLConnection conn = (HttpURLConnection) var21;
                        conn.setRequestMethod("POST");
                        conn.setReadTimeout(5000);
                        conn.setConnectTimeout(5000);
                        conn.setDoOutput(true);
                        conn.setDoInput(true);
                        conn.setUseCaches(false);
                        conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                        conn.addRequestProperty("Timestamp", String.valueOf(timestamp));
                        conn.addRequestProperty("Nonce", String.valueOf(nonce));
                        conn.addRequestProperty("Signature", signature);
                        conn.addRequestProperty("App-Key", mAppkey);
                        String data = "userId=" + userId + "&groupId=" + groupId;
                        output = conn.getOutputStream();
                        Charset var11 = Charsets.UTF_8;
                        if (data == null) {
                            throw new NullPointerException("null cannot be cast to non-null type java.lang.String");
                        }

                        byte[] var22 = data.getBytes(var11);
                        Intrinsics.checkNotNullExpressionValue(var22, "(this as java.lang.String).getBytes(charset)");
                        output.write(var22);
                        output.flush();
                        if (conn.getResponseCode() == 200) {
                            input = conn.getInputStream();
                            message = new ByteArrayOutputStream();
                            int len = 0;
                            byte[] buffer = new byte[1024];

                            while (true) {
                                int var12 = input.read(buffer);
                                int var14 = 0;
                                if (var12 == -1) {
                                    byte[] var10002 = message.toByteArray();
                                    Intrinsics.checkNotNullExpressionValue(var10002, "message.toByteArray()");
                                    byte[] var13 = var10002;
                                    JSONObject jsonObject = new JSONObject(new String(var13, Charsets.UTF_8));
                                    if (jsonObject.optInt("code") == 200) {
                                        var10000 = callback;

                                        var10000.onSuccess();
                                    } else {
                                        callback.onFailed("content code = " + jsonObject.optInt("code"));
                                    }
                                    break;
                                }

                                message.write(buffer, 0, var12);
                            }
                        } else {
                            callback.onFailed("http code = " + conn.getResponseCode());
                        }
                    } catch (Exception var17) {
                        var17.printStackTrace();
                        var10000 = callback;
                        Intrinsics.checkNotNull(var17.getMessage());
                        var10000.onFailed(var17.getMessage());
                    }
                } finally {
                    if (input != null) {
                        try {
                            input.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (output != null) {
                        try {
                            output.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (message != null) {
                        try {
                            message.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }

            }
        }))).start();
    }

    public interface JoinGroupCallBack {
        void onSuccess();

        void onFailed(@NotNull String err);
    }

    // todo: 退出群组，这个需要
    @NotNull
    public static void quitGroup(@NotNull final String userId, @NotNull final String groupId, @NotNull final QuitGroupCallBack callback) {
        Intrinsics.checkNotNullParameter(userId, "userId");
        Intrinsics.checkNotNullParameter(groupId, "groupId");
        Intrinsics.checkNotNullParameter(callback, "callback");
        (new Thread((Runnable) (new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public final void run() {
                long timestamp = System.currentTimeMillis();
                int nonce = (new Random()).nextInt(9999) + 10000;
                String signature = sha1(mAppSecret + nonce + timestamp);
                InputStream input = (InputStream) null;
                OutputStream output = (OutputStream) null;
                ByteArrayOutputStream message = (ByteArrayOutputStream) null;

                try {
                    QuitGroupCallBack var10000;
                    String var10001;
                    try {
                        URLConnection var21 = (new URL(Constants.BASE_URL + "/group/join.json")).openConnection();
                        if (var21 == null) {
                            throw new NullPointerException("null cannot be cast to non-null type java.net.HttpURLConnection");
                        }

                        HttpURLConnection conn = (HttpURLConnection) var21;
                        conn.setRequestMethod("POST");
                        conn.setReadTimeout(5000);
                        conn.setConnectTimeout(5000);
                        conn.setDoOutput(true);
                        conn.setDoInput(true);
                        conn.setUseCaches(false);
                        conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                        conn.addRequestProperty("Timestamp", String.valueOf(timestamp));
                        conn.addRequestProperty("Nonce", String.valueOf(nonce));
                        conn.addRequestProperty("Signature", signature);
                        conn.addRequestProperty("App-Key", mAppkey);
                        String data = "userId=" + userId + "&groupId=" + groupId;
                        output = conn.getOutputStream();
                        Charset var11 = Charsets.UTF_8;
                        if (data == null) {
                            throw new NullPointerException("null cannot be cast to non-null type java.lang.String");
                        }

                        byte[] var22 = data.getBytes(var11);
                        Intrinsics.checkNotNullExpressionValue(var22, "(this as java.lang.String).getBytes(charset)");
                        output.write(var22);
                        output.flush();
                        if (conn.getResponseCode() == 200) {
                            input = conn.getInputStream();
                            message = new ByteArrayOutputStream();
                            int len = 0;
                            byte[] buffer = new byte[1024];

                            while (true) {
                                int var12 = input.read(buffer);
                                int var14 = 0;
                                if (var12 == -1) {
                                    byte[] var10002 = message.toByteArray();
                                    Intrinsics.checkNotNullExpressionValue(var10002, "message.toByteArray()");
                                    byte[] var13 = var10002;
                                    JSONObject jsonObject = new JSONObject(new String(var13, Charsets.UTF_8));
                                    if (jsonObject.optInt("code") == 200) {
                                        var10000 = callback;

                                        var10000.onSuccess();
                                    } else {
                                        callback.onFailed("content code = " + jsonObject.optInt("code"));
                                    }
                                    break;
                                }

                                message.write(buffer, 0, var12);
                            }
                        } else {
                            callback.onFailed("http code = " + conn.getResponseCode());
                        }
                    } catch (Exception var17) {
                        var17.printStackTrace();
                        var10000 = callback;
                        Intrinsics.checkNotNull(var17.getMessage());
                        var10000.onFailed(var17.getMessage());
                    }
                } finally {
                    if (input != null) {
                        try {
                            input.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (output != null) {
                        try {
                            output.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (message != null) {
                        try {
                            message.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }

            }
        }))).start();
    }

    public interface QuitGroupCallBack {
        void onSuccess();

        void onFailed(@NotNull String err);
    }

    // 查询群组
    @NotNull
    public static void queryGroup(@NotNull final String groupId, @NotNull final QueryGroupCallBack callback) {
        Intrinsics.checkNotNullParameter(groupId, "groupId");
        Intrinsics.checkNotNullParameter(callback, "callback");
        (new Thread((Runnable) (new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public final void run() {
                long timestamp = System.currentTimeMillis();
                int nonce = (new Random()).nextInt(9999) + 10000;
                String signature = sha1(mAppSecret + nonce + timestamp);
                InputStream input = (InputStream) null;
                OutputStream output = (OutputStream) null;
                ByteArrayOutputStream message = (ByteArrayOutputStream) null;

                try {
                    QueryGroupCallBack var10000;
                    String var10001;
                    try {
                        URLConnection var21 = (new URL(Constants.BASE_URL + "/group/user/query.json")).openConnection();
                        if (var21 == null) {
                            throw new NullPointerException("null cannot be cast to non-null type java.net.HttpURLConnection");
                        }

                        HttpURLConnection conn = (HttpURLConnection) var21;
                        conn.setRequestMethod("POST");
                        conn.setReadTimeout(5000);
                        conn.setConnectTimeout(5000);
                        conn.setDoOutput(true);
                        conn.setDoInput(true);
                        conn.setUseCaches(false);
                        conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                        conn.addRequestProperty("Timestamp", String.valueOf(timestamp));
                        conn.addRequestProperty("Nonce", String.valueOf(nonce));
                        conn.addRequestProperty("Signature", signature);
                        conn.addRequestProperty("App-Key", mAppkey);
                        String data = "groupId=" + groupId;
                        output = conn.getOutputStream();
                        Charset var11 = Charsets.UTF_8;
                        if (data == null) {
                            throw new NullPointerException("null cannot be cast to non-null type java.lang.String");
                        }

                        byte[] var22 = data.getBytes(var11);
                        Intrinsics.checkNotNullExpressionValue(var22, "(this as java.lang.String).getBytes(charset)");
                        output.write(var22);
                        output.flush();
                        if (conn.getResponseCode() == 200) {
                            input = conn.getInputStream();
                            message = new ByteArrayOutputStream();
                            int len = 0;
                            byte[] buffer = new byte[1024];

                            while (true) {
                                int var12 = input.read(buffer);
                                int var14 = 0;
                                if (var12 == -1) {
                                    byte[] var10002 = message.toByteArray();
                                    Intrinsics.checkNotNullExpressionValue(var10002, "message.toByteArray()");
                                    byte[] var13 = var10002;
                                    JSONObject jsonObject = new JSONObject(new String(var13, Charsets.UTF_8));
                                    if (jsonObject.optInt("code") == 200) {
                                        var10000 = callback;
                                        var10001 = jsonObject.optString("users");
                                        Intrinsics.checkNotNullExpressionValue(var10001, "jsonObject.optString(\"users\")");
                                        JSONArray jsonArray = new JSONArray(var10001);
                                        List<GroupMemberInfo> groupMemberInfos = new ArrayList<>();
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                            GroupMemberInfo groupMemberInfo = new GroupMemberInfo(jsonObject1.optString("id"));
                                            groupMemberInfos.add(groupMemberInfo);
                                        }

                                        var10000.onSuccess(groupMemberInfos);
                                    } else {
                                        callback.onFailed("content code = " + jsonObject.optInt("code"));
                                    }
                                    break;
                                }

                                message.write(buffer, 0, var12);
                            }
                        } else {
                            callback.onFailed("http code = " + conn.getResponseCode());
                        }
                    } catch (Exception var17) {
                        var17.printStackTrace();
                        var10000 = callback;
                        Intrinsics.checkNotNull(var17.getMessage());
                        var10000.onFailed(var17.getMessage());
                    }
                } finally {
                    if (input != null) {
                        try {
                            input.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (output != null) {
                        try {
                            output.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (message != null) {
                        try {
                            message.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }

            }
        }))).start();
    }

    public interface QueryGroupCallBack {
        void onSuccess(List<GroupMemberInfo> groupMemberInfos);

        void onFailed(@NotNull String err);
    }

}
