package com.reveetech.cat;

import android.util.Log;

import com.reveetech.cat.bean.RequestCardResponse;
import com.reveetech.cat.constants.Url;
import com.reveetech.cat.http.HttpUrlUtil;
import com.reveetech.cat.http.callback.IHttpRespond;
import com.reveetech.cat.http.task.PostThread;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 猫池sdk
 */

public class CatSdk {

    public static void getPhoneInfo() {
        requestCardRes("37", "016817210000062", new OnCardRequestListener() {
            @Override
            public void success(String phoneNumber, String checkCode) {

            }

            @Override
            public void failed(String msg) {
                Log.e("guoxing", "msg:" + msg);
            }
        });
    }

    private static OnCardRequestListener listener;

    /**
     * 请求卡资源的接口
     *
     * @param subProjID 项目id
     * @param imei      手机的imei号
     */
    private static void requestCardRes(String subProjID, String imei, OnCardRequestListener onCardRequestListener) {
        listener = onCardRequestListener;
        Map<String, String> params = new HashMap<>();
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("SubProjID", subProjID);
            jsonObject.put("IMEI", imei);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("Data", jsonObject.toString());
        params.put("Token", "dinghao");

        HttpUrlUtil.post(Url.REQUEST_CARD, params, PostThread.ParamType.JSON, new IHttpRespond() {
            @Override
            public void onSuccess(Object result) {
                Log.e("guoxing", "result:" + String.valueOf(result));
                parsePhoneInfo(result);
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String msg) {
                if (listener != null) {
                    listener.failed(msg);
                }
            }

            @Override
            public void onLoading(long count, long current) {

            }

            @Override
            public void onCancel() {
                if (listener != null) {
                    listener.failed("请求取消");
                }
            }
        });
    }

    /**
     * 解析手机号等信息
     *
     * @param result
     */
    private static void parsePhoneInfo(Object result) {
        try {
            RequestCardResponse requestCardResponse = new RequestCardResponse();
            JSONObject responseObject = new JSONObject(String.valueOf(result));
            int errcode = responseObject.getInt("errcode");
            requestCardResponse.errcode = errcode;
            String errmsg = responseObject.getString("errmsg");
            requestCardResponse.errmsg = errmsg;
            if (errcode == 0) {
                // 代表申请成功
                JSONObject dataObject = responseObject.getJSONObject("bizdata");
                RequestCardResponse.RequestCard requestCard = new RequestCardResponse().new RequestCard();
                requestCard.IMEI = dataObject.getString("IMEI");
                requestCard.IMSI = dataObject.getString("IMSI");
                requestCard.Number = dataObject.getString("Number");
                requestCard.ProjID = dataObject.getString("ProjID");
                requestCard.SubProjID = dataObject.getString("SubProjID");
                requestCard.ProjType = dataObject.getInt("ProjType");
                requestCardResponse.bizdata = requestCard;
                startCardBusiness(requestCard.SubProjID, requestCard.IMEI, requestCard.Number);
            } else {
                // 第一步就失败了
                if (listener != null) {
                    listener.failed(errmsg);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开启透传业务
     *
     * @param subProjID 项目id
     * @param imei      imei号码
     * @param number    电话号码
     */
    private static void startCardBusiness(String subProjID, String imei, String number) {
        HashMap<String, String> params = new HashMap<>();
        JSONObject dataObject = new JSONObject();
        try {
            dataObject.put("SubProjID", subProjID);
            dataObject.put("IMEI", imei);
            dataObject.put("PhoneNo", number);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("Data", dataObject.toString());
        params.put("Token", "dinghao");
        HttpUrlUtil.post(Url.START_CARD_BUSINESS, params, PostThread.ParamType.JSON, new IHttpRespond() {
            @Override
            public void onSuccess(Object result) {
                try {
                    JSONObject responseObj = new JSONObject(String.valueOf(result));
                    int errcode = responseObj.getInt("errcode");
                    String errmsg = responseObj.getString("errmsg");
                    if (errcode == 0) {
                        String id = responseObj.getJSONObject("bizdata").getString("ID");
                    } else {
                        if (listener != null) {
                            listener.failed(errmsg);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String msg) {

            }

            @Override
            public void onLoading(long count, long current) {

            }

            @Override
            public void onCancel() {

            }
        });
    }

    /**
     * 申请卡的接口回调
     */
    public interface OnCardRequestListener {

        /**
         * 申请卡成功
         *
         * @param phoneNumber 电话号码
         * @param checkCode   验证码
         */
        void success(String phoneNumber, String checkCode);

        /**
         * 申请卡失败
         *
         * @param msg 失败的消息
         */
        void failed(String msg);
    }
}
