package com.reveetech.cat.http.cache;

/**
 * description：缓存接口
 * <br>author：caowugao
 * <br>time： 2017/05/03 11:16
 */
public interface IHttpCache {

    void add(String url, String json);

    String get(String url);
}
