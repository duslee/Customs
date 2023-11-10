package com.h3c.ywrj.dzkf.hkcustoms.service;

import com.h3c.ywrj.dzkf.hkcustoms.common.util.ListUtils;
import com.h3c.ywrj.dzkf.hkcustoms.repository.BackupIpRepository;
import com.h3c.ywrj.dzkf.hkcustoms.repository.BackupPortRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by @author Jeff on 2019-12-18 23:38
 */
@Service
@ComponentScan(basePackages = "com.h3c.ywrj.dzkf.hkcustoms")
@Slf4j
public class ScheduleService {
    @Value("${nmap.scan.thread}")
    private int maxThread;
    @Value("${nmap.scan.expired}")
    private long expiredDay;

    @Autowired
    private BackupIpRepository ipRepository;
    @Autowired
    private BackupPortRepository bpRepository;
    @Autowired
    private RealtimeScanService rsService;

    @Async
    @Scheduled(cron = "${fixed-time.cron}")
    public void scanAtFixedTime() {
        // 1. 从IP库中读出全部的IP数据
        final List<String> ips = ipRepository.findExistedIps();
        if (ListUtils.isEmptyOrNull(ips)) {
            return;
        }

        // 2. 使用nmap工具实时扫描全部的IP地址并删除历史库中的超期数据
        // 减少线程池的线程个数，保证最多30个(以配置文件中的配置值为准）
        final int size = ips.size();
        final ExecutorService executor = Executors.newFixedThreadPool(size > maxThread ? maxThread : size);

        // 全流式处理list，执行并发任务
        final CompletableFuture[] cfs = ips.stream().map(ip ->
                CompletableFuture.supplyAsync(() -> rsService.doScan(ip, false), executor)).toArray(CompletableFuture[]::new);
        // 等待所有任务都完成
        CompletableFuture.allOf(cfs).join();
        executor.shutdown();

        // 3. 删除超期数据
        bpRepository.deleteExpiredPort(expiredDay);
    }
}
