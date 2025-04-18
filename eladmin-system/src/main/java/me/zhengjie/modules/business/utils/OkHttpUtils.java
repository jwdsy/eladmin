package me.zhengjie.modules.business.utils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;

@Slf4j
public class OkHttpUtils {
    private static final OkHttpClient client = new OkHttpClient();

    public static byte[] downloadImage(String url) throws IOException {
        long start = System.currentTimeMillis();
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            ResponseBody body = response.body();
            if (body != null) {
                return body.bytes();
            }
        } finally {
            long end = System.currentTimeMillis();
            log.info("download time:" + (end - start) + "ms");
        }
        return null;
    }
}
