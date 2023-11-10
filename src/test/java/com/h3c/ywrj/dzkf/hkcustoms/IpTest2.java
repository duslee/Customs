package com.h3c.ywrj.dzkf.hkcustoms;

import com.h3c.ywrj.dzkf.hkcustoms.common.util.IpUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jeff on 2019-12-19 23:24
 */
public class IpTest2 {
    public static void main(String[] args) {
//        final String ips = "1.1.1.*";
//        final String ips = "1.1.1.2-4";
//        final String ips = "1.1.1.2-6";
//        final String ips = "1.1.1.2-2";
//        final String ips = "1.1.1.2-20";
        final String ips = "1.1.1.2-22";

        final int ipType = IpUtils.checkIpType(ips);
        System.out.println(ipType);
        final int scanMax = 4;

        final List<String> list = new ArrayList<>();
        // 获取IP地址的前三位
        final String _13 = ips.substring(0, ips.lastIndexOf(".") + 1);
        System.out.println(_13);

        int start = 0;
        if (2 == ipType) {
            // 将全段IP地址根据scanMax平分
            for (int i = 0; i < 256 / scanMax; i++) {
                list.add(_13 + start + "-" + (start + scanMax - 1));
                start += scanMax;
            }
        } else {
            // 获取IP地址的后面的起始和结束位置
            final String[] arr = ips.substring(ips.lastIndexOf(".") + 1).split("-");

            // 获取起始和结束对应的数字
            start = Integer.parseInt(arr[0]);
            final int end = Integer.parseInt(arr[1]);
            System.out.println("start: " + start + ", end: " + end);

            // 将IP端地址根据scanMax平分
            int tmpEnd;
            while (start <= end) {
                // 注意只有一个的特殊情况
                if (start == end) {
                    list.add(_13 + start);
                    break;
                } else {
                    tmpEnd = start + scanMax - 1;
                    list.add(_13 + start + "-" + (tmpEnd <= end ? tmpEnd : end));
                    start += scanMax;
                }
            }
        }

        System.out.println(list);
    }
}
