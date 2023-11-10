package com.h3c.ywrj.dzkf.hkcustoms.repository;

import com.h3c.ywrj.dzkf.hkcustoms.domain.BackupIp;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by @author Jeff on 2019-12-17 10:49
 */
@Repository
@Transactional
@ComponentScan(basePackages = "com.h3c.ywrj.dzkf.hkcustoms")
public interface BackupIpRepository extends JpaRepository<BackupIp, Long> {
    /**
     * 根据指定的IP列表查询数据库是否存在，返回已存在的IP列表
     */
    @Query("select distinct t from BackupIp t where t.ip in ?1")
    List<BackupIp> findExistedList(List<String> ips);

    /**
     * 从数据库中获取全部的IP列表
     */
    @Query("select distinct t.ip from BackupIp t")
    List<String> findExistedIps();
}
