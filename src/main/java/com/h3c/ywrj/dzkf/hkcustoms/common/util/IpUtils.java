package com.h3c.ywrj.dzkf.hkcustoms.common.util;

import javax.validation.constraints.NotNull;

import static com.h3c.ywrj.dzkf.hkcustoms.common.util.RegexUtils.IPV4_PATTERN1;
import static com.h3c.ywrj.dzkf.hkcustoms.common.util.RegexUtils.IPV4_SEG_PATTERN2;

/**
 * Created by @author Jeff on 2019-12-18 23:56
 */
public final class IpUtils {
    /**
     * 验证IP地址是否有效
     *
     * @param ip
     * @return 若有效，返回1表示单一IP地址，2表示全段IP地址，3表示某段IP地址，0表示无效IP地址
     */
    public static int checkIpType(@NotNull String ip) {
        int ipType = 0;
        if (IPV4_PATTERN1.matcher(ip).matches())
            return ip.contains("*") ? 2 : 1;
        else if (IPV4_SEG_PATTERN2.matcher(ip).matches()) {
            final String[] ipArr = ip.substring(ip.lastIndexOf(".") + 1).split("-");
            // 验证最后一位满足结束地址至少比起始地址大的条件
            if (ipArr[1].compareTo(ipArr[0]) >= 0) return 3;
        }
        return ipType;
    }

    /**
     * 截取IP地址的前三位，用于比较是否相等
     *
     * @param ip
     * @return 返回IP地址的前三位
     */
    private static String trim1(String ip) {
        return ip.substring(0, ip.lastIndexOf("."));
    }

    /**
     * 截取IP地址的最后一位，用于比较大小
     *
     * @param ip
     * @return 返回IP地址的最后一位
     */
    private static String trim2(String ip) {
        return ip.substring(ip.lastIndexOf(".") + 1);
    }
}
