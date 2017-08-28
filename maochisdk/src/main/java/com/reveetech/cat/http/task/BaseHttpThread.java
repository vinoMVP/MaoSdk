package com.reveetech.cat.http.task;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.reveetech.cat.http.callback.IHttpRespond;
import com.reveetech.cat.http.config.RequestConfig;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.util.Map;

import javax.net.ssl.SSLSocketFactory;

/**
 * description：http请求线程基类
 * <br>author：caowugao
 * <br>time： 2017/05/03 11:32
 */

public abstract class BaseHttpThread extends Thread {

    protected String url;
    protected IHttpRespond callback;
    protected SSLSocketFactory sslSocketFactory;
    protected RequestConfig config;
    protected int code;
    private static final String TAG = BaseHttpThread.class.getSimpleName();
    protected Map<String, String> outterParams;

    protected MainHandler mainHandler;

    private static final int TAG_CALLBACK = 100;
    private static final int TAG_CANCEL = 101;
    private static final int TAG_LOADING = 102;

    protected boolean isCancel = false;

    public BaseHttpThread(String url, Map<String, String> params, IHttpRespond callback) {
        this.url = url;
        this.outterParams = params;
        this.callback = callback;
        mainHandler = new MainHandler(this);
    }

    public void setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
    }

    public void setRequestConfig(RequestConfig config) {
        this.config = config;
        if (null == this.config) {
            this.config = RequestConfig.getDefaultConfig();
        }
    }

    public void cancelTask() {
        isCancel = true;
    }

    protected void logDebug(String msg) {
        Log.d(TAG, msg);
    }

    protected void logInfo(String msg) {
        Log.i(TAG, msg);
    }

//    protected void sendCancelMessage() {
//        mainHandler.sendEmptyMessage(TAG_CANCEL);
//    }

    private static class MainHandler extends Handler {
        private WeakReference<BaseHttpThread> threadWeakReference;

        public MainHandler(BaseHttpThread thread) {
            super(Looper.getMainLooper());
            this.threadWeakReference = new WeakReference<BaseHttpThread>(thread);
        }

        @Override
        public void handleMessage(Message msg) {
            BaseHttpThread thread = threadWeakReference.get();
            if (null == thread) {
                return;
            }
            switch (msg.what) {
                case TAG_CALLBACK:
                    thread.onPostExecute(msg.obj);
                    break;
                case TAG_LOADING:
                    int count = msg.arg1;
                    int current = msg.arg2;
                    thread.publishProgress(count, current);
                    break;
//                case TAG_CANCEL:
//                    break;
            }
        }
    }

    @Override
    public void run() {
        Object result = doInBackground();
        sendCallbackMsg(result);
    }

    private void sendCallbackMsg(Object result) {
        mainHandler.obtainMessage(TAG_CALLBACK, result).sendToTarget();
    }


    protected abstract Object doInBackground();

    private void onPostExecute(Object result) {
        if (null == callback) {
            return;
        }
        if (null == result) {
            callback.onCancel();
            logDebug("用户取消");
            return;
        }
        if (result instanceof MalformedURLException) {
            callback.onFailure((Throwable) result, 3721, "加载失败");
            logDebug("URL错误");
        } else if (result instanceof IOException) {
            callback.onFailure((Throwable) result, code, "加载失败");
            logDebug("IO错误:" + code);
        } else if (result instanceof Exception) {
            callback.onFailure((Throwable) result, code, "未知错误");
            logDebug("未知错误:" + code);
        } else {
            callback.onSuccess(result);
            if (config.isUseCache()) {
                config.getCache().add(url, result.toString());
            }
        }
    }

    private void publishProgress(int count, int current) {
        if (null == callback) {
            return;
        }
        callback.onLoading(count, current);
    }

    protected void sendProgressMsg(int count, int current) {
        mainHandler.obtainMessage(TAG_LOADING, count, current).sendToTarget();
    }
}
