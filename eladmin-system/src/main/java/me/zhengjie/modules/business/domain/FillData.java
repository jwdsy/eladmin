package me.zhengjie.modules.business.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
public class FillData {
    private String to;
    private String attn;
    private String from;
    private String date;
    private String priceTerm;
    private List<ColumnData> columnDataList;

    public static FillData getInstance() throws Exception {
        FillData fillData = new FillData();
        fillData.setTo("Mr.J");
        fillData.setAttn("Attn xxx");
        fillData.setFrom("Mr.Li");
        fillData.setDate("2025-01-01 10:02:11");
        fillData.setPriceTerm("priceTerm");
        return fillData;
    }
}