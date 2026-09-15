package com.library.seatsystem.repository;

import com.library.seatsystem.entity.SysLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** A 系统基建与权限 —— 操作日志仓储。 */
public interface SysLogRepository extends JpaRepository<SysLog, Long> {

    List<SysLog> findAllByOrderByTimeDesc();

    List<SysLog> findByUserIdOrderByTimeDesc(Long userId);
}
