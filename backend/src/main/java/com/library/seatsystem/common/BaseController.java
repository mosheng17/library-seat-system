package com.library.seatsystem.common;

import com.library.seatsystem.entity.User;
import com.library.seatsystem.exception.BusinessException;

/**
 * A 系统基建与权限 —— 公共基础模块。
 *
 * <p>所有 Controller 的基类，统一封装响应结构与登录用户获取，
 * 避免每个控制器重复写 {@code ApiResponse.success(...)}。
 */
public abstract class BaseController {

    /** 成功响应（默认文案）。 */
    protected <T> ApiResponse<T> ok(T data) {
        return ApiResponse.success("操作成功", data);
    }

    /** 成功响应（自定义文案）。 */
    protected <T> ApiResponse<T> ok(String message, T data) {
        return ApiResponse.success(message, data);
    }

    /** 业务失败响应。 */
    protected <T> ApiResponse<T> fail(String message) {
        return ApiResponse.error(400, message);
    }

    /**
     * 获取当前登录用户。
     *
     * <p>当前为简化实现：登录接口返回用户信息，前端在后续请求中携带用户标识；
     * 如需严格鉴权可在此处接入 JWT 解析。未登录时返回 {@code null}。
     */
    protected User getCurrentUser() {
        return null;
    }

    /** 要求已登录，否则抛出业务异常。 */
    protected User requireLogin() {
        User current = getCurrentUser();
        if (current == null) {
            throw new BusinessException("请先登录");
        }
        return current;
    }

    /** 要求管理员角色。 */
    protected void requireAdmin(User user) {
        if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
            throw new BusinessException("需要管理员权限");
        }
    }
}
