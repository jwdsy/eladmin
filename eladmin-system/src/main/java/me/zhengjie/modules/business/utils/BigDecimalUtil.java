package me.zhengjie.modules.business.utils;

import java.math.BigDecimal;

public class BigDecimalUtil {
    public static String convertToString(BigDecimal decimal) {
        if (decimal == null) {
            return null;
        }
        // 为整数
        if (decimal.compareTo(new BigDecimal(Integer.toString(decimal.intValue()))) == 0) {
            return Integer.toString(decimal.intValue());
        }
        // 一位小数
        BigDecimal multiply = decimal.multiply(new BigDecimal("10"));
        if (multiply.compareTo(new BigDecimal(Integer.toString(multiply.intValue()))) == 0) {
            decimal.setScale(1, BigDecimal.ROUND_DOWN);
            return decimal.toString();
        }
        // 其余情况，保留两位小数
        decimal.setScale(2, BigDecimal.ROUND_DOWN);
        return decimal.toString();
    }

    /**
     * @description: 保留两位小数
     * @auther: wangpengfei
     * @Date: 2023-08-29 11:01
     */
    public static String convertToStrTowScale(BigDecimal decimal) {
        return convertToStrAnyScale(decimal, 2);
    }

    public static String convertToStrAnyScale(BigDecimal decimal, Integer scale) {
        if (decimal == null) {
            return null;
        }
        // 保留任意小数
        decimal.setScale(scale, BigDecimal.ROUND_DOWN);
        return decimal.toString();
    }

    public static boolean isEmpty(BigDecimal decimal) {
        if(null == decimal || BigDecimal.ZERO.equals(decimal) || 0 == BigDecimal.ZERO.compareTo(decimal)){
            return true;
        }
        return false;
    }

    public static BigDecimal getMax(BigDecimal decimal1, BigDecimal decimal2) {
        if(isEmpty(decimal2)){
            return decimal1;
        }
        if(isEmpty(decimal1)){
            return decimal2;
        }
        if(0 < decimal1.compareTo(decimal2)){
            return decimal1;
        }
        return decimal2;
    }

    public static BigDecimal getMin(BigDecimal decimal1, BigDecimal decimal2) {
        if(isEmpty(decimal2)){
            return decimal1;
        }
        if(isEmpty(decimal1)){
            return decimal2;
        }

        if(0 > decimal1.compareTo(decimal2)){
            return decimal1;
        }
        return decimal2;
    }
}
