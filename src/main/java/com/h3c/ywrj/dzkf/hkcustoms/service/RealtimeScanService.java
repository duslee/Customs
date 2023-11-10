package com.h3c.ywrj.dzkf.hkcustoms.service;

import com.h3c.ywrj.dzkf.hkcustoms.common.ErrorCode;
import com.h3c.ywrj.dzkf.hkcustoms.common.RestResult;
import com.h3c.ywrj.dzkf.hkcustoms.common.RestResultGenerator;
import com.h3c.ywrj.dzkf.hkcustoms.common.util.IpUtils;
import com.h3c.ywrj.dzkf.hkcustoms.common.util.ListUtils;
import com.h3c.ywrj.dzkf.hkcustoms.common.util.RegexUtils;
import com.h3c.ywrj.dzkf.hkcustoms.domain.BackupPort;
import com.h3c.ywrj.dzkf.hkcustoms.repository.BackupIpRepository;
import com.h3c.ywrj.dzkf.hkcustoms.repository.BackupPortRepository;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

/**
 * Created by @author Jeff on 2019-12-17 11:48
 */
@Service
@ComponentScan(basePackages = "com.h3c.ywrj.dzkf.hkcustoms")
@Slf4j
public class RealtimeScanService {
    /**
     * 在Windows系统下nmap.exe所在目录
     */
    @Value("${nmap.dir.windows}")
    private String nmapDir1;
    /**
     * 在非Windows系统下nmap命令所在目录
     */
    @Value("${nmap.dir.other}")
    private String nmapDir2;
    /**
     * 时间模板参数
     */
    @Value("${nmap.param.time}")
    private String timeParam;
    /**
     * 其它参数
     */
    @Value("${nmap.param.other}")
    private String otherParam;
    /**
     * 一次扫描最多支持的IP地址数
     */
    @Value("${nmap.scan.max}")
    private int scanMax;
    /**
     * 一次扫描最多支持的线程数，多余的排队等待
     */
    @Value("${nmap.scan.thread}")
    private int maxThread;

    @Autowired
    private BackupIpRepository ipRepository;
    @Autowired
    private BackupPortRepository portRepository;
    @Autowired
    private LmsService lmsService;
    @Autowired
    private IpAndPortService ipAndPortService;

    private final Function<Throwable, RestResult<List<BackupPort>>> failedToFetchData = throwable -> {
        log.error("Failed to fetch data: {}", throwable);
        return RestResultGenerator.genErrorResult(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                lmsService.getMessage("response.error.fetch.data"));
    };

    @Async
    public CompletableFuture<RestResult<List<BackupPort>>> scanPorts(@NotNull String ips, boolean realtime) {
        // 1. 检查IP地址是否有效
        final int ipType = IpUtils.checkIpType(ips);
        if (0 == ipType) {
            return CompletableFuture.completedFuture(
                    RestResultGenerator.genErrorResult(ErrorCode.CODE_ERROR_IP_FORMAT,
                            lmsService.getMessage("response.error.ip.format")));
        }

        // 2. 根据IP类型来判断
        // 2.1 若为单一IP地址，单独去扫描
        // 2.2 若为某段或全段IP地址，可根据配置文件分配的子线程最多扫描IP数目来分组
        if (1 == ipType) {
            return CompletableFuture.completedFuture(RestResultGenerator.genSuccessResult(
                    lmsService.getMessage("response.success.fetch.data"), doScan(ips, realtime)))
                    .exceptionally(failedToFetchData);
        } else {
            final List<String> scanList = new ArrayList<>();
            // 获取IP地址的前三位
            final String _13 = ips.substring(0, ips.lastIndexOf(".") + 1);

            int start = 0;
            if (2 == ipType) {
                // 将全段IP地址根据scanMax平分
                for (int i = 0; i < 256 / scanMax; i++) {
                    scanList.add(_13 + start + "-" + (start + scanMax - 1));
                    start += scanMax;
                }
            } else {
                // 获取IP地址的后面的起始和结束位置
                final String[] arr = ips.substring(ips.lastIndexOf(".") + 1).split("-");

                // 获取起始和结束对应的数字
                start = Integer.parseInt(arr[0]);
                final int end = Integer.parseInt(arr[1]);

                // 将IP端地址根据scanMax平分
                int tmpEnd;
                while (start <= end) {
                    // 注意只有一个的特殊情况
                    if (start == end) {
                        scanList.add(_13 + start);
                        break;
                    } else {
                        tmpEnd = start + scanMax - 1;
                        scanList.add(_13 + start + "-" + (tmpEnd <= end ? tmpEnd : end));
                        start += scanMax;
                    }
                }
            }

            final List<BackupPort> rList = new ArrayList<>();
            // 减少线程池的线程个数，保证最多30个(以配置文件中的配置值为准）
            final int size = scanList.size();
            final ExecutorService executor = Executors.newFixedThreadPool(size > maxThread ? maxThread : size);

            // 全流式处理list，执行并发任务
            final CompletableFuture[] cfs = scanList.stream().map(ip ->
                    CompletableFuture.supplyAsync(() -> doScan(ip, realtime), executor)
                            .whenComplete((_list, e) -> {
//                                log.info("Has already received data: {}", _list);
                                if (!ListUtils.isEmptyOrNull(_list)) {
                                    rList.addAll(_list);
                                }
                            })).toArray(CompletableFuture[]::new);
            // 等待所有任务都完成
            CompletableFuture.allOf(cfs).join();
            executor.shutdown();

            return CompletableFuture.completedFuture(RestResultGenerator.genSuccessResult(
                    lmsService.getMessage("response.success.fetch.data"), rList))
                    .exceptionally(failedToFetchData);
        }
    }

