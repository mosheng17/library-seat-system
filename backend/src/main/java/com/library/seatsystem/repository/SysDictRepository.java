package com.library.seatsystem.repository;

import com.library.seatsystem.entity.SysDict;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** B 基础数据管理 —— 数据字典仓储。 */
public interface SysDictRepository extends JpaRepository<SysDict, Long> {

    List<SysDict> findByTypeOrderBySortAsc(String type);

    boolean existsByTypeAndDictKey(String type, String dictKey);
}
