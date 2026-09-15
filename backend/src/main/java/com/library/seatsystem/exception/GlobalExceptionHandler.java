package com.library.seatsystem.exception;

import com.library.seatsystem.common.ApiResponse;
import java.util.stream.Collectors;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * A 系统基建与权限 —— 全局异常处理器。
 *
 * <p>把业务异常与未预期异常统一转换为 {@link com.library.seatsystem.common.ApiResponse}，
 * 保证前端拿到的永远是 {@code code / message / data} 三段式结构。
 *
 * <p>改进点：改进前只覆盖 {@code BusinessException} 与 {@code MethodArgumentNotValidException}
 * 两类，且校验失败时只取 {@code getFieldError()} 的第一条，
 * 表单有多个字段出错时前端只能看到其中一个。现在：
 * <ol>
 *   <li>校验失败返回全部字段错误的清单，格式为 {@code 字段: 提示; 字段: 提示}；</li>
 *   <li>补上"缺少必需参数"与"请求体不是合法 JSON"两类客户端错误的专门处理，
 *       避免它们落进兜底分支被报成 500 服务器内部错误；</li>
 *   <li>把状态码提为常量，避免 400/500 字面量散落在各处。</li>
 * </ol>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 客户端请求错误。 */
    private static final int CODE_BAD_REQUEST = 400;

    /** 服务端内部错误。 */
    private static final int CODE_SERVER_ERROR = 500;

    /**
     * 处理可预期的业务异常。
     *
     * @param exception 业务异常
     * @return 状态码 400 与异常自带提示
     */
    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusinessException(BusinessException exception) {
        return ApiResponse.error(CODE_BAD_REQUEST, exception.getMessage());
    }

    /**
     * 处理 {@code @Valid} 参数校验失败。
     *
     * @param exception 校验异常
     * @return 状态码 400 与全部字段错误清单
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> handleValidationException(MethodArgumentNotValidException exception) {
        return ApiResponse.error(CODE_BAD_REQUEST, formatFieldErrors(exception));
    }

    /**
     * 处理缺少必需查询参数。
     *
     * @param exception 缺参异常
     * @return 状态码 400 与缺失的参数名
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ApiResponse<Void> handleMissingParameter(MissingServletRequestParameterException exception) {
        return ApiResponse.error(CODE_BAD_REQUEST, "缺少必需参数：" + exception.getParameterName());
    }

    /**
     * 处理请求体无法解析（JSON 语法错误、类型不匹配等）。
     *
     * @param exception 报文解析异常
     * @return 状态码 400 与格式提示
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResponse<Void> handleUnreadableBody(HttpMessageNotReadableException exception) {
        return ApiResponse.error(CODE_BAD_REQUEST, "请求体格式错误，请检查 JSON 结构");
    }

    /**
     * 兜底处理未预期异常。
     *
     * @param exception 未预期异常
     * @return 状态码 500，不向前端暴露堆栈细节
     */
    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(Exception exception) {
        return ApiResponse.error(CODE_SERVER_ERROR, "服务器内部错误");
    }

    /**
     * 把全部字段错误拼成 {@code 字段: 提示} 的清单。
     *
     * @param exception 校验异常
     * @return 拼接后的提示；没有字段级错误时返回兜底文案
     */
    private String formatFieldErrors(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return message.isBlank() ? "请求参数不合法" : message;
    }
}
