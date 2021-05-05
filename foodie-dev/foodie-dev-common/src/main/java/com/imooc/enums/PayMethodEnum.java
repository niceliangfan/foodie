package com.imooc.enums;

public enum PayMethodEnum {

    WEIXIN(1,"微信支付"),
    ALIPAY(2,"支付宝支付");

    public final Integer type;
    public final String value;

    PayMethodEnum(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
