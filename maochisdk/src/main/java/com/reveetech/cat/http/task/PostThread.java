package com.reveetech.cat.http.task;

import android.util.Log;

import com.reveetech.cat.http.callback.IHttpRespond;
import com.reveetech.cat.http.config.RequestConfig;
import com.reveetech.cat.http.params.JsonParam;
import com.reveetech.cat.http.util.FileUtil;
import com.reveetech.cat.http.util.UrlUtil;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * description：httpurlconnection POST请求线程
 * <br>author：caowugao
 * <br>time： 2017/05/02 20:39
 */

public class PostThread extends BaseHttpThread {

    public static class ParamType {
        public static final int NORMAL = 1;
        public static final int JSON = 2;
    }

    private String formBodyString = null;

    public PostThread(String url, Map<String, String> params, int paramType, IHttpRespond callback) {
        super(url, params, callback);
        if (null != params) {
            if (ParamType.NORMAL == paramType) {
                formBodyString = UrlUtil.pieceParmasByGETNoWenHao(params);
                formBodyString.replaceAll("\\s*", "");
            } else if (ParamType.JSON == paramType) {
                JsonParam formBody = new JsonParam(params);
                formBodyString = formBody.toString();
                formBodyString.replaceAll("\\s*", "");
            } else {
                new IllegalArgumentException("paramType 必须是ParamType.NORMAL或者ParamType.JSON");
            }
        }
    }

    public PostThread(String url, JSONObject jsonObject, IHttpRespond callback) {
        super(url, null, callback);
        formBodyString = jsonObject.toString();
    }

    @Override
    protected Object doInBackground() {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        HttpURLConnection conn = null;
        try {
            if (null == this.config) {
                this.config = RequestConfig.getDefaultConfig();
            }
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            if (url.startsWith("https")) {
                conn = (HttpsURLConnection) realUrl.openConnection();
                ((HttpsURLConnection) conn).setSSLSocketFactory(sslSocketFactory);
            } else {
                conn = (HttpURLConnection) realUrl.openConnection();
            }
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("charset", config.getCharSet());
            conn.setUseCaches(false);
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setReadTimeout(config.getReadTimeout());
            conn.setConnectTimeout(config.getConnectTimeOut());

            if (formBodyString != null && !formBodyString.trim().equals("")) {
                // 获取URLConnection对象对应的输出流
                out = new PrintWriter(conn.getOutputStream());
                // 发送请求参数
                Log.e("guoxing", "formBodyString:" + formBodyString);
                out.print(formBodyString);
                // flush输出流的缓冲
                out.flush();
            }
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (null != conn) {
                    conn.disconnect();
                }
                FileUtil.closeIO(out, in);
            } catch (Exception e) {
                e.printStackTrace();
                return e;
            }
        }
        return result;

    }
}
