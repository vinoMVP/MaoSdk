package com.reveetech.cat.http;

import android.util.Log;

import com.reveetech.cat.http.callback.IHttpRespond;
import com.reveetech.cat.http.task.PostThread;

import org.json.JSONObject;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vinoMVP on 2017/8/28.
 */
public class HttpUrlUtilTest {
    @Test
    public void get() throws Exception {

    }

    @Test
    public void post() throws Exception {
        Map<String, String> params = new HashMap<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("SubProjID", "37");
        jsonObject.put("IMEI", "016817210000062");
        System.out.print(jsonObject.toString());
        params.put("Data", jsonObject.toString());
        HttpUrlUtil.post("http://device.kxcontrol.com:8002/smstaskex/AllocSimByPro", params, PostThread.ParamType.JSON, new IHttpRespond() {
            @Override
            public void onSuccess(Object result) {
                Log.e("guoxing", "result:" + String.valueOf(result));
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String msg) {
                Log.e("guoxing", "result:fail");
            }

            @Override
            public void onLoading(long count, long current) {

            }

            @Override
            public void onCancel() {

            }
        });
    }

}