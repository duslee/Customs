package com.h3c.ywrj.dzkf.hkcustoms.service;

import com.h3c.ywrj.dzkf.hkcustoms.common.prop.ReportPortProperties;
import com.h3c.ywrj.dzkf.hkcustoms.common.util.Excel2007Utils;
import com.h3c.ywrj.dzkf.hkcustoms.common.util.GsonUtils;
import com.h3c.ywrj.dzkf.hkcustoms.domain.BackupPort;
import com.h3c.ywrj.dzkf.hkcustoms.repository.BackupPortRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by @author Jeff on 2019-12-19 23:47
 */
@Service
@ComponentScan(basePackages = "com.h3c.ywrj.dzkf.hkcustoms")
@EnableConfigurationProperties(ReportPortProperties.class)
@Slf4j
public class ImportExportService {
    /**
     * 导出Excel文件的表头信息：序号、IP地址、端口号、协议、服务，创建时间可选，上次扫描时间、扫描次数、版本暂不用支持
     */
    @Autowired
    private ReportPortProperties reportPortProperties;
    @Autowired
    private BackupPortRepository portRepository;

    /**
     * 检查文件名是否符合Excel格式要求
     */
    public boolean checkExcel(String fileName) {
        return Excel2007Utils.isValidExcel(fileName);
    }

    /**
     * 将全部的端口数据导出到Excel中
     */
    public void exportAllPortExcel(HttpServletRequest request, HttpServletResponse response) {
        final List<BackupPort> portList = portRepository.findAll();
        Excel2007Utils.export2007Excel(1, false, portList, reportPortProperties, request, response);
    }

    /**
     * 将选定的端口数据导出到Excel中
     */
    public void exportPortExcel(String ports, HttpServletRequest request, HttpServletResponse response) {
//        log.info("export ports: {}", ports);
        final List<BackupPort> portList = GsonUtils.fromJsonArray(ports, BackupPort.class);
        Excel2007Utils.export2007Excel(1, false, portList, reportPortProperties, request, response);
    }

    /**
     * 导出端口数据模板文件
     */
    public void exportPortTemplate(HttpServletRequest request, HttpServletResponse response) {
        Excel2007Utils.export2007Excel(1, true, null, reportPortProperties, request, response);
    }
}
