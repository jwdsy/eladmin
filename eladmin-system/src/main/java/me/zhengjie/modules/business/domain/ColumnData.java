package me.zhengjie.modules.business.domain;

import com.alibaba.excel.metadata.data.WriteCellData;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class ColumnData {
    private String itemNo;
    private WriteCellData<Void> picture;
    private String description;
    private String deliveryPort;
    private String length;
    private String width;
    private String height;
    private Integer innerBox;
    private Integer outerCtn;
    private String g;
    private Integer pcs;
    private String l;
    private String w;
    private String h;
    private String cbmCtn;
    private String unitPrice;
    private String qtyIn20gp;
    private String qtyIn40gp;
    private String qtyIn40hc;
}