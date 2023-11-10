package com.h3c.ywrj.dzkf.hkcustoms;

import com.h3c.ywrj.dzkf.hkcustoms.common.util.ListUtils;
import com.h3c.ywrj.dzkf.hkcustoms.domain.BackupPort;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by wfw2525 on 2019/12/22 15:57
 */
public class BackupPortTest {
    public static void main(String[] args) {
        final List<BackupPort> dbList = dbList();
        final List<BackupPort> scanList = scanList();

        final List<BackupPort> notInDbList = dbList.stream()
                .filter(item -> !scanList.contains(item)).collect(Collectors.toList());
        System.out.println("111: " + notInDbList);
        if (!ListUtils.isEmptyOrNull(notInDbList)) {
            dbList.removeAll(notInDbList);
            // 清空列表对象
            ListUtils.clear(notInDbList);
        }
        System.out.println("222: " + dbList);

        dbList.stream().forEach(port -> {
            // 从扫描到的数据库中获取数据库该条目数据所对应的数据，然后完成状态和上次扫描时间的更新操作
            final BackupPort tmpPort = scanList.get(scanList.indexOf(port));
            port.setLastScannedTime(new Date());
            if (!tmpPort.getState().equalsIgnoreCase(port.getState()))
                port.setState(tmpPort.getState());
            if (!Objects.equals(port.getVersion(), tmpPort.getVersion()))
                port.setVersion(tmpPort.getVersion());
        });
        System.out.println("333: " + dbList);

        final List<BackupPort> insertedList = scanList.stream()
                .filter(item -> !dbList.contains(item)).collect(Collectors.toList());
        System.out.println("444: " + insertedList);
        dbList.addAll(insertedList);
        System.out.println("555: " + dbList);
    }

    private static List<BackupPort> dbList() {
        List<BackupPort> dbList = new ArrayList<>();
        final Date date = new Date();
        dbList.add(new BackupPort(1, "1.1.1.1", 30, "tcp", "open",
                date, null, "AA", null));
        dbList.add(new BackupPort(3, "1.1.1.1", 40, "tcp", "open",
                date, null, "BB", null));
        dbList.add(new BackupPort(4, "1.1.1.2", 50, "tcp", "open",
                date, null, "AA", null));
        dbList.add(new BackupPort(8, "1.1.1.3", 60, "tcp", "open|filtered",
                date, null, "CC", null));
        return dbList;
    }

    private static List<BackupPort> scanList() {
        List<BackupPort> scanList = new ArrayList<>();
        final Date date = new Date();
        scanList.add(new BackupPort("1.1.1.1", 40, "tcp", "open|filtered", "BB"));
        scanList.add(new BackupPort("1.1.1.2", 50, "tcp", "open", "AA"));
        final BackupPort port = new BackupPort("1.1.1.3", 60, "tcp", "open", "CC");
        port.setVersion("V1.1");
        scanList.add(port);
        scanList.add(new BackupPort("1.1.1.4", 30, "tcp", "open", "AA"));
        return scanList;
    }
}
