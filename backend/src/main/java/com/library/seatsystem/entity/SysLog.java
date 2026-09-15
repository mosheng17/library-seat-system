package com.library.seatsystem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * A 系统基建与权限 —— 操作日志表（sys_log）。
 *
 * <p>记录用户的关键操作，供管理员查询与审计。
 */
@Entity
@Table(name = "sys_log")
public class SysLog extends BaseEntity {

    @Column(name = "user_id")
/** 操作用户 ID */
    private Long userId;

    @Column(nullable = false, length = 100)
/** 操作内容 */
    private String operation;

    @Column(length = 50)
/** 所属模块 */
    private String module;

    @Column(name = "log_time", nullable = false)
    private LocalDateTime time = LocalDateTime.now();

    @Column(length = 50)
/** 来源 IP */
    private String ip;

    public SysLog() {
    }

    public SysLog(Long userId, String operation, String module) {
        this.userId = userId;
        this.operation = operation;
        this.module = module;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
