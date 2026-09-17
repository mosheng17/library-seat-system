package com.library.seatsystem.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.library.seatsystem.common.ApiResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.http.converter.HttpMessageNotReadableException;

/**
 * A 全局异常处理器 GlobalExceptionHandler 的单元测试。
 *
 * <p>该类在 S5 阶段做过增强：校验失败返回全部字段错误、
 * 补齐"缺少必需参数"与"请求体格式错误"两类客户端错误分支。
 */
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("业务异常统一转换为 400 与业务提示")
    void businessException() {
        ApiResponse<Void> response = handler.handleBusinessException(
                new BusinessException("该时间段座位已被预约"));
        assertEquals(400, response.getCode().intValue());
        assertEquals("该时间段座位已被预约", response.getMessage());
    }

    @Test
    @DisplayName("参数校验失败：返回全部字段错误的“字段: 提示”清单")
    void validationWithFieldErrors() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("req", "userId", "不能为空"),
                new FieldError("req", "startTime", "格式不正确")));

        ApiResponse<Void> response = handler.handleValidationException(exception);

        assertEquals(400, response.getCode().intValue());
        assertEquals("userId: 不能为空; startTime: 格式不正确", response.getMessage());
    }

    @Test
    @DisplayName("参数校验失败：没有字段级错误时给出兜底文案")
    void validationWithoutFieldErrors() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of());

        assertEquals("请求参数不合法", handler.handleValidationException(exception).getMessage());
    }

    @Test
    @DisplayName("缺少必需查询参数：返回 400 并带上参数名")
    void missingParameter() {
        MissingServletRequestParameterException exception =
                mock(MissingServletRequestParameterException.class);
        when(exception.getParameterName()).thenReturn("roomId");

        ApiResponse<Void> response = handler.handleMissingParameter(exception);

        assertEquals(400, response.getCode().intValue());
        assertEquals("缺少必需参数：roomId", response.getMessage());
    }

    @Test
    @DisplayName("请求体不是合法 JSON：返回 400 而不是 500")
    void unreadableBody() {
        ApiResponse<Void> response = handler.handleUnreadableBody(
                mock(HttpMessageNotReadableException.class));
        assertEquals(400, response.getCode().intValue());
        assertEquals("请求体格式错误，请检查 JSON 结构", response.getMessage());
    }

    @Test
    @DisplayName("未预期异常兜底为 500，且不向前端暴露堆栈细节")
    void unexpectedException() {
        ApiResponse<Void> response = handler.handleException(new RuntimeException("boom: null pointer"));
        assertEquals(500, response.getCode().intValue());
        assertEquals("服务器内部错误", response.getMessage());
    }
}
