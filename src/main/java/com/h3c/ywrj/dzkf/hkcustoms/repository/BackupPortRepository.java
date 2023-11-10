package com.h3c.ywrj.dzkf.hkcustoms.repository;

import com.h3c.ywrj.dzkf.hkcustoms.domain.BackupPort;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by @author Jeff on 2019-12-17 11:49
 */
@Repository
@Transactional
@ComponentScan(basePackages = "com.h3c.ywrj.dzkf.hkcustoms")
public interface BackupPortRepository extends JpaRepository<BackupPort, Long>, JpaSpecificationExecutor<BackupPort> {
    /**
     * 根据指定的IP地址获取数据库中全部的端口数据列表
     */
    @Query("select distinct p from BackupPort p where p.ip = ?1")
    List<BackupPort> findExistedListById(String ip);

    /**
     * 根据配置文件中配置的超期时间来删除超期的端口数据
     */
    @Modifying
    @Query("delete from BackupPort p where TO_DAYS(NOW()) - TO_DAYS(p.createdTime) > ?1")
    void deleteExpiredPort(Long expiredDay);

    /**
     * 根据指定的IP地址删除对应的端口数据
     */
    @Modifying
    @Query("delete from BackupPort p where p.ip = ?1")
    void deleteByIp(String ip);
}
