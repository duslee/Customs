package com.h3c.ywrj.dzkf.hkcustoms.controller;

import com.h3c.ywrj.dzkf.hkcustoms.common.ErrorCode;
import com.h3c.ywrj.dzkf.hkcustoms.common.RestResult;
import com.h3c.ywrj.dzkf.hkcustoms.common.RestResultGenerator;
import com.h3c.ywrj.dzkf.hkcustoms.service.ImportExportService;
import com.h3c.ywrj.dzkf.hkcustoms.service.LmsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.util.concurrent.CompletableFuture;

/**
 * Created by @author Jeff on 2019-12-17 11:13
 */
@RestController
@RequestMapping("api/v1/reports")
@ComponentScan(basePackages = "com.h3c.ywrj.dzkf.hkcustoms")
@Api(tags = "报表管理（导入导出等）")
@Slf4j
public class ReportsController {
    @Autowired
    private ImportExportService ieService;
    @Autowired
    private LmsService ls;

    @ApiOperation(value = "判断Excel格式是否符合要求", notes = "根据给定的文件判断文件名后缀是否符合Excel格式要求（.xls或.xlsx后缀名）")
    @PostMapping(value = "/checkExcel")
    public CompletableFuture<RestResult<Boolean>> checkExcel(@NotNull @ApiParam(value = "待检查Excel文件", required = true) @RequestParam("file") MultipartFile file) {
        if (ieService.checkExcel(file.getOriginalFilename()))
            return CompletableFuture.completedFuture(RestResultGenerator.genSuccessResult(
                    ls.getMessage("response.right.excel.format")
            ));
        else return CompletableFuture.completedFuture(RestResultGenerator.genErrorResult(
                ErrorCode.CODE_ERROR_EXCEL_FORMAT, ls.getMessage("response.error.excel.format")
        ));
    }

    @ApiOperation(value = "导出全部端口数据的Excel文件", notes = "根据给定的全部端口数据导出Excel文件")
//    @RequestMapping(value = "/allPortExcel", method = {RequestMethod.GET, RequestMethod.POST})
    @PostMapping(value = "/allPortExcel")
    public void exportAllPortExcel(HttpServletRequest request, HttpServletResponse response) {
        ieService.exportAllPortExcel(request, response);
    }

    @ApiOperation(value = "导出所选端口数据的Excel文件", notes = "根据给定的端口数据导出Excel文件")
    @ApiImplicitParam(name = "ports", value = "导出的端口数据（JSON字符串）", paramType = "body", dataType = "string", required = true)
//    @RequestMapping(value = "/portExcel", method = {RequestMethod.GET, RequestMethod.POST})
    @PostMapping(value = "/portExcel")
    public void exportPortExcel(@NotNull @RequestBody String ports, HttpServletRequest request, HttpServletResponse response) {
        ieService.exportPortExcel(ports, request, response);
    }

    @ApiOperation(value = "导出端口报表的Excel模板", notes = "导出端口报表的Excel模板文件")
//    @RequestMapping(value = "/portTemplate", method = {RequestMethod.GET, RequestMethod.POST})
    @PostMapping(value = "/portTemplate")
    public void exportPortTemplate(HttpServletRequest request, HttpServletResponse response) {
        ieService.exportPortTemplate(request, response);
    }
}