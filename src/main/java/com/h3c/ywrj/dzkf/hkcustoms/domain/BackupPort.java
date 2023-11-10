package com.h3c.ywrj.dzkf.hkcustoms.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;

import static com.h3c.ywrj.dzkf.hkcustoms.common.util.DateUtils.DATE_FORMAT_2;
import static com.h3c.ywrj.dzkf.hkcustoms.common.util.DateUtils.TIME_ZONE;

/**
 * 通过备份的IP地址定时扫描处于开发状态的端口数据，并备份在数据库中
 * <p>
 * Created by @author Jeff on 2019-12-17 10:18
 */
@Data
@Entity
@Table(name = "tbl_backup_port")
@JsonIgnoreProperties(value = {"lastScannedTime", "version"})
public class BackupPort {
    @Id
    @Column(name = "ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotNull
    @Column(nullable = false)
    private String ip;
    @NotNull
    @Column(nullable = false)
    private int port;
    /**
     * 协议名称，如tcp、udp
     */
    @NotNull
    @Column(nullable = false)
    private String protocol;
    /**
     * open(开放的)， closed(关闭的)，filtered(被过滤的)， unfiltered(未被过滤的)，
     * open|filtered(开放或者被过滤的)， 或者 closed|filtered(关闭或者被过滤的)
     */
    @NotNull
    @Column(nullable = false)
    private String state;
    /**
     * 第一次扫描时间，定时扫描过程中遇到一样的数据时不会更新
     */
    @JsonFormat(pattern = DATE_FORMAT_2, timezone = TIME_ZONE)
    private Date createdTime;
    @JsonFormat(pattern = DATE_FORMAT_2, timezone = TIME_ZONE)
    /** 上次扫描时间 */
    private Date lastScannedTime;
    @NotNull
    @Column(nullable = false)
    /** 服务名称 */
    private String service;
    /**
     * 服务对应的版本
     */
    private String version;

    public BackupPort() {
    }

    public BackupPort(@NotNull String ip, @NotNull int port, @NotNull String protocol, @NotNull String state, @NotNull String service) {
        this.ip = ip;
        this.port = port;
        this.protocol = protocol;
        this.state = state;
        this.service = service;
    }

    public BackupPort(long id, @NotNull String ip, @NotNull int port, @NotNull String protocol, @NotNull String state, Date createdTime, Date lastScannedTime, @NotNull String service, String version) {
        this.id = id;
        this.ip = ip;
        this.port = port;
        this.protocol = protocol;
        this.state = state;
        this.createdTime = createdTime;
        this.lastScannedTime = lastScannedTime;
        this.service = service;
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BackupPort that = (BackupPort) o;
        return port == that.port &&
                Objects.equals(ip, that.ip) &&
                Objects.equals(protocol, that.protocol) &&
                Objects.equals(service, that.service);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, port, protocol, service);
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", protocol='" + protocol + '\'' +
                ", state='" + state + '\'' +
                ", createdTime=" + createdTime +
                ", lastScannedTime=" + lastScannedTime +
                ", service='" + service + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
