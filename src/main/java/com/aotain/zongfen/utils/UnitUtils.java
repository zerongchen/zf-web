package com.aotain.zongfen.utils;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class UnitUtils {

    public static String getUnit(Double maxSize) {
        if (maxSize < Long.parseLong("1048576")&& maxSize >= Long.parseLong("1024")) {
            return "KB";
        } else if (Long.parseLong("1073741824") > maxSize && maxSize >= Long.parseLong("1048576")) {
            return "M";
        } else if (Long.parseLong("1099511627776") > maxSize && maxSize >= Long.parseLong("1073741824")) {
            return "G";
        } else if (maxSize >= Long.parseLong("1099511627776")) {
            return "T";
        } else {
            return "B";
        }
    }

    public static Double transferUnit(Double flowValue, String unit) {
        DecimalFormat decimalFormat = new DecimalFormat("######0.00");
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        String realSize = "";

        if (flowValue != null) {
            Long ut = Long.parseLong("1024");
            if ("KB".equals(unit)) {
                realSize = decimalFormat.format(flowValue / ut.longValue());
            } else if ("M".equals(unit)) {
                ut = Long.parseLong("1048576");
                realSize = decimalFormat.format((flowValue / ut.longValue()));
            } else if ("G".equals(unit)) {
                ut = Long.parseLong("1073741824");
                realSize = decimalFormat.format((flowValue / ut.longValue()));
            } else if ("T".equals(unit)) {
                ut = Long.parseLong("1099511627776");
                realSize = decimalFormat.format((flowValue / ut.longValue()));
            } else {
                realSize = flowValue + "";
            }
        }
        if (realSize != "") {
            return Double.valueOf(realSize);
        } else {
            return null;
        }
    }


    public static String getValueStrByValue(Double value){
        String unit = UnitUtils.getUnit(value);
        return UnitUtils.transferUnit(value,unit)+unit;
    }

    public static String getValueStrByValue(String value){
        String unit = UnitUtils.getUnit(Double.valueOf(value));
        return UnitUtils.transferUnit(Double.valueOf(value),unit)+unit;
    }
}
