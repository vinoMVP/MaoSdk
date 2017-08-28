package com.reveetech.cat.constants;

/**
 * 请求url链接
 */

public interface Url {
    // 主机地址
    String HOST = "http://device.kxcontrol.com:8002";
    // 请求卡资源
    String REQUEST_CARD = HOST + "/smstaskex/AllocSimByProj";
    // 启动卡透传业务
    String START_CARD_BUSINESS = HOST + "/smstaskex/NewTask";
    // 查询透传业务
    String QUERY_CARD_STATUS = HOST + "/smstaskex/QueryTask";
    // 取消透传业务
    String CANCEL_TASK = HOST + "/smstaskex/CancelTask";
    // 添加手机号
    String ADD_PHONE_NUMBER = HOST + "/PhoneNumber/Add";
}
