package com.library.seatsystem.service;

import com.library.seatsystem.entity.BaseEntity;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * A 系统基建与权限 —— 公共基础模块：服务层抽象基类。
 *
 * <p>提供通用单表增删改查与批量导入导出，子类只需注入对应的
 * {@link JpaRepository} 即可复用，不必重复实现 CRUD。
 *
 * @param <T>  实体类型（继承 {@link BaseEntity}）
 * @param <ID> 主键类型
 */
public abstract class BaseService<T extends BaseEntity, ID>
        implements ReadService<T, ID>, WriteService<T, ID>, ImportExportable<T> {

    protected final JpaRepository<T, ID> repository;

    protected BaseService(JpaRepository<T, ID> repository) {
        this.repository = repository;
    }

/** 按主键查询。 */
    @Override
    public Optional<T> findById(ID id) {
        return repository.findById(id);
    }

/** 查询全部。 */
    @Override
    public List<T> findAll() {
        return repository.findAll();
    }

/** 分页查询。 */
    @Override
    public Page<T> findPage(int pageNo, int size) {
        return repository.findAll(PageRequest.of(pageNo, size));
    }

/** 新增。 */
    @Override
    public T save(T entity) {
        return repository.save(entity);
    }

/** 更新。 */
    @Override
    public T update(T entity) {
        return repository.save(entity);
    }

/** 按主键删除。 */
    @Override
    public void delete(ID id) {
        repository.deleteById(id);
    }

/** 批量导入，返回成功写入条数。 */
    @Override
    public int importBatch(List<T> batch) {
        return repository.saveAll(batch).size();
    }

/** 批量导出为文本。 */
    @Override
    public String exportBatch(List<T> batch) {
        return batch.stream().map(Object::toString).collect(Collectors.joining("\n"));
    }

    /** 按主键查询，不存在则抛出业务异常。 */
    protected T findByIdOrThrow(ID id) {
        return repository.findById(id)
                .orElseThrow(() -> new com.library.seatsystem.exception.BusinessException("记录不存在：" + id));
    }
}