    /**
     * 根据给定的IP地址通过nmap工具进行扫描
     */
    public List<BackupPort> doScan(@NotNull String ip, boolean realtime) {
        log.info("Start scanning ip: {}", ip);
        Process process = null;
        List<BackupPort> portList = null;
        List<String> ipList = null;

        try {
            process = Runtime.getRuntime().exec(nmapCmd() + " " + ip);
            @Cleanup
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
            String line;
            // true表示开始解析数据
            // 若一行出现“Nmap scan report”内容，表示已读取到数据，可以设置startParsing为true
            // 若读取到空白一行，可以设置startParsing为false，表示数去读取完毕
            boolean startParsing = false;
            // 存放当前正解析数据的IP地址
            String tmpIp = null;
            String[] tmpArr;
            String[] tmpPp;
            BackupPort tmpBackupPort;
            Date date;

            // 获取每行数据进行解析
            while ((line = reader.readLine()) != null) {
                // 先确认startParsing是否为true，若是，表示正在解析数据，
                // 否则继续寻找是否含有“Nmap scan report”内容
                if (!startParsing) {
                    // 判断是否有“Nmap scan report”内容，若有，设置startParsing为true
                    // 否则跳到下一行
                    if (line.contains("Nmap scan report")) {
                        if (null == portList) {
                            portList = new ArrayList<>();
                            ipList = new ArrayList<>();
                        }

                        startParsing = true;
                        // 解析出读取端口数据的IP地址
                        tmpIp = line.substring(line.lastIndexOf(" ") + 1);
                        if (tmpIp.startsWith("(")) {
                            tmpIp = tmpIp.substring(1);
                        }
                        if (tmpIp.endsWith(")")) {
                            tmpIp = tmpIp.substring(0, tmpIp.length() - 1);
                        }
//                        log.info("111 tmpIp: {}", tmpIp);
                        // 这时候应该可以确定该IP地址是有获取到开放端口数据的，可以执行备份操作
                        ipList.add(tmpIp);
                    }
                    continue;
                } else {
                    // 如果读取到一行空的内容，表示端口已读取完毕
                    if (StringUtils.isEmpty(line)) {
                        startParsing = false;
                        tmpIp = null;
//                        log.info("222 portList: {}", portList);
                    } else if (RegexUtils.hasNmapPort(line)) {
                        date = new Date();
                        // 解析端口数据，格式可参考“3306/tcp open  mysql”
                        tmpArr = RegexUtils.split(line);
                        // 将数据备份到列表中
                        tmpPp = tmpArr[0].split("/");

                        tmpBackupPort = new BackupPort(tmpIp, Integer.parseInt(tmpPp[0]),
                                tmpPp[1], tmpArr[1], tmpArr[2]);
                        tmpBackupPort.setCreatedTime(date);
                        portList.add(tmpBackupPort);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != process) {
                try {
                    process.waitFor();
                    process.destroy();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        if (realtime) {
            // 只有实时扫描操作时，才存在备份IP地址操作
            if (!ListUtils.isEmptyOrNull(ipList)) {
                saveIps(ipList);
            }
        } else {
            // 定时扫描操作时，需更新扫描到的端口数据信息
            updatePorts(ip, portList);
        }
        return portList;
    }

    private void saveIps(List<String> ipList) {
        ipAndPortService.saveOrUpdateIps(ipList);
    }

    private void updatePorts(@NotNull String ip, List<BackupPort> portList) {
        ipAndPortService.updatePorts(ip, portList);
    }

    /**
     * 根据当前运行系统来拼装nmap命令（包括参数）
     */
    private String nmapCmd() {
        final String os = System.getProperties().getProperty("os.name");
        final StringBuilder sb = new StringBuilder();
        sb.append(os.toUpperCase().contains("WINDOWS") ? nmapDir1 : nmapDir2);
        // 判断时间参数是否为空，若不为空，追加到命令中
        if (!StringUtils.isEmpty(timeParam)) {
            sb.append(" ");
            sb.append(timeParam);
        }
        // 判断其它参数是否为空，若不为空，追加到命令中
        if (!StringUtils.isEmpty(otherParam)) {
            sb.append(" ");
            sb.append(otherParam);
        }

        final String nmapCmd = sb.toString();
//        log.info("nmap cmd: {}.", nmapCmd);
        return nmapCmd;
    }
}
