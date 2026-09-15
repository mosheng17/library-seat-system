CREATE DATABASE IF NOT EXISTS library_seat_system DEFAULT CHARACTER SET utf8mb4;

USE library_seat_system;

-- ===== A 系统基建与权限：用户表 =====
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    real_name VARCHAR(50) NOT NULL,
    created_at DATETIME
);

-- ===== B 基础数据管理：自习室表 =====
CREATE TABLE IF NOT EXISTS study_rooms (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    room_name VARCHAR(50) NOT NULL UNIQUE,
    floor INT NOT NULL,
    capacity INT NOT NULL,
    created_at DATETIME
);

-- ===== B 基础数据管理：座位表 =====
CREATE TABLE IF NOT EXISTS seats (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    room_id BIGINT NOT NULL,
    seat_code VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    created_at DATETIME,
    CONSTRAINT fk_seat_room FOREIGN KEY (room_id) REFERENCES study_rooms(id)
);

-- ===== C 核心业务流程：预约表 =====
CREATE TABLE IF NOT EXISTS reservations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    seat_id BIGINT NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'RESERVED',
    created_at DATETIME,
    CONSTRAINT fk_reservation_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_reservation_seat FOREIGN KEY (seat_id) REFERENCES seats(id)
);

-- ===== B 基础数据管理：数据字典表 =====
CREATE TABLE IF NOT EXISTS sys_dict (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    type VARCHAR(50) NOT NULL,
    dict_key VARCHAR(50) NOT NULL,
    dict_value VARCHAR(100) NOT NULL,
    sort_no INT,
    created_at DATETIME,
    UNIQUE KEY uk_dict_type_key (type, dict_key)
);

-- ===== A 系统基建与权限：操作日志表 =====
CREATE TABLE IF NOT EXISTS sys_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    operation VARCHAR(100) NOT NULL,
    module VARCHAR(50),
    log_time DATETIME NOT NULL,
    ip VARCHAR(50),
    created_at DATETIME,
    CONSTRAINT fk_log_user FOREIGN KEY (user_id) REFERENCES users(id)
);

INSERT INTO users (username, password, role, real_name)
VALUES
('student01', '123456', 'STUDENT', '张三'),
('admin01', '123456', 'ADMIN', '管理员')
ON DUPLICATE KEY UPDATE
password = VALUES(password),
role = VALUES(role),
real_name = VALUES(real_name);

INSERT INTO study_rooms (room_name, floor, capacity)
VALUES
('A101自习室', 1, 40),
('B201自习室', 2, 60)
ON DUPLICATE KEY UPDATE room_name = VALUES(room_name);

INSERT INTO seats (room_id, seat_code, status)
SELECT 1, 'A101-01', 'AVAILABLE'
WHERE NOT EXISTS (SELECT 1 FROM seats WHERE room_id = 1 AND seat_code = 'A101-01');

INSERT INTO seats (room_id, seat_code, status)
SELECT 1, 'A101-02', 'AVAILABLE'
WHERE NOT EXISTS (SELECT 1 FROM seats WHERE room_id = 1 AND seat_code = 'A101-02');

INSERT INTO seats (room_id, seat_code, status)
SELECT 1, 'A101-03', 'RESERVED'
WHERE NOT EXISTS (SELECT 1 FROM seats WHERE room_id = 1 AND seat_code = 'A101-03');

INSERT INTO seats (room_id, seat_code, status)
SELECT 2, 'B201-01', 'AVAILABLE'
WHERE NOT EXISTS (SELECT 1 FROM seats WHERE room_id = 2 AND seat_code = 'B201-01');

INSERT INTO seats (room_id, seat_code, status)
SELECT 2, 'B201-02', 'IN_USE'
WHERE NOT EXISTS (SELECT 1 FROM seats WHERE room_id = 2 AND seat_code = 'B201-02');

INSERT INTO seats (room_id, seat_code, status)
SELECT 2, 'B201-03', 'AVAILABLE'
WHERE NOT EXISTS (SELECT 1 FROM seats WHERE room_id = 2 AND seat_code = 'B201-03');

-- ===== 数据字典初始数据（座位状态 / 预约状态 / 用户角色）=====
INSERT INTO sys_dict (type, dict_key, dict_value, sort_no)
VALUES
('SEAT_STATUS', 'AVAILABLE', '空闲', 1),
('SEAT_STATUS', 'RESERVED', '已预约', 2),
('SEAT_STATUS', 'IN_USE', '使用中', 3),
('SEAT_STATUS', 'DISABLED', '停用', 4),
('SEAT_STATUS', 'MAINTENANCE', '维修中', 5),
('RESERVATION_STATUS', 'RESERVED', '已预约', 1),
('RESERVATION_STATUS', 'PENDING', '待审核', 2),
('RESERVATION_STATUS', 'APPROVED', '已通过', 3),
('RESERVATION_STATUS', 'REJECTED', '已驳回', 4),
('RESERVATION_STATUS', 'CANCELLED', '已取消', 5),
('USER_ROLE', 'STUDENT', '学生', 1),
('USER_ROLE', 'TEACHER', '教师', 2),
('USER_ROLE', 'ADMIN', '管理员', 3)
ON DUPLICATE KEY UPDATE dict_value = VALUES(dict_value), sort_no = VALUES(sort_no);
