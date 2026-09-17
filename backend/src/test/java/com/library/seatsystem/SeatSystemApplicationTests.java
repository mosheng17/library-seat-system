package com.library.seatsystem;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Spring 上下文冒烟测试。
 *
 * <p>该用例需要真实的 MySQL 连接，属于集成测试，不在单元测试阶段执行；
 * 数据库相关的功能验证由 S8 第 1 节的手工功能测试用例覆盖。
 */
@SpringBootTest
@Disabled("需要真实 MySQL 连接，属集成测试；功能验证见 S8 第 1 节手工用例")
class SeatSystemApplicationTests {

    @Test
    void contextLoads() {
    }
}
