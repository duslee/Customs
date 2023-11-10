package com.h3c.ywrj.dzkf.hkcustoms.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

import static com.h3c.ywrj.dzkf.hkcustoms.common.util.DateUtils.DATE_FORMAT_2;
import static com.h3c.ywrj.dzkf.hkcustoms.common.util.DateUtils.TIME_ZONE;

/**
 * 每次实时扫描的IP地址备份在数据库（有获取到数据的情况下）；也会用于定时扫描获取数据
 * <p>
 * Created by @author Jeff on 2019-12-17 10:01
 */
@Data
@Entity
@EqualsAndHashCode(exclude = {"id"})
@Table(name = "tbl_backup_ip", uniqueConstraints = @UniqueConstraint(columnNames = {"ip"}))
public class BackupIp {
    @Id
    @Column(name = "ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotNull
    @Column(nullable = false)
    private String ip;
    @JsonFormat(pattern = DATE_FORMAT_2, timezone = TIME_ZONE)
    /** 第一次扫描时间，定时扫描过程中遇到一样的数据时不会更新 */
    private Date createdTime;
    @JsonFormat(pattern = DATE_FORMAT_2, timezone = TIME_ZONE)
    /** 上次扫描时间 */
    private Date lastScannedTime;
    @JsonIgnore
    /** 扫描次数 */
    private long scannedCount;

    public BackupIp() {
    }

    public BackupIp(@NotNull String ip) {
        this.ip = ip;
    }

    public BackupIp(@NotNull String ip, Date createdTime) {
        this.ip = ip;
        this.createdTime = createdTime;
    }

    public BackupIp(long id, @NotNull String ip, Date createdTime, Date lastScannedTime, long scannedCount) {
        this.id = id;
        this.ip = ip;
        this.createdTime = createdTime;
        this.lastScannedTime = lastScannedTime;
        this.scannedCount = scannedCount;
    }
}
