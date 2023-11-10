package com.h3c.ywrj.dzkf.hkcustoms.common.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by @author wfw2525 on 2019/12/23 23:55
 */
public final class Utils {
    public static boolean isBeyondWordLimit(String str, int limit) {
        return StringUtils.isNotBlank(str) && str.trim().length() >= limit;
    }

    /**
     * 转换Mysql特殊字符(% _ /) -
     */
    public static String excludeSpecialStrForMysql(String str) {
        char[] strArr = str.toCharArray();
        //存放需要替换特殊字符的索引值的集合
        List<Integer> indexList = new ArrayList<>();
        for (int i = 0; i < strArr.length; i++) {
            //SQL特殊字符集
            if ("%_/".contains(String.valueOf(strArr[i]))) indexList.add(i);
        }
        StringBuilder sb = new StringBuilder(str);
        if (!CollectionUtils.isEmpty(indexList)) {
            for (int i = 0; i < indexList.size(); i++) {
                sb.insert(indexList.get(i), "\\");
                indexList = indexList.stream().map(index -> ++index).collect(Collectors.toList());
            }
        }
        return sb.toString();
    }
}
