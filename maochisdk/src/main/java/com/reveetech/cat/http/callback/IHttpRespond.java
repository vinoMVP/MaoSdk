package com.reveetech.cat.http.callback;

/**
 * description：http回调接口
 * <br>author：caowugao
 * <br>time： 2017/05/03 11:19
 */

public interface IHttpRespond {

    void onSuccess(Object result);

    void onFailure(Throwable t, int errorNo, String msg);

    void onLoading(long count, long current);

    void onCancel();
}
