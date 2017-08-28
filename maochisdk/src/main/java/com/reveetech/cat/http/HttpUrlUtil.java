package com.reveetech.cat.http;


import com.reveetech.cat.http.callback.IHttpRespond;
import com.reveetech.cat.http.task.BaseHttpThread;
import com.reveetech.cat.http.task.GetThread;
import com.reveetech.cat.http.task.PostThread;

import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * description：httpurlconnection请求辅助类
 * <br>author：caowugao
 * <br>time： 2017/05/02 20:35
 */

public class HttpUrlUtil {

    private static final  Map<String, BaseHttpThread> CACHE_MAP = new LinkedHashMap<>(5);

    private HttpUrlUtil() {
    }

    public static void get(final String url, final IHttpRespond callback) {
        GetThread getThread = new GetThread(url, null, new IHttpRespond() {
            @Override
            public void onSuccess(Object result) {
                remove(url);
                if (null != callback) {
                    callback.onSuccess(result);
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String msg) {
                remove(url);
                if (null != callback) {
                    callback.onFailure(t, errorNo, msg);
                }
            }

            @Override
            public void onLoading(long count, long current) {
                if (null != callback) {
                    callback.onLoading(count, current);
                }
            }

            @Override
            public void onCancel() {
                remove(url);
                if (null != callback) {
                    callback.onCancel();
                }
            }
        });
        CACHE_MAP.put(url, getThread);
        getThread.start();
    }

    /**
     * 
     * @param url
     * @param params
     * @param paramType PostThread.ParamType.NORMAL:param1=value1&param2=value2...。PostThread.ParamType.JSON:即是json参数
     * @param callback
     */
    public static void post(final String url, Map<String, String> params, int paramType,final IHttpRespond callback) {
        PostThread postThread = new PostThread(url, params, paramType,new IHttpRespond() {
            @Override
            public void onSuccess(Object result) {
                remove(url);
                if (null != callback) {
                    callback.onSuccess(result);
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String msg) {
                remove(url);
                if (null != callback) {
                    callback.onFailure(t, errorNo, msg);
                }
            }

            @Override
            public void onLoading(long count, long current) {
                if (null != callback) {
                    callback.onLoading(count, current);
                }
            }

            @Override
            public void onCancel() {
                remove(url);
                if (null != callback) {
                    callback.onCancel();
                }

            }
        });
        CACHE_MAP.put(url, postThread);
        postThread.start();
    }

    public static void post(final String url, JSONObject jsonObject, final IHttpRespond callback) {
        PostThread postThread = new PostThread(url, jsonObject, new IHttpRespond() {
            @Override
            public void onSuccess(Object result) {
                remove(url);
                if (null != callback) {
                    callback.onSuccess(result);
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String msg) {
                remove(url);
                if (null != callback) {
                    callback.onFailure(t, errorNo, msg);
                }
            }

            @Override
            public void onLoading(long count, long current) {
                if (null != callback) {
                    callback.onLoading(count, current);
                }
            }

            @Override
            public void onCancel() {
                remove(url);
                if (null != callback) {
                    callback.onCancel();
                }

            }
        });
        CACHE_MAP.put(url, postThread);
        postThread.start();
    }

    public static void cancel(String url) {
        BaseHttpThread thread = CACHE_MAP.get(url);
        if (null != thread) {
            thread.cancelTask();
        }
    }

    public static void remove(String url) {
        CACHE_MAP.remove(url);
    }

    public static void clear() {
        CACHE_MAP.clear();
    }

}
