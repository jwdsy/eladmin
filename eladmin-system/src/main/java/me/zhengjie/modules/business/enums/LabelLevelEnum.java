package me.zhengjie.modules.business.enums;

import lombok.Getter;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;


public enum LabelLevelEnum {

    LEVEL_1(1, "一级标签"),
    LEVEL_2(2, "二级标签"),



    ;
    @Getter
    private Integer code;

    @Getter
    private String name;

    LabelLevelEnum(Integer code, String name) {
        this.name = name;
        this.code = code;
    }

    public static Map<Integer, String> getLookup() {
        return lookup;
    }

    private static final Map<Integer, String> lookup = new HashMap<>();

    static {
        for (LabelLevelEnum s : EnumSet.allOf(LabelLevelEnum.class)) {
            lookup.put(s.getCode(), s.getName());
        }
    }




}
