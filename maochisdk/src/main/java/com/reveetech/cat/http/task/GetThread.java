package com.reveetech.cat.http.task;

import android.text.TextUtils;

import com.reveetech.cat.http.callback.IHttpRespond;
import com.reveetech.cat.http.config.RequestConfig;
import com.reveetech.cat.http.util.FileUtil;
import com.reveetech.cat.http.util.UrlUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * description：httpurlconnection GET请求线程
 * <br>author：caowugao
 * <br>time： 2017/05/03 14:36
 */

public class GetThread extends BaseHttpThread {
    public GetThread(String url, Map<String, String> params, IHttpRespond callback) {
        super(url, params, callback);
        this.url = this.url + UrlUtil.pieceParmasByGETWithWenHao(params);
    }

    @Override
    protected Object doInBackground() {
        String res = null;
        if (null == config) {
            config = RequestConfig.getDefaultConfig();
        }
        if (config.isUseCache()) {
            res = config.getCache().get(url);
        }
        if (res != null) { // 如果有缓存
            return res;
        } else {
            InputStream input = null;
            BufferedReader reader = null;
            StringBuilder respond = null;
            HttpURLConnection conn = null;
            try {
                URL urlEntity = new URL(url);
                logInfo("GET请求url:" + url);
                if (url.startsWith("https")) {
                    conn = (HttpsURLConnection) urlEntity.openConnection();
                    ((HttpsURLConnection) conn).setSSLSocketFactory(sslSocketFactory);
                } else {
                    conn = (HttpURLConnection) urlEntity.openConnection();
                }
                conn.setUseCaches(config.isUseCache());
                conn.setReadTimeout(config.getReadTimeout());
                conn.setConnectTimeout(config.getConnectTimeOut());
                conn.setRequestProperty("Charset", config.getCharSet());
                //                conn.setRequestMethod("GET");
                conn.setRequestMethod("GET");
                String cookie = config.getCookie();
                if (!TextUtils.isEmpty(cookie)) {
                    conn.setRequestProperty("Cookie", cookie);
                }
                if (config.getRequestHeader() != null) {
                    for (Map.Entry<String, String> entry : config.getRequestHeader().entrySet()) {
                        conn.setRequestProperty(entry.getKey(), entry.getValue());
                    }
                }
                code = conn.getResponseCode();
                input = conn.getInputStream();
                reader = new BufferedReader(new InputStreamReader(input));
                int i = 0, current = 0;
                int count = conn.getContentLength();
                char[] buf = new char[1024];
                respond = new StringBuilder();
                while ((i = reader.read(buf)) != -1) {
                    respond.append(buf, 0, i);
                    current += i;
                    // 每次循环调用一次
                    sendProgressMsg(count, current);
                    if (isCancel) {
//                    sendCancelMessage();
                        return null;
                    }
                }

            } catch (MalformedURLException e) {
                return e;
            } catch (IOException e) {
                return e;
            } catch (Exception e) {
                return e;
            } finally {
                try {
                    if (null != conn) {
                        conn.disconnect();
                    }
                    FileUtil.closeIO(input, reader);
                } catch (Exception e) {
                    e.printStackTrace();
                    return e;
                }
            }
            return respond;
        }
    }
}
