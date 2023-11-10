package com.h3c.ywrj.dzkf.hkcustoms.service;

import com.h3c.ywrj.dzkf.hkcustoms.common.ErrorCode;
import com.h3c.ywrj.dzkf.hkcustoms.common.ParamConstants;
import com.h3c.ywrj.dzkf.hkcustoms.common.RestResult;
import com.h3c.ywrj.dzkf.hkcustoms.common.RestResultGenerator;
import com.h3c.ywrj.dzkf.hkcustoms.common.prop.SearchPortProperties;
import com.h3c.ywrj.dzkf.hkcustoms.common.util.CriteriaUtils;
import com.h3c.ywrj.dzkf.hkcustoms.common.util.ListUtils;
import com.h3c.ywrj.dzkf.hkcustoms.domain.BackupPort;
import com.h3c.ywrj.dzkf.hkcustoms.repository.BackupPortRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;

import javax.persistence.criteria.Predicate;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Created by @author wfw2525 on 2019/12/23 11:56
 */
@Service
@ComponentScan(basePackages = "com.h3c.ywrj.dzkf.hkcustoms")
@EnableConfigurationProperties(SearchPortProperties.class)
@Slf4j
public class FixedTimeScanService {
    @Autowired
    private BackupPortRepository portRepository;
    @Autowired
    private LmsService lmsService;
    @Autowired
    SearchPortProperties searchPortProperties;

    private final Function<Throwable, RestResult<Page<BackupPort>>> failedToFetchPageableData = throwable -> {
        log.error("Failed to fetch pageable data: {}", throwable);
        return RestResultGenerator.genErrorResult(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                lmsService.getMessage("response.error.fetch.data"));
    };

    private final Function<Throwable, RestResult<List<BackupPort>>> failedToFetchAllData = throwable -> {
        log.error("Failed to fetch all data: {}", throwable);
        return RestResultGenerator.genErrorResult(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                lmsService.getMessage("response.error.fetch.data"));
    };

    @Async
    public CompletableFuture<RestResult<Page<BackupPort>>> fetchPageableData(LinkedMultiValueMap queryMap, Pageable pageable) {
        Page<BackupPort> page;
//        log.info("fetchPageableData: queryMap={}, pageable={}", queryMap, pageable);
        if (null == queryMap || queryMap.size() <= 0) {
            page = fetchPageableDataByPageable(pageable);
        } else {
            // 选择时间段：要么必须同时存在或同时不存在
            if (!CriteriaUtils.hasRightTimeRange(queryMap)) {
                return CompletableFuture.completedFuture(RestResultGenerator
                        .genErrorResult(ErrorCode.CODE_ERROR_TIME_RANGE,
                                lmsService.getMessage("response.error.time.range")));
            }

            // 把时间段整理到需要统计的字段上
            if ((queryMap.containsKey(ParamConstants.START_TIME) || queryMap.containsKey(ParamConstants.END_TIME))) {
                if (!ListUtils.isEmptyOrNull(searchPortProperties.getTimeRange())) {
                    queryMap.put(searchPortProperties.getTimeRange().get(0), Arrays.asList(
                            queryMap.get(ParamConstants.START_TIME).get(0),
                            queryMap.get(ParamConstants.END_TIME).get(0)
                    ));
                }

                // 手动删除startTime和endTime
                queryMap.remove(ParamConstants.START_TIME);
                queryMap.remove(ParamConstants.END_TIME);
            }

            page = fetchPageableDataByParamsAndPage(queryMap, pageable);
        }

        return CompletableFuture.completedFuture(RestResultGenerator.genSuccessResult(
                lmsService.getMessage("response.success.fetch.data"), page))
                .exceptionally(failedToFetchPageableData);
    }

    private Page<BackupPort> fetchPageableDataByPageable(Pageable pageable) {
        return portRepository.findAll(pageable);
    }

    private Page<BackupPort> fetchPageableDataByParamsAndPage(LinkedMultiValueMap queryMap, Pageable pageable) {
        try {
            return portRepository.findAll((root, query, cb) -> {
                final List<Predicate> condList = new LinkedList<>();
                CriteriaUtils.configQueryConditions(cb, root, BackupPort.class, queryMap, searchPortProperties, condList);
                final Predicate[] predicates = new Predicate[condList.size()];
                Predicate predicate = cb.and(condList.toArray(predicates));
//                log.info("111 condList: {}", condList);
                return predicate;
            }, pageable);
        } catch (Exception e) {
            log.error("fetchAllByParamsAndPageable: {}", e);
            return null;
        }
    }

    @Async
    public CompletableFuture<RestResult<List<BackupPort>>> fetchAllData(LinkedMultiValueMap queryMap, Sort sort) {
        List<BackupPort> list;
//        log.info("fetchAllData: queryMap={}, sort={}", queryMap, sort);
        if (null == queryMap || queryMap.size() == 0) {
            list = portRepository.findAll(sort);
        } else {
            // 选择时间段：起始时间和结束时间必须同时存在或同时不存在
            if (!CriteriaUtils.hasRightTimeRange(queryMap)) {
                return CompletableFuture.completedFuture(RestResultGenerator
                        .genErrorResult(ErrorCode.CODE_ERROR_TIME_RANGE,
                                lmsService.getMessage("response.error.time.range")));
            }

            // 把时间段整理到需要统计的字段上
            if ((queryMap.containsKey(ParamConstants.START_TIME) || queryMap.containsKey(ParamConstants.END_TIME))) {
                if (!ListUtils.isEmptyOrNull(searchPortProperties.getTimeRange())) {
                    queryMap.put(searchPortProperties.getTimeRange().get(0), Arrays.asList(
                            queryMap.get(ParamConstants.START_TIME).get(0),
                            queryMap.get(ParamConstants.END_TIME).get(0)
                    ));
                }

                // 手动删除startTime和endTime
                queryMap.remove(ParamConstants.START_TIME);
                queryMap.remove(ParamConstants.END_TIME);
            }

            list = fetchAllDataByParams(queryMap, sort);
        }

        return CompletableFuture.completedFuture(RestResultGenerator.genSuccessResult(
                lmsService.getMessage("response.success.fetch.data"), list))
                .exceptionally(failedToFetchAllData);
    }

    private List<BackupPort> fetchAllDataByParams(LinkedMultiValueMap queryMap, Sort sort) {
        try {
            return portRepository.findAll((root, query, cb) -> {
                final List<Predicate> condList = new LinkedList<>();
                CriteriaUtils.configQueryConditions(cb, root, BackupPort.class, queryMap, searchPortProperties, condList);
                final Predicate[] predicates = new Predicate[condList.size()];
                Predicate predicate = cb.and(condList.toArray(predicates));
//                log.info("222 condList: {}", condList);
                return predicate;
            }, sort);
        } catch (Exception e) {
            log.error("fetchAllData: {}", e);
            return null;
        }
    }
}
