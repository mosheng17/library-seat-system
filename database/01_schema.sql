CREATE DATABASE IF NOT EXISTS library_seat_system DEFAULT CHARACTER SET utf8mb4;

USE library_seat_system;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    real_name VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS study_rooms (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    room_name VARCHAR(50) NOT NULL UNIQUE,
    floor INT NOT NULL,
    capacity INT NOT NULL
);

CREATE TABLE IF NOT EXISTS seats (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    room_id BIGINT NOT NULL,
    seat_code VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    CONSTRAINT fk_seat_room FOREIGN KEY (room_id) REFERENCES study_rooms(id)
);

CREATE TABLE IF NOT EXISTS reservations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    seat_id BIGINT NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'RESERVED',
    CONSTRAINT fk_reservation_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_reservation_seat FOREIGN KEY (seat_id) REFERENCES seats(id)
);

INSERT INTO users (username, password, role, real_name)
VALUES
('student01', '123456', 'STUDENT', '张三'),
('admin01', '123456', 'ADMIN', '管理员')
ON DUPLICATE KEY UPDATE username = VALUES(username);

INSERT INTO study_rooms (room_name, floor, capacity)
VALUES
('A101自习室', 1, 40),
('B201自习室', 2, 60)
ON DUPLICATE KEY UPDATE room_name = VALUES(room_name);

