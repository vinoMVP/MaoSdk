package com.reveetech.cat.bean;

/**
 * 请求card的返回数据
 */

public class RequestCardResponse extends Base {

    public RequestCard bizdata;

    public class RequestCard {
        public String ProjID;
        public String SubProjID;
        public String Number;
        public String IMSI;
        public String IMEI;
        public int ProjType;
    }

}
