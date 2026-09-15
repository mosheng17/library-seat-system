package com.library.seatsystem.common;

/**
 * A 系统基建与权限 —— 业务常量定义。
 *
 * <p>把散落在各服务里的状态字符串、角色字符串集中到一处，
 * 避免魔法值（magic string）导致的不一致与拼写错误。
 */
public final class BizConstants {

    private BizConstants() {
    }

    /** 座位状态：空闲。 */
    public static final String SEAT_AVAILABLE = "AVAILABLE";

    /** 座位状态：已预约。 */
    public static final String SEAT_RESERVED = "RESERVED";

    /** 座位状态：使用中。 */
    public static final String SEAT_IN_USE = "IN_USE";

    /** 座位状态：停用。 */
    public static final String SEAT_DISABLED = "DISABLED";

    /** 座位状态：维修中。 */
    public static final String SEAT_MAINTENANCE = "MAINTENANCE";

    /** 预约状态：已预约（待审核）。 */
    public static final String RESERVATION_RESERVED = "RESERVED";

    /** 预约状态：待审核。 */
    public static final String RESERVATION_PENDING = "PENDING";

    /** 预约状态：审核通过。 */
    public static final String RESERVATION_APPROVED = "APPROVED";

    /** 预约状态：审核驳回。 */
    public static final String RESERVATION_REJECTED = "REJECTED";

    /** 预约状态：已取消。 */
    public static final String RESERVATION_CANCELLED = "CANCELLED";

    /** 角色：学生。 */
    public static final String ROLE_STUDENT = "STUDENT";

    /** 角色：教师。 */
    public static final String ROLE_TEACHER = "TEACHER";

    /** 角色：管理员。 */
    public static final String ROLE_ADMIN = "ADMIN";
}
