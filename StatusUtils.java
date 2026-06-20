package com.example.fridgemate.utils;

import com.example.fridgemate.R;

public final class StatusUtils {
    // 食材状态常量统一放在工具类中，避免页面和数据库层出现散落字符串。
    public static final String NORMAL = "正常";
    public static final String SOON = "即将过期";
    public static final String EXPIRED = "已过期";
    public static final String USED = "已使用";

    private StatusUtils() {
    }

    public static String statusFor(String expireDate) {
        // 状态规则：过期优先，其次是 3 天内即将过期，其他为正常。
        if (DateUtils.isExpired(expireDate)) {
            return EXPIRED;
        }
        if (DateUtils.isExpiringSoon(expireDate)) {
            return SOON;
        }
        return NORMAL;
    }

    public static int chipBackgroundFor(String status) {
        if (SOON.equals(status)) {
            return R.drawable.bg_chip_orange;
        }
        if (EXPIRED.equals(status)) {
            return R.drawable.bg_chip_red;
        }
        if (USED.equals(status)) {
            return R.drawable.bg_chip_gray;
        }
        return R.drawable.bg_chip_green;
    }

    public static int colorFor(String status) {
        if (SOON.equals(status)) {
            return R.color.accent_orange;
        }
        if (EXPIRED.equals(status)) {
            return R.color.accent_red;
        }
        if (USED.equals(status)) {
            return R.color.accent_gray;
        }
        return R.color.primary;
    }
}
