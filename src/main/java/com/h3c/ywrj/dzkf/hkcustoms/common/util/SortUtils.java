package com.h3c.ywrj.dzkf.hkcustoms.common.util;

import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * 统一管理REST处理相关网络请求过程中的排序操作
 * <p>
 * Created by@author  wfw2525 on 2019/12/23 23:55
 */
public final class SortUtils {

    /**
     * 根据给定的排序关键字和排序规则生成Sort对象
     *
     * @param sortKey
     * @param direction
     * @return
     */
    public static Sort genSort(@NonNull String sortKey, @NonNull Sort.Direction direction) {
        return Sort.by(direction, sortKey);
    }

    /**
     * 根据给定的排序关键字和排序规则生成Sort对象
     * <p>
     * 特殊处理空或错误的排序规则情况
     *
     * @param sortKey
     * @param sortRule
     * @param defaultSortKeys
     * @return
     */
    public static Sort genSort(String sortKey, String sortRule, @NonNull String... defaultSortKeys) {
        // 设置排序规则（默认为ASC，包括提供错误规则的情况）
        Sort.Direction direction = Sort.Direction.ASC;
        if (StringUtils.equalsIgnoreCase(Sort.Direction.DESC.name(), sortRule))
            direction = Sort.Direction.DESC;
        return genSort(sortKey, direction, defaultSortKeys);
    }

    /**
     * 根据给定的排序关键字和排序规则生成Sort对象
     *
     * @param sortKey
     * @param direction
     * @param defaultSortKeys 默认的一组排序关键字，用于处理空的排序关键字的情况下
     * @return
     */
    public static Sort genSort(String sortKey, Sort.Direction direction, String... defaultSortKeys) {
        Sort sort;
        // 有效关键字排序处理
        if (StringUtils.isNotBlank(sortKey)) sort = genSort(sortKey, direction);
        else sort = Sort.by(direction, defaultSortKeys);    // 默认排序关键字处理
        return sort;
    }

    /**
     * 验证排序关键字是否符合数据库要求
     *
     * @param sortKey
     * @param sortKeys
     * @return true表示有效，false表示无效
     */
    public static boolean isSortKeyValid(String sortKey, List<String> sortKeys) {
        return sortKeys.contains(sortKey);
    }
}
