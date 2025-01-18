package me.zhengjie.modules.business.enums;

import lombok.Getter;


public enum IsTypeInteger {

    NO(0, "否"),
    YES(1, "是"),



    ;
    @Getter
    private Integer code;

    @Getter
    private String name;

    IsTypeInteger(Integer code, String name) {
        this.name = name;
        this.code = code;
    }




}
