package me.zhengjie.modules.business.enums;

import lombok.Getter;

/**
 * @Description ：description
 * @Author ：wangpengfei
 * @Date ：created in 2025/10/6
 */
public enum OrderTypeEnum {

    ASC(1, "升序"),
    DESC(2, "降序"),



            ;
    @Getter
    private Integer code;

    @Getter
    private String name;

    OrderTypeEnum(Integer code, String name) {
        this.name = name;
        this.code = code;
    }
}
