package com.reveetech.cat.bean.request;

import com.reveetech.cat.bean.BaseRequest;

/**
 * 申请卡的请求类
 */

public class RequestCard extends BaseRequest {

    public RequestData Data;

    public class RequestData {
        public String IMEI;
        public String SubProjID;
    }

}
