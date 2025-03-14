package cn.rongcloud.um.base;

/**
 * 常量类
 */
public final class Constants {

    public static final String BASE_URL = "https://api-cn.ronghub.com";
    // 默认app secret ， 融云
    public static final String SECRET = "EXCZVCZaJDMi";
    // 默认app key
    public static final String APP_KEY = "p5tvi9dst19k4";
    // 默认展示的用户id
    public static final String USER_ID = "1";
    public static final String PRIVATE = "1";
    public static final String GROUP = "3";
    public static final String SIGN_METHOD_MD5 = "MD5";
    public static final String SIGN_METHOD_HMAC = "HMAC";
    public static final String CHARSET_UTF8 = "UTF-8";

    public static final String TAG = "retrofit";

    /**
     * ======================   sp key  start   ==============================
     */
    public static final String SP_KEY_APP_KEY = "appKey"; // p5tvi9dst19k4
    public static final String SP_KEY_APP_SECRET = "appSecret"; // EXCZVCZaJDMi
    public static final String SP_KEY_NAV_URL = "naviServer";
    public static final String SP_KEY_FILE_URL = "fileServer";
    public static final String SP_KEY_APP_TOKEN = "token"; // todo token可能需要网络请求
    public static final String SP_KEY_LANGUAGE_SETTING = "LanguageServer";
    public static final String SP_KEY_CONVERSATION_TYPE = "conversationType";
    public static final String SP_KEY_SUB_CONVERSATION_TYPE = "subConversationType";
    /**======================   sp key  end     ==============================*/

    /**
     * ======================   default avatar start   ==============================
     */
    // 头像链接
    public static final String[] DefaultAvatar = new String[]{
            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Finews.gtimg.com%2Fnewsapp_bt%2F0%2F13617583465%2F1000.jpg&refer=http%3A%2F%2Finews.gtimg.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1662279145&t=89a2a4e3468cfe398fc41b54c3506c71",
            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fup.enterdesk.com%2Fedpic%2F62%2F3b%2Fe1%2F623be1bed1dcdbc71ee2dc3c28505aed.jpg&refer=http%3A%2F%2Fup.enterdesk.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1662279208&t=6ddad26308b44841c3dc7afc7887ff2c",
            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Finews.gtimg.com%2Fnewsapp_bt%2F0%2F13715839860%2F641&refer=http%3A%2F%2Finews.gtimg.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1662279240&t=b800909ba8e8f52003d5b65d87b0e667",
            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fnimg.ws.126.net%2F%3Furl%3Dhttp%253A%252F%252Fdingyue.ws.126.net%252F2021%252F1202%252F5cef0995j00r3gz1h000tc000c800c8c.jpg%26thumbnail%3D650x2147483647%26quality%3D80%26type%3Djpg&refer=http%3A%2F%2Fnimg.ws.126.net&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1662279240&t=db4639039e2317a2b151839b6f70ca90",
            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Finews.gtimg.com%2Fnewsapp_bt%2F0%2F13905311255%2F1000.jpg&refer=http%3A%2F%2Finews.gtimg.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1662279286&t=d3f106091379d69813c17e848c8e3de4",
            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fn.sinaimg.cn%2Fsinakd2020328s%2F580%2Fw690h690%2F20200328%2F9beb-irpunaf9040343.jpg&refer=http%3A%2F%2Fn.sinaimg.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1662279286&t=d27a575e43509addad68174b7de85638",
            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fi.qqkou.com%2Fi%2F0a3738897897x1113329549b26.jpg&refer=http%3A%2F%2Fi.qqkou.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1662279286&t=730373dac61ca3f708e896adfff37f2a",
            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Finews.gtimg.com%2Fnewsapp_bt%2F0%2F9434916430%2F1000.jpg&refer=http%3A%2F%2Finews.gtimg.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1662279367&t=aac3e44df012726012c500dce283bc41",
            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fi.qqkou.com%2Fi%2F0a274095798x2052101250b26.jpg&refer=http%3A%2F%2Fi.qqkou.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1662279368&t=001f308642c3a196b9cb8d9d2026e881",
            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fnimg.ws.126.net%2F%3Furl%3Dhttp%253A%252F%252Fdingyue.ws.126.net%252FuPedPaMCtutVUiIzg21%253DZvjFJuqzZ9T8kYMMqMGZdJ4ig1519569380474compressflag.jpeg%26thumbnail%3D650x2147483647%26quality%3D80%26type%3Djpg&refer=http%3A%2F%2Fnimg.ws.126.net&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1662279421&t=7e9220f3f4d1fb4c3502c2b43409d4b1"
    };

    /**======================   default avatar end     ==============================*/

}