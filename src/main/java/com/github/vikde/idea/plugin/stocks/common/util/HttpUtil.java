package com.github.vikde.idea.plugin.stocks.common.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.util.concurrent.TimeUnit;

/**
 * @author vikde
 * @date 2021/01/15
 */
public class HttpUtil {
    private HttpUtil() {
    }

    private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS).build();

    /**
     * 获取请求返回的字符串
     */
    public static String get(String url) throws Exception {
        Request request = new Request.Builder().url(url).build();
        String dataStr = null;
        try (Response response = OK_HTTP_CLIENT.newCall(request).execute()) {
            if (response.isSuccessful()) {
                ResponseBody body = response.body();
                dataStr = body == null ? "" : body.string();
            }
        }
        return dataStr;
    }
}
