/*
 *  Copyright 2019-2020 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package me.zhengjie.modules.bizitem.service.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Zheng Jie
 * @date 2026-05-11
 */
@Data
public class ItemDisplayDto implements Serializable {

    /**
     * 商品ID
     */
    private Long itemId;

    /**
     * 产品编号
     */
    private String itemNo;

    /**
     * 产品图片
     */
    private String itemPic;

    /**
     * 产品缩略图片
     */
    private String itemCompressPic;

    /**
     * 标签描述
     */
    private String description;

    /**
     * 是否喜欢 1 是，0 否
     */
    private Integer pickFlag;

    /**
     * 用户喜欢备注
     */
    private String pickRemark;

    /**
     * 商品长(cm)
     */
    private String itemLength;

    /**
     * 商品宽(cm)
     */
    private String itemWidth;

    /**
     * 商品高(cm)
     */
    private String itemHeight;
}
