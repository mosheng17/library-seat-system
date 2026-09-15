package com.library.seatsystem.service;

/**
 * S4 详细设计（改进后）—— 写能力接口。
 *
 * @param <T>  实体类型
 * @param <ID> 主键类型
 */
public interface WriteService<T, ID> {

    T save(T entity);

    T update(T entity);

    void delete(ID id);
}
