package com.library.seatsystem.service;

import com.library.seatsystem.entity.SysDict;
import com.library.seatsystem.exception.BusinessException;
import com.library.seatsystem.repository.SysDictRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * B 基础数据管理 —— 数据字典模块。
 *
 * <p>维护"座位状态 / 预约状态 / 用户角色"等分类字典，支持按类型查询与批量导入导出。
 */
@Service
public class SysDictService extends BaseService<SysDict, Long> {

    private final SysDictRepository sysDictRepository;

    public SysDictService(SysDictRepository sysDictRepository) {
        super(sysDictRepository);
        this.sysDictRepository = sysDictRepository;
    }

/** 按类型查询字典项。 */
    public List<SysDict> listByType(String type) {
        return sysDictRepository.findByTypeOrderBySortAsc(type);
    }

/** 新增字典项（同类型下键唯一）。 */
    @Transactional
    public SysDict create(SysDict dict) {
        if (sysDictRepository.existsByTypeAndDictKey(dict.getType(), dict.getDictKey())) {
            throw new BusinessException("该分类下的字典键已存在");
        }
        return sysDictRepository.save(dict);
    }

/** 保存字典项。 */
    @Transactional
    public void enable(Long id) {
        SysDict dict = findByIdOrThrow(id);
        dict.setType(dict.getType());
        sysDictRepository.save(dict);
    }

/** 批量导入字典项。 */
    @Override
    @Transactional
    public int importBatch(List<SysDict> batch) {
        return super.importBatch(batch);
    }

/** 导出字典项为 CSV。 */
    @Override
    public String exportBatch(List<SysDict> batch) {
        StringBuilder sb = new StringBuilder("type,dictKey,dictValue,sort\n");
        for (SysDict d : batch) {
            sb.append(d.getType()).append(',')
              .append(d.getDictKey()).append(',')
              .append(d.getDictValue()).append(',')
              .append(d.getSort() == null ? 0 : d.getSort()).append('\n');
        }
        return sb.toString();
    }
}
