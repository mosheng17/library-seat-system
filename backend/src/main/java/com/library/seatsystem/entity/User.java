package com.library.seatsystem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * A 系统基建与权限 —— 用户实体（users 表）。
 *
 * <p>保存登录账号、密码、角色与真实姓名，学生与管理员共用一张表，以 role 区分。
 */
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
/** 登录账号（唯一） */
    private String username;

    @Column(nullable = false, length = 100)
/** 密码（教学演示用明文存储） */
    private String password;

    @Column(nullable = false, length = 20)
/** 角色：STUDENT / TEACHER / ADMIN */
    private String role;

    @Column(nullable = false, length = 50)
/** 真实姓名 */
    private String realName;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }
}
