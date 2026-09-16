package com.library.seatsystem.repository;

import com.library.seatsystem.entity.SysDict;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** B 基础数据管理 —— 数据字典仓储。 */
public interface SysDictRepository extends JpaRepository<SysDict, Long> {

    /** 按字典类型查询，并按排序号升序返回。 */
    List<SysDict> findByTypeOrderBySortAsc(String type);

    /** 判断同一字典类型下某个字典键是否已存在。 */
    boolean existsByTypeAndDictKey(String type, String dictKey);

    /** 按"类型 + 键"精确定位一个字典项，供前端下拉框回显单个选项时使用。 */
    Optional<SysDict> findByTypeAndDictKey(String type, String dictKey);
}
