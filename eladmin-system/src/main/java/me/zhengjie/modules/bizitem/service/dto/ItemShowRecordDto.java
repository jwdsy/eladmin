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

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.zhengjie.base.BaseDTO;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Zheng Jie
 * @date 2026-05-12
 */
@Getter
@Setter
@NoArgsConstructor
public class ItemShowRecordDto extends BaseDTO implements Serializable {

    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 商品编号
     */
    private String itemNo;
    
    /**
     * 商品图片
     */
    private String itemPic;
    
    /**
     * 商品缩略图片
     */
    private String itemCompressPic;
    
    /**
     * 标签描述
     */
    private String description;
    
    /**
     * 商品备注
     */
    private String itemRemark;
    
    /**
     * 年
     */
    private Integer year;
    
    /**
     * 1 春；2 夏；3 秋；4 冬
     */
    private Integer season;
    
    /**
     * 产品状态 1 上线；2 下线
     */
    private Integer itemStatus;
    
    /**
     * 删除标识 0 否，1 是
     */
    private Integer delFlag;
    
    /**
     * 创建人ID
     */
    private Long createUserId;
    
    /**
     * 最近修改时间
     */
    private Date lastModifyTime;
    
    /**
     * 最近修改人ID
     */
    private Long modifyUserId;
}
