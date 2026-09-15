package com.library.seatsystem.service;

import java.util.List;

/**
 * S4 详细设计（改进后）—— 批量导入导出能力接口。
 *
 * <p>对应 B 基础数据管理模块的"批量导入导出"需求。
 *
 * @param <T> 实体类型
 */
public interface ImportExportable<T> {

    /** 批量导入，返回成功写入条数。 */
    int importBatch(List<T> batch);

    /** 导出为 CSV 文本。 */
    String exportBatch(List<T> batch);
}
