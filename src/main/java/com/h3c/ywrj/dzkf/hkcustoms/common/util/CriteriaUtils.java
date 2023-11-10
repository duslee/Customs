package com.h3c.ywrj.dzkf.hkcustoms.common.util;

import com.h3c.ywrj.dzkf.hkcustoms.common.prop.SearchPortProperties;
import com.h3c.ywrj.dzkf.hkcustoms.common.prop.SearchProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.h3c.ywrj.dzkf.hkcustoms.common.ParamConstants.END_TIME;
import static com.h3c.ywrj.dzkf.hkcustoms.common.ParamConstants.START_TIME;

/**
 * Created by @author wfw2525 on 2019/12/23 23:48
 */
@Slf4j
public final class CriteriaUtils {
    /**
     * 用于判断提供的参数是否符合时间范围要求：要么同时存在，要么同时不存在
     *
     * @param queryMap
     * @return
     */
    public static boolean hasRightTimeRange(LinkedMultiValueMap queryMap) {
        return (queryMap.containsKey(START_TIME) && queryMap.containsKey(END_TIME)) ||
                (!queryMap.containsKey(START_TIME) && !queryMap.containsKey(END_TIME));
    }

    /**
     * 设置参数查询条件：支持值等同、值包含和时间区间选择
     *
     * @param cb
     * @param root
     * @param tClazz
     * @param queryMap
     * @param searchProperties
     * @param condList
     * @param <T>
     * @param <P>
     */
    public static <T, P extends SearchProperties> void configQueryConditions(
            CriteriaBuilder cb, Root<T> root, Class<T> tClazz,
            LinkedMultiValueMap queryMap, P searchProperties, List<Predicate> condList) {
        List<String> ins = null, likes = null, timeRange = null;

        if (searchProperties instanceof SearchPortProperties) {
            final SearchPortProperties spp = (SearchPortProperties) searchProperties;
            ins = spp.getIns();
            likes = spp.getLikes();
            timeRange = spp.getTimeRange();
        }

        List<Object> condValues;
        Field field;
        Class fieldClazz;
        // 获取前端传递的参数集
        final Set<String> paramSet = queryMap.keySet();
        for (String param : paramSet) {
            // 获取当前param对应的值（列表对象）
            condValues = queryMap.get(param);
            // 若对应的值为空，遍历下一个参数
            if (ListUtils.isEmptyOrNull(condValues)) continue;

            // 这些条件可能是String、Integer、Long、Date类型,所以需要根据param名来判断
            try {
                field = tClazz.getDeclaredField(param);
            } catch (NoSuchFieldException e) {
                throw new PropertyNotFoundException("此成员(" + param + ")不存在!");
            }

            // 获取当前成员的类类型
            fieldClazz = field.getType();
            log.info("condValue: {}, fieldClazz: {}", condValues, fieldClazz.getName());
            configQueryConditions2(cb, root, param, fieldClazz, condValues, ins, likes, timeRange, condList);
        }
    }

    private static <T> void configQueryConditions2(CriteriaBuilder cb, Root<T> root, String param, Class<?> fieldClazz,
                                                   List<Object> condValues, List<String> ins, List<String> likes,
                                                   List<String> timeRange, List<Predicate> condList) {
        // 1. 先处理IN
        if (!ListUtils.isEmptyOrNull(ins) && ins.contains(param)) {
            commonInSearch(cb, root, param, fieldClazz, condValues, condList);
            return;
        }

        // 2. 再处理LIKE
        if (!ListUtils.isEmptyOrNull(likes) && likes.contains(param)) {
            commonLikeSearch(cb, root, param, fieldClazz, condValues, condList);
            return;
        }

        // 3. 处理时间区间选择
        if (!ListUtils.isEmptyOrNull(timeRange) && timeRange.contains(param)) {
            commonTimeRangeSearch(cb, root, param, fieldClazz, condValues, condList);
            return;
        }

        // 4. 其它处理：默认使用IN操作处理
        commonInSearch(cb, root, param, fieldClazz, condValues, condList);
    }

