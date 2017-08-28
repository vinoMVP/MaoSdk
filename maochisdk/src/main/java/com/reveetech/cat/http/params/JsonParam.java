package com.reveetech.cat.http.params;

import android.text.TextUtils;
import android.util.Log;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * description：json参数
 * <br>author：caowugao
 * <br>time： 2017/05/03 11:04
 */

public class JsonParam {
    private static final String TAG = JsonParam.class.getSimpleName();

    protected ConcurrentHashMap<String, String> parmas;

    /**
     *
     */
    public JsonParam() {
        parmas = new ConcurrentHashMap<String, String>(6);
    }

    public JsonParam(Map<String, String> source) {
        this();
        for (Map.Entry<String, String> entry : source.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    public void put(String key, String value) {
        if (key != null && value != null) {
            parmas.put(key, value);
        } else if (key != null && value == null) {
            parmas.put(key, "");
            logDebug("put(key,value)----->" + key + "=null");
        } else {
            throw new RuntimeException("key or value is NULL");
        }
    }

    /**
     * 移除key与对应的value
     */
    public void remove(String key) {
        if (key == null) {
            throw new RuntimeException("key or value is NULL");
        } else {
            parmas.remove(key);
        }
    }

    private void logDebug(String msg) {
        Log.d(TAG, msg);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");
        Iterator<Map.Entry<String, String>> it = parmas.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            String key = entry.getKey();
            String value;
            value = entry.getValue();

            if (TextUtils.isEmpty(key)) {

                stringBuilder.delete(0, stringBuilder.length());
                stringBuilder.append(value);
                return stringBuilder.toString();
            } else if (value.equals("")) {

                stringBuilder.append("\"").append(key).append("\":\"").append("\",");
            } else if (value.equals("{}")) {

                stringBuilder.append("\"").append(key).append("\":\"").append("\",");
            } else if (value.substring(0, 1).equals("{")) {

                stringBuilder.append("\"").append(key).append("\":").append(value).append(",");
            } else {

                stringBuilder.append("\"").append(key).append("\":\"").append(value).append("\",");
            }
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
