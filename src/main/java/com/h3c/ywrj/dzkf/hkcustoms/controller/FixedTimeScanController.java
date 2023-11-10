package com.h3c.ywrj.dzkf.hkcustoms.controller;

import com.h3c.ywrj.dzkf.hkcustoms.common.RestResult;
import com.h3c.ywrj.dzkf.hkcustoms.domain.BackupPort;
import com.h3c.ywrj.dzkf.hkcustoms.service.FixedTimeScanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Created by @author wfw2525 on 2019/12/23 11:53
 */
@RestController
@RequestMapping("api/v1/fixed-time")
@ComponentScan(basePackages = "com.h3c.ywrj.dzkf.hkcustoms")
@Api(tags = "定时扫描管理")
public class FixedTimeScanController {
    @Autowired
    private FixedTimeScanService ftsService;

    @ApiOperation(value = "根据给定的参数去定时库里查询端口数据", notes = "查询端口数据，" +
            "支持根据IP地址、协议名称、服务名称模糊搜索，ID、端口号精确搜索，创建时间段搜索等；" +
            "支持根据对应字段进行排序")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "queryMap", paramType = "body", dataTypeClass = LinkedMultiValueMap.class
                    , value = "查询条件, 如ID：{\"id\":[1,2,3]}；IP地址：{\"ip\":[\"1.1.\"]}；端口号：{\"port\":[54]}；" +
                    "协议名称：{\"protocol\":[\"tcp\"]}；服务名称：{\"service\":[\"mysql\"]}；" +
                    "创建时间段：{\"startTime\":[\"2019/12/20 12:00:00\"],\"endTime\":[\"2019/12/25 15:00:00\"]}；或上述组合",
                    example = "{'id':[1,2,3],'ip':['1.1.']}"
            ),
            @ApiImplicitParam(name = "page", value = "页码", dataType = "integer", paramType = "query", example = "0"),
            @ApiImplicitParam(name = "size", value = "页数", dataType = "integer", paramType = "query", example = "10"),
            @ApiImplicitParam(name = "sort", value = "排序，如使用ip或ip,DESC等格式，支持换行；  " +
                    "另外排序关键字支持id、ip、port、protocol、tcp、service、createdTime等",
                    dataType = "string", paramType = "query", allowMultiple = true, example = "port,desc")
    })
    @PostMapping(value = "/search")
    public CompletableFuture<RestResult<Page<BackupPort>>> fetchPageableData(
            @RequestBody(required = false) LinkedMultiValueMap queryMap,
            @PageableDefault(sort = "ip", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ftsService.fetchPageableData(queryMap, pageable);
    }

    @ApiOperation(value = "获取全部的端口数据", notes = "获取全部的端口数据，" +
            "支持根据IP地址、服务名称、协议名称模糊搜索，ID、端口号精确搜索，创建时间端搜索等；" +
            "支持根据对应字段进行排序")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "queryMap", value = "查询条件, 如ID：{\"id\":[1,2,3]}；IP地址：{\"ip\":[\"1.1.\"]}；端口号：{\"port\":[54]}；" +
                    "协议名称：{\"protocol\":[\"tcp\"]}；服务名称：{\"service\":[\"mysql\"]}；" +
                    "创建时间段：{\"startTime\":[\"2019/12/20 12:00:00\"],\"endTime\":[\"2019/12/25 15:00:00\"]}；或上述组合",
                    dataTypeClass = LinkedMultiValueMap.class, paramType = "body", example = "{'id':[1,2,3],'ip':['1.1.']}"),
            @ApiImplicitParam(name = "sort", value = "排序，如使用ip或ip,DESC等格式，支持换行；  " +
                    "另外排序关键字支持id、ip、port、protocol、tcp、service、createdTime等",
                    dataType = "string", paramType = "query", allowMultiple = true)
    })
    @PostMapping(value = "/searchAll")
    public CompletableFuture<RestResult<List<BackupPort>>> fetchAllData(
            @RequestBody(required = false) LinkedMultiValueMap queryMap,
            @SortDefault(sort = "ip", direction = Sort.Direction.ASC) Sort sort) {
        return ftsService.fetchAllData(queryMap, sort);
    }
}
