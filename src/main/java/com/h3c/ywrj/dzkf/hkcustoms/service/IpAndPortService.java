package com.h3c.ywrj.dzkf.hkcustoms.service;

import com.h3c.ywrj.dzkf.hkcustoms.common.util.ListUtils;
import com.h3c.ywrj.dzkf.hkcustoms.domain.BackupIp;
import com.h3c.ywrj.dzkf.hkcustoms.domain.BackupPort;
import com.h3c.ywrj.dzkf.hkcustoms.repository.BackupIpRepository;
import com.h3c.ywrj.dzkf.hkcustoms.repository.BackupPortRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by @author Jeff on 2019-12-17 11:41
 */
@Service
@ComponentScan(basePackages = "com.h3c.ywrj.dzkf.hkcustoms")
@Slf4j
public class IpAndPortService {
    @Autowired
    private BackupIpRepository ipRepository;
    @Autowired
    private BackupPortRepository portRepository;

    @Async
    public void saveOrUpdateIps(List<String> ips) {
//        log.info("saveOrUpdateIps ips: {}", ips);
        // 查询IP列表中已存放在数据库中的IP数据
        final List<BackupIp> existedIpList = ipRepository.findExistedList(ips);
        final Date date = new Date();

        // 判断数据库中是否存在，若不存在（为空），则将不存在的IP地址全部存放到数据库中
        // 若存在（不为空），则将存在的IP地址更新（扫描时间），不存在的IP地址存放到数据库中
        List<BackupIp> list;
        if (ListUtils.isEmptyOrNull(existedIpList))
            list = ips.stream().map(ip -> new BackupIp(ip, date)).collect(Collectors.toList());
        else {
            // 将不存在数据库中的IP地址整理出来
            final List<String> tmpIps = existedIpList.stream().filter(item -> !ips.contains(item.getIp()))
                    .map(item -> item.getIp()).collect(Collectors.toList());

            // 更新数据库中存在的IP地址数据，新建数据库中不存在的IP地址数据
            list = tmpIps.stream().map(ip -> new BackupIp(ip, date)).collect(Collectors.toList());
            list.addAll(existedIpList.stream().map(item -> {
                item.setLastScannedTime(date);
                // TODO: 2019/12/22 可能存在Bug，如两个子线程同时扫描同一个IP地址，同时执行加1操作
                item.setScannedCount(item.getScannedCount() + 1);
                return item;
            }).collect(Collectors.toList()));

            // 清空列表对象
            ListUtils.clear(tmpIps);
        }

        // 备份或更新IP地址数据
        ipRepository.saveAll(list);

        // 清空列表对象
        ListUtils.clear(ips);
        ListUtils.clear(existedIpList, list);
    }

    @Async
    public boolean isIpExist(String ip) {
        return false;
    }

    @Async
    public void updatePorts(String ip, List<BackupPort> scanList) {
        // TODO: 2019/12/25 先保留，等待超期的时候自动清空
        // 如果定时扫描到的端口数据为空，说明数据库的数据都应该被清空
        if (ListUtils.isEmptyOrNull(scanList)) {
            // 已测试OK
//            portRepository.deleteByIp(ip);
            return;
        }

        // 1. 从数据库中获取根据指定的IP获取全部的端口数据
        final List<BackupPort> dbList = portRepository.findExistedListById(ip);
        if (ListUtils.isEmptyOrNull(dbList)) {
            // 将这次扫描到的端口数据全部插入数据库中
            portRepository.saveAll(scanList);
            return;
        }

        // 2. 数据对比（和扫描到的端口数据进行对比），数据库中存在的进行更新操作（状态、上次扫描时间、版本），
        //      数据库中不存在的则插入数据库中。注意：若扫描到的数据中存在，而数据库中不存在，一定要删除
        // 2.1 先删除数据库中的数据在扫描到的数据中不存在的情况下的数据
        final List<BackupPort> notInDbList = dbList.stream()
                .filter(item -> !scanList.contains(item)).collect(Collectors.toList());

        // 2.2 从数据库中的数据移除上一步筛选出来的数据
        if (!ListUtils.isEmptyOrNull(notInDbList)) {
            dbList.removeAll(notInDbList);
            // 删除数据库中此次未被扫描到的数据
            portRepository.deleteAll(notInDbList);
            // 清空列表对象
            ListUtils.clear(notInDbList);
        }

        // 2.3 更新移除操作后的数据库中数据
        dbList.stream().forEach(port -> {
            // 从扫描到的数据库中获取数据库该条目数据所对应的数据，然后完成状态和上次扫描时间的更新操作
            final BackupPort tmpPort = scanList.get(scanList.indexOf(port));
            port.setLastScannedTime(new Date());
            if (!tmpPort.getState().equalsIgnoreCase(port.getState()))
                port.setState(tmpPort.getState());
            if (!Objects.equals(port.getVersion(), tmpPort.getVersion()))
                port.setVersion(tmpPort.getVersion());
        });

        // 2.4 剩下的portList中的数据都是新增的（数据库中不存在的），接下来完成数据的备份更新操作
        final List<BackupPort> insertedList = scanList.stream()
                .filter(item -> !dbList.contains(item)).collect(Collectors.toList());
        dbList.addAll(insertedList);
        portRepository.saveAll(dbList);

        // 清空列表对象
        ListUtils.clear(insertedList, dbList);
    }
}
