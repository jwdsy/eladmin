package me.zhengjie.modules.business.enums;

import lombok.Getter;


public enum LabelStatusEnum {

    ONLINE(1, "上线"),
    OFFLINE(2, "下线"),



    ;
    @Getter
    private Integer code;

    @Getter
    private String name;

    LabelStatusEnum(Integer code, String name) {
        this.name = name;
        this.code = code;
    }




}
