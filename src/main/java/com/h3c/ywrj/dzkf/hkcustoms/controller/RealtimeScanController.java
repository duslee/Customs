package com.h3c.ywrj.dzkf.hkcustoms.controller;

import com.h3c.ywrj.dzkf.hkcustoms.common.RestResult;
import com.h3c.ywrj.dzkf.hkcustoms.domain.BackupPort;
import com.h3c.ywrj.dzkf.hkcustoms.service.RealtimeScanService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Created by @author Jeff on 2019-12-17 11:32
 */
@RestController
@RequestMapping("api/v1/real-time")
@ComponentScan(basePackages = "com.h3c.ywrj.dzkf.hkcustoms")
@Api(tags = "实时扫描管理")
public class RealtimeScanController {
    @Autowired
    private RealtimeScanService rsService;

    /**
     * 根据提供的IP（单一IP地址、IP段、全段3种方式）进行扫描
     */
    @ApiOperation(value = "根据IP地址实时扫描", notes = "支持单一IP地址（如192.168.1.3）、IP段（如192.168.1.3-5）、全段（如192.168.1.*）3种方式")
    @ApiImplicitParam(name = "ips", value = "待扫描IP地址", dataType = "string", paramType = "query", required = true, example = "192.168.1.1")
    @GetMapping(value = "/scan")
    @ResponseBody
    public CompletableFuture<RestResult<List<BackupPort>>> scanPorts(@NotNull String ips) {
        return rsService.scanPorts(ips, true);
    }
}
