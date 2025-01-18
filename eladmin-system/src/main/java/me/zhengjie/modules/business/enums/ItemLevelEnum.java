package me.zhengjie.modules.business.enums;

import lombok.Getter;


public enum ItemLevelEnum {

    LEVEL_1(1, "一级标签"),
    LEVEL_2(2, "二级标签"),



    ;
    @Getter
    private Integer code;

    @Getter
    private String name;

    ItemLevelEnum(Integer code, String name) {
        this.name = name;
        this.code = code;
    }




}
