package com.library.seatsystem.exception;

/**
 * A 系统基建与权限 —— 业务异常。
 *
 * <p>用于表达"可以预期的业务失败"（如座位冲突、账号不存在），由全局异常处理器统一转成响应。
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}

