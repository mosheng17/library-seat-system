package com.library.seatsystem.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

/**
 * S4 详细设计（改进后）—— 读能力接口。
 *
 * <p>把原先 {@code BaseService} 中"读"的职责单独抽出成接口，
 * 需要读能力的服务实现本接口即可，避免继承一个过全的基类。
 *
 * @param <T>  实体类型
 * @param <ID> 主键类型
 */
public interface ReadService<T, ID> {

    Optional<T> findById(ID id);

    List<T> findAll();

    Page<T> findPage(int pageNo, int size);
}