    /**
     * 设置值等同的条件
     *
     * @param cb
     * @param root
     * @param param
     * @param fieldClazz
     * @param condValues
     * @param condList
     * @param <T>
     */
    private static <T> void commonInSearch(CriteriaBuilder cb, Root<T> root, String param,
                                           Class<?> fieldClazz, List<Object> condValues, List<Predicate> condList) {
        log.info("111 param: {}, {}, {}", param, fieldClazz, condValues);
        log.info("111 <<< condList: {}", condList);
        final CriteriaBuilder.In<Object> in = cb.in(root.get(param).as(fieldClazz));

        if (fieldClazz.isAssignableFrom(Long.class) || fieldClazz.isAssignableFrom(long.class)) {
//            if (condValues instanceof List) {
//                list = (List<Object>) condValues;
//                for (Integer value : (List<Integer>) condValues) in.value(value);
//            } else in.value(Long.valueOf(String.valueOf(condValues)));
            condValues.forEach(condValue -> {
                if (condValue instanceof String) in.value(Long.valueOf((String) condValue));
                else if (condValue instanceof Long || condValue instanceof Integer) in.value(condValue);
            });
        } else if (fieldClazz.isAssignableFrom(Integer.class) || fieldClazz.isAssignableFrom(int.class)) {
//            if (condValues instanceof List) for (Integer value : (List<Integer>) condValues) in.value(value);
//            else in.value(condValues);
            condValues.forEach(condValue -> {
                if (condValue instanceof String) in.value(Integer.valueOf((String) condValue));
                else if (condValue instanceof Long || condValue instanceof Integer) in.value(condValue);
            });
        } else {
//            if (condValues instanceof List) for (String value : (List<String>) condValues) in.value(value);
//            else in.value(Long.valueOf(String.valueOf(condValues)));
            condValues.forEach(condValue -> {
                if (condValue instanceof String) in.value(condValue);
                else in.value(String.valueOf(condValue));
            });
        }

        condList.add(in);
        log.info("111 >>> condList: {}", condList);
    }

    /**
     * 设置值部分包含的条件
     *
     * @param cb
     * @param root
     * @param param
     * @param fieldClazz
     * @param condValues
     * @param condList
     * @param <T>
     */
    private static <T> void commonLikeSearch(CriteriaBuilder cb, Root<T> root, String param,
                                             Class<?> fieldClazz, List<Object> condValues, List<Predicate> condList) {
        log.info("222 param: {}, {}, {}", param, fieldClazz, condValues);
        log.info("222 <<< condList: {}", condList);
        final List<Predicate> preList = new ArrayList<>();

        // 只有字符串的情况
        condValues.forEach(condValue -> {
            preList.add(cb.like((Expression<String>) root.get(param).as(fieldClazz),
                    "%" + Utils.excludeSpecialStrForMysql(String.valueOf(condValue)) + "%"));
        });

//        if (condValues instanceof List) for (String realCond : (List<String>) condValues)
//            preList.add(cb.like((Expression<String>) root.get(param).as(fieldClazz),
//                    "%" + Utils.excludeSpecialStrForMysql(realCond) + "%"));
//        else preList.add(cb.like((Expression<String>) root.get(param).as(fieldClazz),
//                "%" + Utils.excludeSpecialStrForMysql(String.valueOf(condValues)) + "%"));

        condList.add(cb.or(preList.stream().toArray(Predicate[]::new)));
        log.info("222 >>> condList: {}", condList);
    }

    /**
     * 设置时间区间选择的条件
     *
     * @param cb
     * @param root
     * @param param
     * @param fieldClazz
     * @param condValues
     * @param condList
     * @param <T>
     */
    private static <T> void commonTimeRangeSearch(CriteriaBuilder cb, Root<T> root, String param, Class<?> fieldClazz,
                                                  List<Object> condValues, List<Predicate> condList) {
        log.info("333 param: {}, {}, {}", param, fieldClazz, condValues);
        log.info("333 <<< condList: {}", condList);
        if (fieldClazz.isAssignableFrom(Date.class)) {
//                log.info("111 (list.get(0) instanceof Date): {}", list.get(0) instanceof Date);
//                log.info("111 (list.get(0) instanceof String): {}", list.get(0) instanceof String);
            if (condValues.get(0) instanceof Date)
                condList.add(cb.between((Expression<Date>) root.get(param).as(fieldClazz),
                        (Date) condValues.get(0), (Date) condValues.get(1)));
            else if (condValues.get(0) instanceof String) {
                final Date startTime = DateUtils.convertStringToDate((String) condValues.get(0));
                final Date endTime = DateUtils.convertStringToDate((String) condValues.get(1));
//                    log.info("startTime: {}, endTime: {}", startTime, endTime);
                condList.add(cb.between((Expression<Date>) root.get(param).as(fieldClazz), startTime, endTime));
            }
        }
        log.info("333 >>> condList: {}", condList);
    }
}
