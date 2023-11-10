package com.h3c.ywrj.dzkf.hkcustoms.common.util;

import java.util.List;

/**
 * Created by @author Jeff on 2019-12-20 13:13
 */
public final class ListUtils {
    /**
     * 判断列表对象是否为null或空
     */
    public static <T> boolean isEmptyOrNull(List<T> list) {
        return null == list || list.size() == 0;
    }

    /**
     * 手动清空列表对象
     */
    public static <T> void clear(List<T>... lists) {
        for (List<T> list : lists) list.clear();
    }
}
