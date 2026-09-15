package com.library.seatsystem.service;

import com.library.seatsystem.entity.SysLog;
import com.library.seatsystem.repository.SysLogRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * A 系统基建与权限 —— 操作日志模块。
 *
 * <p>记录并查询用户的关键操作（登录、预约、审核等），供管理员审计。
 */
@Service
public class SysLogService extends BaseService<SysLog, Long> {

    private final SysLogRepository sysLogRepository;

    public SysLogService(SysLogRepository sysLogRepository) {
        super(sysLogRepository);
        this.sysLogRepository = sysLogRepository;
    }

    @Transactional
    public SysLog record(Long userId, String operation, String module) {
        return sysLogRepository.save(new SysLog(userId, operation, module));
    }

    public List<SysLog> listAll() {
        return sysLogRepository.findAllByOrderByTimeDesc();
    }

    public List<SysLog> listByUser(Long userId) {
        return sysLogRepository.findByUserIdOrderByTimeDesc(userId);
    }
}
