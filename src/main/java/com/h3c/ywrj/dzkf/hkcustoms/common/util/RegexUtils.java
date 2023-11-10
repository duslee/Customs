package com.h3c.ywrj.dzkf.hkcustoms.common.util;

import javax.validation.constraints.NotNull;
import java.util.regex.Pattern;

/**
 * Created by Jeff on 2019-12-21 09:31
 *
 * @author wfw2525
 */
public final class RegexUtils {
    // 0-9：\\d
    // 10-99：[1-9]\\d
    // 100-199：1\\d{2}
    // 200-249：2[0-4]\\d
    // 250-255：25[0-5]
    // 匹配IP地址：^(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5]).(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5]).(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5]).(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])$

    /**
     * 用于匹配IP地址的单个数字
     */
    private static final String SINGLE_DECIMAL_OF_IP_ADDRESS = "(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])";

    /**
     * 用于判断是否符合IPv4或全段格式，如192.168.1.4或如192.168.1.*
     */
//    public static Pattern IPV4_PATTERN1 = Pattern.compile("^((\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])\\.){3}((\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])|\\*)$");
    public static Pattern IPV4_PATTERN1 = Pattern.compile("^(" + SINGLE_DECIMAL_OF_IP_ADDRESS + "\\.){3}(" + SINGLE_DECIMAL_OF_IP_ADDRESS + "|\\*)$");
    /**
     * 用于判断是否符合IPv4段格式，如192.168.1.4-10
     */
//    public static Pattern IPV4_SEG_PATTERN2 = Pattern.compile("^((\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])\\.){3}(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])\\-" +
//            "(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])$");
    public static Pattern IPV4_SEG_PATTERN2 = Pattern.compile("^(" + SINGLE_DECIMAL_OF_IP_ADDRESS + "\\.){3}"
            + SINGLE_DECIMAL_OF_IP_ADDRESS + "\\-" + SINGLE_DECIMAL_OF_IP_ADDRESS + "$");

    /**
     * 用于判断文件名是否符合xls后缀
     */
    public static final Pattern XLS_PATTERN = Pattern.compile("^.+\\.(?i)(xls)$");
    /**
     * 用于判断文件名是否符合xlsx后缀
     */
    public static final Pattern XLSX_PATTERN = Pattern.compile("^.+\\.(?i)(xlsx)$");

    /**
     * 用于判断一行数据是否以多个数字开头，并且后面跟着一个斜杠，如“53/tcp open  domain”的“80/”
     */
    private static final Pattern NMAP_PORT_PATTERN = Pattern.compile("^\\d+/\\S+(\\s+\\S+)+");

    /**
     * 用于判断有端口数据
     */
    public static boolean hasNmapPort(@NotNull String line) {
        return NMAP_PORT_PATTERN.matcher(line).matches();
    }

    /**
     * 用于根据1个或多个空格对字符串进行分组
     */
    private static final Pattern SPLIT_PATTERN = Pattern.compile("\\s+");

    public static String[] split(@NotNull String content) {
        return SPLIT_PATTERN.split(content.trim());
    }

    private static final Pattern DECIMAL_PATTERN = Pattern.compile("\\d+\\.\\d+$");

    /**
     * 判断字符串是否为小数
     *
     * @param content
     * @return
     */
    public static boolean isDecimal(String content) {
        return DECIMAL_PATTERN.matcher(content).matches();
    }
}
