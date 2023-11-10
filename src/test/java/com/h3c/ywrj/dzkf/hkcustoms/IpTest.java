package com.h3c.ywrj.dzkf.hkcustoms;

import com.h3c.ywrj.dzkf.hkcustoms.common.util.IpUtils;

/**
 * Created by Jeff on 2019-12-19 00:38
 */
public class IpTest {
    public static void main(String[] args) {
        // 测试IP地址有效性
        System.out.println("0.1.0.1: " + IpUtils.checkIpType("0.1.0.1"));
        System.out.println("1.1.1.1: " + IpUtils.checkIpType("1.1.1.1"));
        System.out.println("1.1.1.1.: " + IpUtils.checkIpType("1.1.1.1."));
        System.out.println("1.1.1.256: " + IpUtils.checkIpType("1.1.1.256"));
        System.out.println("1.01.1.25: " + IpUtils.checkIpType("1.01.1.25"));
        System.out.println("1.*.1.25: " + IpUtils.checkIpType("1.*.1.25"));
        System.out.println("1.1.1.*: " + IpUtils.checkIpType("1.1.1.*"));
        System.out.println("1.1.1.*-1.1.1.1-: " + IpUtils.checkIpType("1.1.1.*-1.1.1.1-"));
        System.out.println("1.1.1.*-1.1.1.1: " + IpUtils.checkIpType("1.1.1.*-1.1.1.1"));
        System.out.println("1.1.1.1-1.1.1.1: " + IpUtils.checkIpType("1.1.1.1-1.1.1.1"));
        System.out.println("1.1.1.2-1.1.1.1: " + IpUtils.checkIpType("1.1.1.2-1.1.1.1"));
        System.out.println("1.1.2.1-1.1.1.11: " + IpUtils.checkIpType("1.1.2.1-1.1.1.11"));

        System.out.println("1.1.1.1".matches("((\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])\\.){3}(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])"));

        System.out.println("1.1.1.3-5: " + IpUtils.checkIpType("1.1.1.3-5"));
        System.out.println("1.1.1.4-3: " + IpUtils.checkIpType("1.1.1.4-3"));
        System.out.println("1.1.1.4-4: " + IpUtils.checkIpType("1.1.1.4-4"));
    }
}
