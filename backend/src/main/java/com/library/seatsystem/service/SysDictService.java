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
 *
 * <p>改进点：原 {@code enable(Long id)} 方法体内只有 {@code dict.setType(dict.getType())}，
 * 既没有修改任何字段，也不表达"启用"语义，是典型的无效方法（dead code）；
 * 同时字典项只能新增和删除，无法修改。现把该方法替换为真正有语义的
 * {@link #updateValue(Long, String, Integer)}，并补上
 * {@link #findByTypeAndKey(String, String)} 精确查询。
 */
@Service
public class SysDictService extends BaseService<SysDict, Long> {

    private final SysDictRepository sysDictRepository;

    public SysDictService(SysDictRepository sysDictRepository) {
        super(sysDictRepository);
        this.sysDictRepository = sysDictRepository;
    }

    /**
     * 按类型查询字典项，按排序号升序返回。
     *
     * @param type 字典类型，如 SEAT_STATUS
     * @return 该类型下的全部字典项
     */
    public List<SysDict> listByType(String type) {
        return sysDictRepository.findByTypeOrderBySortAsc(type);
    }

    /**
     * 按"类型 + 键"精确查询单个字典项。
     *
     * @param type    字典类型，如 SEAT_STATUS
     * @param dictKey 字典键，如 AVAILABLE
     * @return 匹配的字典项
     * @throws BusinessException 该类型下不存在此键
     */
    public SysDict findByTypeAndKey(String type, String dictKey) {
        return sysDictRepository.findByTypeAndDictKey(type, dictKey)
                .orElseThrow(() -> new BusinessException("字典项不存在：" + type + "/" + dictKey));
    }

    /**
     * 新增字典项（同类型下键唯一）。
     *
     * @param dict 待新增的字典项
     * @return 已保存的字典项
     * @throws BusinessException 该分类下的字典键已存在
     */
    @Transactional
    public SysDict create(SysDict dict) {
        if (sysDictRepository.existsByTypeAndDictKey(dict.getType(), dict.getDictKey())) {
            throw new BusinessException("该分类下的字典键已存在");
        }
        return sysDictRepository.save(dict);
    }

    /**
     * 修改字典项的值与排序号。
     *
     * <p>字典类型与字典键是业务主键，不允许修改，因此只开放值（展示名）与排序号。
     *
     * @param id        字典项主键
     * @param dictValue 新的字典值（展示名）
     * @param sort      新的排序号，可为 null
     * @return 更新后的字典项
     * @throws BusinessException 字典值为空或字典项不存在
     */
    @Transactional
    public SysDict updateValue(Long id, String dictValue, Integer sort) {
        if (dictValue == null || dictValue.isBlank()) {
            throw new BusinessException("字典值不能为空");
        }
        SysDict dict = findByIdOrThrow(id);
        dict.setDictValue(dictValue);
        dict.setSort(sort);
        return sysDictRepository.save(dict);
    }

    /**
     * 批量导入字典项。
     *
     * @param batch 待导入的字典项
     * @return 成功写入条数
     */
    @Override
    @Transactional
    public int importBatch(List<SysDict> batch) {
        return super.importBatch(batch);
    }

    /**
     * 导出字典项为 CSV 文本。
     *
     * @param batch 待导出的字典项
     * @return CSV 文本
     */
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
